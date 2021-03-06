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

package com.fkeglevich.rawdumper.camera.feature;

import android.content.SharedPreferences;

import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.RangeValidator;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class ManualFocusFeature extends RangeFeature<ManualFocus>
{
    ManualFocusFeature(AsyncParameterSender asyncParameterSender, ParameterCollection parameterCollection)
    {
        super(asyncParameterSender, AsusParameters.MANUAL_FOCUS, parameterCollection,
                RangeValidator.create(parameterCollection, AsusParameters.MANUAL_FOCUS_RANGE));
    }

    @Override
    public void setValueAsProportion(double proportion)
    {
        int lower = getAvailableValues().getLower().getNumericValue();
        int upper = getAvailableValues().getUpper().getNumericValue();

        double numericValue = (upper - lower) * proportion + lower;

        ManualFocus manualFocus = ManualFocus.create((int) Math.round(numericValue));
        setValueAsync(manualFocus);
    }

    @Override
    void storeValue(SharedPreferences.Editor editor)
    {
        if (!isAvailable()) return;

        ManualFocus value = getValue();
        if (!ManualFocus.DISABLED.equals(value))
            editor.putInt(parameter.getKey(), value.getNumericValue());
    }

    @Override
    void loadValue(SharedPreferences preferences)
    {
        if (!isAvailable()) return;

        int numValue = preferences.getInt(parameter.getKey(), 0);
        if (numValue != 0)
            setValue(ManualFocus.create(numValue));
    }
}
