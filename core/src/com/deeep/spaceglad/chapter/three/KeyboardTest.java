package com.deeep.spaceglad.chapter.three;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardTest extends ApplicationAdapter{

    public String inputString = "";

    @Override
    public void create () {
        System.out.println("|Input test|");
    }

    @Override
    public void render () {
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) outputString();
        if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && inputString.length() > 0) inputString = inputString.substring(1, inputString.length());

        if(Gdx.input.isKeyPressed(Input.Keys.A)) inputString += "A";
        if(Gdx.input.isKeyPressed(Input.Keys.B)) inputString += "B";
        if(Gdx.input.isKeyPressed(Input.Keys.C)) inputString += "C";
        // ...
        if(Gdx.input.isKeyPressed(Input.Keys.X)) inputString += "X";
        if(Gdx.input.isKeyPressed(Input.Keys.Y)) inputString += "Y";
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) inputString += "Z";
    }

    public void outputString() {
        if(inputString.equals("")) return;
        System.out.println(inputString);
        inputString = "";
    }
}
