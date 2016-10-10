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
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Marc on 10/4/2016.
 */
public class PoolBall extends ModelInstance implements Disposable {
    public int id;

    public BoundingBox bounds = new BoundingBox();
    public Vector3 center = new Vector3();
    public float radius;

    public final btRigidBody body;
    public final PoolMotionState motionState;
    public final btCollisionShape shape;
    public final float mass;
    private static Vector3 localInertia = new Vector3();
    public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    public PoolBall(Model model, String node, int id, TextureAttribute taImg) {
        super(model, node);
        getMaterial("ballMaterial").set(taImg);

        this.id = id;

        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        radius = bounds.getWidth() / 2.0f;

        shape = new btSphereShape(radius);
        mass = 0.170f; // 6 oz

        motionState = new PoolMotionState();
        motionState.transform = transform;

        shape.calculateLocalInertia(mass, localInertia);

        constructionInfo =
                new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);

        // experimenting with these settings...
        body.setFriction(.3f);
        body.setRollingFriction(.1f);
        body.setRestitution(.1f);
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
