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

package com.fkeglevich.rawdumper.raw.capture;

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.util.ColorUtil;

import java.util.Arrays;

import androidx.annotation.Nullable;

import static android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class WhiteBalanceInfo
{
    private static final WhiteBalanceInfo DEFAULT_WB_INFO = new WhiteBalanceInfo(new double[]{1, 1, 1});

    public static WhiteBalanceInfo create()
    {
        return DEFAULT_WB_INFO;
    }

    public static WhiteBalanceInfo create(ExtraCameraInfo cameraInfo, MakerNoteInfo makerNoteInfo, @Nullable Camera.Parameters parameters)
    {
        double[] neutralValues = WhiteBalanceService.getInstance().isAvailable() ? WhiteBalanceService.getInstance().getValue() : null;

        if (neutralValues != null)
        {
            Log.i(WhiteBalanceInfo.class.getSimpleName(), "Using neutral values from WB Service: " + Arrays.toString(neutralValues));
            return new WhiteBalanceInfo(neutralValues);
        }
        if (cameraInfo.hasKnownMakernote())
            return createFromMakerNote(makerNoteInfo, cameraInfo.getColor());
        else
        {
            if (parameters != null && parameters.getWhiteBalance() != null && !parameters.getWhiteBalance().equals(WHITE_BALANCE_AUTO))
            {
                for (WhiteBalancePreset preset : WhiteBalancePreset.values())
                    if (preset.getParameterValue().equals(parameters.getWhiteBalance()))
                        return createFromWhiteBalancePreset(preset, cameraInfo.getColor());

            }
            return createFromWhiteBalancePreset(WhiteBalancePreset.DAYLIGHT, cameraInfo.getColor());
        }
    }

    private static WhiteBalanceInfo createFromXYCoords(double x, double y, ColorInfo colorInfo)
    {
        double[] neutralValues = colorInfo.calculateSimpleAsShotNeutral(x, y);
        return new WhiteBalanceInfo(neutralValues);
    }

    private static WhiteBalanceInfo createFromMakerNote(MakerNoteInfo makerNoteInfo, ColorInfo colorInfo)
    {
        if (makerNoteInfo.wbCoordinatesXY != null)
            return createFromXYCoords(makerNoteInfo.wbCoordinatesXY[0], makerNoteInfo.wbCoordinatesXY[1], colorInfo);
        else
            return createFromDaylightTemperature(colorInfo);
    }

    private static WhiteBalanceInfo createFromWhiteBalancePreset(WhiteBalancePreset preset, ColorInfo colorInfo)
    {
        float[] xy = ColorUtil.getXYFromCCT(preset.getTemperature().get(), colorInfo);
        return createFromXYCoords(xy[0], xy[1], colorInfo);
    }

    private static WhiteBalanceInfo createFromDaylightTemperature(ColorInfo colorInfo)
    {
        return createFromWhiteBalancePreset(WhiteBalancePreset.DAYLIGHT, colorInfo);
    }

    private final double[] asShotNeutral;

    private WhiteBalanceInfo(double[] asShotNeutral)
    {
        this.asShotNeutral = asShotNeutral;
    }

    public void writeInfoTo(DngNegative negative)
    {
        negative.setCameraNeutral(asShotNeutral);
    }
}
