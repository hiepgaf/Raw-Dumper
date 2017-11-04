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

package com.fkeglevich.rawdumper.camera.parameter;

import com.fkeglevich.rawdumper.camera.async.direct.mutable.MutableParameterCollection;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class PictureSizeLayer extends MutableParameterCollection
{
    private final RawImageSize[] rawImageSizes;
    private boolean rawMode = false;

    public PictureSizeLayer(ParameterCollection parameterCollection, SensorInfo sensorInfo)
    {
        super(parameterCollection);
        rawImageSizes = sensorInfo.getRawImageSizes();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Parameter<T> parameter)
    {
        if (parameter == Parameters.PICTURE_SIZE && rawMode)
        {
            return (T) pictureSizeToRawSize(super.get(Parameters.PICTURE_SIZE));
        }
        return super.get(parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(Parameter<T> parameter, T value)
    {
        if (parameter == Parameters.PICTURE_SIZE && rawMode)
            super.set(parameter, (T) rawSizeToPictureSize((CaptureSize) value));
        else
            super.set(parameter, value);
    }

    public void enableRawMode()
    {
        rawMode = true;
    }

    public void disableRawMode()
    {
        rawMode = false;
    }

    private CaptureSize pictureSizeToRawSize(CaptureSize toFix)
    {
        for (RawImageSize rawImageSize : rawImageSizes)
            if (rawImageSize.getWidth() == toFix.getWidth() && rawImageSize.getHeight() == toFix.getHeight())
                return new CaptureSize(rawImageSize.getPaddedWidth(), rawImageSize.getPaddedHeight());

        throw new IllegalStateException("The current picture size is not a valid raw size!");
    }

    private CaptureSize rawSizeToPictureSize(CaptureSize toFix)
    {
        for (RawImageSize rawImageSize : rawImageSizes)
            if (rawImageSize.getPaddedWidth() == toFix.getWidth() && rawImageSize.getPaddedHeight() == toFix.getHeight())
                return new CaptureSize(rawImageSize.getWidth(), rawImageSize.getHeight());

        throw new IllegalStateException("The current raw size is not a valid picture size!");
    }
}
