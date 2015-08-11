package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.UI.GameUI;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameScreen implements Screen {
    Core game;
    GameUI gameUI;
    GameWorld gameWorld;

    public GameScreen(Core game) {
        this.game = game;
        gameUI = new GameUI(game);
        gameWorld = new GameWorld(gameUI);
        Core.Pause = false;
        Gdx.input.setInputProcessor(new InputMultiplexer(gameUI.stage/*, gameWorld.firstPersonCameraController*/));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        /** Updates */
        if (!Core.Pause) {
            gameWorld.update(delta);
            gameUI.update(delta);
        }
        /** Draw */
        gameWorld.render(delta);
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);
        gameWorld.resize(width, height);
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        gameUI.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}