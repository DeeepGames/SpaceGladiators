package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.deeep.spaceglad.Logger;
import com.deeep.spaceglad.Settings;

/**
 * Created by Andreas on 10/21/2015.
 */

public class CrosshairWidget extends Actor {

    private Image crosshairDot, crosshairOuterRing, crosshairInnerRing;
    private float rotationSpeed;

    public CrosshairWidget() {
        crosshairDot = new Image(new Texture(Gdx.files.internal("crosshair/crossHairPoint.png")));
        crosshairInnerRing = new Image(new Texture(Gdx.files.internal("crosshair/crossHairInnerRing.png")));
        crosshairOuterRing = new Image(new Texture(Gdx.files.internal("crosshair/crossHairOuterRing.png")));
        rotationSpeed = 1F;
    }

    @Override
    public void act(float delta) {
        if (Settings.Paused) return;
        //crosshairInnerRing.rotateBy(rotationSpeed);
        crosshairOuterRing.rotateBy(rotationSpeed);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Settings.Paused) return;
        crosshairDot.draw(batch, parentAlpha);
        //crosshairInnerRing.draw(batch, parentAlpha);
        //crosshairOuterRing.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        crosshairDot.setPosition(x, y);
        crosshairInnerRing.setPosition(x, y);
        crosshairOuterRing.setPosition(x - 16, y - 16);
        crosshairInnerRing.setOrigin(x, y);
        crosshairOuterRing.setOrigin(x - 16, y - 16);
        Logger.log(Logger.ANDREAS, Logger.INFO, "Setting origin to " + x + ", " + y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        crosshairDot.setSize(width, height);
        crosshairInnerRing.setSize(width, height);
        crosshairOuterRing.setSize(width * 2, height * 2);
    }

}
