package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.systems.*;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;
    private ModelBatch modelBatch;
    private PerspectiveCamera perspectiveCamera;
    private MovementSystem movementSystem;

    public EntityManager(PerspectiveCamera perspectiveCamera, Environment environment, GameUI gameUI) {
        this.perspectiveCamera = perspectiveCamera;
        engine = new Engine();
        modelBatch = new ModelBatch();
        addSystems(gameUI, environment);
        addEntities();
        createLevel();
    }

    private void addSystems(GameUI gameUI, Environment environment) {
        engine.addSystem(new PlayerSystem(perspectiveCamera, gameUI, engine));
        engine.addSystem(movementSystem = new MovementSystem());
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(new AISystem());
        engine.addSystem(new CollisionSystem());
    }

    private void addEntities() {
        engine.addEntity(EntityFactory.createEnemy(0, 0, 0));
        engine.addEntity(EntityFactory.createPlayer(1f, 1.5f, 2));
    }

    private void createLevel() {
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
        modelBatch.begin(perspectiveCamera);
        engine.update(delta);
        modelBatch.end();
        if (Core.Pause) movementSystem.setProcessing(false);
        else movementSystem.setProcessing(true);
    }

    public void dispose() {
        modelBatch.dispose();
    }
}