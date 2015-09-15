package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.CollisionComponent;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.VelocityComponent;
import com.deeep.spaceglad.managers.EntityFactory;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private PlayerComponent playerComponent;
    private CollisionComponent playerCollision;
    private GameUI gameUI;
    private final Camera camera;
    private final Vector3 tempVector = new Vector3();
    private Engine engine;
    private ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> velocityComponentMapper = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<CollisionComponent> collisionComponentComponentMapper = ComponentMapper.getFor(CollisionComponent.class);
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
        if (Gdx.input.justTouched()) fire();
        updateStatus();
        checkGameOver();
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
        camera.position.set(positionComponentMapper.get(player).position);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerCollision.characterTransform.rotate(0, 1, 0, 5f);
            playerCollision.pair.setWorldTransform(playerCollision.characterTransform);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerCollision.characterTransform.rotate(0, 1, 0, -5f);
            playerCollision.pair.setWorldTransform(playerCollision.characterTransform);
        }
        // Fetch which direction the character is facing now
        characterDirection.set(-1,0,0).rot(playerCollision.characterTransform).nor();
        // Set the walking direction accordingly (either forward or backward)
        walkDirection.set(0,0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            walkDirection.add(characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
        walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
        // And update the character controller
        playerCollision.characterController.setWalkDirection(walkDirection);
        // Now we can update the world as normally
        // And fetch the new transformation of the character (this will make the model be rendered correctly)
        playerCollision.pair.getWorldTransform(playerCollision.characterTransform);
        camera.update(true);
    }

    private void updateStatus() {
        gameUI.healthWidget.setValue(playerComponent.health);
    }


    private void fire() {
        engine.addEntity(
                EntityFactory.createBullet(
                        positionComponentMapper.get(player).position.cpy(),
                        camera.direction.cpy().scl(150)
                )
        );
    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        playerCollision = entity.getComponent(CollisionComponent.class);
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}