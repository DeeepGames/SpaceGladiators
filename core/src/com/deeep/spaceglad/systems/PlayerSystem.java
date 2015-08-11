package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
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
    private final Vector3 tmp = new Vector3();
    private float jumpPower = 2f;
    private boolean jumping = false;

    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public PlayerSystem(Camera camera, GameUI gameUI) {
        this.camera = camera;
        this.gameUI = gameUI;
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }


    @Override
    public void update(float deltaTime) {
        if (player == null) return;

        float deltaX = -Gdx.input.getDeltaX() * 0.5f;
        float deltaY = -Gdx.input.getDeltaY() * 0.5f;

        camera.direction.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);

        //set velocity
        if (jumping) {
            System.out.println("jumping");
            if (jumpPower > 0) {
                //jumpPower -= deltaTime * 4;
                //vm.get(player).velocity.y += deltaTime * 4;
                vm.get(player).velocity.y += jumpPower;
                jumpPower-=jumpPower/4;
                if(jumpPower<=0.25f){
                    jumpPower = 0;
                }
            } else {
                jumping = false;
            }
        }
        if (vm.get(player).velocity.y > -5) {
            vm.get(player).velocity.y -= deltaTime * 1;
        }

        vm.get(player).velocity.z = 0;
        vm.get(player).velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tmp.set(camera.direction).nor().scl(13);
            vm.get(player).velocity.x = tmp.x;
            vm.get(player).velocity.z = tmp.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vm.get(player).velocity.z = 3;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-13);
            vm.get(player).velocity.x = tmp.x;
            vm.get(player).velocity.z = tmp.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(13);
            vm.get(player).velocity.x = tmp.x;
            vm.get(player).velocity.z = tmp.z;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (jumpPower > 0) {
                jumping = true;
            }else{
                if(jumping == false){
                    jumpPower = 2.5f;
                    jumping = true;
                }
            }
        }
        System.out.println(jumping + " - " + jumpPower + " - " + vm.get(player).velocity.y);


        camera.position.set(pm.get(player).position);
        camera.update(true);
        gameUI.energyWidget.setValue(playerComponent.energy -= deltaTime * 5);
    }

    @Override
    public void entityAdded(Entity entity) {
        //this is the player!
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        gameUI.energyWidget.setValue(playerComponent.energy);
        gameUI.oxigenWidget.setValue(playerComponent.oxygen);
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
