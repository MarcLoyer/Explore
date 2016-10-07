package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

/**
 * Created by Marc on 9/29/2016.
 */
public class XoppaScreen implements Screen {
    ExploreGame game;
    AssetManager manager;
    Skin skin;
    InputMultiplexer inputMux;

    Stage stage;
    StringBuilder stringBuilder;
    Label lbl_fps;
    PerspectiveCamera camera;
    CameraInputController camController;

    public Shader shader;
    public RenderContext renderContext;
    public Model model;
    public Environment environment;
    public Renderable renderable;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public ModelBatch modelBatch;

    public XoppaScreen(ExploreGame g) {
        game = g;
        manager = g.manager;
        skin = g.skin;
        stringBuilder = new StringBuilder();

        inputMux = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMux);
    }

    private void createStage(int w, int h) {
        //TODO: support smaller screens
        float aspectRatio = (float)h / (float)w;
        int x = 1000;
        int y = (int)(1000.0f * aspectRatio);
        stage = new Stage(new FitViewport(x, y));
    }

    public static class TestColorAttribute extends ColorAttribute {
        public final static String DiffuseUAlias = "diffuseUColor";
        public final static long DiffuseU = register(DiffuseUAlias);

        public final static String DiffuseVAlias = "diffuseVColor";
        public final static long DiffuseV = register(DiffuseVAlias);

        static {
            Mask = Mask | DiffuseU | DiffuseV;
        }

        public TestColorAttribute (long type, float r, float g, float b, float a) {
            super(type, r, g, b, a);
        }
    }

    @Override
    public void show() {
        createStage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        inputMux.addProcessor(stage);

        // Build a simple model, convert it to a renderable, and pass it to our shader...
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20,
                new Material(),
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);

//        NodePart blockPart = model.nodes.get(0).parts.get(0);
//        renderable = new Renderable();
//        blockPart.setRenderable(renderable);
//        renderable.environment = null;
//        renderable.worldTransform.idt();
        for (int x = -5; x <= 5; x+=2) {
            for (int z = -5; z<=5; z+=2) {
                ModelInstance instance = new ModelInstance(model, x, 0, z);
                ColorAttribute attrU = new TestColorAttribute(TestColorAttribute.DiffuseU, (x+5f)/10f, 1f - (z+5f)/10f, 0, 1);
                instance.materials.get(0).set(attrU);
                ColorAttribute attrV = new TestColorAttribute(TestColorAttribute.DiffuseV, 1f - (x+5f)/10f, 0, (z+5f)/10f, 1);
                instance.materials.get(0).set(attrV);
                instances.add(instance);
            }
        }

        // debug code to just show the vertices
        //renderable.meshPart.primitiveType = GL20.GL_POINTS;

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        // Use the default shader (boring white circle)
        //shader = new DefaultShader(renderable);

        // Use a custom shaderprogram (red/green colored circle)
        //String vert = Gdx.files.internal("data/test.vertex.glsl").readString();
        //String frag = Gdx.files.internal("data/test.fragment.glsl").readString();
        //shader = new DefaultShader(renderable, new DefaultShader.Config(vert, frag));

        // Use a custom shader
        shader = new XoppaShader();

        shader.init();

        modelBatch = new ModelBatch();

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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();
        camera.update();

        // use our shader to draw the model...
//        renderContext.begin();
//        shader.begin(camera, renderContext);
//        shader.render(renderable);
//        shader.end();
//        renderContext.end();
        modelBatch.begin(camera);
        for (ModelInstance instance : instances)
            modelBatch.render(instance, shader);
        modelBatch.end();

        // manage the UI
        stage.act(delta);
        stringBuilder.setLength(0);
        lbl_fps.setText(stringBuilder.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()));
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
        shader.dispose();
        model.dispose();
        modelBatch.dispose();
    }
}
