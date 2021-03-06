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

package com.fkeglevich.rawdumper.io.async;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunctionContext;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.io.async.function.LoadDeviceInfoFunction;
import com.fkeglevich.rawdumper.io.async.function.SaveDngFunction;
import com.fkeglevich.rawdumper.io.async.function.SaveFileFunction;
import com.fkeglevich.rawdumper.io.async.function.SaveYuvFunction;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.YuvCaptureInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

public class IOAccess
{
    private final ThrowingAsyncFunctionContext functionContext;

    IOAccess(ThrowingAsyncFunctionContext functionContext)
    {
        this.functionContext = functionContext;
    }

    public void saveFileAsync(byte[] data, String filePath, AsyncOperation<Void> callback, AsyncOperation<MessageException> exception)
    {
        functionContext.call(new SaveFileFunction(filePath), data, callback, exception);
    }

    public void loadDeviceInfo(AsyncOperation<DeviceInfo> callback, AsyncOperation<MessageException> exception)
    {
        functionContext.call(new LoadDeviceInfoFunction(), null, callback, exception);
    }

    public void saveDng(RawCaptureInfo captureInfo, AsyncOperation<Void> callback, AsyncOperation<MessageException> exception)
    {
        functionContext.call(new SaveDngFunction(), captureInfo, callback, exception);
    }

    public void saveYuv(YuvCaptureInfo captureInfo, AsyncOperation<Void> callback, AsyncOperation<MessageException> exception)
    {
        functionContext.call(new SaveYuvFunction(), captureInfo, callback, exception);
    }

    public void saveStringAsync(String data, String filePath, AsyncOperation<Void> callback, AsyncOperation<MessageException> exception)
    {
        saveFileAsync(data.getBytes(), filePath, callback, exception);
    }
}
