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

    public HealthWidget() {
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle(
                Assets.skin.newDrawable("white", Color.RED),
                Assets.skin.newDrawable("white", Color.GREEN));
        progressBarStyle.background.setMinHeight(25);
        progressBarStyle.knob.setMinHeight(25);
        progressBarStyle.knobBefore = progressBarStyle.knob;
        healthBar = new ProgressBar(0, 100, 20, false, progressBarStyle);
        healthBar.setHeight(150);
        healthBar.setValue(75);
    }

    @Override
    public void act(float delta) {
        healthBar.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBar.draw(batch, parentAlpha);
    }
}