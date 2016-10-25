package com.obduratereptile.explore.pool;

import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Marc on 10/13/2016.
 */
public class CueStickAccessor implements TweenAccessor<CueStick> {
    public static final int TYPE_POS = 1;

    @Override
    public int getValues(CueStick obj, int i, float[] floats) {
        switch (i) {
            case (1):
                Vector3 p = obj.getPosition();
                floats[0] = p.x;
                floats[1] = p.y;
                floats[2] = p.z;
                return 3;
            default:
                return -1;
        }
    }

    @Override
    public void setValues(CueStick obj, int i, float[] floats) {
        switch (i) {
            case (1):
                obj.setPosition(new Vector3(floats[0], floats[1], floats[2]));
                break;
            default:
                return;
        }
    }
}
