package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;

/**
 * Created by scanevaro on 04/08/2015.
 */
public class PauseWidget extends Actor {
    Image image;
    Window window;
    TextButton textButton;
    Stage stage;

    public PauseWidget(Stage stage) {
        this.stage = stage;
        image = new Image(new Texture(Gdx.files.internal("data/pauseButton0.png")));
        window = new Window("Pause", Assets.skin);
        window.getTitleTable().add(textButton = new TextButton("X", Assets.skin)).height(window.getPadTop());
        setListeners();
    }

    private void setListeners() {
        super.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                if (window.getStage() == null) stage.addActor(window);
                else window.remove();
            }
        });
        super.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    handleUpdates();
                    return true;
                }
                return false;
            }
        });
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                handleUpdates();
            }
        });
    }

    @Override
    public void act(float delta) {
        image.act(delta);
        window.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        image.draw(batch, parentAlpha);
        if (window.getStage() != null) window.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        image.setPosition(x, y);
        window.setPosition(Core.VIRTUAL_WIDTH / 2 - window.getWidth() / 2, Core.VIRTUAL_HEIGHT / 2 - window.getHeight() / 2);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        image.setSize(width, height);
        window.setSize(width * 2, height * 2);
    }

    public void handleUpdates() {
        if (window.getStage() == null) {
            stage.addActor(window);
            Gdx.input.setCursorCatched(false);
        } else {
            window.remove();
            Gdx.input.setCursorCatched(true);
        }
    }
}