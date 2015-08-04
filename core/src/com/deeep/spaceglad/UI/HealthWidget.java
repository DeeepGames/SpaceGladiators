package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Logger;

/**
 * Created by scanevaro on 01/08/2015.
 */
public class HealthWidget extends Actor {
    ProgressBar healthBar;
    ProgressBar.ProgressBarStyle progressBarStyle;

    public HealthWidget() {
        progressBarStyle = new ProgressBar.ProgressBarStyle(
                Assets.skin.newDrawable("white", Color.RED),
                Assets.skin.newDrawable("white", Color.GREEN));
        progressBarStyle.knobBefore = progressBarStyle.knob;
        healthBar = new ProgressBar(0, 100, 20, false, progressBarStyle);
        healthBar.setValue(100);
    }

    @Override
    public void act(float delta) {
        healthBar.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBar.draw(batch, parentAlpha);
        Logger.log(Logger.SEBA, Logger.INFO, String.valueOf(healthBar.getValue()));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        healthBar.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        healthBar.setSize(width, height);
        progressBarStyle.background.setMinWidth(width);
        progressBarStyle.background.setMinHeight(height);
        progressBarStyle.knob.setMinWidth(healthBar.getValue());
        progressBarStyle.knob.setMinHeight(height);
    }
}