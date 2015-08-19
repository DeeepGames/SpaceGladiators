package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.managers.EnemySpawner;
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
    private PlayerSystem playerSystem;
    private CollisionSystem collisionSystem;
    private EnemySpawner enemySpawner;

    public GameWorld(GameUI gameUI) {
        initPersCamera();
        initEnvironment();
        initModelBatch();
        addSystems(gameUI);
        addEntities();
        createLevel();
        SoundManager.setCamera(perspectiveCamera);
        enemySpawner = new EnemySpawner(engine);
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
        engine.addSystem(collisionSystem = new CollisionSystem());
        engine.addSystem(playerSystem = new PlayerSystem(perspectiveCamera, gameUI, engine));
        engine.addSystem(movementSystem = new MovementSystem());
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(new AISystem());
    }

    private void addEntities() {
        engine.addEntity(EntityFactory.createEnemy(12, 0, 10));
        engine.addEntity(EntityFactory.createEnemy(14, 0, 10));
        engine.addEntity(EntityFactory.createEnemy(-12, 0, 10));
        engine.addEntity(EntityFactory.createPlayer(1f, 1.5f, 2));
    }

    private void createLevel() {
        engine.addEntity(EntityFactory.createGround());
        engine.addEntity(EntityFactory.createWall(50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, 50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
        engine.addEntity(EntityFactory.createWall(-50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, -50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
    }

    public void render(float delta) {
        enemySpawner.update(delta);
        modelBatch.begin(perspectiveCamera);
        engine.update(delta);
        modelBatch.end();
        if (CollisionSystem.collisionWorld != null) {/*
            CollisionSystem.debugDrawer.begin(perspectiveCamera);
            CollisionSystem.collisionWorld.debugDrawWorld();
            CollisionSystem.debugDrawer.end();*/
        }
        checkPause();
    }

    private void checkPause() {
        if (Settings.Pause) {
            movementSystem.setProcessing(false);
            playerSystem.setProcessing(false);
            collisionSystem.setProcessing(false);
        } else {
            movementSystem.setProcessing(true);
            playerSystem.setProcessing(true);
            collisionSystem.setProcessing(true);
        }
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        modelBatch.dispose();
    }
}
