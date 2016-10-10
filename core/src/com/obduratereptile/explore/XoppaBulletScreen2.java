package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.explore.pool.PoolBall;
import com.obduratereptile.explore.pool.PoolTable;

/**
 * Created by Marc on 9/30/2016.
 */
public class XoppaBulletScreen2 implements Screen {
    final static short GROUND_FLAG = 1<<8;
    final static short OBJECT_FLAG = 1<<9;
    final static short ALL_FLAG = -1;

    ExploreGame game;
    AssetManager manager;
    Skin skin;
    SpriteBatch batch;
    InputMultiplexer inputMux;

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0,
                                       int userValue1, int partId1, int index1, boolean match1) {
//            if (match0)
//                ((ColorAttribute)instances.get("obj_" + userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
//            if (match1)
//                ((ColorAttribute)instances.get("obj_" + userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            return true;
        }
    }
    MyContactListener contactListener;

    static class MyMotionState extends btMotionState {
        Matrix4 transform;
        @Override
        public void getWorldTransform (Matrix4 worldTrans) {
            worldTrans.set(transform);
        }
        @Override
        public void setWorldTransform (Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }

    class Ground extends ModelInstance implements Disposable {
        public final btCollisionShape shapeGround;
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfoGround;

        public final btRigidBody body;
        public final MyMotionState motionState;

        public Ground(Model model) {
            super(model, "ground");
            motionState = new MyMotionState();
            motionState.transform = transform;

            shapeGround = new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f));
            constructionInfoGround = new btRigidBody.btRigidBodyConstructionInfo(0, null, shapeGround, new Vector3(0,0,0));

            body = new btRigidBody(constructionInfoGround);
            body.setMotionState(motionState);
        }

        @Override
        public void dispose () {
            shapeGround.dispose();
            constructionInfoGround.dispose();
            body.dispose();
            motionState.dispose();
        }
    }

    static class GameObject extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MyMotionState motionState;

        public GameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MyMotionState();
            motionState.transform = transform;
            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);
        }

        @Override
        public void dispose () {
            body.dispose();
            motionState.dispose();
        }

        static class Constructor implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static Vector3 localInertia = new Vector3();

            public Constructor(Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }

            public GameObject construct() {
                return new GameObject(model, node, constructionInfo);
            }

            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        }
    }
    ArrayMap<String, GameObject.Constructor> constructors;

    ModelBatch mbatch;
    ModelBuilder mbuilder;
    Model model;
    ArrayMap<String, ModelInstance> instances;
    Environment env;

    boolean collision;
    btCollisionShape groundShape;
    btCollisionShape ballShape;
    btCollisionObject groundObject;
    btCollisionObject ballObject;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

    Stage stage;
    StringBuilder stringBuilder;
    Label lbl_fps;
    PerspectiveCamera camera;
    CameraInputController camController;

    TextureAtlas atlas;

    DebugDrawer debugDrawer;

    public XoppaBulletScreen2(ExploreGame g) {
        game = g;
        manager = g.manager;
        skin = g.skin;
        batch = g.batch;
        stringBuilder = new StringBuilder();

        inputMux = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMux);

        instances = new ArrayMap<String, ModelInstance>();

        Bullet.init();
    }

    private void createStage(int w, int h) {
        //TODO: support smaller screens
        float aspectRatio = (float)h / (float)w;
        int x = 1000;
        int y = (int)(1000.0f * aspectRatio);
        stage = new Stage(new FitViewport(x, y));
    }

    @Override
    public void show() {
        createStage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        inputMux.addProcessor(stage);

        atlas = game.manager.get("atlas/textures.pack.atlas", TextureAtlas.class);

        // create the UI
        TextButton btn = new TextButton("MainMenu", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        btn.setPosition(850, 20);
        btn.setSize(100, 50);
        stage.addActor(btn);

        lbl_fps = new Label("FPS: 0", skin);
        lbl_fps.setPosition(850, 90);
        stage.addActor(lbl_fps);

        mbatch = new ModelBatch();
        mbuilder = new ModelBuilder();

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));

        mbuilder.begin();
        mbuilder.node().id = "ground";
        mbuilder.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(5f, 1f, 5f);
        mbuilder.node().id = "sphere";
        mbuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        mbuilder.node().id = "box";
        mbuilder.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mbuilder.node().id = "cone";
        mbuilder.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mbuilder.node().id = "capsule";
        mbuilder.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mbuilder.node().id = "cylinder";
        mbuilder.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        model = mbuilder.end();

        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors.put("ground", new GameObject.Constructor(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f)), 0f));
        constructors.put("sphere", new GameObject.Constructor(model, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box", new GameObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder", new GameObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));

        contactListener = new MyContactListener();

        // create the table static object line by line...
        JsonReader jsonReader = new JsonReader();
        G3dModelLoader mloaderg3d = new G3dModelLoader(jsonReader);
        Model mdl = mloaderg3d.loadModel(Gdx.files.internal("meshes/testtableD.g3dj"));
        ModelInstance inst = new PoolTable(mdl, "Bed", 20);
