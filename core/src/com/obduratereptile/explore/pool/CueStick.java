package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Marc on 10/4/2016.
 */
public class CueStick extends ModelInstance implements Disposable {
    public btRigidBody body;
    public final PoolMotionState motionState;
    public final btCollisionShape shape;
    private float mass = 0;
    private static Vector3 localInertia = new Vector3(0,0,0);
    public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    public Vector3 orientation;
    public Vector3 position;

    public CueStick(Model mdl, String node) {
        super(mdl, node);
        motionState = new PoolMotionState();
        motionState.transform = transform;

        shape = Bullet.obtainStaticNodeShape(nodes);
        //shape.calculateLocalInertia(mass, localInertia);

        //TODO: should the cue stick be a kinematic or a dynamic object?
        constructionInfo =
                new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);

        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);

        // experimenting with these settings...
        body.setFriction(1f);
        body.setRollingFriction(1f);
        body.setRestitution(1f);

        body.setCollisionFlags(body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);

        orientation = new Vector3();
        position = new Vector3();
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
    public Color getColor() {
        return ((ColorAttribute)getMaterial("CueBodyMat").get(ColorAttribute.Diffuse)).color;
    }

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

    public void setOrientation(Vector3 dir) {
        orientation.set(dir);
    }

    public Vector3 getPosition() {
        transform.getTranslation(position);
        return position;
    }

    public void setPosition(Vector3 pos) {
        position.set(pos);
        transform.setTranslation(pos);
        updateMatrix();
    }

    public void setupShot(PoolBall ball, Vector3 target) {
        // TODO: aligns the cue stick to the ball and target, leaving a small distance between the
        // ball and stick.
    }

    public void shoot(float power, PoolBall ball) {
        Vector3 impulse = new Vector3(orientation);

        ball.body.applyImpulse(impulse.scl(power), position);
        // TODO maybe actually move the cue stick instead of applying a force on a target ball
        //   I could animate the whole stroke - line up on the ball, draw back proportional to
        // the power, then apply the force through the stroke.
    }

    public void updateMatrix() {
        body.proceedToTransform(transform);
    }

    @Override
    public void dispose () {
        body.dispose();
        motionState.dispose();
        shape.dispose();
        constructionInfo.dispose();
    }
}
