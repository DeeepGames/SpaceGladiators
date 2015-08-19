package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.screens.GameScreen;

/**
 * Created by scanevaro on 04/08/2015.
 */
public class PauseWidget extends Actor {
    private Core game;
    private Image image;
    private Window window;
    private TextButton dialogTitle, restartButton, quitButton;
    private Stage stage;

    public PauseWidget(Core game, Stage stage) {
        this.game = game;
        this.stage = stage;
        setWidgets();
        configureWidgets();
        setListeners();
    }

    private void setWidgets() {
        image = new Image(new Texture(Gdx.files.internal("data/pauseButton0.png")));
        window = new Window("Pause", Assets.skin);
    }

    private void configureWidgets() {
        stage.addActor(image);
        dialogTitle = new TextButton("X", Assets.skin);
        window.getTitleTable().add(dialogTitle).height(window.getPadTop());
        restartButton = new TextButton("Restart", Assets.skin);
        window.add(restartButton);
        quitButton = new TextButton("Quit", Assets.skin);
        window.add(quitButton);
    }

    private void setListeners() {
        super.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                handleUpdates();
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
        dialogTitle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                handleUpdates();
            }
        });
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                Gdx.app.exit();
            }
        });
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

    private void handleUpdates() {
        if (window.getStage() == null) {
            stage.addActor(window);
            Gdx.input.setCursorCatched(false);
            Settings.Pause = true;
        } else {
            window.remove();
            Gdx.input.setCursorCatched(true);
            Settings.Pause = false;
        }
    }
}