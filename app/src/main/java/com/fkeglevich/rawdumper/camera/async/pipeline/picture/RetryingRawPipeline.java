/*
 * Copyright 2017, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.camera.async.pipeline.picture;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureSkipListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.dummy.RetryingPipelineSimulator;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.io.async.exception.SaveFileException;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.raw_builder.FileCaptureInfo;
import com.fkeglevich.rawdumper.raw.data.DumpFile;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.image.FileRawImage;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;
import com.topjohnwu.superuser.io.SuFile;

import java.io.IOException;

import static android.hardware.Camera.CAMERA_ERROR_UNKNOWN;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

public class RetryingRawPipeline implements PicturePipeline
{
    private final Camera.ErrorCallback      errorCallback;
    private final Mutable<ICameraExtension> cameraExtension;
    private final Object                    lock;
    private final CameraContext             cameraContext;
    private final RestartableCamera         restartableCamera;
    private final int                       minRetryingDelay;

    private Camera.Parameters               parameters = null;
    private boolean                         ignoreError = false;
    private volatile int                    pipelineDelay;

    private final Camera.PictureCallback dummyJpegCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            cameraExtension.get().getCameraDevice().startPreview();
            errorCallback.onError(CAMERA_ERROR_UNKNOWN, camera);
        }
    };

    private PictureListener nextPictureCallback;
    private PictureExceptionListener nextExceptionCallback;

    private final Handler uiHandler;
    private final Handler cameraHandler;

    RetryingRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, RestartableCamera restartableCamera, byte[] buffer)
    {
        this.errorCallback      = createErrorCallback();
        this.cameraExtension    = cameraExtension;
        this.lock               = lock;
        this.cameraContext      = cameraContext;
        this.restartableCamera  = restartableCamera;
        this.minRetryingDelay   = cameraContext.getCameraInfo().getRetryPipelineDelay();
        this.pipelineDelay      = minRetryingDelay;
        this.uiHandler          = new Handler(Looper.getMainLooper());
        this.cameraHandler      = new Handler(Looper.myLooper());
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            nextPictureCallback = pictureCallback;
            nextExceptionCallback = exceptionCallback;
            ignoreError = true;
            Camera camera = cameraExtension.get().getCameraDevice();
            parameters = camera.getParameters();

            if (DebugFlag.usingRetryPipelineSimulator())
                RetryingPipelineSimulator.simulate(cameraContext, errorCallback);
            else
            {
                camera.setErrorCallback(errorCallback);
                camera.takePicture(null, null, dummyJpegCallback);
            }
        }
    }

    @Override
    public void skipPicture(PictureSkipListener skipCallback)
    {
        CameraThread.getInstance().restartCamera(restartableCamera, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                skipCallback.onPictureSkipped();
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                skipCallback.onPictureSkipped(); //We shouldn't ignore this error
            }
        });
    }

    @Override
    public void updateShutterSpeed(ShutterSpeed shutterSpeed)
    {
        if (shutterSpeed == null || ShutterSpeed.AUTO.equals(shutterSpeed))
        {
            pipelineDelay = minRetryingDelay;
        }
        else
        {
            int delay = (int)Math.ceil(shutterSpeed.getExposureInSeconds() * 1000);
            if (delay < minRetryingDelay) delay = minRetryingDelay;

            pipelineDelay = delay;
        }
    }

    private void processPicture() throws IOException
    {
        RawImageSize size = cameraContext.getSensorInfo().getRawImageSizeFromParameters(parameters);
        String dumpDirectory = cameraContext.getDeviceInfo().getDumpDirectoryLocation();

        SuFile i3av4Image = DumpFile.selectI3av4Image(dumpDirectory);
        FileRawImage rawImage = new FileRawImage(i3av4Image, size);

        RawCaptureInfo captureInfo = new FileCaptureInfo(cameraContext, rawImage, parameters);

        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                postOnPictureSaved();
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                postOnPictureException(argument);
            }
        });
    }

    private void tryProcessPicture()
    {
        try
        {
            processPicture();
            postOnPictureTaken();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            postOnPictureException(new SaveFileException());
        }
    }

    private void restartCamera()
    {
        CameraThread.getInstance().restartCamera(restartableCamera, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                cameraHandler.post(() -> tryProcessPicture());
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                postOnPictureException(argument);
            }
        });
    }

    private Camera.ErrorCallback createErrorCallback()
    {
        return (error, camera) ->
        {
            if (ignoreError)
            {
                ThreadUtil.simpleDelay(pipelineDelay);
                restartCamera();
                ignoreError = false;
            }
        };
    }

    private void postOnPictureTaken()
    {
        uiHandler.post(() -> nextPictureCallback.onPictureTaken());
    }

    private void postOnPictureSaved()
    {
        uiHandler.post(() -> nextPictureCallback.onPictureSaved());
    }

    private void postOnPictureException(MessageException exception)
    {
        uiHandler.post(() -> nextExceptionCallback.onException(exception));
    }
}
