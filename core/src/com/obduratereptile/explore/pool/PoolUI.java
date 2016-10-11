package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.obduratereptile.explore.MainMenuScreen;

/**
 * Created by Marc on 10/5/2016.
 */
public class PoolUI {
    public enum Placement {LL, LR, UL, UR, ML, MR, LM, UM}

    public PoolPlayScreen screen;
    public AssetManager manager;
    public Skin skin;
    public TextureAtlas atlas;
    public Stage stage;

    static private final float ZOOMMAX = 1.0f;
    static private final float ZOOMMIN = 100.0f;

    public PoolUI(PoolPlayScreen screen) {
        this.screen = screen;
        this.manager = screen.manager;
        this.skin = screen.skin;
        this.atlas = screen.atlas;
        this.stage = screen.stage;
    }

    public void createCameraControls(Placement p) {
        //TODO: camera zoom/pan controls
        //  fix bounds checking on zoom, pan
        //  zoom/pan based on camera view, not camera position
        //  don't pan into the weeds
        //  recenter the table as we zoom out
        //  fix zoomin/zoomout limits

        ImageButton btnZoomIn = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedZoomIn")),
                new SpriteDrawable(atlas.createSprite("btnRedZoomInPressed"))
        ) {
            static final private float ZOOMSPEED = 10;

            @Override
            public void act(float delta) {
                super.act(delta);
                if (isPressed()) {
                    Vector3 p = screen.camera.position;
                    p.y -= ZOOMSPEED * delta;
                    if (p.y < ZOOMMAX) return;
                    screen.camera.position.set(p);
                }
            }
        };

