package com.deeep.spaceglad.components;

import com.badlogic.gdx.physics.bullet.Bullet;

/**
 * Created by Andreas on 8/12/2015.
 */
public class BulletComponent extends Bullet{
    public boolean isFriendly;
    public float damage;

    public BulletComponent(boolean isFriendly, float damage){
        this.isFriendly = isFriendly;
        this.damage = damage;
    }
}
