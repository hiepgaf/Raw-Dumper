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

package com.fkeglevich.rawdumper.raw.info;

import android.os.Build;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Created by Flávio Keglevich on 11/06/2017.
 * TODO: Add a class header comment!
 */

public class DeviceInfo
{
    private String manufacturer;
    private CameraInfo[] cameras;

    private DeviceInfo()
    {   }

    public CameraInfo[] getCameras()
    {
        return cameras;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MAKE,   manufacturer);
    }

    public static DeviceInfo createZ00ADInfo()
    {
        DeviceInfo result = new DeviceInfo();
        result.manufacturer = Build.MANUFACTURER;
        result.cameras = new CameraInfo[]{CameraInfo.createT4k37CInfo(), CameraInfo.createOV5670CInfo()};

        return result;
    }


}
