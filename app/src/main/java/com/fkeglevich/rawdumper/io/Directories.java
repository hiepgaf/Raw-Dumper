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

package com.fkeglevich.rawdumper.io;

import java.io.File;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 06/11/17.
 */

public class Directories
{
    private static final String RAW_DUMPER_DIR_NAME = "RawDumper";

    private static final File RAW_DUMPER_DIR = new File(getExternalStoragePublicDirectory(DIRECTORY_DCIM), RAW_DUMPER_DIR_NAME);

    public static File getPicturesDirectory()
    {
        return RAW_DUMPER_DIR;
    }

    public static File getVideosDirectory()
    {
        return getPicturesDirectory();
    }
}
