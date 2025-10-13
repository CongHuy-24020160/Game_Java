package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ArkanoidGame;
import game.Utilities;

public class PreferenceScreen implements Screen {
    private ArkanoidGame game;
    private Viewport viewport;

    // ui
    private Stage stage;
    private Table table;
    private Skin skin;

    private Label titleLabel, volumeMusicLabel, volumeSoundLabel, musicOnOffLabel, soundOnOffLabel;
    private Slider volumeMusicSlider, volumeSoundSlider;
    private TextButton musicOnOffCheck, soundOnOffCheck;
    private TextButton back;

    private Music backgroundMusic;
    private Sound ding1Sound, ding2Sound;

    public PreferenceScreen(ArkanoidGame game){
        this.game = game;
        viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT);
        stage = new Stage(viewport);
        stage.setDebugAll(false);
        skin = game.assetManager.manager.get("skin/craftacular-ui.json", Skin.class);

        backgroundMusic = game.assetManager.manager.get("music/night night.ogg");
        backgroundMusic.setLooping(true);

        ding1Sound = game.assetManager.manager.get("soundfx/ding_1.wav");
        ding2Sound = game.assetManager.manager.get("soundfx/ding_2.wav");
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        // labels
        titleLabel = new Label("Settings", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        volumeMusicLabel = new Label("Music Volume", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        volumeSoundLabel = new Label("Sound Volume", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        musicOnOffLabel = new Label("Music On/Off", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        soundOnOffLabel = new Label("Sound On/Off", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        // sliders
        volumeMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeMusicSlider.setValue(game.getGameSettings().getMusicVolume());
        volumeMusicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                game.getGameSettings().setMusicVolume(volumeMusicSlider.getValue());
                backgroundMusic.setVolume(volumeMusicSlider.getValue());
                return false;
            }
        });

        volumeSoundSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeSoundSlider.setValue(game.getGameSettings().getSoundVolume());
        volumeSoundSlider.addListener(new DragListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);

                if(!game.getGameSettings().isSoundEnabled()) return;
                game.getGameSettings().setMusicVolume(volumeSoundSlider.getValue());
                ding1Sound.setVolume(ding1Sound.play(), volumeSoundSlider.getValue());
                ding2Sound.setVolume(ding2Sound.play(), volumeSoundSlider.getValue());
            }
        });

        // on/off toggle buttons
        if(game.getGameSettings().isMusicEnabled()){
            musicOnOffCheck = new TextButton("Enabled", skin);
            musicOnOffCheck.setDisabled(false);
        }else{
            musicOnOffCheck = new TextButton("Disabled", skin);
            musicOnOffCheck.setDisabled(true);
        }

        musicOnOffCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // toggle
                musicOnOffCheck.setDisabled(!musicOnOffCheck.isDisabled());
                boolean enabled = !musicOnOffCheck.isDisabled();

                // update button
                if(enabled){
                    musicOnOffCheck.setDisabled(false);
                    musicOnOffCheck.setText("Enabled");
                }else{
                    musicOnOffCheck.setDisabled(true);
                    musicOnOffCheck.setText("Disabled");
                }

                // update prefs
                game.getGameSettings().setMusicEnabled(enabled);

                if(enabled){
                    backgroundMusic.play();
                }else{
                    backgroundMusic.stop();
                }
            }
        });

        if(game.getGameSettings().isSoundEnabled()){
            soundOnOffCheck = new TextButton("Enabled", skin);
            soundOnOffCheck.setDisabled(false);
        }else{
            soundOnOffCheck = new TextButton("Disabled", skin);
            soundOnOffCheck.setDisabled(true);
        }
        soundOnOffCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // toggle
                soundOnOffCheck.setDisabled(!soundOnOffCheck.isDisabled());
                boolean enabled = !soundOnOffCheck.isDisabled();

                // update button
                if(enabled){
                    soundOnOffCheck.setDisabled(false);
                    soundOnOffCheck.setText("Enabled");
                }else{
                    soundOnOffCheck.setDisabled(true);
                    soundOnOffCheck.setText("Disabled");
                }

                // update prefs
                game.getGameSettings().setSoundEnabled(enabled);
            }
        });

        back = new TextButton("Back", skin);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // go back to menu screen
                game.screenManager.changeScreen(ScreenManager.MENU);
            }
        });

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 0, 10);
        table.add(volumeMusicLabel);
        table.add(volumeMusicSlider).fillX();
        table.row().pad(10, 0, 0, 10);
        table.add(musicOnOffLabel);
        table.add(musicOnOffCheck);
        table.row().pad(10, 0, 0, 10);
        table.add(volumeSoundLabel);
        table.add(volumeSoundSlider).fillX();
        table.row().pad(10, 0, 0, 10);
        table.add(soundOnOffLabel);
        table.add(soundOnOffCheck);
        table.row().pad(10, 0, 0, 10);
        table.add(back).colspan(2);

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
        stage.getViewport().update(width, height);
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
        stage.dispose();
        skin.dispose();
    }
}
