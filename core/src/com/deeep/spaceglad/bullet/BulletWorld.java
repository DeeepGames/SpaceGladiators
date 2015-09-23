package com.deeep.spaceglad.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.managers.EntityFactory;

/**
 * Created by scanevaro on 22/09/2015.
 */
public class BulletWorld {
    private static final float FOV = 67F;
    protected final Array<BulletEntity> entities = new Array<BulletEntity>();

    public DebugDrawer debugDrawer = null;
    public boolean renderMeshes = true;
    public btCollisionConfiguration collisionConfiguration;
    public btCollisionDispatcher dispatcher;
    public btBroadphaseInterface broadphase;
    public btConstraintSolver solver;
    public btCollisionWorld collisionWorld;
    public Vector3 gravity;
    private btPairCachingGhostObject ghostObject;
    private btConvexShape ghostShape;
    private btKinematicCharacterController characterController;
    private btGhostPairCallback ghostPairCallback;
    private Vector3 characterDirection = new Vector3();
    private Vector3 walkDirection = new Vector3();
    public ModelBatch modelBatch;
    public DirectionalShadowLight light;
    public ModelBatch shadowBatch;
    public Environment environment;
    private PerspectiveCamera perspectiveCamera;
    public ModelBuilder modelBuilder = new ModelBuilder();
    private BulletEntity ground;
    private BulletEntity character;
    private Matrix4 characterTransform;
    private Model boxModel;
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;
    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;
    public Array<Disposable> disposables = new Array<Disposable>();

    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;

    public BulletWorld() {

        initBullet();
        initEnvironment();
        initModelBatch();
        initPersCamera();

        btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
        ghostPairCallback = new btGhostPairCallback();
        gravity = new Vector3(0, -10, 0);
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase =  new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);;
        if (collisionWorld instanceof btDynamicsWorld) ((btDynamicsWorld) this.collisionWorld).setGravity(gravity);
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

    public BulletWorld(final Vector3 gravity) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ((btDynamicsWorld) collisionWorld).setGravity(gravity);
        this.gravity = gravity;

        addEntities();
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
        collisionWorld.addCollisionObject(ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter));
        ((btDiscreteDynamicsWorld) (collisionWorld)).addAction(characterController);

        /** Create some boxes to play with */
        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    addBox(BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                }
            }
        }
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
        add(ground);
    }

    private BulletEntity addBox(float x, float y, float z) {
        boxModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        disposables.add(boxModel);
        BulletEntity box = EntityFactory.createDynamic(boxModel, 0.05f, x, y, z);
        add(box);
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
        add(character);
    }


    public void add(final BulletEntity entity) {
        entities.add(entity);
        if (entity.body != null) {
            if (entity.body instanceof btRigidBody)
                ((btDiscreteDynamicsWorld) collisionWorld).addRigidBody((btRigidBody) entity.body);
            else
                collisionWorld.addCollisionObject(entity.body);
            // Store the index of the entity in the collision object.
            entity.body.setUserValue(entities.size - 1);
        }
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

        if (collisionWorld instanceof btDynamicsWorld)
            ((btDynamicsWorld) collisionWorld).stepSimulation(Gdx.graphics.getDeltaTime(), maxSubSteps, fixedTimeStep);

        /** And fetch the new transformation of the character (this will make the model be rendered correctly) */
        ghostObject.getWorldTransform(characterTransform);
    }

    public void renderModelsWithoutShadows () {

        modelBatch.begin(perspectiveCamera);
        for (final BulletEntity e : entities) {
            modelBatch.render(e.modelInstance, (Environment) null);
        }
        modelBatch.end();
    }

    public void renderShadows () {

        light.begin(Vector3.Zero, perspectiveCamera.direction);
        shadowBatch.begin(light.getCamera());
        for (final BulletEntity e : entities) {
            shadowBatch.render(e.modelInstance, (Shader) light);
        }
        shadowBatch.end();
        light.end();
    }

    public void render(ModelBatch batch, Environment lights, Iterable<BulletEntity> entities) {
        if (renderMeshes) {
            for (final BulletEntity e : entities) {
                batch.render(e.modelInstance, lights);
            }
        }
        if (debugDrawer != null && debugDrawer.getDebugMode() > 0) {
            batch.flush();
            debugDrawer.begin(batch.getCamera());
            collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
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

        add(entity);
        entity.setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                1f);
        ((btRigidBody) entity.body).applyCentralImpulse(ray.direction.scl(impulse));
        return entity;
    }

    public void dispose() {


        for (Disposable disposable : disposables) disposable.dispose();
        disposables.clear();
        boxModel = null;

        for (int i = 0; i < entities.size; i++) {
            btCollisionObject body = entities.get(i).body;
            if (body != null) {
                if (body instanceof btRigidBody)
                    ((btDynamicsWorld) collisionWorld).removeRigidBody((btRigidBody) body);
                else
                    collisionWorld.removeCollisionObject(body);
            }
        }

        for (int i = 0; i < entities.size; i++)
            entities.get(i).dispose();
        entities.clear();



        //models.clear();

        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadphase != null) broadphase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();

        ((btDiscreteDynamicsWorld) (collisionWorld)).removeAction(characterController);
        collisionWorld.removeCollisionObject(ghostObject);
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
    }

    public void setDebugMode(final int mode) {
        if (mode == btIDebugDraw.DebugDrawModes.DBG_NoDebug && debugDrawer == null) return;
        if (debugDrawer == null) collisionWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
        debugDrawer.setDebugMode(mode);
    }

    public int getDebugMode() {
        return (debugDrawer == null) ? 0 : debugDrawer.getDebugMode();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }
}