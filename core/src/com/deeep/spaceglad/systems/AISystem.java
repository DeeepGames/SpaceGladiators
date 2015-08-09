package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.deeep.spaceglad.Logger;
import com.deeep.spaceglad.components.*;

/**
 * Created by Andreas on 8/5/2015.
 */
public class AISystem extends EntitySystem{
    private ImmutableArray<Entity> entities;

    private PerspectiveCamera cam;

    public AISystem(PerspectiveCamera cam) {
        this.cam = cam;
    }

    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void addedToEngine(Engine e){
        entities = e.getEntitiesFor(Family.all(ModelComponent.class, RotationComponent.class, PositionComponent.class, VelocityComponent.class, AIComponent.class).get());
    }

    public void update(float delta){
        for(Entity e: entities){
            RotationComponent rot =  e.getComponent(RotationComponent.class);
            VelocityComponent vel =  e.getComponent(VelocityComponent.class);
            ModelComponent mod =  e.getComponent(ModelComponent.class);
            AIComponent aic =  e.getComponent(AIComponent.class);


            float dX = cam.position.x - pm.get(e).position.x;
            float dZ = cam.position.z - pm.get(e).position.z;

            rot.yaw = (float) Math.toDegrees(Math.atan2(dX, dZ));

            mod.instance.transform.setFromEulerAngles(rot.yaw, rot.pitch, rot.roll);

            if(aic.state != AIComponent.STATE.IDLE){
                float mX = (float) Math.sin(rot.yaw) * delta * vel.velocity.x;
                float mZ = (float) Math.cos(rot.yaw) * delta * vel.velocity.z;

                pm.get(e).position.x += mX;
                pm.get(e).position.z += mZ;
            }

        }
    }


}