//        ModelInstance inst = new PoolTable(model);
//        inst.transform.setToTranslation(0, -3f, 0);
//        ((PoolTable)inst).updateMatrix();
        btRigidBody b = ((PoolTable)inst).body;
        b.setUserValue(0);
        b.setCollisionFlags(b.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        b.setContactCallbackFlag(GROUND_FLAG);
        b.setContactCallbackFilter(0);
        b.setActivationState(Collision.DISABLE_DEACTIVATION);
        instances.put("table", inst);
        dynamicsWorld.addRigidBody(b);

        // create the static object line-by-line...
//        Ground object = new Ground(model);
//        object.body.setUserValue(0);
//        object.body.setCollisionFlags(object.body.getCollisionFlags()
//                | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
//        object.body.setContactCallbackFlag(GROUND_FLAG);
//        object.body.setContactCallbackFilter(0);
//        object.body.setActivationState(Collision.DISABLE_DEACTIVATION);
//        instances.put("ground", object);
//        dynamicsWorld.addRigidBody(object.body);

        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        debugDrawer = new DebugDrawer();
        dynamicsWorld.setDebugDrawer(debugDrawer);
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

    }

    int id = 1;
    float spawnTimer;

    public void spawn() {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.put("obj_"+id, obj);
        dynamicsWorld.addRigidBody(obj.body);
        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);
        id++;
    }

    float angle, speed = 90f;

    @Override
    public void render(float del) {
        Gdx.gl.glClearColor(.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // update the game objects
        final float delta = Math.min(1f / 30f, del);

        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);

        if ((spawnTimer -= delta) < 0) {
            spawn();
            spawnTimer = 1.5f;
        }

        // draw the game objects
        mbatch.begin(camera);
        mbatch.render(instances.values(), env);
        mbatch.end();

        debugDrawer.begin(camera);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();

        // manage the UI
        stage.act(delta);
        stringBuilder.setLength(0);
        stringBuilder.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append("  CNT: ").append(instances.size);
        lbl_fps.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
        camera = new PerspectiveCamera(35f, 18f*aspectRatio, 18f); //TODO: decide on world size
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,10f);
        camera.lookAt(0,0,0);
        camera.near = 0.1f;
        camera.far = 1000f;

        camController = new CameraInputController(camera);
        inputMux.addProcessor(camController);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        atlas.dispose();
        mbatch.dispose();
        model.dispose();
        instances.clear();

        for (ModelInstance obj : instances.values()) {
            if (obj instanceof PoolBall) ((PoolBall)obj).dispose();
            if (obj instanceof PoolTable) ((PoolTable)obj).dispose();
            if (obj instanceof GameObject) ((GameObject)obj).dispose();
        }
        instances.clear();

        for (GameObject.Constructor ctor : constructors.values())
            ctor.dispose();
        constructors.clear();

        contactListener.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
    }
}
