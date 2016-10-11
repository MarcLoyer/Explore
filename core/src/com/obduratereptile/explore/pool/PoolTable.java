package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Marc on 10/7/2016.
 */
public class PoolTable extends ModelInstance implements Disposable {
    public int id;

    public final btRigidBody body;
    public final PoolMotionState motionState;
    public final btCollisionShape shape;
    private static Vector3 localInertia = new Vector3(0,0,0);
    public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    public PoolTable(Model model, String node, int id) {
        super(model, node);
        motionState = new PoolMotionState();
        motionState.transform = transform;

        //TODO: split the static model into table bed, cushions, and pockets
        // in order to make sound effects easier.
        shape = Bullet.obtainStaticNodeShape(nodes);

        //TODO: friction, restitution, ???
        //  setFriction(float)
        //  setRollingFriction(float)
        //  setLinearDamping(float)
        //  setAngularDamping(float)
        //  setRestitution(float)
        //  setLinearSleepingThreshold(float)
        //  setAngularSleepingThreshold(float)
        //  setAdditionalDamping(float)
        //  setAdditionalDampingFactor(float)
        //  setAdditionalLinearDampingThresholdSqr(float)
        //  setAdditionalAngularDampingThresholdSqr(float)
        //  setAdditionalAngularDampingFactor(float)
        //  setDamping(float lin_damping, float ang_damping)
        //  setLinearFactor(Vector3 linearFactor)
        //  setAngularFactor(Vector3 angFac)
        //  setAngularFactor(float angFac)
        //
        //  setFrictionSolverType(int value)

        constructionInfo =
                new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia); // TODO: add friction

        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);

        // experimenting with these settings...
        body.setFriction(.1f);
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
