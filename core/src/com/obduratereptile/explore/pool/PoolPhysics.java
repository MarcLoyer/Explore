package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Marc on 10/7/2016.
 */
public class PoolPhysics {
    final static short GROUND_FLAG = 1<<8;
    final static short OBJECT_FLAG = 1<<9;
    final static short ALL_FLAG = -1;

    PoolPlayScreen screen;

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0,
                                       int userValue1, int partId1, int index1, boolean match1) {
//            Gdx.app.error("EX", "collision detected: (" +
//                    userValue0 + ", " + partId0 + ", " + index0 + ", " + match0 + "), (" +
//                    userValue1 + ", " + partId1 + ", " + index1 + ", " + match1 + ")"
//            );
            return true;
        }
    }
    MyContactListener contactListener;

    static public void init() {
        Bullet.init();
    }

    public PoolPhysics(PoolPlayScreen screen) {
        this.screen = screen;

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);

        // World units are 1 unit = 1 inch, so gravity is
        // 9.8m.s^2 = 385 inches/s^2
        dynamicsWorld.setGravity(new Vector3(0, -385f, 0));

        // add all the PoolBalls to the simulation
        for (int i=0; i<16; i++) {
            String key = (i==0)? "ballcue": "ball"+i;
            addBalltoWorld((PoolBall)screen.instances.get(key));
        }

        // add static body (the table) to the simulation
        btRigidBody bodyTable = ((PoolTable)screen.instances.get("table")).body;
        bodyTable.setUserValue(20);
        bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        bodyTable.setContactCallbackFlag(GROUND_FLAG);
        bodyTable.setContactCallbackFilter(0);
        bodyTable.setActivationState(Collision.DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(bodyTable);

        //TODO: construct bed and cushion and pocket static objects
        Gdx.app.error("EX", "bodyTable.isStaticObject() = " + bodyTable.isStaticObject());

        contactListener = new MyContactListener();
    }

    public void addBalltoWorld(PoolBall ball) {
        btRigidBody obj = ball.body;
        obj.proceedToTransform(ball.transform);
        obj.setUserValue(ball.id);
        //obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        obj.setActivationState(Collision.DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(obj);
        obj.setContactCallbackFlag(OBJECT_FLAG); //TODO: what does this do again?
        obj.setContactCallbackFilter(GROUND_FLAG);
    }

    public void act(float del) {
        final float delta = Math.min(1f / 30f, del);

        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
    }

    public void dispose() {
        contactListener.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
    }
}
