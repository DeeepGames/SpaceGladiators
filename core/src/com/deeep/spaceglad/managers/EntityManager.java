package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.systems.MovementSystem;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;
    private MovementSystem movementSystem;

    public EntityManager(Engine e, ModelBatch batch, Environment environment) {
        engine = e;

        movementSystem = new MovementSystem();
        engine.addSystem(movementSystem);

        RenderSystem rs = new RenderSystem(batch, environment);
        engine.addSystem(rs);

        Entity entity = new Entity();

        entity.add(new PositionComponent(20, 20, 20))
                .add(new VelocityComponent(2, -2, 2))
                .add(new RotationComponent(0, 45, 0))
                .add(new RenderableComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(20f, 2f, 20f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));

        engine.addEntity(entity);
    }

    public void update(float delta) {
        if (Core.Pause) movementSystem.setProcessing(false);
        else movementSystem.setProcessing(true);
        engine.update(delta);
    }


}
