package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PositionComponent;
import com.deeep.spaceglad.components.VelocityComponent;
import com.deeep.spaceglad.systems.MovementSystem;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;

    public EntityManager(Engine e, ModelBatch batch){
        engine = e;

        MovementSystem ms = new MovementSystem();
        engine.addSystem(ms);

        //RenderSystem rs = new RenderSystem();
        //engine.addSystem(rs);

        Entity entity = new Entity();
        entity.add(new PositionComponent(20, 20)).add(new VelocityComponent(2)).add(new ModelComponent( new ModelBuilder().createBox(50f, 1f, 50f,
                                                                                                        new Material(ColorAttribute.createDiffuse(Color.RED)),
                                                                                                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        engine.addEntity(entity);
    }

    public void update(float delta){
        engine.update(delta);
    }



}
