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
                btCollisionObject enemy = (colObj0.getUserValue() == 1)? colObj0 : colObj1;
                btCollisionObject other  = (enemy == colObj1)? colObj0 : colObj1;
                System.out.println("collide with: " + other.getUserValue());;
                switch (other.getUserValue()){
                    case 4:
                        collisionWorld.removeCollisionObject(colObj0);
                        collisionWorld.removeCollisionObject(colObj1);
                        engine.removeEntity((Entity) other.userData);
                        engine.removeEntity((Entity) enemy.userData);
                        break;
                }
            }

            /*
            System.out.println(colObj0.getUserValue() + " " + colObj1.getUserValue());
            boolean isPlayer = entity_1.getComponent(PlayerComponent.class) != null || entity_2.getComponent(PlayerComponent.class) != null;
            boolean isBullet = entity_1.getComponent(BulletComponent.class) != null || entity_2.getComponent(BulletComponent.class) != null;
            boolean isEnemy = entity_1.getComponent(AIComponent.class) != null || entity_2.getComponent(AIComponent.class) != null;
            if(isPlayer && isEnemy){
                //TODO Add player damage and enemy knockback here
                //Logger.log(2, 0, "User was struck!");
            }
            if(isEnemy && isBullet){
                float damage = 0;
                //TODO Elmar can probably optimize this with his smart brain
                if(entity_1.getComponent(BulletComponent.class) != null)
                    damage = entity_1.getComponent(BulletComponent.class).damage;
                else
                    damage = entity_2.getComponent(BulletComponent.class).damage;
                Logger.log(2, 0, "Enemy was struck for " + damage);
            }*/

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
