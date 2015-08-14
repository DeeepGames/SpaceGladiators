package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Andreas on 8/4/2015.
 */
public class VelocityComponent extends Component{
    public Vector3 velocity = new Vector3();

    public VelocityComponent(){}

    public VelocityComponent(Vector3 velocity){
        this.velocity = velocity;
    }

    public VelocityComponent(float dX, float dY, float dZ, float base){
        this.velocity = new Vector3(dX,dY,dZ);
    }
}
