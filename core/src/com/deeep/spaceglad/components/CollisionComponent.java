package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Created by Elmar on 8-8-2015.
 */
public class CollisionComponent extends Component {
    public btCollisionShape collisionShape;
    public btRigidBody rigidBody;

    public CollisionComponent(btCollisionShape btCollisionShape, float mass) {
        this.collisionShape = btCollisionShape;
        Vector3 temp = Vector3.Zero;
        if(mass>1){
            btCollisionShape.calculateLocalInertia(mass, temp);
        }
        this.rigidBody = new btRigidBody(mass, null, collisionShape, temp);
    }
}
