package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.deeep.spaceglad.Settings;

import java.util.Random;

/**
 * Created by Andreas on 8/19/2015.
 */
public class EnemySpawner {

    private static float spawnTimer = 1F;

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
        if(timer > 0xffffffff) timer = spawnTimer;

        if(timer >= spawnTimer && !Settings.Paused){
            timer = 0;
            float x = random.nextInt(80) - 40;
            float z = random.nextInt(80) - 40;
            float y = 0;
            addEnemy(x, y, z);
        }
    }

}
