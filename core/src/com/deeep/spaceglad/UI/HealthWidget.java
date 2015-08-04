package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.deeep.spaceglad.Assets;

/**
 * Created by scanevaro on 01/08/2015.
 */
public class HealthWidget extends Actor {
    ProgressBar healthBar;
    ProgressBar.ProgressBarStyle progressBarStyle;
    Label label;

    public HealthWidget() {
        progressBarStyle = new ProgressBar.ProgressBarStyle(
                Assets.skin.newDrawable("white", Color.RED),
                Assets.skin.newDrawable("white", Color.GREEN));
        progressBarStyle.knobBefore = progressBarStyle.knob;
        healthBar = new ProgressBar(0, 100, 20, false, progressBarStyle);
        healthBar.setValue(100);
        label = new Label("Health", Assets.skin);
    }

    @Override
    public void act(float delta) {
        healthBar.act(delta);
        label.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBar.draw(batch, parentAlpha);
        label.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        healthBar.setPosition(x, y);
        label.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        healthBar.setSize(width, height);
        progressBarStyle.background.setMinWidth(width);
        progressBarStyle.background.setMinHeight(height);
        progressBarStyle.knob.setMinWidth(healthBar.getValue());
        progressBarStyle.knob.setMinHeight(height);
        label.setSize(width, height);
    }
}