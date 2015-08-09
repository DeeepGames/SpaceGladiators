package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

/**
 * Created by Elmar on 8-8-2015.
 */
public class CollisionComponent extends Component {
    public btCollisionShape collisionShape;
    public btCollisionObject collisionObject;

    public CollisionComponent(btCollisionShape btCollisionShape) {
        this.collisionShape = btCollisionShape;
        this.collisionObject = new btCollisionObject();
        this.collisionObject.setCollisionShape(this.collisionShape);
    }
}
