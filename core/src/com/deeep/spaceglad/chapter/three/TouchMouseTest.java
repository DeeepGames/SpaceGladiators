package com.deeep.spaceglad.chapter.three;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/**
 * Created by scanevaro on 25/07/2015.
 */
public class TouchMouseTest extends ApplicationAdapter {
    @Override
    public void render() {
        if (Gdx.input.justTouched())
            System.out.println("justTouched");
    }
}
