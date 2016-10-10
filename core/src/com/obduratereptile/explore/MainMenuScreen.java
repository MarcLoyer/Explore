package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.explore.pool.PoolPlayScreen;

/**
 * Created by Marc on 9/15/2016.
 */
public class MainMenuScreen implements Screen {
    ExploreGame game;
    Skin skin;
    SpriteBatch batch;
    TextureAtlas atlas;

    Stage stage;
    OrthographicCamera camera;

    public MainMenuScreen(ExploreGame g) {
        super();
        game = g;
        skin = game.skin;
        batch = game.batch;
    }

    /*
    Creates a stage with a convenient viewport size. (That is, the default font will be
    readable). Large screens will be 1000 wide, smaller screens will be smaller. All screens
    will have the aspect ratio of the given arguments.
    */
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
        Gdx.input.setInputProcessor(stage);

        atlas = game.manager.get("atlas/textures.pack.atlas", TextureAtlas.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton btn = new TextButton("Play example platformer", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlatformerScreen(game));
            }
        });
        table.add(btn).fillX().row();

        btn = new TextButton("Play Pool 3D game", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PoolPlayScreen(game));
            }
        });
        table.add(btn).fillX().row();

        btn = new TextButton("Xoppa", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new XoppaScreen(game));
            }
        });
        table.add(btn).fillX().row();

        btn = new TextButton("Xoppa Bullet", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new XoppaBulletScreen(game));
            }
        });
        table.add(btn).fillX().row();

        btn = new TextButton("Xoppa Bullet 2", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new XoppaBulletScreen2(game));
            }
        });
        table.add(btn).fillX().row();

        // TODO: fix the skin
        Button.ButtonStyle btnStyle = btn.getStyle();
        btnStyle.up = new NinePatchDrawable(atlas.createPatch("btnRedText"));
        btnStyle.down = new NinePatchDrawable(atlas.createPatch("btnRedTextPressed"));
        btn.setStyle(btnStyle);

    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (!game.manager.update()) {
            // still loading assets...
        } else {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
        camera = new OrthographicCamera(10f*aspectRatio, 10f);
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
        stage.dispose();
    }
}
