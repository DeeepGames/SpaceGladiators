package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
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

    @Override
    public void addedToEngine(Engine e){
        entities = e.getEntitiesFor(Family.all(ModelComponent.class, RotationComponent.class, PositionComponent.class, VelocityComponent.class, AIComponent.class).get());
    }

    public void update(float delta){
        for(Entity e: entities){
            PositionComponent pos = e.getComponent(PositionComponent.class);
            RotationComponent rot =  e.getComponent(RotationComponent.class);
            VelocityComponent vel =  e.getComponent(VelocityComponent.class);
            ModelComponent mod =  e.getComponent(ModelComponent.class);
            AIComponent aic =  e.getComponent(AIComponent.class);


            float dX = cam.position.x - pos.x;
            float dZ = cam.position.z - pos.z;

            rot.yaw = (float) Math.toDegrees(Math.atan2(dX, dZ));

            Logger.log(2, 0, "angle " + rot.yaw);
            mod.instance.transform.setFromEulerAngles(rot.yaw, rot.pitch, rot.roll);

            if(aic.state != AIComponent.STATE.IDLE){
                float mX = (float) Math.sin(rot.yaw) * delta * vel.velocity;
                float mZ = (float) Math.cos(rot.yaw) * delta * vel.velocity;

                pos.x += mX;
                pos.z += mZ;
            }

        }
    }


}
