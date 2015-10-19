package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.deeep.spaceglad.bullet.BulletWorld;
import com.deeep.spaceglad.bullet.MotionState;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 7-8-2015.
 */
public class EntityFactory {
    private static Model playerModel;
    private static Texture playerTexture;
    private static ModelBuilder modelBuilder;
    private static Model boxModel;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        Material material = new Material(TextureAttribute.createDiffuse(playerTexture), ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    }


    public static Entity createStaticEntity(Model model, float x, float y, float z) {

        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.instance = new ModelInstance(model, new Matrix4().setToTranslation(x, y, z));
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);

        return entity;
    }

    public static Entity createDynamicEntity(Model model, float mass, float x, float y, float z) {
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Vector3 localInertia;
        col.calculateLocalInertia(mass, tmpV);
        localInertia = tmpV;

        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.instance = new ModelInstance(model, new Matrix4().setToTranslation(x, y, z));
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        // For now just pass null as the motionstate, we'll add that to the body in the entity itself
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, col, localInertia);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);

        entity.add(bulletComponent);

        return entity;
    }

    private static Entity createCharacter(BulletWorld bulletWorld, float x, float y, float z) {
        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(playerModel);
        modelComponent.instance = new ModelInstance(playerModel, new Matrix4().setToTranslation(x, y, z));
        entity.add(modelComponent);

        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);

        bulletWorld.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
        ((btDiscreteDynamicsWorld) (bulletWorld.collisionWorld)).addAction(entity.getComponent(CharacterComponent.class).characterController);
        return entity;
    }

    public static Entity createPlayer(BulletWorld bulletWorld, float x, float y, float z) {
        Entity entity = createCharacter(bulletWorld, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    public static Entity createEnemy(BulletWorld bulletWorld, float x, float y, float z) {
        Entity entity = createCharacter(bulletWorld, x, y, z);
        entity.add(new AIComponent(AIComponent.STATE.HUNTING));
        entity.add(new StatusComponent());
        //TODO andreas
        return entity;
    }

    public static Entity createEmpty(Model model, float x, float y, float z) {
        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.instance = new ModelInstance(model, new Matrix4().setToTranslation(x, y, z));
        entity.add(modelComponent);

        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        entity.add(characterComponent);

        return entity;
    }

    public static Entity createBullet(Vector3 start, Vector3 direction) {
        Entity entity = createDynamicEntity(boxModel, 1f, start.x, start.y, start.z);
        Ray ray = new Ray(start,direction);
        entity.getComponent(ModelComponent.class).setColor(new Color(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f));
        ((btRigidBody) entity.getComponent(BulletComponent.class).body).applyCentralImpulse(ray.direction.scl(30));

        entity.add(new ProjectileComponent());
        entity.add(new StatusComponent());
        return entity;
    }

    public static Entity createBullet(Ray ray, float x, float y, float z) {
        Entity entity = createDynamicEntity(boxModel, 1, x, y, z);
        entity.getComponent(ModelComponent.class).setColor(new Color(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f));
        ((btRigidBody) entity.getComponent(BulletComponent.class).body).applyCentralImpulse(ray.direction.scl(30));
        entity.add(new ProjectileComponent());
        entity.add(new StatusComponent());
        return entity;
    }

    public static void dispose() {
        playerModel.dispose();
        playerTexture.dispose();
        boxModel.dispose();
    }
}