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

package com.fkeglevich.rawdumper.camera.extension;

import com.asus.camera.extensions.AsusCameraExtension;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

public interface IMeteringExtension
{
    void release();
    void startQueryData(int queryInterval);
    void stopQueryingData();

    EventDispatcher<AsusCameraExtension.CaptureFrameData> getOnGotCaptureFrameData();
    EventDispatcher<AsusCameraExtension.ProfessionalData> getOnGotProfessionalData();
}
