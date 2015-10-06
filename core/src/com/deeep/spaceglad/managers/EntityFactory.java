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
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.deeep.spaceglad.bullet.BulletEntity;
import com.deeep.spaceglad.bullet.MotionState;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 7-8-2015.
 */
public class EntityFactory {
    private static Model playerModel;
    private static Texture playerTexture;
    private static ModelBuilder modelBuilder;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        Material material = new Material(TextureAttribute.createDiffuse(playerTexture), ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material,  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    public static Entity createWall(float x, float y, float z, float sizeX, float sizeY, float sizeZ, Vector3 orientation) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(orientation));
        entity.add(new RenderableComponent());
        entity.add(new StatusComponent());
        entity.add(new ModelComponent(
                new ModelBuilder().createBox(sizeX, sizeY, sizeZ,
                        new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(sizeX / 2, sizeY / 2, sizeZ / 2)),0);
        collisionComponent.body.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.body.setUserValue(3);
        entity.add(collisionComponent);
        return entity;
    }

    /*
    public static Entity createEnemy(float x, float y, float z) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(0, 0, 0));
        entity.add(new StatusComponent());
        entity.add(new AIComponent(AIComponent.STATE.HUNTING));
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(
                new ModelBuilder().createBox(4f, 4f, 4f,
                        new Material(ColorAttribute.createDiffuse(Color.RED)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(2f, 2f, 2f)),1);
        collisionComponent.body.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.body.setUserValue(4);
        entity.add(collisionComponent);
        return entity;
    }*/

    public static Entity createBullet(Vector3 position, Vector3 velocity) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(position));
        entity.add(new VelocityComponent(velocity));
        entity.add(new RotationComponent(velocity));
        entity.add(new StatusComponent());
        entity.add(new RenderableComponent());
        entity.add(new BulletComponent());
        entity.add(new ModelComponent(
                new ModelBuilder().createSphere(0.5f, 0.5f, 0.5f, 20, 20,
                        new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btSphereShape(0.25f),1);
        collisionComponent.body.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.body.setUserValue(5);
        collisionComponent.body.setCollisionFlags(collisionComponent.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        entity.add(collisionComponent);
        return entity;
    }

    /*
    public static Entity createPlayer(float x, float y, float z) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(0, 0, 0));
        entity.add(new StatusComponent());
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(
                new ModelBuilder().createCapsule(1, 4, 16,
                        new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        entity.add(new PlayerComponent());
        CollisionComponent collisionComponent = new CollisionComponent(new btCapsuleShape(1, 2),1, true);
        //collisionComponent.rigidBody.userData = entity;
        //collisionComponent.rigidBody.userData = entity;
        //collisionComponent.rigidBody.setUserValue(1);
//        collisionComponent.rigidBody.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        entity.add(collisionComponent);
        return entity;
    }
    */

    public static Entity createGround() {
        Entity ground = new Entity();
        ground.add(new PositionComponent(0, -2.2f, 0))
                .add(new VelocityComponent())
                .add(new RotationComponent(0, 0, 0))
                .add(new RenderableComponent())
                .add(new StatusComponent())
                .add(new ModelComponent(
                        new ModelBuilder().createBox(100f, 0.5f, 100f,
                                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(49.99f, 0.25f, 49.99f)),0 );
        collisionComponent.body.setWorldTransform(ground.getComponent(ModelComponent.class).instance.transform);
       // collisionComponent.rigidBody.userData = ground;
        collisionComponent.body.setUserValue(2);
        collisionComponent.body.setCollisionFlags(collisionComponent.body.getCollisionFlags()
                        | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
                //dynamicsWorld.addRigidBody(object.body);
                // collisionComponent.rigidBody.setContactCallbackFlag(3);
                //collisionComponent.rigidBody.setContactCallbackFilter(0);
                collisionComponent.body.setActivationState(Collision.DISABLE_DEACTIVATION);

        ground.add(collisionComponent);
        return ground;
    }

    public static Entity createStaticEntity(Model model,float x, float y, float z){

        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.transform = new Matrix4().setToTranslation(x,y,z);
        modelComponent.instance = new ModelInstance(model,modelComponent.transform.cpy());
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody)bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);

        return entity;
    }

    public static Entity createBulletPlayerComponent(){
        Entity entity = new Entity();
        CharacterComponent characterComponent = new CharacterComponent();
        entity.add(characterComponent);
        return entity;
    }

    public static BulletEntity createStatic(Model model,float x, float y, float z){

        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));
        btRigidBody.btRigidBodyConstructionInfo bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero);
        return new BulletEntity(model, bodyInfo, x, y, z);
    }

    public static Entity createDynamicEntity(Model model, float mass, float x, float y, float z){
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Vector3 localInertia;
        col.calculateLocalInertia(mass, tmpV);
        localInertia = tmpV;

        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.transform = new Matrix4().setToTranslation(x,y,z);
        modelComponent.instance = new ModelInstance(model,modelComponent.transform.cpy());
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        // For now just pass null as the motionstate, we'll add that to the body in the entity itself
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, col, localInertia);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody)bulletComponent.body).setMotionState(bulletComponent.motionState);

        entity.add(bulletComponent);

        return entity;
    }

    public static BulletEntity createDynamic(Model model, float mass, float x, float y, float z){
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Vector3 localInertia;
        col.calculateLocalInertia(mass, tmpV);
        localInertia = tmpV;

        // For now just pass null as the motionstate, we'll add that to the body in the entity itself
        btRigidBody.btRigidBodyConstructionInfo bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, col, localInertia);
        return new BulletEntity(model, bodyInfo, x, y, z);
    }

    private static Entity createCharacter(float x, float y, float z){
        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(playerModel);
        modelComponent.transform = new Matrix4().setToTranslation(x,y,z);
        modelComponent.instance = new ModelInstance(playerModel,modelComponent.transform.cpy());
        entity.add(modelComponent);

        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        entity.add(characterComponent);

        return entity;
    }

    public static Entity createPlayer(float x, float y, float z){
        Entity entity = createCharacter(x,y,z);
        entity.add(new PlayerComponent());
        return entity;
    }

    public static Entity createEnemy(float x, float y, float z){
        Entity entity = createCharacter(x,y,z);
        entity.add(new AIComponent(AIComponent.STATE.HUNTING));
        //TODO andreas
        return entity;
    }

    public static Entity createEmpty(Model model, float x, float y, float z){
        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(model);
        modelComponent.transform = new Matrix4().setToTranslation(x,y,z);
        modelComponent.instance = new ModelInstance(model,modelComponent.transform.cpy());
        entity.add(modelComponent);

        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        entity.add(characterComponent);

        return entity;
    }

    public static void dispose() {
        playerModel.dispose();
        playerTexture.dispose();
    }
}