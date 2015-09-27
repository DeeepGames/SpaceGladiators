package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.bullet.BulletWorld;
import com.deeep.spaceglad.chapter.seven.SoundManager;
import com.deeep.spaceglad.chapter.two.FirstPersonCameraController;
import com.deeep.spaceglad.components.BulletComponent;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameWorld implements GestureDetector.GestureListener {
    private static final float FOV = 67F;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;
    private Engine engine;
    private ModelBatch modelBatch;
    public DirectionalShadowLight light;
    public ModelBatch shadowBatch;
    public BulletWorld world;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public Array<Disposable> disposables = new Array<Disposable>();
    Entity ground;
    Entity wall;

    Entity character;

    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;
    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;

    private int debugMode = btIDebugDraw.DebugDrawModes.DBG_NoDebug;
    public static DebugDrawer debugDrawer;
    private Model boxModel;

    FirstPersonCameraController firstPersonCameraController;

    public GameWorld(GameUI gameUI) {

//        addEntities();
//        createLevel();
//        enemySpawner = new EnemySpawner(engine);
        initBullet();
        initEnvironment();
        initModelBatch();
        initWorld();
        initPersCamera();
        addSystems(gameUI);
        addEntities();
        SoundManager.setCamera(perspectiveCamera);
    }

    private void initBullet() {
        Bullet.init();
    }

    private void initEnvironment() {
//        environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
//        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        light = new DirectionalShadowLight(1024, 1024, 20f, 20f, 1f, 300f);
        light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
        environment.add(light);
        environment.shadowMap = light;
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0f, 0f, 0f);
        perspectiveCamera.update();
        firstPersonCameraController = new FirstPersonCameraController(perspectiveCamera);
        Gdx.input.setInputProcessor(firstPersonCameraController);
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    private void initWorld() {
        /** We create the world using an axis sweep broadphase for this test */

        world = new BulletWorld();
        //TODO remove this
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        world.collisionWorld.setDebugDrawer(debugDrawer);
    }

    private void addEntities() {
        createGround();

        /***/
        /** Create a visual representation of the character (note that we don't use the physics part of BulletEntity, we'll do that manually) */
        createPlayer(5, 3, 5);

        /** Create the physics representation of the character */

        /** And add it to the physics world */
        world.collisionWorld.addCollisionObject(character.getComponent(CharacterComponent.class).ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
        ((btDiscreteDynamicsWorld) (world.collisionWorld)).addAction(character.getComponent(CharacterComponent.class).characterController);

        /** Create some boxes to play with */
        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    //addBox(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                    addBox(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).getComponent(ModelComponent.class).setColor(new Color(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f));
                }
            }
        }
    }

    private Entity addBox(float x, float y, float z) {
        boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(boxModel);
        Entity box = EntityFactory.createDynamicEntity(boxModel, 0.05f, x, y, z);
        engine.addEntity(box);
        return box;
    }

    private void createPlayer(float x, float y, float z) {
        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        final Material material = new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        final Model capsule = modelBuilder.createCapsule(2f, 6f, 16, material, attributes);
        disposables.add(capsule);
        disposables.add(texture);
        character = EntityFactory.createEmpty(capsule, x, y, z);
        engine.addEntity(character);
    }

    private void createGround() {
        final Model wallModel = modelBuilder.createRect(
                0f, -10f, -20f,
                0f, 10f, -20f,
                0f, 10f, 20f,
                0f, -10f, 20f,
                0, 1, 0,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        wall = EntityFactory.createStaticEntity(wallModel, -20, 10, 0);
        wall.getComponent(ModelComponent.class).setColor(new Color(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f));
        engine.addEntity(wall);
        //world.add(wall);

        final Model groundModel = modelBuilder.createRect(
                20f, 0f, -20f,
                -20f, 0f, -20f,
                -20f, 0f, 20f,
                20f, 0f, 20f,
                0, 1, 0,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(groundModel);
        ground = EntityFactory.createStaticEntity(groundModel, 0, 0, 0);
        ground.getComponent(ModelComponent.class).setColor(new Color(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f));
        engine.addEntity(ground);
        //world.add(ground));


    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
//        engine.addSystem(collisionSystem = new CollisionSystem());
//        engine.addSystem(playerSystem = new PlayerSystem(perspectiveCamera, gameUI, engine));
//        engine.addSystem(movementSystem = new MovementSystem());
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(world);
//        engine.addSystem(new AISystem());
    }

//    private void addEntities() {
//        engine.addEntity(EntityFactory.createEnemy(12, 0, 10));
//        engine.addEntity(EntityFactory.createEnemy(14, 0, 10));
//        engine.addEntity(EntityFactory.createEnemy(-12, 0, 10));
//        engine.addEntity(EntityFactory.createPlayer(1f, 1.5f, 2));
//    }

//    private void createLevel() {
//        engine.addEntity(EntityFactory.createGround());
//        engine.addEntity(EntityFactory.createWall(50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
//        engine.addEntity(EntityFactory.createWall(0, 10, 50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
//        engine.addEntity(EntityFactory.createWall(-50.50f, 10, 0, 100, 25, 0.5f, new Vector3(90, 0, 0)));
//        engine.addEntity(EntityFactory.createWall(0, 10, -50.50f, 100, 25, 0.5f, new Vector3(0, 0, 0)));
//    }

    public void render(float delta) {
//        enemySpawner.update(delta);
//        modelBatch.begin(perspectiveCamera);

//        modelBatch.end();
//        if (CollisionSystem.collisionWorld != null) {
//            CollisionSystem.debugDrawer.begin(perspectiveCamera);
//            CollisionSystem.collisionWorld.debugDrawWorld();
//            CollisionSystem.debugDrawer.end();
//        }
//        checkPause();
        //perspectiveCamera.update();
        renderWorld();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        if (debugMode != btIDebugDraw.DebugDrawModes.DBG_NoDebug) world.setDebugMode(debugMode);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

    }

    protected void renderWorld() {
        light.begin(Vector3.Zero, perspectiveCamera.direction);
        shadowBatch.begin(light.getCamera());
        world.render(shadowBatch, null);
        shadowBatch.end();
        light.end();
        modelBatch.begin(perspectiveCamera);
        world.render(modelBatch, environment);
        debugDrawer.begin(perspectiveCamera);
        engine.update(Gdx.graphics.getDeltaTime());
        world.collisionWorld.debugDrawWorld();
        debugDrawer.end();
        modelBatch.end();
    }

    public void update() {
        /** If the left or right key is pressed, rotate the character and update its physics update accordingly. */
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(character.getComponent(ModelComponent.class).transform.rotate(0, 1, 0, 5f));
            character.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(character.getComponent(ModelComponent.class).transform);

        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(character.getComponent(ModelComponent.class).transform.rotate(0, 1, 0, -5f));
            character.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(character.getComponent(ModelComponent.class).transform);
        }
        /** Fetch which direction the character is facing now */
        character.getComponent(CharacterComponent.class).characterDirection.set(-1, 0, 0).rot(character.getComponent(ModelComponent.class).transform).nor();
        /** Set the walking direction accordingly (either forward or backward) */
        character.getComponent(CharacterComponent.class).walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            character.getComponent(CharacterComponent.class).walkDirection.add(character.getComponent(CharacterComponent.class).characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            character.getComponent(CharacterComponent.class).walkDirection.add(-character.getComponent(CharacterComponent.class).characterDirection.x, -character.getComponent(CharacterComponent.class).characterDirection.y, -character.getComponent(CharacterComponent.class).characterDirection.z);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            firstPersonCameraController.forward();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            firstPersonCameraController.backward();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            firstPersonCameraController.left();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            firstPersonCameraController.right();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            character.getComponent(CharacterComponent.class).characterController.setJumpSpeed(15);
            character.getComponent(CharacterComponent.class).characterController.jump();  //.body).applyCentralImpulse(new Vector3(0,5,0));
        }

        character.getComponent(CharacterComponent.class).walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
        /** And update the character controller */
        character.getComponent(CharacterComponent.class).characterController.setWalkDirection(character.getComponent(CharacterComponent.class).walkDirection);
        /** Now we can update the world as normally */
        world.update(Gdx.graphics.getDeltaTime());
        /** And fetch the new transformation of the character (this will make the model be rendered correctly) */
        character.getComponent(CharacterComponent.class).ghostObject.getWorldTransform(character.getComponent(ModelComponent.class).transform);
        character.getComponent(ModelComponent.class).instance.transform = character.getComponent(ModelComponent.class).transform;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
        //shoot(Core.VIRTUAL_WIDTH / 2 /*x*/, Core.VIRTUAL_HEIGHT / 2 /*y*/);
        shoot(x, y);
        return true;
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

    public Entity shoot(final float x, final float y) {
        return shoot(x, y, 30f);
    }

    public Entity shoot(final float x, final float y, final float impulse) {
        /** Shoot a box */
        Ray ray = perspectiveCamera.getPickRay(x, y);
        float mass = 1f;
        Entity bullet = EntityFactory.createDynamicEntity(boxModel, mass, ray.origin.x, ray.origin.y, ray.origin.z);
        //world.add(bullet);
        bullet.getComponent(ModelComponent.class).setColor(new Color(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f));
        ((btRigidBody) bullet.getComponent(BulletComponent.class).body).applyCentralImpulse(ray.direction.scl(impulse));
        engine.addEntity(bullet);
        return bullet;
    }

    private void checkPause() {
//        if (Settings.Paused) {
//            movementSystem.setProcessing(false);
//            playerSystem.setProcessing(false);
//            collisionSystem.setProcessing(false);
//        } else {
//            movementSystem.setProcessing(true);
//            playerSystem.setProcessing(true);
//            collisionSystem.setProcessing(true);
//        }
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        ((btDiscreteDynamicsWorld) (world.collisionWorld)).removeAction(character.getComponent(CharacterComponent.class).characterController);
        world.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        /***/
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
        /***/
        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
        //ghostPairCallback.dispose();
        ground = null;
    }
}