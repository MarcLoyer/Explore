package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by Marc on 9/15/2016.
 */
public class PlatformerScreen implements Screen {
    ExploreGame game;
    AssetManager manager;
    Skin skin;
    SpriteBatch batch;

    Stage stage;
    OrthographicCamera camera;

    TextureAtlas atlas;
    World world;
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    com.obduratereptile.explore.platformer.Character percy;
    Body percyBody;

    public int WorldWidth = 220;
    public int WorldHeight = 18;

    public PlatformerScreen(ExploreGame g) {
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
        Gdx.input.setInputProcessor(stage);

        atlas = manager.get("atlas/textures.pack.atlas", TextureAtlas.class);

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

        ImageButton btnImage = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedArrowLeft")),
                new SpriteDrawable(atlas.createSprite("btnRedArrowLeftPressed"))
        );
        btnImage.setPosition(50, 20);
        btnImage.setSize(50, 50);
        btnImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                percy.setSpeed(-3);
            }
        });
        stage.addActor(btnImage);

        btnImage = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedStop")),
                new SpriteDrawable(atlas.createSprite("btnRedStopPressed"))
        );
        btnImage.setPosition(120, 20);
        btnImage.setSize(50, 50);
        btnImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                percy.setSpeed(0);
            }
        });
        stage.addActor(btnImage);

        btnImage = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedArrowRight")),
                new SpriteDrawable(atlas.createSprite("btnRedArrowRightPressed"))
        );
        btnImage.setPosition(190, 20);
        btnImage.setSize(50, 50);
        btnImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                percy.setSpeed(3);
            }
        });
        stage.addActor(btnImage);

        btnImage = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedArrowUp")),
                new SpriteDrawable(atlas.createSprite("btnRedArrowUpPressed"))
        );
        btnImage.setPosition(120, 90);
        btnImage.setSize(50, 50);
        btnImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                percy.jump(9);
            }
        });
        stage.addActor(btnImage);

        map = new TmxMapLoader().load("TiledMaps/TileKitDemo.tmx");
        WorldWidth = map.getProperties().get("width", Integer.class);
        WorldHeight = map.getProperties().get("height", Integer.class);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/32f, batch);

        percy = new com.obduratereptile.explore.platformer.Character();
        percy.standing = atlas.createSprite("stand");
        percy.jumping = atlas.createSprite("jumping");
        percy.walking = new Animation(0.2f, atlas.createSprites("walking"), Animation.PlayMode.LOOP);
        percy.setPosition(2, 3);
        percy.setSize(1.0f, 2.0f);
        percy.setMap(map);

        // We use the Box2d stuff for collision detection only - we do not actually
        // simulate the world.
        /*
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        MapBodyManager mapBodyManager = new MapBodyManager(world, 1/32f, null, 0);
        percyBody = createBody(percy);
        */
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        mapRenderer.setView(camera);
        mapRenderer.render();

        percy.act(delta);
        /*
        deleteBody(percyBody);
        createBody(percy);
        */

        batch.begin();
        percy.draw(batch, 1);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    public Body createBody(Actor a) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(a.getX(), a.getY());
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(a.getWidth()/2, a.getHeight()/2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public void deleteBody(Body body) {
        world.destroyBody(body);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width/(float)height;
        stage.getViewport().update(width, height, false);
        camera = new OrthographicCamera(18f*aspectRatio, 18f);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
        percy.addListener(new com.obduratereptile.explore.platformer.Character.CharacterListener() {
            public void moved(float x, float y) {
                // don't allow percy to move off the ends of the map
                if (x<0)
                    percy.setX(0);
                if (x>(WorldWidth-percy.getWidth()))
                    percy.setX(WorldWidth-percy.getWidth());

                // if percy fell off the map, reset to the beginning
                if (y<-2) {
                    percy.state= com.obduratereptile.explore.platformer.Character.State.STANDING;
                    percy.velocity.set(0,0);
                    percy.setPosition(2, 3);
                    x=2; // so the camera updates properly
                }

                camera.position.x = x;
                camera.position.x = MathUtils.clamp(camera.position.x, camera.viewportWidth/2, WorldWidth-camera.viewportWidth/2);
            }
        });
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
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
    }
}
