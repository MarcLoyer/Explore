package com.obduratereptile.explore.pool;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Marc on 10/6/2016.
 */
public class PoolInputAdapter extends InputAdapter {
    PoolPlayScreen screen;

    enum State {DISABLED, EDIT, PLAY}
    public State state;

    private Vector3 position = new Vector3();
    private int selected = -1;
    private int selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    public PoolInputAdapter(PoolPlayScreen screen) {
        this.screen = screen;

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.PINK));
        selectionMaterial.set(new BlendingAttribute(true, .6f));
        originalMaterial = new Material();

        state = State.PLAY;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (state != State.EDIT) return false;

        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (state != State.EDIT) return false;

        if (selecting < 0)
            return false;
        if (selected == selecting) {
            String key = (selected==0)? "ballcue": "ball"+selected;
            Ray ray = screen.camera.getPickRay(screenX, screenY);

            // All the balls are on the y==0 plane, so we project the pick ray onto that
            // plane to position the selected ball. See "https://xoppa.github.io/blog/interacting-with-3d-objects/"
            final float distance = -ray.origin.y / ray.direction.y;
            position.set(ray.direction).scl(distance).add(ray.origin);

            screen.instances.get(key).transform.setTranslation(position);
        }
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (state != State.EDIT) return false;

        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY))
                setSelected(selecting);
            selecting = -1;
            return true;
        }
        if (selected != getObject(screenX, screenY))
            clearSelected();
        return false;
    }

    public void clearSelected() {
        if (selected<0) return;
        String key = (selected==0)? "ballcue": "ball"+selected;
        Material mat = screen.instances.get(key).materials.get(0);
        mat.clear();
        mat.set(originalMaterial);
    }

    public void setSelected (int value) {
        if (selected == value) return;
        clearSelected();
        selected = value;
        if (selected >= 0) {
            String key = (selected==0)? "ballcue": "ball"+selected;
            Material mat = screen.instances.get(key).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
        }
    }

//    public int getObject (int screenX, int screenY) {
//        Ray ray = screen.camera.getPickRay(screenX, screenY);
//        int result = -1;
//        float distance = -1;
//        for (int i = 0; i < 16; ++i) {
//            String key = (i==0)? "ballcue": "ball"+i;
//            final PoolBall instance = (PoolBall)screen.instances.get(key);
//            instance.transform.getTranslation(position);
//            position.add(instance.center);
//            float dist2 = ray.origin.dst2(position);
//            if (distance >= 0f && dist2 > distance) continue;
//            if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
//                result = i;
//                distance = dist2;
//            }
//        }
//        return result;
//    }

    public int getObject (int screenX, int screenY) {
        Ray ray = screen.camera.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < 16; ++i) {
            String key = (i==0)? "ballcue": "ball"+i;
            final PoolBall instance = (PoolBall)screen.instances.get(key);

            instance.transform.getTranslation(position);
            position.add(instance.center);

            final float len = ray.direction.dot(position.x-ray.origin.x, position.y-ray.origin.y, position.z-ray.origin.z);
            if (len < 0f)
                continue;

            float dist2 = position.dst2(ray.origin.x+ray.direction.x*len, ray.origin.y+ray.direction.y*len, ray.origin.z+ray.direction.z*len);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (dist2 <= instance.radius * instance.radius) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }
}
