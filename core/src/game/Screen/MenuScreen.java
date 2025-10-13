package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.Utilities;

import java.awt.*;
import java.util.logging.Logger;
import game.ArkanoidGame;

public class MenuScreen implements Screen {
    private static final Logger logger = Logger.getLogger(MenuScreen.class.getName());
    private static final boolean DEBUG = true;

    private ArkanoidGame game;
    private Viewport viewport;

    private Stage stage;
    private Table table;
    private Skin skin;

    private Label title;
    private TextButton startGame,settings,exit;

    private Music backgroundMusic;

    public MenuScreen(ArkanoidGame game){
        this.game = game;
        viewport =  new FitViewport(Utilities.VIRTUAL_WIDTH,Utilities.VIRTUAL_HEIGHT);
        stage = new Stage(viewport);
        stage.setDebugAll(false);
        //skin here
        backgroundMusic = game.assetManager.manager.get(game.assetManager.backgroundMusic,Music.class);
        backgroundMusic.setLooping(true);

        if (game.getGameSettings().isMusicEnabled()){
            backgroundMusic.play();
        }
        else {
            backgroundMusic.pause();
        }
        backgroundMusic.setVolume(game.getGameSettings().getMusicVolume());
    }
    @Override
    public void show(){
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        //title = new Label("Arkanoid"); set title

        // buttons
        startGame = new TextButton("Start Game", skin);
        startGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Start Game");

                // change to main screen
                game.screenManager.changeScreen(ScreenManager.APPLICATION);
            }
        });

        settings = new TextButton("Settings", skin);
        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Settings");

                // change to setting screen
                game.screenManager.changeScreen(ScreenManager.PREFERENCES);
            }
        });

        exit = new TextButton("Exit", skin);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Exit");

                // exit
                Gdx.app.exit();
            }
        });

        table.add(title).fillX().uniformX();
        table.row().pad(40, 0, 20, 0);
        table.add(startGame);
        table.row().pad(20);
        table.add(settings);
        table.row().pad(20);
        table.add(exit);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1); //  clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
        stage.dispose();
        skin.dispose();
    }
    }
}
