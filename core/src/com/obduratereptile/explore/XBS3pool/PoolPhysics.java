package com.obduratereptile.explore.XBS3pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.obduratereptile.explore.XoppaBulletScreen3;
import com.obduratereptile.explore.pool.CueStick;
import com.obduratereptile.explore.pool.PoolBall;
import com.obduratereptile.explore.pool.PoolTable;

/**
 * Created by Marc on 10/7/2016.
 */
public class PoolPhysics {
    public final static short GROUND_FLAG = 1<<8;
    public final static short OBJECT_FLAG = 1<<9;
    public final static short ALL_FLAG = -1;

    public XoppaBulletScreen3 screen;

    public btCollisionConfiguration collisionConfig;
    public btDispatcher dispatcher;
    public btBroadphaseInterface broadphase;
    public btDynamicsWorld dynamicsWorld;
    public btConstraintSolver constraintSolver;

    public class MyContactListener extends ContactListener {
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
    public MyContactListener contactListener;

    static public void init() {
        Bullet.init();
    }

    public PoolPhysics(XoppaBulletScreen3 screen) {
        this.screen = screen;

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);

        // World units are 1 unit = 1 inch, so gravity is
        // 9.8m.s^2 = 385 inches/s^2
//        dynamicsWorld.setGravity(new Vector3(0, -385f, 0));
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
//        dynamicsWorld.setGravity(new Vector3(0, -38.5f, 0)); // scale is 10 inches

        // add all the PoolBalls to the simulation
        for (int i=0; i<16; i++) {
            String key = (i==0)? "ballcue": "ball"+i;
            addBalltoWorld((PoolBall)screen.balls.get(key));
        }

        // add static body (the table) to the simulation
        PoolTable pt = (PoolTable)screen.otherObjects.get("table");
        btRigidBody bodyTable = pt.body;
//        btRigidBody bodyTable = pt.bed.body;
        bodyTable.setUserValue(20);
        bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        bodyTable.setContactCallbackFlag(GROUND_FLAG);
        bodyTable.setContactCallbackFilter(ALL_FLAG);
        bodyTable.setActivationState(Collision.DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(bodyTable);

//        for (int i=0; i<pt.cushions.size; i++) {
//            bodyTable = pt.cushions.get(i).body;
//            bodyTable.setUserValue(21);
//            bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
//                    | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
//            bodyTable.setContactCallbackFlag(GROUND_FLAG);
//            bodyTable.setContactCallbackFilter(0);
//            bodyTable.setActivationState(Collision.DISABLE_DEACTIVATION);
//            dynamicsWorld.addRigidBody(bodyTable);
//        }
//
//        for (int i=0; i<pt.pocketJackets.size; i++) {
//            bodyTable = pt.pocketJackets.get(i).body;
//            bodyTable.setUserValue(22);
//            bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
//                    | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
//            bodyTable.setContactCallbackFlag(GROUND_FLAG);
//            bodyTable.setContactCallbackFilter(0);
//            bodyTable.setActivationState(Collision.DISABLE_DEACTIVATION);
//            dynamicsWorld.addRigidBody(bodyTable);
//        }
//
//        for (int i=0; i<pt.pocketLiners.size; i++) {
//            bodyTable = pt.pocketLiners.get(i).body;
//            bodyTable.setUserValue(23);
//            bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
//                    | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
//            bodyTable.setContactCallbackFlag(GROUND_FLAG);
//            bodyTable.setContactCallbackFilter(0);
//            bodyTable.setActivationState(Collision.DISABLE_DEACTIVATION);
//            dynamicsWorld.addRigidBody(bodyTable);
//        }

        // add the cuestick to the simulation
        btRigidBody bodyStick = ((CueStick)screen.otherObjects.get("cuestick")).body;
        bodyTable.setUserValue(40);
        bodyTable.setCollisionFlags(bodyTable.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        bodyTable.setContactCallbackFlag(GROUND_FLAG);
        bodyTable.setContactCallbackFilter(0);
        dynamicsWorld.addRigidBody(bodyStick);

        contactListener = new MyContactListener();
    }

    public void addBalltoWorld(PoolBall ball) {
        btRigidBody obj = ball.body;
        obj.proceedToTransform(ball.transform);
        obj.setUserValue(ball.id);
        //obj.setActivationState(Collision.DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(obj);
        obj.setContactCallbackFlag(OBJECT_FLAG);
//        obj.setContactCallbackFilter(GROUND_FLAG);
        obj.setContactCallbackFilter(ALL_FLAG);
    }

    public void showBall(int id, Vector3 pos) {
        String key = (id==0)? "ballcue": "ball"+id;
        PoolBall b = (PoolBall)screen.balls.get(key);

        // add the ball to the render loop
        if (!screen.instances.containsKey(key)) screen.instances.put(key, b);

        // move the ball back onto the playing surface
        b.transform.setToTranslation(pos);
        b.updateMatrix();

        // wake it up
        b.body.activate();
    }

    public void hideBall(int id) {
        String key = (id==0)? "ballcue": "ball"+id;
        PoolBall b = (PoolBall)screen.balls.get(key);

        // remove the ball from the render loop
        screen.instances.removeKey(key);

        // move the ball out of the way
        b.transform.setToTranslation(10*id, -10, 0);
        b.updateMatrix();

        // put it to sleep
        b.body.setActivationState(Collision.DISABLE_SIMULATION);
    }

    public void act(float del) {
        final float delta = Math.min(1f / 30f, del);

//        dynamicsWorld.stepSimulation(delta, 20, 1f/60f);
        dynamicsWorld.stepSimulation(10*del, 20, 1f/60f);
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
