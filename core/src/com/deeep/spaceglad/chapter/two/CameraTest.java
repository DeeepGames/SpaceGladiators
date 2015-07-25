package com.deeep.spaceglad.chapter.two;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.chapter.seven.SoundManager;

/**
 * Created by Elmar on 25-7-2015.
 */
public class CameraTest extends ApplicationAdapter {
   /* SpriteBatch batch;
    Texture img;*/
    public PerspectiveCamera cam;
    public Model model;
    public Model model2;
    public ModelInstance instance;
    public ModelInstance instance2;
    public ModelBatch modelBatch;
    FirstPersonCameraController firstPersonCameraController;
    public Environment environment;
    @Override
    public void create () {

        /*batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");*/


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        modelBatch = new ModelBatch();
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 2f, 20f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        Gdx.input.setCursorCatched(true);
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        model2 = modelBuilder.createBox(50f, 1f, 50f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
        instance2 = new ModelInstance(model2);
        firstPersonCameraController = new FirstPersonCameraController(cam);
        Gdx.input.setInputProcessor(firstPersonCameraController);

        SoundManager.setCamera(cam);
        SoundManager.getInstance().playMusicAtPosition(SoundManager.getInstance().defaultMusic, SoundManager.DEFAULT_VOLUME, new Vector3(20, 2, 20));
    }

    @Override
    public void render () {
        firstPersonCameraController.update();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        /*batch.begin();
        batch.draw(img, 0, 0);
        batch.end();*/
        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.render(instance2, environment);
        modelBatch.end();
    }
}
