package com.deeep.spaceglad;

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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.bullet.BulletConstructor;
import com.deeep.spaceglad.bullet.BulletEntity;
import com.deeep.spaceglad.bullet.BulletWorld;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameWorld implements GestureDetector.GestureListener {
    private static final float FOV = 67F;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;
    //    private Engine engine;
    private ModelBatch modelBatch;
    //    private MovementSystem movementSystem;
//    private PlayerSystem playerSystem;
//    private CollisionSystem collisionSystem;
    //    private EnemySpawner enemySpawner;
    private final static String customDesktopLib = null;
    public DirectionalShadowLight light;
    public ModelBatch shadowBatch;
    public BulletWorld world;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public Array<Disposable> disposables = new Array<Disposable>();
    BulletEntity ground;
    BulletEntity character;
    Matrix4 characterTransform;
    btPairCachingGhostObject ghostObject;
    btConvexShape ghostShape;
    btKinematicCharacterController characterController;
    btGhostPairCallback ghostPairCallback;
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;
    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;
    Vector3 characterDirection = new Vector3();
    Vector3 walkDirection = new Vector3();
    private int debugMode = btIDebugDraw.DebugDrawModes.DBG_NoDebug;

    public GameWorld(GameUI gameUI) {
//        initPersCamera();
//        initEnvironment();
//        initModelBatch();
//        addSystems(gameUI);
//        addEntities();
//        createLevel();
//        SoundManager.setCamera(perspectiveCamera);
//        enemySpawner = new EnemySpawner(engine);
        Bullet.init();
        /**create()*/
        initEnvironment();
        initModelBatch();
        world = createWorld();
        initPersCamera();

        /** Create some simple models */
        final Model groundModel = modelBuilder.createRect(
                20f,
                0f,
                -20f,
                -20f,
                0f,
                -20f,
                -20f,
                0f,
                20f,
                20f,
                0f,
                20f,
                0,
                1,
                0,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(groundModel);
        final Model boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(boxModel);

        /** Add the constructors */
        world.addConstructor("ground", new BulletConstructor(groundModel, 0f)); /** mass = 0: static body */
        world.addConstructor("box", new BulletConstructor(boxModel, 1f)); /** mass = 1kg: dynamic body */
        world.addConstructor("staticbox", new BulletConstructor(boxModel, 0f)); /** mass = 0: static body */
        /***/
        /** Create a visual representation of the character (note that we don't use the physics part of BulletEntity, we'll do that manually) */
        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        disposables.add(texture);
        final Material material = new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        final Model capsule = modelBuilder.createCapsule(2f, 6f, 16, material, attributes);
        disposables.add(capsule);
        world.addConstructor("capsule", new BulletConstructor(capsule, null));
        character = world.add("capsule", 5f, 3f, 5f);
        characterTransform = character.transform; /** Set by reference */

        /** Create the physics representation of the character */
        ghostObject = new btPairCachingGhostObject();
        ghostObject.setWorldTransform(characterTransform);
        ghostShape = new btCapsuleShape(2f, 2f);
        ghostObject.setCollisionShape(ghostShape);
        ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterController = new btKinematicCharacterController(ghostObject, ghostShape, .35f);

        /** And add it to the physics world */
        world.collisionWorld.addCollisionObject(ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
        ((btDiscreteDynamicsWorld) (world.collisionWorld)).addAction(characterController);

        /** Add the ground */
        (ground = world.add("ground", 0f, 0f, 0f))
                .setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);
        /** Create some boxes to play with */
        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    world.add("box", BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z)
                            .setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                }
            }
        }
    }

    public BulletWorld createWorld() {
        /** We create the world using an axis sweep broadphase for this test */
        btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
        btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
        btAxisSweep3 sweep = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
        btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
        btDiscreteDynamicsWorld collisionWorld = new btDiscreteDynamicsWorld(dispatcher, sweep, solver, collisionConfiguration);
        ghostPairCallback = new btGhostPairCallback();
        sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        return new BulletWorld(collisionConfiguration, dispatcher, sweep, solver, collisionWorld);
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
//        perspectiveCamera.position.set(20f, 0f, 20f);
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0f, 0f, 0f);
//        perspectiveCamera.near = 1f;
//        perspectiveCamera.far = 300f;
        perspectiveCamera.update();
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

    private void initModelBatch() {
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

//    private void addSystems(GameUI gameUI) {
//        engine = new Engine();
//        engine.addSystem(collisionSystem = new CollisionSystem());
//        engine.addSystem(playerSystem = new PlayerSystem(perspectiveCamera, gameUI, engine));
//        engine.addSystem(movementSystem = new MovementSystem());
//        engine.addSystem(new RenderSystem(modelBatch, environment));
//        engine.addSystem(new AISystem());
//    }

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
//        engine.update(delta);
//        modelBatch.end();
//        if (CollisionSystem.collisionWorld != null) {
//            CollisionSystem.debugDrawer.begin(perspectiveCamera);
//            CollisionSystem.collisionWorld.debugDrawWorld();
//            CollisionSystem.debugDrawer.end();
//        }
//        checkPause();
        perspectiveCamera.update();
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
        modelBatch.end();
    }

    public void update() {
        /** If the left or right key is pressed, rotate the character and update its physics update accordingly. */
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterTransform.rotate(0, 1, 0, 5f);
            ghostObject.setWorldTransform(characterTransform);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterTransform.rotate(0, 1, 0, -5f);
            ghostObject.setWorldTransform(characterTransform);
        }
        /** Fetch which direction the character is facing now */
        characterDirection.set(-1, 0, 0).rot(characterTransform).nor();
        /** Set the walking direction accordingly (either forward or backward) */
        walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            walkDirection.add(characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
        walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
        /** And update the character controller */
        characterController.setWalkDirection(walkDirection);
        /** Now we can update the world as normally */
        world.update();
        /** And fetch the new transformation of the character (this will make the model be rendered correctly) */
        ghostObject.getWorldTransform(characterTransform);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public boolean tap(float x, float y, int count, int button) {
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

    public BulletEntity shoot(final float x, final float y) {
        return shoot(x, y, 30f);
    }

    public BulletEntity shoot(final float x, final float y, final float impulse) {
        return shoot("box", x, y, impulse);
    }

    public BulletEntity shoot(final String what, final float x, final float y, final float impulse) {
        // Shoot a box
        Ray ray = perspectiveCamera.getPickRay(x, y);
        BulletEntity entity = world.add(what, ray.origin.x, ray.origin.y, ray.origin.z);
        entity.setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f);
        ((btRigidBody) entity.body).applyCentralImpulse(ray.direction.scl(impulse));
        return entity;
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
        ((btDiscreteDynamicsWorld) (world.collisionWorld)).removeAction(characterController);
        world.collisionWorld.removeCollisionObject(ghostObject);
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
        characterController.dispose();
        ghostObject.dispose();
        ghostShape.dispose();
        ghostPairCallback.dispose();
        ground = null;
    }
}