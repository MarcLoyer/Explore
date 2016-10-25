package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.explore.ExploreGame;
import com.obduratereptile.explore.MainMenuScreen;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.*;

/**
 * Created by Marc on 9/19/2016.
 */
public class PoolPlayScreen implements Screen {
    ExploreGame game;
    AssetManager manager;
    Skin skin;
    SpriteBatch batch;
    InputMultiplexer inputMux;

    ModelBatch mbatch;
    ShaderProvider shader;
    ModelBuilder mbuilder;
    ObjLoader mloader;
    Model mdl;
    ArrayMap<String, ModelInstance> instances;
    ArrayMap<String, ModelInstance> balls;
    ArrayMap<String, ModelInstance> otherObjects;
    Environment env;
    //experimenting with shadows...
    DirectionalShadowLight shadowLight;
    Vector3 shadowDirection;
    ModelBatch shadowBatch;

    Stage stage;
    PoolUI poolUI;
    PerspectiveCamera camera;
    PoolInputAdapter poolInputAdapter;
    CameraInputController camController;

    TextureAtlas atlas;

    public TweenManager tween;
    PoolPhysics poolPhysics;

    boolean debug = false;
    DebugDrawer debugDrawer;
    boolean pause = true;
    final float SPINSPEED = 90;
    boolean spin = false;

    //TODO: recode to use bullet physics (check that spin behavior works)
    //TODO: add save/load functions
    //TODO: add table setup capability (move balls individually, rack'em shortcut, remove balls,
    //   snap-to neighbor balls, etc)
    //TODO: revamp camera motion:
    //   default is top view of the whole table
    //   when a player selects a shot, move the camera down over the cueball, looking at the target ball
    //   if the player cancels, back to top view
    //   if the player hasn't selected a shot, allow moving the camera around the table (zooming to
    //     different points on the table, rotating away from top view, etc.
    //TODO: shot mechanics
    //   add tinted transparent cuesticks
    //   set stick angle and offset (this is what cause ball spin)
    //   set strike force
    //   show aiming lines?
    //   allow camera adjustments
    //TODO: allow different games: 8ball, 9ball, knockout, ???
    //   foul detection & ball-in-hand/penalty spotting support
    //   score keeping?
    //TODO: add game services features:
    //   network turnbased play
    //   awards/accomplishments
    //TODO: sound effects
    //   stick-cueball collision (hard and soft)
    //   ball-ball collision (hard and soft)
    //   ball-cushion collision (hard and soft)
    //   ball-pocket collision (hard and soft)
    public PoolPlayScreen(ExploreGame g) {
        game = g;
        manager = g.manager;
        skin = g.skin;
        batch = g.batch;

        inputMux = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMux);

        instances = new ArrayMap<String, ModelInstance>();
        balls = new ArrayMap<String, ModelInstance>();
        otherObjects = new ArrayMap<String, ModelInstance>();

        shadowDirection = new Vector3(-1f, -.8f, -.2f);

        poolInputAdapter = new PoolInputAdapter(this);
        inputMux.addProcessor(poolInputAdapter);

        tween = new TweenManager();
        Tween.registerAccessor(CueStick.class, new CueStickAccessor());
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
        poolUI = new PoolUI(this);

