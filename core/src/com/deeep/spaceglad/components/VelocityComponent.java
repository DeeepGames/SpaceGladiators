package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/4/2015.
 */
public class VelocityComponent extends Component{
    public float velocity = 0.0f;

    public VelocityComponent(float velocity){
        this.velocity = velocity;
    }
}
