package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private PlayerComponent playerComponent;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private GameUI gameUI;
    private final Vector3 tmp = new Vector3();
    private final Camera camera;
    private final Vector3 tempVector = new Vector3();

    private Quaternion quat = new Quaternion();
    float rotation = 0;

    public PlayerSystem(Camera camera) {
        this.camera = camera;
    }

    public PlayerSystem(Camera camera, GameUI gameUI, Engine engine) {
        this.camera = camera;
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
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);

//        rotation += deltaX;
//        rotation %= 360;
//        if (rotation < 0) {
//            rotation = 360;
//        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // rotation+=5f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // rotation-=5f;
        }

        Quaternion rot = quat.setFromAxis(0, 1, 0, 270 + (float) Math.toDegrees(camera.direction.x * Math.PI));
        // System.out.println(camera.direction.x + " - " + camera.direction.y + " - " + camera.direction.z);
        System.out.println(Math.toDegrees(camera.direction.x * Math.PI));

        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            characterComponent.walkDirection.add(characterComponent.characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            characterComponent.walkDirection.add(-characterComponent.characterDirection.x, -characterComponent.characterDirection.y, -characterComponent.characterDirection.z);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            characterComponent.characterController.setJumpSpeed(15);
            characterComponent.characterController.jump();  //.body).applyCentralImpulse(new Vector3(0,5,0));
        }
        characterComponent.walkDirection.scl(4f * delta);

        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);
        Matrix4 ghost = new Matrix4();
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);   //TODO export this
        ghost.getTranslation(translation);

        modelComponent.transform.set(translation.x, translation.y, translation.z, rot.x, rot.y, rot.z, rot.w);
        modelComponent.instance.transform = modelComponent.transform;

        camera.position.set(translation.x, translation.y, translation.z);
        camera.update(true);
    }

    private void updateStatus() {
        gameUI.healthWidget.setValue(playerComponent.health);
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
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
        //gameUI.healthWidget.setValue(playerComponent.health);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}