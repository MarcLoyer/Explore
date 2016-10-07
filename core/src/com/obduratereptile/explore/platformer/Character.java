package com.obduratereptile.explore.platformer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import static com.obduratereptile.explore.platformer.Character.State.*;

/**
 * Created by Marc on 9/16/2016.
 */
public class Character extends Actor {
    public enum State {STANDING, WALKING, JUMPING};
    public State state = STANDING;

    public Polygon bounds = new Polygon();

    public Sprite standing;
    public Animation walking;
    public Sprite jumping;
    public float elapsedTime = 0;

    public Vector2 velocity = new Vector2();
    public float acceleration = -10.0f;

    public TiledMap map;

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public void setSpeed(float vx) {
        if (state == JUMPING) return;
        velocity.x = vx;
        if (vx==0) {
            elapsedTime = 0;
            state = STANDING;
        } else {
            state = WALKING;
            if (vx > 0) {
                setScaleX(1.0f);
                standing.setScale(1.0f, 1.0f);
                jumping.setScale(1.0f, 1.0f);
            } else {
                setScaleX(-1.0f);
                standing.setScale(-1.0f, 1.0f);
                jumping.setScale(-1.0f, 1.0f);
            }
        }
    }

    public void jump(float vy) {
        if (state == JUMPING) return;
        state = JUMPING;
        elapsedTime = 0;
        velocity.y = vy;
    }

    public void land() {
        if (state != JUMPING) return;
        velocity.y = 0;
        if (velocity.x == 0)
            state = STANDING;
        else
            state = WALKING;
    }

    public void fall() {
        jump(0);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        standing.setSize(getWidth(), getHeight());
        jumping.setSize(getWidth(), getHeight());

        setOrigin(getWidth() / 2, getHeight() / 2);
        standing.setOrigin(getOriginX(), getOriginY());
        jumping.setOrigin(getOriginX(), getOriginY());

        bounds.setVertices(new float[]{
                getX(), getY(),
                getRight(), getY(),
                getRight(), getTop(),
                getX(), getTop()
        });
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        standing.setPosition(getX(), getY());
        jumping.setPosition(getX(), getY());
        for (int i=0; i<listeners.size; i++) {
            listeners.get(i).moved(getX(), getY());
        }

        bounds.setVertices(new float[]{
                getX(), getY(),
                getRight(), getY(),
                getRight(), getTop(),
                getX(), getTop()
        });
    }

    public Array<CharacterListener> listeners = new Array<CharacterListener>();

    public void addListener(CharacterListener cl) {
        listeners.add(cl);
    }

    public interface CharacterListener {
        public void moved(float x, float y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float dx = delta * velocity.x;
        float dvy = delta * acceleration;
        float dy = delta * velocity.y;

        // add the position updates, then check if a collision occurred
        // if a collision occurred, undo the update
        setX(getX()+dx);
        if (checkMapCollisions(true)) setX(getX()-dx);
        //if (checkMapCollisions()) setX((dx>0)? intersectionLocation-1/32f: intersectionLocation+1/32f);
        setY(getY()+dy);
        if (dy>0) { // we're jumping up, so ignore collisions
            velocity.y += dvy;
        } else { // we're falling down, so land if we collide
            if (checkMapCollisions(false)) {
                //setY(getY()-dy);
                setY(intersectionLocation);
                //Gdx.app.error("EX", "collision (" + state + ", y=" + getY() + ", dy=" + dy + ", iL=" + intersectionLocation + ")");
                land();
            } else {
                //Gdx.app.error("EX", "no collision (" + state + ", y=" + getY() + ", dy=" + dy + ", iL=" + intersectionLocation + ")");
                velocity.y += dvy;
                fall();
            }
        }

        if (state == WALKING) elapsedTime += delta;
        // TODO: detect when the character falls out of world
    }

    public boolean checkMapCollisions(boolean horizontal) {
        MapObjects objs = map.getLayers().get("GroundObjects").getObjects();
        for (int i=0; i<objs.getCount(); i++) {
            Polyline poly = ((PolylineMapObject) objs.get(i)).getPolyline();
            if (checkPolylineIntersection(poly, horizontal)) return true;
        }
        return false;
    }

    public float intersectionLocation=0;

    public boolean checkPolylineIntersection(Polyline poly, boolean horizontal) {
        Intersector intersector = new Intersector();

        float [] tverts = poly.getTransformedVertices();
        Vector2 p1 = new Vector2();
        Vector2 p2 = new Vector2();

        p2.set(tverts[0]/32.0f, tverts[1]/32.0f);
        for (int i=2; i<tverts.length; i+=2) {
            p1.set(p2);
            p2.set(tverts[i]/32.0f, tverts[i+1]/32.0f);

            if (horizontal) {
                if (p1.y==p2.y) continue;
            } else {
                if (p1.x==p2.x) continue;
            }

            if (intersector.intersectSegmentPolygon(p1, p2, bounds)) {
                if (p1.x == p2.x)
                    intersectionLocation = p1.x;
                else
                    intersectionLocation = p1.y;
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        switch (state) {
            case STANDING:
                standing.draw(batch, parentAlpha);
                break;
            case WALKING:
                    batch.draw(walking.getKeyFrame(elapsedTime, true),
                            getX(), getY(),
                            getOriginX(), getOriginY(),
                            getWidth(), getHeight(),
                            getScaleX(), getScaleY(),
                            0.0f
                    );
                break;
            case JUMPING:
                jumping.draw(batch, parentAlpha);
                break;
        }
    }
}
