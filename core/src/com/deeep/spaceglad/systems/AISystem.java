package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.components.AIComponent;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;

/**
 * Created by Andreas on 8/5/2015.
 */
public class AISystem extends EntitySystem implements EntityListener {
    private ImmutableArray<Entity> entities;
    private Entity player;
    private Quaternion quat = new Quaternion();

    ComponentMapper<CharacterComponent> cm = ComponentMapper.getFor(CharacterComponent.class);

    @Override
    public void addedToEngine(Engine e) {
        entities = e.getEntitiesFor(Family.all(AIComponent.class, CharacterComponent.class).get());
        e.addEntityListener(Family.one(PlayerComponent.class).get(), this);
    }

    public void update(float delta) {
        for (Entity e : entities) {
            ModelComponent mod = e.getComponent(ModelComponent.class);
            ModelComponent playerModel = player.getComponent(ModelComponent.class);

            Vector3 playerPosition = new Vector3();
            Vector3 enemyPosition = new Vector3();

            playerPosition = playerModel.transform.getTranslation(playerPosition);
            enemyPosition = mod.transform.getTranslation(enemyPosition);

            float dX = playerPosition.x - enemyPosition.x;
            float dZ = playerPosition.z - enemyPosition.z;

            float theta = (float) (Math.atan2(dX, dZ));

            //Calculate the transforms
            Quaternion rot = quat.setFromAxis(0, 1, 0, (float) Math.toDegrees(theta) + 90);

            cm.get(e).characterDirection.set(-1, 0, 0).rot(mod.transform).nor();
            cm.get(e).walkDirection.set(0, 0, 0);
            cm.get(e).walkDirection.add(cm.get(e).characterDirection);
            cm.get(e).walkDirection.scl(1f * delta);

            cm.get(e).characterController.setWalkDirection(cm.get(e).walkDirection);
            Matrix4 ghost = new Matrix4();
            Vector3 translation = new Vector3();
            cm.get(e).ghostObject.getWorldTransform(ghost);   //TODO export this
            ghost.getTranslation(translation);

            mod.transform.set(translation.x, translation.y, translation.z, rot.x, rot.y, rot.z, rot.w);
            mod.instance.transform = mod.transform;


        }
    }


    @Override
    public void entityAdded(Entity entity) {
        player = entity;
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
