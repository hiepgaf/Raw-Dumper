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

package com.fkeglevich.rawdumper.controller.activity;

import android.support.v7.app.AppCompatActivity;

import com.fkeglevich.rawdumper.controller.activity.event.InteractiveEvent;
import com.fkeglevich.rawdumper.controller.activity.event.LifetimeEvent;

import java.lang.ref.WeakReference;

/**
 * Created by Flávio Keglevich on 30/08/2017.
 * TODO: Add a class header comment!
 */

public class ActivityReference
{
    private WeakReference<AppCompatActivity> reference;
    private EventDispatcher<LifetimeEvent> lifetimeEvents = new EventDispatcher<>(LifetimeEvent.class);
    private EventDispatcher<InteractiveEvent> interactiveEvents = new EventDispatcher<>(InteractiveEvent.class);

    public AppCompatActivity weaklyGet()
    {
        return reference.get();
    }

    public EventDispatcher<LifetimeEvent> getLifetimeEvents()
    {
        return lifetimeEvents;
    }

    public EventDispatcher<InteractiveEvent> getInteractiveEvents()
    {
        return interactiveEvents;
    }

    void updateReference(AppCompatActivity compatActivity)
    {
        reference = new WeakReference<>(compatActivity);
    }
}