        ImageButton btnZoomOut = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedZoomOut")),
                new SpriteDrawable(atlas.createSprite("btnRedZoomOutPressed"))
        ) {
            static final private float ZOOMSPEED = 10;

            @Override
            public void act(float delta) {
                super.act(delta);
                if (isPressed()) {
                    Vector3 p = screen.camera.position;
                    p.y += ZOOMSPEED * delta;
                    if (p.y > ZOOMMIN) return;
                    screen.camera.position.set(p);
                }
            }
        };

        Touchpad touchPan = new Touchpad(5, skin, "default") {
            static final private float PANSPEED = 10;

            @Override
            public void act(float delta) {
                super.act(delta);

                Vector3 cameraPan = new Vector3(getKnobPercentX()*PANSPEED, 0, -getKnobPercentY()*PANSPEED);
                screen.camera.position.add(cameraPan.scl(delta));
            }
        };
        touchPan.getStyle().background = new SpriteDrawable(atlas.createSprite("touchpadBackground"));
        touchPan.getStyle().knob = new SpriteDrawable(atlas.createSprite("touchpadKnob"));

        float w = stage.getWidth();
        float h = stage.getHeight();

        btnZoomIn.setSize(50, 50);
        btnZoomOut.setSize(50, 50);
        touchPan.setSize(100, 100);

        switch (p) {
            //TODO: add the other cases
            case UL:
            default:
                btnZoomIn.setPosition(30, h-70);
                btnZoomOut.setPosition(120, h-70);
                touchPan.setPosition(50, h-160);
                break;
        }

        stage.addActor(btnZoomIn);
        stage.addActor(btnZoomOut);
        stage.addActor(touchPan);
    }

    private Table menuPopup;

    public void createMenuBtn(Placement p) {
        ImageButton btnMenu = new ImageButton(
                new SpriteDrawable(atlas.createSprite("btnRedMenu")),
                new SpriteDrawable(atlas.createSprite("btnRedMenuPressed"))
        );

        menuPopup = new Table();
        //TODO: implement callbacks for these buttons
        TextButton btn;

        btn = new TextButton("Save Table", skin, "default");
        menuPopup.add(btn).fillX().row();

        btn = new TextButton("Load Table", skin, "default");
        menuPopup.add(btn).fillX().row();

        btn = new TextButton("Edit Mode", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.poolInputAdapter.state = PoolInputAdapter.State.EDIT;
                screen.pause = true;
                editPopup.setVisible(true);
                menuPopup.setVisible(false);
            }
        });
        menuPopup.add(btn).fillX().row();

        btn = new TextButton("Toggle Control Locations", skin, "default");
        menuPopup.add(btn).fillX().row();

        btn = new TextButton("Enable target lines", skin, "default");
        menuPopup.add(btn).fillX().row();

        menuPopup.setVisible(false);
        stage.addActor(menuPopup);

        float w = stage.getWidth();
        float h = stage.getHeight();

        btnMenu.setSize(50, 50);

        //TODO: implement other placement cases
        switch (p) {
            case UR:
            default:
                btnMenu.setPosition(w-70, h-70);
                menuPopup.setPosition(w-150, h-150);
                break;
        }

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuPopup.setVisible(!menuPopup.isVisible());
            }
        });

        stage.addActor(btnMenu);
    }

    private Table editPopup;

    public void createEditControls(Placement p) {
        editPopup = new Table();
        //TODO: implement callbacks for these buttons
        TextButton btn;

        btn = new TextButton("Rack 'em up (8ball)", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector3 position = new Vector3();

                float dia = ((PoolBall)screen.instances.get("ballcue")).radius * 2.0f;
                float sin = dia * MathUtils.sinDeg(30);
                float cos = dia * MathUtils.cosDeg(30);
                Vector3 rowOffset = new Vector3(cos, 0, sin);
                Vector3 colOffset = new Vector3(0, 0, -dia);

                position.set(-20.8f, 0, 0); // head spot
                screen.poolPhysics.showBall(0, position);

                position.set(20.8f, 0, 0); // foot spot
                screen.poolPhysics.showBall(1, position);

                position.add(rowOffset);
                screen.poolPhysics.showBall(15, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(2, position);

                rowOffset.z *= -1f;
                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(14, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(8, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(13, position);

                rowOffset.z *= -1f;
                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(3, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(12, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(4, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(11, position);

                rowOffset.z *= -1f;
                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(5, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(10, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(6, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(9, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(7, position);
            }
        });
        editPopup.add(btn).fillX().row();

        btn = new TextButton("Rack 'em up (9ball)", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Vector3 position = new Vector3();

                float dia = ((PoolBall)screen.instances.get("ballcue")).radius * 2.0f;
                float sin = dia * MathUtils.sinDeg(30);
                float cos = dia * MathUtils.cosDeg(30);
                Vector3 rowOffset = new Vector3(cos, 0, sin);
                Vector3 colOffset = new Vector3(0, 0, -dia);

                for (int i=10; i<16; i++) {
                    screen.poolPhysics.hideBall(i);
                }

                position.set(-20.8f, 0, 0); // head spot
                screen.poolPhysics.showBall(0, position);

                position.set(20.8f, 0, 0); // foot spot
                screen.poolPhysics.showBall(1, position);

                position.add(rowOffset);
                screen.poolPhysics.showBall(2, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(3, position);

                rowOffset.z *= -1f;
                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(5, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(9, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(4, position);

                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(6, position);
                position.add(colOffset);
                screen.poolPhysics.showBall(7, position);

                rowOffset.z *= -1f;
                colOffset.z *= -1f;
                position.add(rowOffset);
                screen.poolPhysics.showBall(8, position);
            }
        });
        editPopup.add(btn).fillX().row();

        btn = new TextButton("Toggle snap", skin, "default");
        editPopup.add(btn).fillX().row();

        btn = new TextButton("Exit Edit Mode", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.poolInputAdapter.state = PoolInputAdapter.State.DISABLED;

                for (int i=0; i<16; i++) {
                    String key = (i==0)? "ballcue": "ball"+i;
                    PoolBall ball = (PoolBall)screen.instances.get(key);
                    //TODO: these don't seem to do anything
                    ball.body.setLinearVelocity(new Vector3(0,0,0));
                    ball.body.setAngularVelocity(new Vector3(0,0,0));
                    ball.updateMatrix();
                }

                screen.pause = false;
                editPopup.setVisible(false);
            }
        });
        editPopup.add(btn).fillX().row();

        float w = stage.getWidth();
        float h = stage.getHeight();

        //TODO: implement other placement cases
        switch (p) {
            case ML:
            default:
                editPopup.setPosition(120, h/2);
                break;
        }

        editPopup.setVisible(false);
        stage.addActor(editPopup);
    }

    private Table shotPopup;

    public void createShotControls(Placement p) {
        //TODO: implement callbacks for all these actors
        TextButton btnShoot = new TextButton("Shoot", skin, "default");
        TextButton btnCancel = new TextButton("Cancel", skin, "default");

        Label lblEnglish = new Label("English", skin, "default");
        lblEnglish.setAlignment(Align.bottom);

        Touchpad touchEnglish = new Touchpad(5, skin, "default");
        touchEnglish.getStyle().background = new SpriteDrawable(atlas.createSprite("touchpadBackground"));
        touchEnglish.getStyle().knob = new SpriteDrawable(atlas.createSprite("touchpadKnob"));

        Label lblAngle = new Label("Cue Angle", skin, "default");
        lblAngle.setAlignment(Align.bottom);
        
        Touchpad touchAngle = new Touchpad(5, skin, "default");
        touchAngle.getStyle().background = new SpriteDrawable(atlas.createSprite("touchpadBackground"));
        touchAngle.getStyle().knob = new SpriteDrawable(atlas.createSprite("touchpadKnob"));

        Label lblPower = new Label("Shot Power", skin, "default");
        lblPower.setAlignment(Align.bottom);

        Slider sliderPower = new Slider(0, 100, 1, false, skin);
        sliderPower.getStyle().background = new SpriteDrawable(atlas.createSprite("sliderThermBackground"));
        sliderPower.getStyle().knob = new SpriteDrawable(atlas.createSprite("sliderThermKnob"));

        shotPopup = new Table(skin);
        shotPopup.add(btnShoot).fillX();
        shotPopup.add(btnCancel).fillX().row();
        shotPopup.add(lblEnglish).expandX().fillX();
        shotPopup.add(lblAngle).expandX().fillX().row();
        shotPopup.add(touchEnglish);
        shotPopup.add(touchAngle).row();
        shotPopup.add(lblPower).colspan(2).expandX().fillX().row();
        shotPopup.add(sliderPower).colspan(2).fillX();

        float w = stage.getWidth();
        float h = stage.getHeight();
        
        Vector2 position = new Vector2();
        switch (p) {
            //TODO: add the other cases
            case ML:
            default:
                shotPopup.setPosition(120, h/2);
                break;
        }

        shotPopup.setVisible(false);
        stage.addActor(shotPopup);
    }

    private Label status;

    public void createStatusLine() {
        status = new Label("", skin, "default");
        status.setAlignment(Align.bottom);
        status.setWrap(true);

        float w = stage.getWidth();
        float h = stage.getHeight();

        // This will always be LM position
        status.setSize(w*0.8f, 100);
        status.setPosition(w*0.1f, 10);

        stage.addActor(status);
    }

    public void setStatus(String s) {
        status.setText(s);
    }

    private Table debugMenu;
    private Label fps;

    public void createDebugMenu(Placement p) {
        debugMenu = new Table();
        //TODO: debug why button width is messed up
        //TODO: implement callbacks for these buttons
        TextButton btn;

        fps = new Label("", skin, "default") {
            @Override
            public void act(float delta) {
                this.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
            }
        };
        fps.setAlignment(Align.center);
        debugMenu.add(fps).fillX().row();

        btn = new TextButton("Debug", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.debug = !screen.debug;
            }
        });
        debugMenu.add(btn).fillX().row();

        btn = new TextButton("Normal", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Experiment - load a normal map onto one of the corner pockets
                Material mat = screen.instances.get("table").getNode("PocketJacket", true).parts.get(0).material;
                if (mat.has(TextureAttribute.Normal)) {
                    Gdx.app.error("EX", "Removing normal texture attribute");
                    mat.remove(TextureAttribute.Normal);
                } else {
                    Gdx.app.error("EX", "Setting normal texture attribute");
                    TextureAttribute tA = new TextureAttribute(TextureAttribute.Normal, new Texture(Gdx.files.internal("meshes/brownleatherNormal.jpg")));
                    mat.set(tA);
                }
                Gdx.app.error("EX", "isChecked() == " + ((TextButton)event.getListenerActor()).isChecked());
            }
        });
        debugMenu.add(btn).fillX().row();

        btn = new TextButton("Main Menu", skin, "default");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.game.setScreen(new MainMenuScreen(screen.game));
            }
        });
        debugMenu.add(btn).fillX().row();

        float w = stage.getWidth();
        float h = stage.getHeight();

        //TODO: implement other placement cases
        switch (p) {
            case LR:
            default:
                debugMenu.setPosition(w-100, 120);
                break;
        }

        stage.addActor(debugMenu);
    }
}
