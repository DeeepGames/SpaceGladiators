package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.*;

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
    private GameWorld gameWorld;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();
    ClosestRayResultCallback rayTestCB;


    public PlayerSystem(GameWorld gameWorld, Camera camera) {
        this.camera = camera;
        this.gameWorld = gameWorld;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
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
        tmp.set(0,0,0);
        camera.rotate(camera.up,deltaX);
        //camera.rotate(new Vector3(0, 1, 0),deltaY);
        //camera.direction.rotate(camera.up, rotation);
        tmp.set(camera.direction).crs(camera.up).nor();
        //tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);



        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            characterComponent.walkDirection.add(camera.direction);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            characterComponent.walkDirection.sub(camera.direction);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            characterComponent.characterController.setJumpSpeed(25);
            characterComponent.characterController.jump();
        }
        if(Gdx.input.isTouched()){
            Ray ray = camera.getPickRay(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

            rayFrom.set(ray.origin);
            rayTo.set(ray.direction).scl(50f).add(rayFrom); // 50 meters max from the origin

            // Because we reuse the ClosestRayResultCallback, we need reset it's values
            rayTestCB.setCollisionObject(null);
            rayTestCB.setClosestHitFraction(1f);
            rayTestCB.setRayFromWorld(rayFrom);
            rayTestCB.setRayToWorld(rayTo);


            gameWorld.world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

            if (rayTestCB.hasHit()) {
                final btCollisionObject obj = rayTestCB.getCollisionObject();
                if (((Entity) obj.userData).getComponent(AIComponent.class) != null) {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).alive = false;
                }
            }
        }
        tmp.set(0,0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(1);
        }
        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(8f * delta);

        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);
        Matrix4 ghost = new Matrix4();
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);   //TODO export this
        ghost.getTranslation(translation);

        modelComponent.instance.transform.set(translation.x, translation.y, translation.z, camera.direction.x, camera.direction.y, camera.direction.z, 0);

        camera.position.set(translation.x, translation.y, translation.z);
        camera.update(true);
        System.out.println(playerComponent.health);
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