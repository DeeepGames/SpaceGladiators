package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

/**
 * Created by Elmar on 8-8-2015.
 */
public class PlayerComponent extends Component {
    btGhostPairCallback ghostPairCallback;
    btPairCachingGhostObject ghostObject;
    btConvexShape ghostShape;
    btKinematicCharacterController characterController;

    public float energy;
    public float oxygen;
    public float health;
    public static int score;

    public PlayerComponent() {
        energy = 100;
        oxygen = 100;
        health = 100;
        score = 0;
    }

    public void hit() {
        health -= 1;
    }
}