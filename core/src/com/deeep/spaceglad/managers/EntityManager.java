package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * Created by Andreas on 8/3/2015.
 */
public class EntityManager {

    private Engine engine;

    public EntityManager(Engine e, ModelBatch batch){
        engine = e;
    }

    public void update(float delta){
        engine.update(delta);
    }



}
