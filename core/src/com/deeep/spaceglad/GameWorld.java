package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.bullet.BulletEntity;
import com.deeep.spaceglad.bullet.BulletWorld;
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
    public BulletWorld bulletWorld;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public Array<Disposable> disposables = new Array<Disposable>();
    private BulletEntity ground;
    private BulletEntity character;
    private Matrix4 characterTransform;
    private btPairCachingGhostObject ghostObject;
    private btConvexShape ghostShape;
    private btKinematicCharacterController characterController;
    private btGhostPairCallback ghostPairCallback;
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;
    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;
    private Vector3 characterDirection = new Vector3();
    private Vector3 walkDirection = new Vector3();
    private Model boxModel;

    public GameWorld(GameUI gameUI) {
        initBullet();
        initEnvironment();
        initModelBatch();
        initWorld();
        initPersCamera();
        addSystems(gameUI);
        addEntities();
    }

    private void initBullet() {
        Bullet.init();
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
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0f, 0f, 0f);
        perspectiveCamera.update();
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    private void initWorld() {
        /** We create the bulletWorld using an axis sweep broadphase for this test */
        btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
        btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
        btAxisSweep3 sweep = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
        btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
        btDiscreteDynamicsWorld collisionWorld = new btDiscreteDynamicsWorld(dispatcher, sweep, solver, collisionConfiguration);
        ghostPairCallback = new btGhostPairCallback();
        sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        bulletWorld = new BulletWorld(collisionConfiguration, dispatcher, sweep, solver, collisionWorld);
    }

    private void addEntities() {
        createGround();

        /***/
        /** Create a visual representation of the character (note that we don't use the physics part of BulletEntity, we'll do that manually) */
        addCapsule();
        characterTransform = character.transform; /** Set by reference */

        /** Create the physics representation of the character */
        ghostObject = new btPairCachingGhostObject();
        ghostObject.setWorldTransform(characterTransform);
        ghostShape = new btCapsuleShape(2f, 2f);
        ghostObject.setCollisionShape(ghostShape);
        ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterController = new btKinematicCharacterController(ghostObject, ghostShape, .35f);

        /** And add it to the physics bulletWorld */
        bulletWorld.collisionWorld.addCollisionObject(ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
        ((btDiscreteDynamicsWorld) (bulletWorld.collisionWorld)).addAction(characterController);

        /** Create some boxes to play with */
        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    addBox(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                }
            }
        }
    }

    private BulletEntity addBox(float x, float y, float z) {
        boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(boxModel);
        BulletEntity box = EntityFactory.createDynamic(boxModel, 0.05f, x, y, z);
        bulletWorld.add(box);
        return box;
    }

    private void addCapsule() {
        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        disposables.add(texture);
        final Material material = new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        final Model capsule = modelBuilder.createCapsule(2f, 6f, 16, material, attributes);
        disposables.add(capsule);
        character = EntityFactory.createDynamic(capsule, -1, 5, 3, 5);
        bulletWorld.add(character);
    }

    private void createGround() {
        final Model groundModel = modelBuilder.createRect(
                20f, 0f, -20f,
                -20f, 0f, -20f,
                -20f, 0f, 20f,
                20f, 0f, 20f,
                0, 1, 0,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(groundModel);
        ground = EntityFactory.createStatic(groundModel, 0, 0, 0);
        ground.setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);
        bulletWorld.add(ground);
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(new RenderSystem(modelBatch, environment));
    }

    public void render() {
        light.begin(Vector3.Zero, perspectiveCamera.direction);
        shadowBatch.begin(light.getCamera());
        bulletWorld.render(shadowBatch, null);
        shadowBatch.end();
        light.end();
        modelBatch.begin(perspectiveCamera);
        bulletWorld.render(modelBatch, environment);
        engine.update(Gdx.graphics.getDeltaTime());
        bulletWorld.collisionWorld.debugDrawWorld();
        modelBatch.end();
    }

    public void update(float delta) {
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
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) walkDirection.add(characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
        walkDirection.scl(4f * delta);
        /** And update the character controller */
        characterController.setWalkDirection(walkDirection);
        /** Now we can update the bulletWorld as normally */
        bulletWorld.update();
        /** And fetch the new transformation of the character (this will make the model be rendered correctly) */
        ghostObject.getWorldTransform(characterTransform);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(Core.VIRTUAL_WIDTH / 2 /*x*/, Core.VIRTUAL_HEIGHT / 2 /*y*/, 30f);
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

    public BulletEntity shoot(final float x, final float y, final float impulse) {
        /** Shoot a box */
        Ray ray = perspectiveCamera.getPickRay(x, y);
        float mass = 1f;
        final BoundingBox boundingBox = new BoundingBox();
        boxModel.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        Vector3 localInertia;
        col.calculateLocalInertia(mass, tmpV);
        localInertia = tmpV;

        /** For now just pass null as the motionstate, we'll add that to the body in the entity itself */
        btRigidBody.btRigidBodyConstructionInfo bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, col, localInertia);
        BulletEntity entity = new BulletEntity(boxModel, bodyInfo, ray.origin.x, ray.origin.y, ray.origin.z);

        bulletWorld.add(entity);
        entity.setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f);
        ((btRigidBody) entity.body).applyCentralImpulse(ray.direction.scl(impulse));
        return entity;
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        ((btDiscreteDynamicsWorld) (bulletWorld.collisionWorld)).removeAction(characterController);
        bulletWorld.collisionWorld.removeCollisionObject(ghostObject);
        bulletWorld.dispose();
        bulletWorld = null;
        for (Disposable disposable : disposables) disposable.dispose();
        disposables.clear();
        modelBatch.dispose();
        modelBatch = null;
        shadowBatch.dispose();
        shadowBatch = null;
        light.dispose();
        light = null;
        characterController.dispose();
        ghostObject.dispose();
        ghostShape.dispose();
        ghostPairCallback.dispose();
        ground = null;
        boxModel = null;
    }

    //    private void checkPause() {
//        if (Settings.Paused) {
//            movementSystem.setProcessing(false);
//            playerSystem.setProcessing(false);
//            collisionSystem.setProcessing(false);
//        } else {
//            movementSystem.setProcessing(true);
//            playerSystem.setProcessing(true);
//            collisionSystem.setProcessing(true);
//        }
//    }
}