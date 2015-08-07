package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.components.*;

/**
 * Created by Elmar on 7-8-2015.
 */
public class EntityFactory {
    public static Entity createWall(int x, int y, int z, Vector3 orientation) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent(0));
        entity.add(new RotationComponent(orientation));
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(
                new ModelBuilder().createBox(50f, 25f, 0.5f,
                        new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        return entity;
    }

    public static Entity createMonster(int x, int y, int z) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y, z));
        entity.add(new VelocityComponent(2));
        entity.add(new RotationComponent(0, 0, 0));
        entity.add(new AIComponent(AIComponent.STATE.IDLE));
        entity.add(new RenderableComponent());
        entity.add(new ModelComponent(new ModelBuilder().createBox(2f, 2f, 2f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
        return entity;
    }

    public static Entity createPlayer(int x, int y, int z) {
        Entity entity = new Entity();
        return entity;
    }
}
