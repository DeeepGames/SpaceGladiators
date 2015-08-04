package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/4/2015.
 */
public class VelocityComponent extends Component{
    public float velocityX = 0.0f;
    public float velocityY = 0.0f;
    public float velocityZ = 0.0f;

    public VelocityComponent(float velocityX, float velocityY, float velocityZ){
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }
}
