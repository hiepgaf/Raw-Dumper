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

import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.parameter.ExposureParameterFactory;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.ValueCollectionFactory;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 26/09/17.
 */

class IsoFeature extends ListFeature<Iso>
{
    @NonNull
    static IsoFeature create(ExposureInfo exposureInfo, ParameterCollection parameterCollection)
    {
        Parameter<Iso> isoParameter = ExposureParameterFactory.createIsoParameter(exposureInfo);
        List<String> isoValues = exposureInfo.getIsoValues();
        List<Iso> valueList = ValueCollectionFactory.decodeValueList(isoParameter, isoValues);
        return new IsoFeature(isoParameter, parameterCollection, valueList);
    }

    private IsoFeature(Parameter<Iso> featureParameter, ParameterCollection parameterCollection,
                       List<Iso> valueList)
    {
        super(featureParameter, parameterCollection, new ListValidator<>(valueList));
    }
}
