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

package com.fkeglevich.rawdumper.camera.data;

import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class FocusArea
{
    private static final int DEFAULT_TOUCH_SIZE = 100;

    private final int x;
    private final int y;
    private final int viewWidth;
    private final int viewHeight;
    private final int touchSize;

    public static FocusArea createTouchArea(View view, MotionEvent motionEvent, int touchSize)
    {
        return new FocusArea((int) motionEvent.getX(), (int) motionEvent.getY(), view.getWidth(), view.getHeight(), touchSize);
    }

    public static FocusArea createTouchArea(View view, MotionEvent motionEvent)
    {
        return createTouchArea(view, motionEvent, DEFAULT_TOUCH_SIZE);
    }

    public FocusArea(int x, int y, int viewWidth, int viewHeight, int touchSize)
    {
        this.x = x;
        this.y = y;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.touchSize = touchSize;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getViewWidth()
    {
        return viewWidth;
    }

    public int getViewHeight()
    {
        return viewHeight;
    }

    public int getTouchSize()
    {
        return touchSize;
    }
}