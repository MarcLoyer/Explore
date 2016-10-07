package com.obduratereptile.explore;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ExploreGame extends Game {
	public AssetManager manager;
	public Skin skin;
	public SpriteBatch batch;
	public Texture img;
	
	@Override
	public void create () {
		manager = new AssetManager();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		setScreen(new SplashScreen(this));
	}

	@Override
	public void dispose () {
		skin.dispose();
		batch.dispose();
		img.dispose();
	}
}
