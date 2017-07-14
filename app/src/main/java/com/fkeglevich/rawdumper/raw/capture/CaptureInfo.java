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

import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.CameraInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.nio.charset.Charset;

/**
 * Created by Flávio Keglevich on 09/06/2017.
 * TODO: Add a class header comment!
 */

public class CaptureInfo
{
    public DeviceInfo device;
    public CameraInfo camera;
    public DateInfo date;
    public MakerNoteInfo makerNoteInfo;
    public WhiteBalanceInfo whiteBalanceInfo;
    public RawImageSize imageSize;
    public Camera.Parameters captureParameters;
    public byte[] extraJpegBytes;

    public String originalRawFilename;
    public ImageOrientation orientation;

    public CaptureInfo()
    {   }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_ORIGINALRAWFILENAME, originalRawFilename.getBytes(Charset.forName("UTF-8")), true);
        tiffWriter.setField(TiffTag.TIFFTAG_ORIENTATION, orientation.getExifCode());
    }
}
