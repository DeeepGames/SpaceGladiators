package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.deeep.spaceglad.Logger;

import java.util.Random;

/**
 * Created by Andreas on 8/19/2015.
 */
public class EnemySpawner {

    private static float spawnTimer = 2F;

    private float timer = 0F;
    private Engine engine;
    private Random random;

    public EnemySpawner(Engine engine){
        this.engine = engine;
        random = new Random();
    }

    public void addEnemy(float x, float y, float z){
        Entity entity = EntityFactory.createEnemy(x, y, z);
        engine.addEntity(entity);
    }

    public void update(float delta) {
        timer += delta;

        if(timer >= spawnTimer){
            timer = 0;
            float x = random.nextInt(40) - 20;
            float z = random.nextInt(40) - 20;
            float y = 0;
            Logger.log(2, 0, "Enemy added at (" + x + ", " + y + ", " + z + ")");
            addEnemy(x, y, z);
        }
    }

}
