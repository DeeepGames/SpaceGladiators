package com.deeep.spaceglad.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Core;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameScreenUI {
    Stage stage;
    HealthWidget healthWidget;
//    OxigenWidget oxigenWidget;
//    EnergyWidget energyWidget;
//    ScoreWidget scoreWidget;
//    PauseWidget pauseWidget;
//    GameOverWidget gameOverWidget;

    public GameScreenUI() {
        stage = new Stage(new FitViewport(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
//        oxigenWidget = new OxigenWidget();
//        energyWidget = new EnergyWidget();
//        scoreWidget = new ScoreWidget();
//        pauseWidget = new PauseWidget();
//        gameOverWidget = new GameOverWidget();
    }

    public void configureWidgets() {
        stage.addActor(healthWidget);
//        stage.addActor(oxigenWidget);
//        stage.addActor(energyWidget);
//        stage.addActor(scoreWidget);
//        stage.addActor(pauseWidget);
//        stage.addActor(gameOverWidget);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
