package com.deeep.spaceglad.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Core;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameUI {
    Core game;
    public Stage stage;
    HealthWidget healthWidget;
    OxigenWidget oxigenWidget;
    EnergyWidget energyWidget;
    ScoreWidget scoreWidget;
    PauseWidget pauseWidget;
    GameOverWidget gameOverWidget;

    public GameUI(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
        oxigenWidget = new OxigenWidget();
        energyWidget = new EnergyWidget();
        scoreWidget = new ScoreWidget();
        pauseWidget = new PauseWidget(game, stage);
        gameOverWidget = new GameOverWidget();
    }

    public void configureWidgets() {
        healthWidget.setSize(140, 25);
        healthWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - healthWidget.getWidth() / 2, 0);
        oxigenWidget.setSize(140, 25);
        oxigenWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - oxigenWidget.getWidth() / 2, 30);
        energyWidget.setSize(140, 25);
        energyWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - energyWidget.getWidth() / 2, 60);
        scoreWidget.setSize(140, 25);
        scoreWidget.setPosition(0, Core.VIRTUAL_HEIGHT - scoreWidget.getHeight());
        pauseWidget.setSize(64, 64);
        pauseWidget.setPosition(Core.VIRTUAL_WIDTH - pauseWidget.getWidth(), Core.VIRTUAL_HEIGHT - pauseWidget.getHeight());
        gameOverWidget.setSize(140, 25);
        gameOverWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - gameOverWidget.getWidth() / 2, Core.VIRTUAL_HEIGHT / 2 - gameOverWidget.getHeight() / 2);

        stage.addActor(healthWidget);
        stage.addActor(oxigenWidget);
        stage.addActor(energyWidget);
        stage.addActor(scoreWidget);
        stage.addActor(pauseWidget);
        stage.setKeyboardFocus(pauseWidget);
        stage.addActor(gameOverWidget);
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

    public void dispose() {
        stage.dispose();
    }
}