package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 8-8-2015.
 */

public class CollisionSystem extends EntitySystem implements EntityListener {
    public static DebugDrawer debugDrawer;
    public static btDiscreteDynamicsWorld collisionWorld;
    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;
    ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
    ComponentMapper<ModelComponent> mm = ComponentMapper.getFor(ModelComponent.class);
    ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    btSequentialImpulseConstraintSolver solver;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    MyContactListener myContactListener;
    private Engine engine;
    private ImmutableArray<Entity> entities;

    public CollisionSystem() {
        super(1);
        Bullet.init();
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
        collisionWorld.setGravity(new Vector3(0, -10, 0));
        myContactListener = new MyContactListener();
        collisionWorld.setDebugDrawer(debugDrawer);
        myContactListener.enable();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, CollisionComponent.class, ModelComponent.class).get());
        engine.addEntityListener(Family.one(CollisionComponent.class).get(), this);
        this.engine = engine;
    }

    @Override
    public void removedFromEngine(Engine engine) {
        dispatcher.dispose();
        collisionConfig.dispose();
        solver.dispose();
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            // cm.get(entities.get(i)).rigidBody.setWorldTransform(mm.get(entities.get(i)).instance.transform);
        }
        collisionWorld.stepSimulation(deltaTime, maxSubSteps, fixedTimeStep);
    }

    @Override
    public void entityAdded(Entity entity) {
        if (cm.get(entity).body instanceof btRigidBody) {
            collisionWorld.addRigidBody((btRigidBody) cm.get(entity).body);
        }else
            collisionWorld.addCollisionObject(cm.get(entity).body);

        //cm.get(entity).rigidBody.setCollisionFlags(cm.get(entity).rigidBody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        //collisionObject.setUserValue(entities.size());
        cm.get(entity).body.userData = entity;
        if (cm.get(entity).character) {
            collisionWorld.addCollisionObject(cm.get(entity).pair,
                    (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                    (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
            ((collisionWorld)).addAction(cm.get(entity).characterController);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
    }

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded(btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1, int index1) {
            if (colObj0.getUserValue() == 1 || colObj1.getUserValue() == 1) {
                //either of both is the player, lets find out which!
                btCollisionObject player = (colObj0.getUserValue() == 1) ? colObj0 : colObj1;
                btCollisionObject other = (player == colObj1) ? colObj0 : colObj1;
                switch (other.getUserValue()) {/*
                    case 2:
                        pm.get((Entity) player.userData).position.y = pm.get((Entity) player.userData).prevPosition.y;
                        vm.get((Entity) player.userData).velocity.y = Math.max(vm.get((Entity) player.userData).velocity.y, 0);
                        break;
                    case 3:
                        pm.get((Entity) player.userData).position.x = pm.get((Entity) player.userData).prevPosition.x;
                        pm.get((Entity) player.userData).position.z = pm.get((Entity) player.userData).prevPosition.z;
                        break;*/
                    case 4:
                        ((Entity) player.userData).getComponent(PlayerComponent.class).hit();
                        break;
                }
            } else if (colObj0.getUserValue() == 5 || colObj1.getUserValue() == 5) {
                //either of both is the player, lets find out which!
                btCollisionObject bullet = (colObj0.getUserValue() == 5) ? colObj0 : colObj1;
                btCollisionObject other = (bullet == colObj1) ? colObj0 : colObj1;
                collisionWorld.removeCollisionObject(bullet);
                switch (other.getUserValue()) {
                    case 4:
                        collisionWorld.removeCollisionObject(other);
                        engine.removeEntity((Entity) other.userData);
                        engine.removeEntity((Entity) bullet.userData);
                        PlayerComponent.score += 100;
                        break;
                }
            }
            return true;
        }
    }
}