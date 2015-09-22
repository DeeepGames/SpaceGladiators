package com.deeep.spaceglad.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;

/**
 * Created by scanevaro on 22/09/2015.
 */
public class BulletWorld {
    public DebugDrawer debugDrawer = null;
    public boolean renderMeshes = true;
    protected final Array<BulletEntity> entities = new Array<BulletEntity>();
    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btCollisionWorld collisionWorld;
    public final Vector3 gravity;

    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;

    public BulletWorld(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher,
                       final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world, final Vector3 gravity) {
        this.collisionConfiguration = collisionConfiguration;
        this.dispatcher = dispatcher;
        this.broadphase = broadphase;
        this.solver = solver;
        this.collisionWorld = world;
        if (world instanceof btDynamicsWorld) ((btDynamicsWorld) this.collisionWorld).setGravity(gravity);
        this.gravity = gravity;
    }

    public BulletWorld(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher,
                       final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world) {
        this(collisionConfiguration, dispatcher, broadphase, solver, world, new Vector3(0, -10, 0));
    }

    public BulletWorld(final Vector3 gravity) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ((btDynamicsWorld) collisionWorld).setGravity(gravity);
        this.gravity = gravity;
    }

    public BulletWorld() {
        this(new Vector3(0, -10, 0));
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

    public void update() {
        if (collisionWorld instanceof btDynamicsWorld)
            ((btDynamicsWorld) collisionWorld).stepSimulation(Gdx.graphics.getDeltaTime(), maxSubSteps, fixedTimeStep);
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
    }

    public void setDebugMode(final int mode) {
        if (mode == btIDebugDraw.DebugDrawModes.DBG_NoDebug && debugDrawer == null) return;
        if (debugDrawer == null) collisionWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
        debugDrawer.setDebugMode(mode);
    }

    public int getDebugMode() {
        return (debugDrawer == null) ? 0 : debugDrawer.getDebugMode();
    }
}