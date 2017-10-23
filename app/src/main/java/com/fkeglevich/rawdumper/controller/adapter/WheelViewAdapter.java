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

package com.fkeglevich.rawdumper.controller.adapter;

import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.controller.feature.DisplayableFeatureUi;
import com.fkeglevich.rawdumper.ui.listener.ItemSelectedListener;
import com.lantouzi.wheelview.WheelView;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 19/10/17.
 */

public class WheelViewAdapter implements DisplayableFeatureUi
{
    private final WheelView wheelView;

    public WheelViewAdapter(WheelView wheelView)
    {
        this.wheelView = wheelView;
    }

    @Override
    public void setItems(List<String> items)
    {
        wheelView.setItems(items);
        wheelView.selectIndex(0);
        wheelView.setMaxSelectableIndex(items.size() - 1);
    }

    @Override
    public void setSelectedIndex(int index)
    {
        wheelView.selectIndex(index);
    }

    @Override
    public void displayExternalChangeNotification(Displayable newValue)
    {   }

    @Override
    public void setListener(final ItemSelectedListener listener)
    {
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener()
        {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position)
            {
                listener.onSelected(position);
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position)
            {   }
        });
    }

    @Override
    public void enable()
    {   }

    @Override
    public void disable()
    {   }
}
