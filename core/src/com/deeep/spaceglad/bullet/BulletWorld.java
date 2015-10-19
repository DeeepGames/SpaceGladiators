package com.deeep.spaceglad.bullet;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.components.*;

/**
 * Created by scanevaro on 22/09/2015.
 */
public class BulletWorld extends EntitySystem implements EntityListener {
    public final btCollisionConfiguration collisionConfiguration;
    public final MyCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btDiscreteDynamicsWorld collisionWorld;
    private btGhostPairCallback ghostPairCallback;
    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;

    public static class MyCollisionDispatcher extends CustomCollisionDispatcher {
        public MyCollisionDispatcher(btCollisionConfiguration collisionConfiguration) {
            super(collisionConfiguration);
        }

        @Override
        public boolean needsCollision(btCollisionObject body0, btCollisionObject body1) {
            if (body0.userData instanceof Entity || body1.userData instanceof Entity) {
                if (((Entity) body0.userData).getComponent(ProjectileComponent.class) != null) {
                    if (((Entity) body1.userData).getComponent(AIComponent.class) != null) {
                        ((Entity) body1.userData).getComponent(StatusComponent.class).alive = false;
                        ((Entity) body0.userData).getComponent(StatusComponent.class).alive = false;
                        return false;
                    } else if(((Entity) body1.userData).getComponent(CharacterComponent.class)!= null){
                        return false;
                    }
                } else if (((Entity) body1.userData).getComponent(ProjectileComponent.class) != null) {
                    if (((Entity) body0.userData).getComponent(AIComponent.class) != null) {
                        ((Entity) body0.userData).getComponent(StatusComponent.class).alive = false;
                        ((Entity) body1.userData).getComponent(StatusComponent.class).alive = false;
                        return false;
                    }else if(((Entity) body0.userData).getComponent(CharacterComponent.class)!= null){
                        return false;
                    }
                }
            }
            return super.needsCollision(body0, body1);
        }

        @Override
        public boolean needsResponse(btCollisionObject body0, btCollisionObject body1) {
            return super.needsCollision(body0, body1);
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(BulletComponent.class).get(), this);
    }


    public BulletWorld() {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new MyCollisionDispatcher(collisionConfiguration);
        broadphase = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ghostPairCallback = new btGhostPairCallback();
        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        this.collisionWorld.setGravity(new Vector3(0, -10, 0));

    }

    @Override
    public void update(float deltaTime) {
        collisionWorld.stepSimulation(deltaTime, maxSubSteps, fixedTimeStep);
    }


    public void dispose() {
        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadphase != null) broadphase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();
        ghostPairCallback.dispose();
    }


    @Override
    public void entityAdded(Entity entity) {
        BulletComponent bulletComponent = entity.getComponent(BulletComponent.class);
        if (bulletComponent.body != null) {
            if (bulletComponent.body instanceof btRigidBody)
                collisionWorld.addRigidBody((btRigidBody) bulletComponent.body);
            else
                collisionWorld.addCollisionObject(bulletComponent.body);
        }
    }

    public void removeBody(Entity entity) {
        BulletComponent comp = entity.getComponent(BulletComponent.class);
        if (comp != null)
            collisionWorld.removeCollisionObject(comp.body);
        CharacterComponent character = entity.getComponent(CharacterComponent.class);
        if(character != null){
            collisionWorld.removeAction(character.characterController);
            collisionWorld.removeCollisionObject(character.ghostObject);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}