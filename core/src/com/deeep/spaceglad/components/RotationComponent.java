package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/4/2015.
 */
public class RotationComponent extends Component{
    public float thetaX = 0.0f;
    public float thetaY = 0.0f;
    public float thetaZ = 0.0f;

    public RotationComponent(float thetaX, float thetaY, float thetaZ){
        this.thetaX = thetaX;
        this.thetaY = thetaY;
        this.thetaZ = thetaZ;
    }
}
