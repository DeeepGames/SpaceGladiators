package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;

/**
 * Created by Andreas on 8/4/2015.
 */
public class ModelComponent extends Component{
    public Model model;
    public ModelInstance instance;
    public BoundingBox boundingBox;

    public ModelComponent(Model model){
        this.model = model;
        this.instance = new ModelInstance(model);
        boundingBox = new BoundingBox();
        this.model.calculateBoundingBox(boundingBox);
    }
}
