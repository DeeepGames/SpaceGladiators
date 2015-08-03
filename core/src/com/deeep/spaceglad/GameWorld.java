package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.chapter.two.FirstPersonCameraController;
import com.deeep.spaceglad.managers.EntityManager;

/**
 * Created by scanevaro on 31/07/2015.
 */

public class GameWorld {

    private EntityManager entityManager;
    private ModelBatch batch;
    private Environment environment;
    private FirstPersonCameraController firstPersonCameraController;
    private PerspectiveCamera cam;
    private ModelBuilder modelBuilder;

    private Model model1;
    private Model model2;
    private ModelInstance instance1;
    private ModelInstance instance2;

    public GameWorld(){
        Engine engine = new Engine();

        batch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        environment = new Environment();

        entityManager = new EntityManager(engine, batch);
        cam = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            cam.position.set(20f, 2f, 20f);
            cam.lookAt(0f, 0f, 0f);
            cam.near = 1f;
            cam.far = 300f;
            cam.update();
        firstPersonCameraController = new FirstPersonCameraController(cam);

        Gdx.input.setCursorCatched(true);
        SoundManager.setCamera(cam);
        Gdx.input.setInputProcessor(firstPersonCameraController);

        model1 = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        model2 = modelBuilder.createBox(50f, 1f, 50f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance1 = new ModelInstance(model1);
        instance2 = new ModelInstance(model2);
    }

    public void render(){
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin(cam);
        batch.render(instance1, environment);
        batch.render(instance2, environment);
        batch.end();
    }

    public void update(float delta){
        firstPersonCameraController.update();
    }

    public void resize(int width, int height) {
        cam.viewportHeight = height;
        cam.viewportWidth = width;
    }


    private static final float FOV = 67F;
}
