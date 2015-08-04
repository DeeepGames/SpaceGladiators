package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.deeep.spaceglad.Assets;

/**
 * Created by scanevaro on 04/08/2015.
 */
public class EnergyWidget extends Actor {
    ProgressBar energyBar;
    ProgressBar.ProgressBarStyle progressBarStyle;
    Label label;

    public EnergyWidget() {
        progressBarStyle = new ProgressBar.ProgressBarStyle(
                Assets.skin.newDrawable("white", Color.OLIVE),
                Assets.skin.newDrawable("white", Color.ORANGE));
        progressBarStyle.knobBefore = progressBarStyle.knob;
        energyBar = new ProgressBar(0, 100, 20, false, progressBarStyle);
        energyBar.setValue(100);
        label = new Label("Energy", Assets.skin);
    }

    @Override
    public void act(float delta) {
        energyBar.act(delta);
        label.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        energyBar.draw(batch, parentAlpha);
        label.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        energyBar.setPosition(x, y);
        label.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        energyBar.setSize(width, height);
        progressBarStyle.background.setMinWidth(width);
        progressBarStyle.background.setMinHeight(height);
        progressBarStyle.knob.setMinWidth(energyBar.getValue());
        progressBarStyle.knob.setMinHeight(height);
        label.setSize(width, height);
    }
}
