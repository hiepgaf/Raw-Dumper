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

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.raw_builder.MemoryCaptureInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.image.MemoryRawImage;
import com.fkeglevich.rawdumper.su.MainSUShell;
import com.fkeglevich.rawdumper.util.MinDelay;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import static com.fkeglevich.rawdumper.raw.data.DumpFile.RAW_DUMP_FILE_EXTENSION;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 03/11/17.
 */

public class StandardRawPipeline extends PicturePipelineBase
{
    private static final int MINIMUM_DELAY_AFTER_START_PREVIEW = 400;

    private final CameraContext cameraContext;
    private final byte[] buffer;
    private final MinDelay delay;

    private Camera.Parameters parameters = null;

    StandardRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, byte[] buffer)
    {
        super(cameraExtension, lock);
        this.cameraContext = cameraContext;
        this.buffer = buffer;
        this.delay = new MinDelay(MINIMUM_DELAY_AFTER_START_PREVIEW);
    }

    @Override
    protected void setupCameraBefore(Camera camera)
    {
        super.setupCameraBefore(camera);
        parameters = camera.getParameters();
        RawImageCallbackAccess.addRawImageCallbackBuffer(camera, buffer);
    }

    @Override
    protected void processPipeline(PipelineData pipelineData, final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        startPreview();
        delay.startCounting();
        saveDngPicture(pipelineData, pictureCallback, exceptionCallback);
        postOnPictureTaken(pictureCallback);
    }

    private void saveDngPicture(PipelineData pipelineData, final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        RawImageSize size = cameraContext.getSensorInfo().getRawImageSizeFromParameters(parameters);
        MemoryRawImage rawImage = new MemoryRawImage(pipelineData.rawData, pipelineData.jpegData, size);
        RawCaptureInfo captureInfo = new MemoryCaptureInfo(cameraContext, rawImage, parameters);

        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                removeI3av4File(pictureCallback);
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                exceptionCallback.onException(argument);
            }
        });
    }

    private void removeI3av4File(final PictureListener pictureCallback)
    {
        String dumpDirectory = cameraContext.getDeviceInfo().getDumpDirectoryLocation();
        if (!DebugFlag.isDisableMandatoryRoot())
        {
            MainSUShell.getInstance().addSingleCommand("rm " + dumpDirectory + "/*" + RAW_DUMP_FILE_EXTENSION,
                    result -> postOnPictureSaved(pictureCallback));
        }
        else
            postOnPictureSaved(pictureCallback);
    }

    private void postOnPictureTaken(final PictureListener pictureCallback)
    {
        uiHandler.postDelayed(pictureCallback::onPictureTaken, delay.stopCounting());
    }

    private void postOnPictureSaved(final PictureListener pictureCallback)
    {
        uiHandler.post(pictureCallback::onPictureSaved);
    }
}
