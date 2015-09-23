package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;

/**
 * Created by Andreas on 8/4/2015.
 */
public class ModelComponent extends Component{
    public Model model;
    public ModelInstance instance;
    public Matrix4 transform;
    private Color color = new Color(1f, 1f, 1f, 1f);

    public ModelComponent(Model model){
        this.model = model;
        this.instance = new ModelInstance(model);
    }

    public void setColor(Color color){
        this.color = color;
        if(instance!=null)
        for (Material m : instance.materials) {
            ColorAttribute ca = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (ca != null) ca.color.set(color);
        }
    }
}
