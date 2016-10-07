package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Marc on 10/4/2016.
 */
class MyMotionState extends btMotionState {
    Matrix4 transform;
    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(transform);
    }
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}

public class PoolBall extends ModelInstance implements Disposable {
    public String id;

    public BoundingBox bounds = new BoundingBox();
    public Vector3 center = new Vector3();
    public float radius;

//    public Vector2 position;
//    public Vector2 velocity;
//    public Vector3 rotationAxis;
//    public float rotationVelocity;

    //public final btRigidBody body;
    //public final MyMotionState motionState;

    public PoolBall(Model model, String node, String id, TextureAttribute taImg, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model, node);
        getMaterial("ballMaterial").set(taImg);

        this.id = id;

        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        radius = bounds.getWidth() / 2.0f;

        //motionState = new MyMotionState();
        //motionState.transform = transform;
        //body = new btRigidBody(constructionInfo);
        //body.setMotionState(motionState);
    }

    @Override
    public void dispose () {
        //body.dispose();
        //motionState.dispose();
    }
}
