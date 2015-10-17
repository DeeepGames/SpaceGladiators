package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.bullet.BulletWorld;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.AISystem;
import com.deeep.spaceglad.systems.PlayerSystem;
import com.deeep.spaceglad.systems.RenderSystem;
import com.deeep.spaceglad.systems.StatusSystem;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameWorld implements GestureDetector.GestureListener {
    private static final float FOV = 67F;

    private PerspectiveCamera perspectiveCamera;
    private Environment environment;
    private Engine engine;
    private ModelBatch modelBatch;

    // TODO These are temporary and should be removed when obsolete
    private Entity ground;
    private Entity wall;
    private Entity character;
    //private FirstPersonCameraController firstPersonCameraController;

    public DirectionalShadowLight light;
    public ModelBatch shadowBatch;
    public BulletWorld world;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public Array<Disposable> disposables = new Array<Disposable>();

    public GameWorld(GameUI gameUI) {
        Bullet.init();
        initEnvironment();
        initModelBatch();
        initWorld();
        initPersCamera();
        addSystems(gameUI);
        addEntities();
        SoundManager.setCamera(perspectiveCamera);
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        light = new DirectionalShadowLight(1024, 1024, 20f, 20f, 1f, 300f);
        light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
        environment.add(light);
        environment.shadowMap = light;
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);

    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    private void initWorld() {
        world = new BulletWorld();
    }

    private void addEntities() {
        createGround();
        createPlayer(5, 3, 5);
        engine.addEntity(EntityFactory.createEnemy(world, 5, 3, 5));
    }

    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(world, x, y, z);
        engine.addEntity(character);
    }

    private void createGround() {
        Model wallHorizontal = modelBuilder.createBox(40, 20, 1,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model wallVertical = modelBuilder.createBox(1, 20, 40,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model groundModel = modelBuilder.createBox(40, 1, 40,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(wallHorizontal);
        disposables.add(wallVertical);
        disposables.add(groundModel);


        ground = EntityFactory.createStaticEntity(groundModel, 0, 0, 0);
        ground.getComponent(ModelComponent.class).setColor(new Color(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f));
        engine.addEntity(ground);

        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, -20));
        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, 20));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 20, 10, 0));
        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -20, 10, 0));

        // test.getComponent(ModelComponent.class).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);

    }

    public void createLevel() {

    }

    private void addSystems(GameUI gameUI) {
        // TODO Add the remaining systems
        engine = new Engine();
        // engine.addSystem(playerSystem = new PlayerSystem(perspectiveCamera, gameUI, engine));
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(world);
        engine.addSystem(new PlayerSystem(perspectiveCamera));
        engine.addSystem(new AISystem());
        engine.addSystem(new StatusSystem(this));
    }

    public void render() {
        renderWorld();
    }

    protected void renderWorld() {
       // light.begin(Vector3.Zero, perspectiveCamera.direction);
        shadowBatch.begin(light.getCamera());
        shadowBatch.end();
        //light.end();
        modelBatch.begin(perspectiveCamera);
        engine.update(Gdx.graphics.getDeltaTime());
        modelBatch.end();
    }

    public void update(float delta) {
        world.update(delta);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
        shoot(x + (perspectiveCamera.viewportWidth / 2), (perspectiveCamera.viewportHeight / 2));
        return true;
    }

    public Entity shoot(final float x, final float y) {
        return shoot(x, y, 30f);
    }

    public Entity shoot(final float x, final float y, final float impulse) {
        /** Shoot a box */
        Ray ray = perspectiveCamera.getPickRay(x, y);
        Entity ent = EntityFactory.createBullet(ray, ray.origin.x, ray.origin.y, ray.origin.z);
        engine.addEntity(ent);
        return ent;
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        ((btDiscreteDynamicsWorld) (world.collisionWorld)).removeAction(character.getComponent(CharacterComponent.class).characterController);
        world.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);

        world.dispose();
        world = null;

        for (Disposable disposable : disposables) disposable.dispose();
        disposables.clear();

        modelBatch.dispose();
        modelBatch = null;

        shadowBatch.dispose();
        shadowBatch = null;

        light.dispose();
        light = null;

        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
        EntityFactory.dispose();
        ground = null;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        world.removeBody(entity);
    }
}