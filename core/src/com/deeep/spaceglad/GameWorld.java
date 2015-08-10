package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.chapter.two.FirstPersonCameraController;
import com.deeep.spaceglad.managers.EntityManager;
import com.deeep.spaceglad.systems.CollisionSystem;

/**
 * Created by scanevaro on 31/07/2015.
 */

public class GameWorld {
    private EntityManager entityManager;
    private ModelBatch batch;
    private Environment environment;
    public FirstPersonCameraController firstPersonCameraController;
    private PerspectiveCamera cam;

    public GameWorld(GameUI gameUI) {
        Engine engine = new Engine();
        batch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        cam = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 2f, 20f);
        cam.lookAt(0f, 0f, 0f);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        entityManager = new EntityManager(engine, batch, environment, gameUI);
        firstPersonCameraController = new FirstPersonCameraController(cam);
        Gdx.input.setCursorCatched(true);
        SoundManager.setCamera(cam);
        Gdx.input.setInputProcessor(firstPersonCameraController);
    }

    public void render(float delta) {
        batch.begin(cam);
        entityManager.update(delta);
        batch.end();
        if (CollisionSystem.collisionWorld != null) {
            CollisionSystem.debugDrawer.begin(cam);
            CollisionSystem.collisionWorld.debugDrawWorld();
            CollisionSystem.debugDrawer.end();
        }
    }

    public void update(float delta) {
        firstPersonCameraController.update(delta);
    }

    public void resize(int width, int height) {
        cam.viewportHeight = height;
        cam.viewportWidth = width;
    }

    public void dispose() {
        batch.dispose();
    }

    private static final float FOV = 67F;
}
