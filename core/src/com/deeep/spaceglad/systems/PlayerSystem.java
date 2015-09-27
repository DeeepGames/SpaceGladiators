package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private CharacterComponent playerComponent;
    private CollisionComponent playerCollision;
    private GameUI gameUI;
    private final Camera camera;
    private final Vector3 tempVector = new Vector3();
    private Engine engine;
    //private ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    //private ComponentMapper<VelocityComponent> velocityComponentMapper = ComponentMapper.getFor(VelocityComponent.class);
    //private ComponentMapper<CollisionComponent> collisionComponentComponentMapper = ComponentMapper.getFor(CollisionComponent.class);
    Vector3 characterDirection = new Vector3();
    Vector3 walkDirection = new Vector3();



    public PlayerSystem(Camera camera, GameUI gameUI, Engine engine) {
        this.camera = camera;
        this.engine = engine;
        this.gameUI = gameUI;
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void update(float delta) {
        if (player == null) return;
        updateMovement(delta);
        //if (Gdx.input.justTouched()) fire();
        //updateStatus();
        //checkGameOver();
    }

    private void updateMovement(float delta) {
        float deltaX = -Gdx.input.getDeltaX() * 0.5f;
        float deltaY = -Gdx.input.getDeltaY() * 0.5f;
        camera.direction.rotate(camera.up, deltaX);
        tempVector.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tempVector, deltaY);
/*
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tempVector.set(camera.direction).nor().scl(13);
            velocityComponentMapper.get(player).velocity.x = tempVector.x;
            velocityComponentMapper.get(player).velocity.z = tempVector.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            tempVector.set(camera.direction).nor().scl(-13);
            velocityComponentMapper.get(player).velocity.x = tempVector.x;
            velocityComponentMapper.get(player).velocity.z = tempVector.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tempVector.set(camera.direction).crs(camera.up).nor().scl(-13);
            velocityComponentMapper.get(player).velocity.x = tempVector.x;
            velocityComponentMapper.get(player).velocity.z = tempVector.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tempVector.set(camera.direction).crs(camera.up).nor().scl(13);
            velocityComponentMapper.get(player).velocity.x = tempVector.x;
            velocityComponentMapper.get(player).velocity.z = tempVector.z;
        }
        collisionComponentComponentMapper.get(player).characterController.setWalkDirection(tempVector);*/

        camera.update(true);
    }

    private void updateStatus() {
        //gameUI.healthWidget.setValue(playerComponent.health);
    }


    private void fire() {
        /*
        engine.addEntity(
                EntityFactory.createBullet(
                        positionComponentMapper.get(player).position.cpy(),
                        camera.direction.cpy().scl(150)
                )
        );*/
    }

    private void checkGameOver() {
        /*
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }*/
    }

    @Override
    public void entityAdded(Entity entity) {
        //player = entity;
        //playerComponent = entity.getComponent(CharacterComponent.class);
        //playerCollision = entity.getComponent(CollisionComponent.class);
        //gameUI.healthWidget.setValue(playerComponent.health);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}