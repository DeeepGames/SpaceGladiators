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
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.components.*;
import com.deeep.spaceglad.systems.AISystem;
import com.deeep.spaceglad.systems.MovementSystem;
import com.deeep.spaceglad.systems.RenderSystem;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;
    private MovementSystem ms;

    public EntityManager(Engine e, ModelBatch batch, Environment environment, PerspectiveCamera cam) {
        engine = e;

        ms = new MovementSystem();
        engine.addSystem(ms);

        RenderSystem rs = new RenderSystem(batch, environment);
        engine.addSystem(rs);

        AISystem as = new AISystem(cam);
        engine.addSystem(as);
        engine.addEntity(EntityFactory.createMonster(0, 0, 0));

        createLevel();
    }

    public void createLevel() {
        Entity ground = new Entity();
        ground.add(new PositionComponent(0, -2.2f, 0))
                .add(new VelocityComponent(0))
                .add(new RotationComponent(0, 0, 0))
                .add(new RenderableComponent())
                .add(new ModelComponent(
                        new ModelBuilder().createBox(50f, 0.5f, 50f,
                                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        engine.addEntity(ground);
        engine.addEntity(EntityFactory.createWall(25, 10, 0, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, 25, new Vector3(0 , 0, 0)));
        engine.addEntity(EntityFactory.createWall(-25, 10, 0, new Vector3(90, 0, 0)));
        engine.addEntity(EntityFactory.createWall(0, 10, -25, new Vector3(0 , 0, 0)));

    }

    public void update(float delta) {
        engine.update(delta);
        if (Core.Pause) ms.setProcessing(false);
        else ms.setProcessing(true);
    }
}