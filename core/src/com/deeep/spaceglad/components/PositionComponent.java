package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/4/2015.
 */
public class PositionComponent extends Component{
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public PositionComponent(float x, float y){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
