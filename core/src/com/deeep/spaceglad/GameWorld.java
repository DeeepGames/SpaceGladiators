package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.*;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameWorld {
    private static final float FOV = 67F;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;
    private Engine engine;
    private ModelBatch modelBatch;
    private MovementSystem movementSystem;

    public GameWorld(GameUI gameUI) {
        initPersCamera();
        initEnvironment();
        initModelBatch();
        addSystems(gameUI);
        addEntities();
        createLevel();
        SoundManager.setCamera(perspectiveCamera);
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        perspectiveCamera.position.set(20f, 0f, 20f);
        perspectiveCamera.lookAt(0f, 0f, 0f);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerSystem(perspectiveCamera, gameUI, engine));
        engine.addSystem(movementSystem = new MovementSystem());
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(new AISystem());
    }

    private void addEntities() {
        engine.addEntity(EntityFactory.createEnemy(2, 0, 0));
        engine.addEntity(EntityFactory.createEnemy(4, 0, 0));
        engine.addEntity(EntityFactory.createEnemy(-2, 0, 0));
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
                        new ModelBuilder().createBox(100f, 0.5f, 100f,
                                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        CollisionComponent collisionComponent = new CollisionComponent(new btBoxShape(new Vector3(49.99f, 0.25f, 49.99f)));
        collisionComponent.collisionObject.setWorldTransform(ground.getComponent(ModelComponent.class).instance.transform);
        collisionComponent.collisionObject.userData = ground;
        collisionComponent.collisionObject.setUserValue(2);
        ground.add(collisionComponent);
        engine.addEntity(ground);
        engine.addEntity(EntityFactory.createWall(50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, 50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
        engine.addEntity(EntityFactory.createWall(-50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, -50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
    }

    public void render(float delta) {
        modelBatch.begin(perspectiveCamera);
        engine.update(delta);
        modelBatch.end();
        if (CollisionSystem.collisionWorld != null) {
            CollisionSystem.debugDrawer.begin(perspectiveCamera);
            CollisionSystem.collisionWorld.debugDrawWorld();
            CollisionSystem.debugDrawer.end();
        }
        if (Settings.Pause) movementSystem.setProcessing(false);
        else movementSystem.setProcessing(true);
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        modelBatch.dispose();
    }
}
