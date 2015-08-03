package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.deeep.spaceglad.Assets;

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
        progressBarStyle.background.setMinHeight(25);
        progressBarStyle.knob.setMinHeight(25);
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
    }

    @Override
    public void setPosition(float x, float y) {
        setPosition(x, y);
        healthBar.setPosition(x, y);
    }
}