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

package com.fkeglevich.rawdumper.controller.feature.preset;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.feature.ProportionFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.feature.FeatureController;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.Visibility;

import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 09/05/18.
 */
public abstract class ManualController<P, M extends Comparable<M>> extends FeatureController
{
    private final View manualButton;
    private final View backButton;
    private final SeekBar seekBar;
    private final View manualChooser;
    private final View presetChooser;

    private final Visibility manualChooserTransition;
    private final Visibility presetChooserTransition;

    private ProportionFeature<M, DataRange<M>> manualFeature;
    private P lastPreset = getDefaultPresetValue();

    public ManualController(View manualButton,
                            View backButton,
                            View manualChooser,
                            SeekBar seekBar,
                            View presetChooser)
    {
        this.manualButton  = manualButton;
        this.backButton    = backButton;
        this.manualChooser = manualChooser;
        this.seekBar       = seekBar;
        this.presetChooser = presetChooser;

        manualChooserTransition = new Fade();
        manualChooserTransition.setDuration(300L);
        presetChooserTransition = new Fade();
        presetChooserTransition.setDuration(300L);
        disable();
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        manualFeature = this.<M>getManualFeature(camera);
        if (!(manualFeature.isAvailable() && getPresetFeature(camera).isAvailable()))
        {
            reset();
            return;
        }

        enable();
        manualButton.setOnClickListener(v ->
        {
            lastPreset = getPresetFeature(camera).getValue();
            setupCameraOnManualButtonClick(camera);
            getPresetFeature(camera).setValue(getDefaultPresetValue());
            showChooser();
        });

        if (manualFeature.getValue() != getDisabledManualValue())
            new Handler().post(manualButton::callOnClick);

        backButton.setOnClickListener(v ->
        {
            hideChooser();
            if (manualFeature.isAvailable())
            {
                manualFeature.setValue(getDisabledManualValue());
                getPresetFeature(camera).setValue(lastPreset);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateManualProportion(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                onShowChooser();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {   }
        });
    }

    private void updateManualProportion(int progress)
    {
        double proportion = progress / (double) seekBar.getMax();
        manualFeature.setValueAsProportion(proportion);
    }

    @Override
    protected void reset()
    {
        manualChooser.setVisibility(View.INVISIBLE);
        presetChooser.setVisibility(View.VISIBLE);
        disable();
    }

    @Override
    protected void disable()
    {
        manualButton.setVisibility(View.GONE);
    }

    @Override
    protected void enable()
    {
        manualButton.setVisibility(View.VISIBLE);
    }

    private void hideChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) manualChooser, manualChooserTransition);
        manualChooser.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition((ViewGroup) presetChooser, presetChooserTransition);
        presetChooser.setVisibility(View.VISIBLE);

        onHideChooser();
    }

    private void showChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) manualChooser, manualChooserTransition);
        manualChooser.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition((ViewGroup) presetChooser, presetChooserTransition);
        presetChooser.setVisibility(View.INVISIBLE);

        updateManualProportion(seekBar.getProgress());
        onShowChooser();
    }

    protected abstract P getDefaultPresetValue();
    protected abstract M getDisabledManualValue();

    @SuppressWarnings("unchecked")
    private ProportionFeature<M, DataRange<M>> getManualFeature(TurboCamera camera)
    {
        return (ProportionFeature<M,DataRange<M>>) camera.getRangeFeature((Class<M>) getDisabledManualValue().getClass());
    }

    @SuppressWarnings("unchecked")
    private WritableFeature<P, List<P>> getPresetFeature(TurboCamera camera)
    {
        return camera.getListFeature((Class<P>) getDefaultPresetValue().getClass());
    }

    protected void setupCameraOnManualButtonClick(TurboCamera camera)
    { }

    protected void onHideChooser()
    { }

    protected void onShowChooser()
    { }
}
