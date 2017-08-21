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

package com.fkeglevich.rawdumper.io.async.tasks;

import com.fkeglevich.rawdumper.io.async.callbacks.IIOResultCallback;

/**
 * Created by Flávio Keglevich on 29/07/2017.
 * TODO: Add a class header comment!
 */

public class WriteFileTask implements Runnable
{
    private byte[] data;
    private String destFilePath;
    private IIOResultCallback resultCallback;

    public WriteFileTask()
    {   }

    @Override
    public void run()
    {
        /*BufferedOutputStream bos;
        try
        {
            bos = new BufferedOutputStream(new FileOutputStream(new File(destFilePath)));
            bos.write(data);
            bos.flush();
            bos.close();
            synchronized(ioLock)
            {
                MediaScannerConnection.scanFile(ioLock.getApplicationContext(), new String[]{destFilePath}, null, null);
            }
            resultCallback.onResult(true);
        }
        catch (IOException ioe)
        {
            resultCallback.onResult(false);
        }*/
    }
}