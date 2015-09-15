package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexInternalShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Created by Elmar on 8-8-2015.
 */
public class CollisionComponent extends Component {
    public btConvexInternalShape collisionShape;
    public btCollisionObject body;
    public boolean character = false;
    public Matrix4 characterTransform = new Matrix4();
    public btKinematicCharacterController characterController;
    public btPairCachingGhostObject pair;

    public CollisionComponent(btConvexInternalShape btCollisionShape) {
        this.collisionShape = btCollisionShape;
        // this.rigidBody = new btRigidBody(0, null, collisionShape, Vector3.Zero);
    }

    public CollisionComponent(btConvexInternalShape btCollisionShape, float mass) {
        this.collisionShape = btCollisionShape;
        Vector3 temp = Vector3.Zero;
        btCollisionShape.calculateLocalInertia(mass, temp);
        body = new btCollisionObject();
        body.setCollisionShape(btCollisionShape);
        //this.rigidBody = new btRigidBody(mass, null, collisionShape, temp);
    }

    public CollisionComponent(btConvexInternalShape btCollisionShape, float mass, boolean character) {
        this.character = character;
        this.collisionShape = btCollisionShape;
        Vector3 temp = Vector3.Zero;
        btCollisionShape.calculateLocalInertia(mass, temp);
        this.body = new btRigidBody(mass, null, collisionShape, temp);
        // Create the physics representation of the character
        pair = new btPairCachingGhostObject();
        pair.setWorldTransform(characterTransform);
        pair.setCollisionShape(btCollisionShape);
        pair.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterController = new btKinematicCharacterController(pair, btCollisionShape, .35f);
    }
}
