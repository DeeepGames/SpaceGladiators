package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;

/**
 * Created by scanevaro on 31/07/2015.
 */
public class GameUI {
    private Core game;
    public Stage stage;
    public HealthWidget healthWidget;
    //  public OxygenWidget oxygenWidget;
//  public EnergyWidget energyWidget;
    private ScoreWidget scoreWidget;
    private PauseWidget pauseWidget;
    public GameOverWidget gameOverWidget;
    private Label fpsLabel;

    public GameUI(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
//      oxygenWidget = new OxygenWidget();
//      energyWidget = new EnergyWidget();
        scoreWidget = new ScoreWidget();
        pauseWidget = new PauseWidget(game, stage);
        gameOverWidget = new GameOverWidget(game, stage);
        fpsLabel = new Label("", Assets.skin);
    }

    public void configureWidgets() {
        healthWidget.setSize(140, 25);
        healthWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - healthWidget.getWidth() / 2, 0);
//      oxygenWidget.setSize(140, 25);
//      oxygenWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - oxygenWidget.getWidth() / 2, 30);
//      energyWidget.setSize(140, 25);
//      energyWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - energyWidget.getWidth() / 2, 60);
        scoreWidget.setSize(140, 25);
        scoreWidget.setPosition(0, Core.VIRTUAL_HEIGHT - scoreWidget.getHeight());
        pauseWidget.setSize(64, 64);
        pauseWidget.setPosition(Core.VIRTUAL_WIDTH - pauseWidget.getWidth(), Core.VIRTUAL_HEIGHT - pauseWidget.getHeight());
        gameOverWidget.setSize(280, 100);
        gameOverWidget.setPosition(Core.VIRTUAL_WIDTH / 2 - 280 / 2, Core.VIRTUAL_HEIGHT / 2);
        fpsLabel.setPosition(0, 10);

        stage.addActor(healthWidget);
//      stage.addActor(oxygenWidget);
//      stage.addActor(energyWidget);
        stage.addActor(scoreWidget);
        stage.setKeyboardFocus(pauseWidget);
        stage.addActor(fpsLabel);
    }

    public void update(float delta) {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
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