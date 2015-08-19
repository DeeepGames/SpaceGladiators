package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.Logger;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 8-8-2015.
 */
public class CollisionSystem extends EntitySystem implements EntityListener {
    private Engine engine;
    public static DebugDrawer debugDrawer;
    private ImmutableArray<Entity> entities;
    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
    ComponentMapper<ModelComponent> mm = ComponentMapper.getFor(ModelComponent.class);
    ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    public static btCollisionWorld collisionWorld;

    MyContactListener myContactListener;

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded(btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1, int index1) {
            if(colObj0.getUserValue() == 1 || colObj1.getUserValue() == 1){
                //either of both is the player, lets find out which!
                btCollisionObject player = (colObj0.getUserValue() == 1)? colObj0 : colObj1;
                btCollisionObject other  = (player == colObj1)? colObj0 : colObj1;
                switch (other.getUserValue()){
                    case 2:
                        pm.get((Entity)player.userData).position.y = pm.get((Entity)player.userData).prevPosition.y;
                        vm.get((Entity)player.userData).velocity.y = Math.max(vm.get((Entity)player.userData).velocity.y,0);
                        break;
                    case 3:
                        pm.get((Entity)player.userData).position.x = pm.get((Entity)player.userData).prevPosition.x;
                        pm.get((Entity)player.userData).position.z = pm.get((Entity)player.userData).prevPosition.z;
                        break;
                    case 4:

                        break;
                }
            }else if(colObj0.getUserValue() == 5 || colObj1.getUserValue() == 5){
                //either of both is the player, lets find out which!
                btCollisionObject bullet = (colObj0.getUserValue() == 5)? colObj0 : colObj1;
                btCollisionObject other  = (bullet == colObj1)? colObj0 : colObj1;
                collisionWorld.removeCollisionObject(bullet);
                switch (other.getUserValue()){
                    case 4:
                        collisionWorld.removeCollisionObject(other);
                        engine.removeEntity((Entity) other.userData);
                        engine.removeEntity((Entity) bullet.userData);
                        break;
                }
            }
            return true;
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, CollisionComponent.class, ModelComponent.class).get());
        engine.addEntityListener(Family.one(CollisionComponent.class).get(), this);
        this.engine = engine;
    }

    public CollisionSystem() {
        super(1);
        Bullet.init();
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        myContactListener = new MyContactListener();
        collisionWorld.setDebugDrawer(debugDrawer);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        dispatcher.dispose();
        collisionConfig.dispose();
    }

    @Override
    public void update(float deltaTime) {
        for(Entity entity : entities){
            cm.get(entity).collisionObject.setWorldTransform(mm.get(entity).instance.transform);
        }
        collisionWorld.performDiscreteCollisionDetection();
    }

    @Override
    public void entityAdded(Entity entity) {
        btCollisionObject collisionObject = cm.get(entity).collisionObject;
        collisionWorld.addCollisionObject(collisionObject);
        collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        //collisionObject.setUserValue(entities.size());
        collisionObject.userData = entity;
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
