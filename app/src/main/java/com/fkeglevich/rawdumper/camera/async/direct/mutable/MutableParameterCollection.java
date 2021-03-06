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

package com.fkeglevich.rawdumper.camera.async.direct.mutable;

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.util.Mutable;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/11/17.
 */

public class MutableParameterCollection extends ParameterCollection
{
    //Mutable state fields
    private final Mutable<ParameterCollection> mutable = Mutable.createInvalid();

    public static MutableParameterCollection createInvalid()
    {
        return new MutableParameterCollection();
    }

    protected MutableParameterCollection()
    {
        super(null);
    }

    public void setupMutableState(@NonNull ParameterCollection mutableCollection)
    {
        mutable.setupMutableState(mutableCollection);
    }

    @Override
    public <T> void remove(final Parameter<T> parameter)
    {
        mutable.get().remove(parameter);
    }

    @Override
    public <T> boolean has(Parameter<T> parameter)
    {
        return mutable.get().has(parameter);
    }

    @Override
    public <T> T get(Parameter<T> parameter)
    {
        return mutable.get().get(parameter);
    }

    @Override
    public <T> void set(Parameter<T> parameter, T value)
    {
        mutable.get().set(parameter, value);
    }

    @Override
    public <T> void override(Parameter<T> parameter, T value)
    {
        mutable.get().override(parameter, value);
    }
}
