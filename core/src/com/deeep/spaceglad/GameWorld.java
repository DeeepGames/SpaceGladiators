package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.bullet.BulletWorld;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameWorld implements GestureDetector.GestureListener {
    private Engine engine;
    public BulletWorld bulletWorld;

    public GameWorld(GameUI gameUI) {
        bulletWorld = new BulletWorld();
        addSystems(gameUI);
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(new RenderSystem(bulletWorld.modelBatch, bulletWorld.environment));
    }

    public void render(float delta) {
        bulletWorld.renderShadows();
        bulletWorld.renderModelsWithoutShadows();
        engine.update(delta);
    }

    public void update(float delta) {
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        bulletWorld.shoot(Core.VIRTUAL_WIDTH / 2 /*x*/, Core.VIRTUAL_HEIGHT / 2 /*y*/, 30f);
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    public void resize(int width, int height) {
        bulletWorld.resize(width, height);
    }

    public void dispose() {
        bulletWorld.dispose();
        bulletWorld = null;
    }

    //    private void checkPause() {
//        if (Settings.Paused) {
//            movementSystem.setProcessing(false);
//            playerSystem.setProcessing(false);
//            collisionSystem.setProcessing(false);
//        } else {
//            movementSystem.setProcessing(true);
//            playerSystem.setProcessing(true);
//            collisionSystem.setProcessing(true);
//        }
//    }
}