package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by Andreas on 8/4/2015.
 */
public class ModelComponent extends Component{
    public Model model;
    public ModelInstance instance;

    public ModelComponent(Model model){
        this.model = model;
        this.instance = new ModelInstance(model);
    }
}
