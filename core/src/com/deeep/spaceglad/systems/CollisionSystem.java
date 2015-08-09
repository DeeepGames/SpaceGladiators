package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 8-8-2015.
 */
public class CollisionSystem extends EntitySystem implements EntityListener {
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
        public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
            Entity entity_1 = entities.get(userValue0-1);
            Entity entity_2 = entities.get(userValue1 - 1);

            if(entity_1.getComponent(PlayerComponent.class) != null){
                vm.get(entity_1).velocity.y = 0;
            }else if (entity_2.getComponent(PlayerComponent.class) != null){
                vm.get(entity_2).velocity.y = 0;
            }
            System.out.println("================================================");
            System.out.println(userValue0 + ", " + partId0 + ", " + index0);
            System.out.println(userValue1 + ", " + partId1 + ", " + index1);
            return true;
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, CollisionComponent.class, ModelComponent.class).get());
        engine.addEntityListener(Family.all(CollisionComponent.class).get(), this);
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
        collisionObject.setUserValue(entities.size());
        collisionObject.userData = entity;
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
