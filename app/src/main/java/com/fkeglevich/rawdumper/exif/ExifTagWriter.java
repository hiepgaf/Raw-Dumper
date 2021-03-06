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

package com.fkeglevich.rawdumper.exif;

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;

import java.util.Calendar;

public interface ExifTagWriter
{
    void writeExposureTimeTags(ShutterSpeed shutterSpeed);

    void writeISOTag(Iso iso);

    void writeMakerNoteTag(byte[] makerNote);

    void writeDateTagsAsCurrentDate();

    void writeDateTimeOriginalTags(Calendar dateTimeOriginal);

    void writeDateTimeDigitizedTags(Calendar dateTimeDigitized);

    void writeApertureTags(double aperture);

    void writeExifVersionTag(byte[] exifVersion);

    void writeExposureBiasTag(Ev exposureBias);

    void writeFlashTag(ExifFlash flash);

    void writeFocalLengthTag(float focalLength);

    default void writeSoftwareTag(String software)
    {  /*no op*/  }

    default void writeMakeTag(String make)
    {  /*no op*/  }

    default void writeModelTag(String model)
    {  /*no op*/  }
}
