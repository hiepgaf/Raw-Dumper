/*
 * Copyright 2018, Flávio Keglevich
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

import android.content.SharedPreferences;

import com.fkeglevich.rawdumper.controller.orientation.OrientationManager;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import androidx.annotation.NonNull;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class RawSettings
{
    public volatile boolean shouldInvertFrontCameraRows = true;
    public volatile boolean keepLensVignetting          = false;
    public volatile boolean compressRawFiles            = true;
    public volatile boolean useOrientationFromPhone     = true;
    public volatile boolean calculateDigest             = true;
    public volatile boolean addAnalogFilter             = false;

    public int getOrientationCode(ExtraCameraInfo cameraInfo)
    {
        if (useOrientationFromPhone)
        {
            return OrientationManager.getInstance().getImageOrientation(cameraInfo, shouldInvertRows(cameraInfo)).getExifCode();
        }
        else
            return ImageOrientation.TOPLEFT.getExifCode();
    }

    boolean shouldInvertRows(ExtraCameraInfo cameraInfo)
    {
        boolean isFrontCamera = cameraInfo.getFacing() == CAMERA_FACING_FRONT;
        return isFrontCamera && shouldInvertFrontCameraRows;
    }

    public void getDataFrom(RawSettings rawSettings)
    {
        shouldInvertFrontCameraRows = rawSettings.shouldInvertFrontCameraRows;
        keepLensVignetting          = rawSettings.keepLensVignetting;
        compressRawFiles            = rawSettings.compressRawFiles;
        useOrientationFromPhone     = rawSettings.useOrientationFromPhone;
        calculateDigest             = rawSettings.calculateDigest;
        addAnalogFilter             = rawSettings.addAnalogFilter;
    }

    public void storeValues(SharedPreferences.Editor editor)
    {
        editor.putBoolean("shouldInvertFrontCameraRows", shouldInvertFrontCameraRows)
                .putBoolean("keepLensVignetting", keepLensVignetting)
                .putBoolean("compressRawFiles", compressRawFiles)
                .putBoolean("useOrientationFromPhone", useOrientationFromPhone)
                .putBoolean("calculateDigest", calculateDigest)
                .putBoolean("addAnalogFilter", addAnalogFilter);
    }

    public void loadValues(SharedPreferences preferences)
    {
        shouldInvertFrontCameraRows = preferences.getBoolean("shouldInvertFrontCameraRows", true);
        keepLensVignetting          = preferences.getBoolean("keepLensVignetting", false);
        compressRawFiles            = preferences.getBoolean("compressRawFiles", true);
        useOrientationFromPhone     = preferences.getBoolean("useOrientationFromPhone", true);
        calculateDigest             = preferences.getBoolean("calculateDigest", true);
        addAnalogFilter             = preferences.getBoolean("addAnalogFilter", false);
    }

    @NonNull
    @Override
    public String toString()
    {
        return "[RawSettings shouldInvertFrontCameraRows=" + shouldInvertFrontCameraRows + ", " +
                "keepLensVignetting=" + keepLensVignetting + ", " +
                "compressRawFiles=" + compressRawFiles + ", " +
                "useOrientationFromPhone=" + useOrientationFromPhone + ", " +
                "calculateDigest=" + calculateDigest + ", " +
                "addAnalogFilter=" + addAnalogFilter + "]";
    }
}