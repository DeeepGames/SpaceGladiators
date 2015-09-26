package com.deeep.spaceglad.bullet;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.deeep.spaceglad.components.BulletComponent;

/**
 * Created by scanevaro on 22/09/2015.
 */
public class BulletWorld extends EntitySystem implements EntityListener{
    public DebugDrawer debugDrawer = null;
    public boolean renderMeshes = true;
    protected final Array<BulletEntity> entities = new Array<BulletEntity>();
    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btCollisionWorld collisionWorld;
    public final Vector3 gravity;
    private btGhostPairCallback ghostPairCallback;
    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;



    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(BulletComponent.class).get(),this);
    }


    public BulletWorld() {
         collisionConfiguration = new btDefaultCollisionConfiguration();
         dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
         solver = new btSequentialImpulseConstraintSolver();
         collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ghostPairCallback = new btGhostPairCallback();
        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);

        this.gravity = new Vector3(0, -10, 0);
        ((btDynamicsWorld) this.collisionWorld).setGravity(gravity);

    }



    public void add(final BulletEntity entity) {
        entities.add(entity);
        if (entity.body != null) {
            if (entity.body instanceof btRigidBody)
                ((btDiscreteDynamicsWorld) collisionWorld).addRigidBody((btRigidBody) entity.body);
            else
                collisionWorld.addCollisionObject(entity.body);
            // Store the index of the entity in the collision object.
            entity.body.setUserValue(entities.size - 1);
        }
    }
    @Override
    public void update(float deltaTime) {
            ((btDynamicsWorld) collisionWorld).stepSimulation(deltaTime, maxSubSteps, fixedTimeStep);
    }

    public void render (final ModelBatch batch, final Environment lights) {
        for (final BulletEntity e : entities) {
            batch.render(e.modelInstance, lights);
        }
    }

    public void render(ModelBatch batch, Environment lights, Iterable<BulletEntity> entities) {
        if (renderMeshes) {
            for (final BulletEntity e : entities) {
                batch.render(e.modelInstance, lights);
            }
        }
        if (debugDrawer != null && debugDrawer.getDebugMode() > 0) {
            batch.flush();
            debugDrawer.begin(batch.getCamera());
            collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    public void dispose() {
        for (int i = 0; i < entities.size; i++) {
            btCollisionObject body = entities.get(i).body;
            if (body != null) {
                if (body instanceof btRigidBody)
                    ((btDynamicsWorld) collisionWorld).removeRigidBody((btRigidBody) body);
                else
                    collisionWorld.removeCollisionObject(body);
            }
        }

        for (int i = 0; i < entities.size; i++)
            entities.get(i).dispose();
        entities.clear();



        //models.clear();

        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadphase != null) broadphase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();
        ghostPairCallback.dispose();
    }

    public void setDebugMode(final int mode) {
        if (mode == btIDebugDraw.DebugDrawModes.DBG_NoDebug && debugDrawer == null) return;
        if (debugDrawer == null) collisionWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
        debugDrawer.setDebugMode(mode);
    }

    public int getDebugMode() {
        return (debugDrawer == null) ? 0 : debugDrawer.getDebugMode();
    }

    @Override
    public void entityAdded(Entity entity) {
        BulletComponent bulletComponent = entity.getComponent(BulletComponent.class);
        if(bulletComponent.body != null){
            if (bulletComponent.body instanceof btRigidBody)
                ((btDiscreteDynamicsWorld) collisionWorld).addRigidBody((btRigidBody) bulletComponent.body);
            else
                collisionWorld.addCollisionObject(bulletComponent.body);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}