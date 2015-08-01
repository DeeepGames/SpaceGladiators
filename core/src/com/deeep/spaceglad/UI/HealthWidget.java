package com.deeep.spaceglad.UI;

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
        healthBar = new ProgressBar(0, 100, 20, false, Assets.skin);
//        healthBar.getStyle().knobBefore = healthBar.getStyle().knob;
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