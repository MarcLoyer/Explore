package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Marc on 10/7/2016.
 */
public class PoolTable extends ModelInstance implements Disposable {
    public int id;

//    public PoolTablePart bed;
//    public Array<PoolTablePart> cushions;
//    public Array<PoolTablePart> pocketLiners;
//    public Array<PoolTablePart> pocketJackets;

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
        constructionInfo =
                new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia); // TODO: add friction
        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);
        // experimenting with these settings...
        body.setFriction(.1f);
        body.setRollingFriction(.001f);
        body.setRestitution(1f);

//        bed = new PoolTablePart(model, "Bed", motionState);
//        cushions = new Array<PoolTablePart>();
//        pocketLiners = new Array<PoolTablePart>();
//        pocketJackets = new Array<PoolTablePart>();
//
//        pocketJackets.add(new PoolTablePart(model, "PocketJacket.003", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLiner.003", motionState));
//        pocketJackets.add(new PoolTablePart(model, "PocketJacket.002", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLiner.002", motionState));
//        pocketJackets.add(new PoolTablePart(model, "PocketJacket.001", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLiner.001", motionState));
//        pocketJackets.add(new PoolTablePart(model, "PocketJacket", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLiner", motionState));
//        pocketJackets.add(new PoolTablePart(model, "PocketJacketSide.001", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLinerSide.001", motionState));
//        pocketJackets.add(new PoolTablePart(model, "PocketJacketSide", motionState));
//        pocketLiners.add(new PoolTablePart(model, "PocketLinerSide", motionState));
//        cushions.add(new PoolTablePart(model, "EndCushionRight", motionState));
//        cushions.add(new PoolTablePart(model, "EndCushion", motionState));
//        cushions.add(new PoolTablePart(model, "SideCushionRightTop", motionState));
//        cushions.add(new PoolTablePart(model, "SideCushionRight", motionState));
//        cushions.add(new PoolTablePart(model, "SideCushionLeftTop", motionState));
//        cushions.add(new PoolTablePart(model, "SideCushionLeft", motionState));

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
    }

    public void updateMatrix() {
        body.proceedToTransform(transform);

//        bed.body.proceedToTransform(transform);
//        for (int i=0; i<cushions.size; i++) cushions.get(i).body.proceedToTransform(transform);
//        for (int i=0; i<pocketJackets.size; i++) pocketJackets.get(i).body.proceedToTransform(transform);
//        for (int i=0; i<pocketLiners.size; i++) pocketLiners.get(i).body.proceedToTransform(transform);
    }

    public class PoolTablePart implements Disposable {
        public final btRigidBody body;
        public final btCollisionShape shape;
        private final Vector3 localInertia = new Vector3(0,0,0);
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;

        PoolTablePart(Model mdl, String node, PoolMotionState motionState) {
            Array<Node> n = new Array<Node>();
            n.add(mdl.getNode(node, true));
            shape = Bullet.obtainStaticNodeShape(n);

            constructionInfo =
                    new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);

            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);

            // experimenting with these settings...
            // TODO: add friction
            body.setFriction(.1f);
            body.setRollingFriction(.1f);
            body.setRestitution(.1f);
        }

        public void updateMatrix() {
            body.proceedToTransform(transform);
        }

        public void dispose() {
            body.dispose();
            shape.dispose();
            constructionInfo.dispose();
        }
    }

    @Override
    public void dispose () {
//        bed.dispose();
//
//        for (int i=0; i<pocketJackets.size; i++) {
//            pocketJackets.get(i).dispose();
//        }
//        pocketJackets.clear();
//
//        for (int i=0; i<pocketLiners.size; i++) {
//            pocketLiners.get(i).dispose();
//        }
//        pocketLiners.clear();
//
//        for (int i=0; i<cushions.size; i++) {
//            cushions.get(i).dispose();
//        }
//        cushions.clear();

        body.dispose();
        motionState.dispose();
        shape.dispose();
        constructionInfo.dispose();
    }
}
