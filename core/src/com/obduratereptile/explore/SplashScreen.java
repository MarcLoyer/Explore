package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by Marc on 9/19/2016.
 */
public class SplashScreen implements Screen {
    ExploreGame game;
    AssetManager manager;
    Skin skin;
    SpriteBatch batch;

    Stage stage;
    Image logo;

    SplashScreen(ExploreGame g) {
        game = g;
        manager = g.manager;
        skin = g.skin;
        batch = g.batch;
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

        logo = new Image(new Texture(Gdx.files.internal("images/obduratereptile.png")));
        stage.addActor(logo);

        //TODO: figure out how to update my skin properly

        // queue the assets for loading...
        manager.load("atlas/textures.pack.atlas", TextureAtlas.class);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.4f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (manager.update()) {
            game.setScreen(new MainMenuScreen(game));
        } else {
            // still loading assets...
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
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
