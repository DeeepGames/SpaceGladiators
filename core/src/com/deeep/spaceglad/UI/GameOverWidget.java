package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by scanevaro on 04/08/2015.
 */
public class GameOverWidget extends Actor {
    Image image;

    public GameOverWidget() {
//        image = new Image(Assets.gameOver);
    }

    @Override
    public void act(float delta) {
        //image.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        image.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
//        image.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
//        image.setSize(width, height);
    }
}
