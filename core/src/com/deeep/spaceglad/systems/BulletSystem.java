package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.RotationComponent;
import com.deeep.spaceglad.components.StatusComponent;
import com.deeep.spaceglad.components.VelocityComponent;
import com.deeep.spaceglad.managers.BulletComponent;

/**
 * Created by Andreas on 8/12/2015.
 */

/*
 * Required components to qualify as a bullet:
 *  Roation
 *  Position
 *  Velocity
 *  Status
 *  Bullet
 */
public class BulletSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    /// Event called when an entity is added to the engine
    public void addedToEngine(Engine e){
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(RotationComponent.class, PositionComponent.class, VelocityComponent.class, StatusComponent.class, BulletComponent.class).get());
    }

    public void update(float delta){
        for(Entity e: entities){
            StatusComponent sta =  e.getComponent(StatusComponent.class);
            if(!sta.enabled | sta.frozen) continue;
            PositionComponent pos = e.getComponent(PositionComponent.class);
            VelocityComponent vel =  e.getComponent(VelocityComponent.class);
            RotationComponent rot =  e.getComponent(RotationComponent.class);



        }
    }
}
