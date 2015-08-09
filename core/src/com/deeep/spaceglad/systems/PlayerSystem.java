package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.VelocityComponent;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerSystem extends EntitySystem implements EntityListener {
    private Entity player;
    private PlayerComponent playerComponent;
    private VelocityComponent playerVelocityComponent;
    private PositionComponent playerPositionComponent;

    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void update(float deltaTime) {
        if (player == null) return;
        //set velocity
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            vm.get(player).velocity.y = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            vm.get(player).velocity.y = -1;
        }

    }

    @Override
    public void entityAdded(Entity entity) {
        //this is the player!
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        playerVelocityComponent = entity.getComponent(VelocityComponent.class);
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
