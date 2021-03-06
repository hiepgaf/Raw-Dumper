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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Keep;

/**
 * Represents the list of known devices.
 *
 * Created by Flávio Keglevich on 15/06/2017.
 */

@Keep
@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
class SupportedDeviceList
{
    private SupportedDevice[] supportedDevices;

    String findDeviceInfoFile(String deviceModel) throws IOException
    {
        for (SupportedDevice sd : supportedDevices)
            if (sd.deviceModel.equals(deviceModel))
                return sd.deviceInfoFile;

        throw new IOException("Couldn't find the device info file! device model: " + deviceModel);
    }

    List<String> listDeviceInfoFiles()
    {
        Set<String> files = new HashSet<>();
        for (SupportedDevice sd : supportedDevices)
            files.add(sd.deviceInfoFile);

        return new ArrayList<>(files);
    }
}
