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

package com.fkeglevich.rawdumper.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Flávio Keglevich on 13/05/2017.
 * TODO: Add a class header comment!
 */

public class UiUtil
{
    private static float dpToPixels(float dp, Context context)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPixels(int dp, Context context)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int getDimensionInPixels(int id, Context context)
    {
        return (int)dpToPixels(context.getResources().getDimension(id), context);
    }

    private static void setDpPadding(float padding, View view)
    {
        Context context = view.getContext();
        view.setPadding((int)dpToPixels(padding, context), (int)dpToPixels(padding, context),
                (int)dpToPixels(padding, context), (int)dpToPixels(padding, context));
    }

    public static void setDpPaddingFromDimension(int id, View view)
    {
        Context context = view.getContext();
        setDpPadding(context.getResources().getDimension(id), view);
    }

    public static void showDialogInImmersiveMode(final Dialog dialog, final AppCompatActivity activity)
    {
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
}
