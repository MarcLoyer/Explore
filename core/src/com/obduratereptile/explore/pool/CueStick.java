package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

/**
 * Created by Marc on 10/4/2016.
 */
public class CueStick extends ModelInstance {


    public CueStick(Model mdl, String node) {
        super(mdl, node);
        //Gdx.app.error("EX", "Cue tip opacity = " + ((BlendingAttribute)getMaterial("CueTipMat").get(BlendingAttribute.Type)).opacity);
    }

    public float getTipTransparency() {
        return ((BlendingAttribute)getMaterial("CueTipMat").get(BlendingAttribute.Type)).opacity;
    }

    public void setTipTransparency(float a) {
        ((BlendingAttribute)getMaterial("CueTipMat").get(BlendingAttribute.Type)).opacity = a;
        //Gdx.app.error("EX", "Cue tip opacity = " + ((BlendingAttribute)getMaterial("CueTipMat").get(BlendingAttribute.Type)).opacity);
    }

    // Note: alpha doesn't matter when setting this color. The transparent nature
    // is encoded in the BlendingAttribute (type==65536)
    public void setColor(Color c) {
        ((ColorAttribute)getMaterial("CueBodyMat").get(ColorAttribute.Diffuse)).color.set(c);
        ((ColorAttribute)getMaterial("CueTipMat").get(ColorAttribute.Diffuse)).color.set(c);
    }

    public void setColor(float r, float g, float b, float a) {
        ((ColorAttribute)getMaterial("CueBodyMat").get(ColorAttribute.Diffuse)).color.set(r,g,b,a);
        ((ColorAttribute)getMaterial("CueTipMat").get(ColorAttribute.Diffuse)).color.set(r,g,b,a);
    }

    public void setColor(float r, float g, float b) {
        ((ColorAttribute)getMaterial("CueBodyMat").get(ColorAttribute.Diffuse)).color.set(r,g,b,1);
        ((ColorAttribute)getMaterial("CueTipMat").get(ColorAttribute.Diffuse)).color.set(r,g,b,1);
    }
}
