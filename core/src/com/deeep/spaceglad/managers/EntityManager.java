package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.systems.AISystem;
import com.deeep.spaceglad.systems.CollisionSystem;
import com.deeep.spaceglad.systems.MovementSystem;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;
    private MovementSystem ms;

    public EntityManager(Engine engine, ModelBatch batch, Environment environment) {
        this.engine = engine;
        engine.addSystem(ms = new MovementSystem());
        engine.addSystem(new RenderSystem(batch, environment));
        engine.addSystem(new AISystem());
        engine.addSystem(new CollisionSystem());
        engine.addEntity(EntityFactory.createEnemy(0, 0, 0));
        engine.addEntity(EntityFactory.createPlayer(1f, 1.5f, 2));
        createLevel();
    }

    public void createLevel() {
        Entity ground = new Entity();
        ground.add(new PositionComponent(0, -2.2f, 0))
                .add(new VelocityComponent())
                .add(new RotationComponent(0, 0, 0))
                .add(new RenderableComponent())
                .add(new StatusComponent())
                .add(new ModelComponent(
                        new ModelBuilder().createBox(50f, 0.5f, 50f,
                                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(24.99f, 0.25f, 24.99f)));
        collisionComponent.collisionObject.setWorldTransform(ground.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.collisionObject.userData = ground;
        collisionComponent.collisionObject.setUserValue(2);
        ground.add(collisionComponent);
        engine.addEntity(ground);

        engine.addEntity(EntityFactory.createWall(25.25f, 10, 0, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, 25.25f, new Vector3(0, 0, 0)));
        engine.addEntity(EntityFactory.createWall(-25.25f, 10, 0, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, -25.25f, new Vector3(0, 0, 0)));

    }

    public void update(float delta) {
        engine.update(delta);
        if (Core.Pause) ms.setProcessing(false);
        else ms.setProcessing(true);
    }
}