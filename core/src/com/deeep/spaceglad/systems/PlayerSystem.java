package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.VelocityComponent;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private PlayerComponent playerComponent;
    private GameUI gameUI;
    private final Camera camera;
    private final Vector3 tempVector = new Vector3();
    private float jumpPower = 2f;
    private boolean jumping = false;
    private ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> velocityComponentMapper = ComponentMapper.getFor(VelocityComponent.class);
    private float energyConsumed;

    public PlayerSystem(Camera camera, GameUI gameUI) {
        this.camera = camera;
        this.gameUI = gameUI;
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void update(float delta) {
        if (Core.Pause) return;
        if (player == null) return;
        updateMovement(delta);
        updateStatus(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) fire();
    }

    private void fire() {

    }

    private void updateMovement(float delta) {
        float deltaX = -Gdx.input.getDeltaX() * 0.5f;
        float deltaY = -Gdx.input.getDeltaY() * 0.5f;
        camera.direction.rotate(camera.up, deltaX);
        tempVector.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tempVector, deltaY);
        //set velocity
        if (jumping) {
            if (jumpPower > 0) {
                //jumpPower -= deltaTime * 4;
                //velocityComponentMapper.get(player).velocity.y += deltaTime * 4;
                velocityComponentMapper.get(player).velocity.y += jumpPower;
                jumpPower -= jumpPower / 4;
                if (jumpPower <= 0.25f) {
                    jumpPower = 0;
                }
            } else {
                jumping = false;
            }
        }
        if (velocityComponentMapper.get(player).velocity.y > -5) {
            velocityComponentMapper.get(player).velocity.y -= delta * 1;
        }
        velocityComponentMapper.get(player).velocity.z = 0;
        velocityComponentMapper.get(player).velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tempVector.set(camera.direction).nor().scl(13);
            velocityComponentMapper.get(player).velocity.x = tempVector.x;
            velocityComponentMapper.get(player).velocity.z = tempVector.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocityComponentMapper.get(player).velocity.z = 3;
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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (jumpPower > 0) {
                jumping = true;
            } else {
                if (jumping == false) {
                    jumpPower = 2.5f;
                    jumping = true;
                }
            }
        }
        camera.position.set(positionComponentMapper.get(player).position);
        camera.update(true);
    }

    private void updateStatus(float delta) {
        energyConsumed += delta;
        if (energyConsumed >= 1) {
            playerComponent.energy -= energyConsumed;
            energyConsumed = 0;
        }

        gameUI.energyWidget.setValue(playerComponent.energy);
        gameUI.oxygenWidget.setValue(playerComponent.oxygen);
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    @Override
    public void entityAdded(Entity entity) {
        //this is the player!
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        gameUI.energyWidget.setValue(playerComponent.energy);
        gameUI.oxygenWidget.setValue(playerComponent.oxygen);
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }

    public void hit(float damage) {
        /**
         * gameUI.healthWidget.setValue(playerComponent.health -= damage);
         */
    }

    public void grabEnergy(float energy) {
        /**
         * gameUI.energyWidget.setValue(playerComponent.energy += energy);
         */
    }
}
