package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.systems.AISystem;
import com.deeep.spaceglad.systems.MovementSystem;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;

    public EntityManager(Engine e, ModelBatch batch, Environment environment, PerspectiveCamera cam){
        engine = e;

        MovementSystem ms = new MovementSystem();
        engine.addSystem(ms);

        RenderSystem rs = new RenderSystem(batch,environment);
        engine.addSystem(rs);

        AISystem as = new AISystem(cam);
        engine.addSystem(as);

        Entity entity = new Entity();

        entity  .add(new PositionComponent(0, 0, 0))
                .add(new VelocityComponent(0,0,0))
                .add(new RotationComponent(0,0,0))
                .add(new AIComponent(AIComponent.STATE.IDLE))
                .add(new RenderableComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(2f, 2f, 2f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));

        engine.addEntity(entity);
    }

    public void update(float delta){
        engine.update(delta);
    }



}
