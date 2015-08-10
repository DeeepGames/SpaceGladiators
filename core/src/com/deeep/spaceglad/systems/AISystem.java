package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.deeep.spaceglad.components.*;

/**
 * Created by Andreas on 8/5/2015.
 */
public class AISystem extends EntitySystem  implements EntityListener{
    private ImmutableArray<Entity> entities;
    private Entity player;

    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void addedToEngine(Engine e){
        entities = e.getEntitiesFor(Family.all(ModelComponent.class, RotationComponent.class, PositionComponent.class, VelocityComponent.class, AIComponent.class).get());
        e.addEntityListener(Family.one(PlayerComponent.class).get(),this);
    }

    public void update(float delta){
        for(Entity e: entities){
            RotationComponent rot =  e.getComponent(RotationComponent.class);
            VelocityComponent vel =  e.getComponent(VelocityComponent.class);
            ModelComponent mod =  e.getComponent(ModelComponent.class);
            AIComponent aic =  e.getComponent(AIComponent.class);

            if(player == null) return;
            PositionComponent playerPositionComponent = player.getComponent(PositionComponent.class);


            float dX = playerPositionComponent.position.x - pm.get(e).position.x;
            float dZ = playerPositionComponent.position.z - pm.get(e).position.z;

            rot.yaw = (float) (Math.atan2(dX, dZ));

            mod.instance.transform.setFromEulerAngles((float) Math.toDegrees(rot.yaw), rot.pitch, rot.roll);

            if(aic.state != AIComponent.STATE.IDLE){
                vel.velocity.x += (float) Math.sin(rot.yaw);
                vel.velocity.z += (float) Math.cos(rot.yaw);
            }

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
