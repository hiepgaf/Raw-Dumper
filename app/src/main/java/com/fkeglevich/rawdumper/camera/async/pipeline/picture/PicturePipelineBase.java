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

import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureSkipListener;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.util.Mutable;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public abstract class PicturePipelineBase implements PicturePipeline
{
    private final Mutable<ICameraExtension> cameraExtension;
    private final Object lock;

    final Handler uiHandler;

    PicturePipelineBase(Mutable<ICameraExtension> cameraExtension, Object lock)
    {
        this.cameraExtension = cameraExtension;
        this.lock = lock;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            final PipelineData pipelineData = new PipelineData();
            configCamera().takePicture(null, createRawCB(pipelineData), createJpegCB(pictureCallback, exceptionCallback, pipelineData));
        }
    }

    @Override
    public void skipPicture(PictureSkipListener skipCallback)
    {
        synchronized (lock)
        {
            configCamera().takePicture(null, createRawCB(new PipelineData()), createJpegCBForSkipping(skipCallback));
        }
    }

    private Camera configCamera()
    {
        Camera camera = cameraExtension.get().getCameraDevice();
        setupCameraBefore(camera);
        return camera;
    }

    @NonNull
    private Camera.PictureCallback createRawCB(final PipelineData pipelineData)
    {
        return (data, camera) -> pipelineData.rawData = data;
    }

    @NonNull
    private Camera.PictureCallback createJpegCB(final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback, final PipelineData pipelineData)
    {
        return (data, camera) ->
        {
            pipelineData.jpegData = data;
            synchronized (lock)
            {
                processPipeline(pipelineData, pictureCallback, exceptionCallback);
            }
        };
    }

    @NonNull
    private Camera.PictureCallback createJpegCBForSkipping(PictureSkipListener skipCallback)
    {
        return (data, camera) ->
        {
            synchronized (lock)
            {
                startPreview();
                uiHandler.post(skipCallback::onPictureSkipped);
            }
        };
    }

    void setupCameraBefore(Camera camera)
    {   /*no op*/    }

    void startPreview()
    {
        cameraExtension.get().getCameraDevice().startPreview();
    }

    protected abstract void processPipeline(PipelineData pipelineData, PictureListener pictureCallback, PictureExceptionListener exceptionCallback);
}
