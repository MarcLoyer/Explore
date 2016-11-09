package com.obduratereptile.explore.Mapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.explore.ExploreGame;
import com.obduratereptile.explore.MainMenuScreen;

/**
 * Created by Marc on 10/28/2016.
 */

public class MapperScreen implements Screen {
    public ExploreGame game;
    public AssetManager manager;
    public Skin skin;
    public SpriteBatch batch;
    public TextureAtlas atlas;
    public InputMultiplexer inputMux;
    public Stage stage;
    public OrthographicCamera camera;
    //TODO: implement OrthoCamController (https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/bench/TiledMapBench.java)
    public CameraInputController camController;

    public TiledMap map;
    public TiledMapRenderer mapRenderer;

    public MapperScreen(ExploreGame g) {
        game = g;
        manager = g.manager;
        skin = g.skin;
        batch = g.batch;

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

    @Override
    public void show() {
        createStage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        inputMux.addProcessor(stage);

        atlas = game.manager.get("atlas/textures.pack.atlas", TextureAtlas.class);

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

        // create a map...
        map = new MyTiledMap(new Texture(Gdx.files.internal("TiledMaps/tileset.png")), 30, 20, 32, 37, 20);
//        map = new TmxMapLoader().load("TiledMaps/test.tmx");


        mapRenderer = new HexagonalTiledMapRenderer(map, 1/32f, batch);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);

        // update the scene

        // render the scene
        mapRenderer.render();

        batch.begin();
        batch.end();

        // manage the UI
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
        camera = new OrthographicCamera(18f*aspectRatio, 18f); //TODO: decide on world size
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

//        camController = new CameraInputController(camera);
//        inputMux.addProcessor(camController);
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
    }
}
