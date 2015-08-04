package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/4/2015.
 */
public class RotationComponent extends Component{
    public float theta = 0.0f;

    public RotationComponent(float theta){
        this.theta = theta;
    }
}
