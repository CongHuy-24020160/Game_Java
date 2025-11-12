package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private final ArkanoidGame game;
    private final Stage stage;
    private TextureRegion backgroundTexture;
    private Music backgroundMusic;
    private Sound ding1Sound;

    /**
     * Hàm khởi tạo chỉ nên làm những việc cơ bản nhất,
     * không lấy tài nguyên ở đây.
     */
    public PreferenceScreen(ArkanoidGame game) {
        this.game = game;
        Viewport viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT);
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        Skin skin = game.assetManager.manager.get(game.assetManager.skin, Skin.class);
        this.backgroundMusic = game.assetManager.manager.get(game.assetManager.backgroundMusic, Music.class);
        this.ding1Sound = game.assetManager.manager.get(game.assetManager.hitBrickSound, Sound.class);

        TextureAtlas atlas = game.assetManager.manager.get(game.assetManager.gameImagaes, TextureAtlas.class);
        this.backgroundTexture = atlas.findRegion("background");
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        BitmapFont font = game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class);

        // Tiêu đề
        Label titleLabel = new Label("Settings", new Label.LabelStyle(font, Color.WHITE));
        titleLabel.setFontScale(2f);

        // Âm lượng nhạc
        Label volumeMusicLabel = new Label("Music Volume", new Label.LabelStyle(font, Color.WHITE));
        Slider volumeMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeMusicSlider.setValue(game.getGameSettings().getMusicVolume());
        volumeMusicSlider.addListener(event -> {
            float volume = volumeMusicSlider.getValue();
            game.getGameSettings().setMusicVolume(volume);
            backgroundMusic.setVolume(volume);
            return false;
        });

        // Âm lượng hiệu ứng
        Label volumeSoundLabel = new Label("Sound Volume", new Label.LabelStyle(font, Color.WHITE));
        Slider volumeSoundSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeSoundSlider.setValue(game.getGameSettings().getSoundVolume());
        volumeSoundSlider.addListener(new DragListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (game.getGameSettings().isSoundEnabled()) {
                    game.getGameSettings().setSoundVolume(volumeSoundSlider.getValue());
                    long id = ding1Sound.play();
                    ding1Sound.setVolume(id, volumeSoundSlider.getValue());
                }
            }
        });

        // Nút Back
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.changeScreen(ScreenManager.MENU);
            }
        });

        // Sắp xếp layout
        table.add(titleLabel).colspan(2).padBottom(40);
        table.row();
        table.add(volumeMusicLabel).left().padRight(20);
        table.add(volumeMusicSlider).width(400);
        table.row().padTop(20);
        table.add(volumeSoundLabel).left().padRight(20);
        table.add(volumeSoundSlider).width(400);
        table.row().padTop(40);
        table.add(backButton).colspan(2).width(200).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //  Cập nhật viewport của stage
        stage.getViewport().apply();

        //  Lấy batch (cọ vẽ) của stage và thiết lập
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        //  Bắt đầu vẽ
        stage.getBatch().begin();

        //  VẼ ẢNH NỀN (vừa với kích thước ảo)
        stage.getBatch().draw(backgroundTexture,
            0, 0,
            Utilities.VIRTUAL_WIDTH,
            Utilities.VIRTUAL_HEIGHT);

        //  Kết thúc vẽ batch
        stage.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
        stage.dispose();
    }
}
