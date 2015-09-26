package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;

/**
 * Created by Elamre on 9/26/2015.
 */
public class BulletPlayerComponent extends Component {
    //public Matrix4 characterTransform;
    //public btPairCachingGhostObject ghostObject;
    public btConvexShape ghostShape;
}
