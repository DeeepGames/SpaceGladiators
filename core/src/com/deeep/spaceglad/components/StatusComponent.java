package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Andreas on 8/10/2015.
 */
public class StatusComponent extends Component{

    public boolean visible;
    public boolean frozen;
    public boolean ignited;
    public boolean enabled;
    public boolean alive;

    public StatusComponent(){
        enabled = true;
        visible = true;
        alive = true;
        frozen = false;
        ignited = false;
    }

}
