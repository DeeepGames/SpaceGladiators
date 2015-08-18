package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 7-8-2015.
 */
public class EntityFactory {
    public static Entity createWall(float x, float y, float z, float sizeX, float sizeY, float sizeZ, Vector3 orientation) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(orientation));
        entity.add(new RenderableComponent());
        entity.add(new StatusComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(sizeX, sizeY, sizeZ,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(sizeX / 2, sizeY / 2, sizeZ / 2)));
        collisionComponent.collisionObject.userData = entity;
        collisionComponent.collisionObject.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.collisionObject.setUserValue(3);
        entity.add(collisionComponent);
        return entity;
    }

    public static Entity createEnemy(int x, int y, int z) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(0, 0, 0));
        entity.add(new StatusComponent());
        entity.add(new AIComponent(AIComponent.STATE.HUNTING));
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(2f, 2f, 2f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(2f, 2f, 2f)));
        collisionComponent.collisionObject.userData = entity;
        collisionComponent.collisionObject.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.collisionObject.setUserValue(4);
        entity.add(collisionComponent);
        return entity;
    }

    public static Entity createBullet(float x, float y, float z, float velocity, float yaw, float pitch, float roll) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent(new Vector3(velocity * yaw, velocity * pitch, velocity * roll)));
        entity.add(new RotationComponent(yaw, pitch, roll));
        entity.add(new StatusComponent());
        entity.add(new RenderableComponent());
        entity.add(new BulletComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(0.05f, 0.05f, 0.05f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));

        CollisionComponent collisionComponent = new CollisionComponent(new btCapsuleShape(0.05f, 0.05f));
        collisionComponent.collisionObject.userData = entity;
        collisionComponent.collisionObject.setUserValue(5);
        collisionComponent.collisionObject.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
        entity.add(collisionComponent);

        return entity;
    }

    public static Entity createPlayer(float x, float y, float z) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent());
        entity.add(new RotationComponent(0, 0, 0));
        entity.add(new StatusComponent());
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(new ModelBuilder().createCapsule(1, 4, 16, new Material(ColorAttribute.createDiffuse(Color.GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        entity.add(new PlayerComponent());
        CollisionComponent collisionComponent = new CollisionComponent(new btCapsuleShape(1, 2));
        collisionComponent.collisionObject.userData = entity;
        collisionComponent.collisionObject.setUserValue(1);
        collisionComponent.collisionObject.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);

        entity.add(collisionComponent);
        return entity;
    }
}
