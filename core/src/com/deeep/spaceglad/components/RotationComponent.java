package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Andreas on 8/4/2015.
 */
public class RotationComponent extends Component{
    public float yaw = 0.0f;
    public float pitch = 0.0f;
    public float roll = 0.0f;

    public RotationComponent(float yaw, float pitch, float roll){
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }
    public RotationComponent(Vector3 rotation){
        this.yaw = rotation.x;
        this.pitch = rotation.y;
        this.roll = rotation.z;
    }
}