        ImageButton btnImage = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedPlay")),
                new SpriteDrawable(atlas.createSprite("btnRedPlayPressed")),
                new SpriteDrawable(atlas.createSprite("btnRedPause"))
        );
        btnImage.setPosition(50, 20);
        btnImage.setSize(50, 50);
        btnImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ImageButton btn = (ImageButton)event.getListenerActor();
                //spin = btn.isChecked();
                pause = !btn.isChecked();
            }
        });
        stage.addActor(btnImage);

        poolUI.createCameraControls(PoolUI.Placement.UL);
        poolUI.createMenuBtn(PoolUI.Placement.UR);
        poolUI.createEditControls(PoolUI.Placement.ML);
        poolUI.createShotControls(PoolUI.Placement.ML);
        poolUI.createStatusLine();
        poolUI.createDebugMenu(PoolUI.Placement.LR);

        // create the game objects
        PoolPhysics.init();

        shader = new DefaultShaderProvider(); // TODO: roll my own so I can get normal mapping working
        mbatch = new ModelBatch(shader);
        mbuilder = new ModelBuilder();
        mloader = new ObjLoader();

        //Notes on exporting blender models:
        //   Objects will be placed where they existed in blender, so move stuff to the origin
        //  before you export.
        //   Select the objects you want to export, Ctrl-A>Scale to bake scale factors into the
        // geometry (bullet collision objects don't read scale, so this needs to be baked into
        // all exported models)
        // File>Export>FBX
        //   - "Main"
        //   - "Selected Objects" CHECKED
        //   - Scale: 0.01
        //   - Forward: -X
        //   - Up: Z
        //   - Empty|Camera|Lamp|Armature|Mesh|Other all blue (selected)
        //   (If one clicks one of these, one can not get more than one selected; one must reset
        //   all the options to defaults to get them all selected again)
        // Once the FBX file is generated, convert to g3db file via:
        //   fbx-conv-win32.exe -f filename.fbx
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader mloaderg3d = new G3dModelLoader(jsonReader);

        // Our world scale is 1 unit = 1 inch.
        mdl = mloaderg3d.loadModel(Gdx.files.internal("meshes/pool.g3db"));
        ModelInstance inst;

        // We generate all the balls from a single mesh by assigning the texture of each instance.
        TextureAttribute tA;
        for (int i=0; i<16; i++) {
            String id = (i==15)? "ballcue": "ball"+(i+1);
            tA = new TextureAttribute(TextureAttribute.Diffuse, atlas.createSprite(id));
            inst = new PoolBall(mdl, "ball", i, tA);

//            float offset = 4.0f; // 4 inches
//            float x = -1.5f*offset + ((i%4) * offset);
//            float z = -1.5f*offset + ((i/4) * offset);
//            inst.transform.setToTranslation(x, 0, z);
//            ((PoolBall)inst).updateMatrix();

            balls.put(id, inst);
            instances.put(id, inst);
        }

        // TODO: Notes on getting the model scaled right:
        // http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Scaling_The_World
        // changing the step interval seemed to help. I'm currently running at 1/240s.
        // I suspect the large triangles on the Table Bed are a problem. I'll have to
        // try breaking those up. (But then why doesn't XoppaBullet2 have the same issue?)
        // I could also try scaling the world to 10in instead of 1in - that would get
        // the table size down to ~8.
        //
        // Update:
        //  I currently have the world scale set to 10 inches, and I've broken up the Table
        // Bed into a grid of triangles. None of it made any difference.
        //  Friction/rolling friction/restitution have the most effect, but they also cause
        // the balls to not react properly when hit.
        //  Maybe damping?
        // Update again:
        //  I reverted to the original scaling (1 inch) and reverted to the undivided table bed.
        inst = new PoolTable(mdl, "Bed", 20);
        otherObjects.put("table", inst);
        instances.put("table", inst);

        inst = new CueStick(mdl, "CueStick");
        inst.transform.setToTranslation(0, -10, -10); // get it out of the way
        ((CueStick)inst).updateMatrix();
        ((CueStick)inst).setColor(.0f, .0f, 1.0f);
        otherObjects.put("cuestick", inst);
        instances.put("cuestick", inst);

        inst = new ModelInstance(mdl, "HeadArea");
        otherObjects.put("headArea", inst);
        inst = new ModelInstance(mdl, "BedArea");
        otherObjects.put("bedArea", inst);
        //instances.put("headArea", inst);

        // TODO: lookup later:
        //  * libGDX FirstPersonCameraController
        //  * libGDX ScreenshotFactory

        // add physics simulator...
        poolPhysics = new PoolPhysics(this);

        poolUI.rackBalls(PoolUI.EIGHTBALL);


        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));

        // experimenting with shadows...
        float w = (float) stage.getViewport().getScreenWidth();
        float h = (float) stage.getViewport().getScreenHeight();
        float a = w/h;
        shadowLight = new DirectionalShadowLight((int)w, (int)h, 18f*a, 18f, .1f, 50f);
        env.add((shadowLight).set(Color.WHITE, shadowDirection));
        env.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        debugDrawer = new DebugDrawer();
        poolPhysics.dynamicsWorld.setDebugDrawer(debugDrawer);
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

        // debug ... remove the table to be able to see the collision objects better
        //instances.removeKey("table");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // update the game objects
        if (spin) {
            float deg = delta * SPINSPEED;
            for (int i=0; i<16; i++) {
                String id = (i==15)? "ballcue": "ball"+(i+1);
                Vector3 axis = new Vector3((i%2)*2-1, ((i%4)/2)*2-1, ((i%8)/4)*2-1);
                instances.get(id).transform.translate(0, 1, 0).rotate(axis, deg).translate(0, -1, 0);
            }

            // Experimenting with shadows...
            shadowLight.setDirection(shadowDirection.rotate(deg, 0, 1, 0));
        }
        tween.update(delta);

        // adding physics :)
        if (!pause) poolPhysics.act(delta);

        // Experimenting with shadows...
        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(instances.values());
        shadowBatch.end();
        shadowLight.end();

        // draw the game objects
        mbatch.begin(camera);
        mbatch.render(instances.values(), env);
        mbatch.end();

        if (debug) {
            debugDrawer.begin(camera);
            poolPhysics.dynamicsWorld.debugDrawWorld();
            debugDrawer.end();
        }

        // manage the UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
        camera = new PerspectiveCamera(35f, 18f*aspectRatio, 18f); //TODO: decide on world size
        camera.position.set(0f, 90.0f, 0f);
        //camera.direction.set(0f, 1f, 0f);
        camera.lookAt(0,0,0);
        camera.near = 0.1f;
        camera.far = 300f;

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
        mdl.dispose();
        mbatch.dispose();
        instances.clear();

        shadowLight.dispose();
        shadowBatch.dispose();
    }

    public TableState createTableState() {
        TableState t = new TableState();
        for (int i=0; i<16; i++) {
            String key = (i==0)? "ballcue": "ball"+i;
            ModelInstance inst = instances.get(key);
            if (inst == null) continue;
            t.put(key, inst.transform);
        }
        return t;
    }

    public void save(TableState t, String filename) {
        t.save(filename);
    }

    public void load(String filename) {
        TableState t = TableState.load(filename);

        for (int i=0; i<16; i++) {
            String key = (i==0)? "ballcue": "ball"+i;
            Matrix4 tval = t.get(key);

            if (tval == null) { // the ball is not listed in the tableState, so hide it
                poolPhysics.hideBall(i);
            } else { // the ball is listed, so show it
                Vector3 v = new Vector3();
                balls.get(key).transform.getTranslation(v);
                poolPhysics.showBall(i, v);
            }
        }
    }
}
