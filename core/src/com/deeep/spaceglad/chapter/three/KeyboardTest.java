package com.deeep.spaceglad.chapter.three;

import com.badlogic.gdx.ApplicationAdapter;

public class KeyboardTest extends ApplicationAdapter{

    public TextListener listener;

    @Override
    public void create () {
        System.out.println("|Input test|");
        listener = new TextListener();
    }

    @Override
    public void render () {
        listener.update();
    }

}
