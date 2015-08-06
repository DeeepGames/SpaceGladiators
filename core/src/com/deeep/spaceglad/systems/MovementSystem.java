package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.RotationComponent;
import com.deeep.spaceglad.components.VelocityComponent;

/**
 * Created by Andreas on 8/4/2015.
 */
public class MovementSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    public MovementSystem() {}

    /// Event called when an entity is added to the engine
    public void addedToEngine(Engine e){
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(RotationComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    public void update(float delta){
        for(Entity e: entities){
            PositionComponent pos = e.getComponent(PositionComponent.class);
            VelocityComponent vel =  e.getComponent(VelocityComponent.class);
            RotationComponent rot =  e.getComponent(RotationComponent.class);

            //if(vel.velocity == 0) return;
            //pos.x += vel.velocity * delta;
        }
    }
}
