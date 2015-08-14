package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/10/2015.
 */
public class BulletComponent extends Component {

    float damage;

    public void BulletComponent(float damage){
        this.damage = damage;
    }


}
