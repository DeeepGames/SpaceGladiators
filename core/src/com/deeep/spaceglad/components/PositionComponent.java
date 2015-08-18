package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Andreas on 8/4/2015.
 */
public class PositionComponent extends Component{
    public Vector3 position;
    public Vector3 prevPosition;

    public PositionComponent(float x, float y, float z){
        this.position = new Vector3(x,y,z);
        this.prevPosition = new Vector3(x,y,z);
    }

    public PositionComponent(Vector3 position){
        this.position = position;
        this.prevPosition = new Vector3(position);
    }
}
