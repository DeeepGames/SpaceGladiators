package com.deeep.spaceglad.chapter.three;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class TextListener {
    String inputString;

    public void update(){
        if(Gdx.input.isButtonPressed(Input.Keys.ENTER)) returnString();
        if(Gdx.input.isButtonPressed(Input.Keys.BACKSPACE) && inputString.length() > 0) inputString = inputString.substring(1, inputString.length());

        if(Gdx.input.isButtonPressed(Input.Keys.A)) inputString += "A";
        if(Gdx.input.isButtonPressed(Input.Keys.B)) inputString += "B";
        if(Gdx.input.isButtonPressed(Input.Keys.C)) inputString += "C";
        // ...
        if(Gdx.input.isButtonPressed(Input.Keys.X)) inputString += "X";
        if(Gdx.input.isButtonPressed(Input.Keys.Y)) inputString += "Y";
        if(Gdx.input.isButtonPressed(Input.Keys.Z)) inputString += "Z";
    }

    public void returnString() {
        System.out.println(inputString);
        inputString = "";
    }
}
