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
import android.util.Log;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.pipeline.BufferFactory;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.YuvCaptureInfo;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public class YuvPipeline extends StandardPipeline
{
    private final FileFormat fileFormat;
    private final byte[] buffer;
    private final int bitmapBufferSize;

    private int[] bitmapBuffer = null;
    private Camera.Size pictureSize = null;

    public YuvPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, FileFormat fileFormat, byte[] buffer)
    {
        super(cameraExtension, lock, cameraContext, fileFormat);
        this.fileFormat = fileFormat;
        this.buffer = buffer;
        this.bitmapBufferSize = BufferFactory.calculateLargestBitmapBufferSize(cameraExtension);
        Log.i(YuvPipeline.class.getSimpleName(), bitmapBufferSize + "");
    }

    @Override
    protected void setupCameraBefore(Camera camera)
    {
        super.setupCameraBefore(camera);
        RawImageCallbackAccess.addRawImageCallbackBuffer(camera, buffer);
        pictureSize = camera.getParameters().getPictureSize();
        if (bitmapBuffer == null)
            bitmapBuffer = new int[bitmapBufferSize];
    }

    @Override
    void saveImage(PipelineData pipelineData, PictureListener pictureCallback, PictureExceptionListener exceptionCallback, String filename)
    {
        YuvCaptureInfo captureInfo  = new YuvCaptureInfo();
        captureInfo.yuvBuffer       = pipelineData.rawData;
        captureInfo.bitmapBuffer    = bitmapBuffer;
        captureInfo.width           = pictureSize.width;
        captureInfo.height          = pictureSize.height;
        captureInfo.fileFormat      = fileFormat;
        captureInfo.filename        = filename;

        IOThread.getIOAccess().saveYuv(captureInfo, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                postOnPictureSaved(pictureCallback);
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                postOnException(exceptionCallback, argument);
            }
        });
    }
}
