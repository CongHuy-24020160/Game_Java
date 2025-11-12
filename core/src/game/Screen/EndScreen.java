package game.Screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import game.ArkanoidGame;
import game.Utilities;

public class EndScreen implements Screen {
    private final ArkanoidGame game;
    private Stage stage;
    private final Skin skin;
    private final int score;
    private World world;
    private Engine engine;
    private TextureRegion backgroundTexture;

    public EndScreen(ArkanoidGame game) {
        this.game = game;
        this.score = game.lastScore;

        // Tạo Stage + Viewport
        stage = new Stage(new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Load skin
        skin = game.assetManager.manager.get("ui/uiskin.json", Skin.class);

        createUI();
    }

    private void createUI() {
        TextureAtlas atlas = game.assetManager.manager.get(game.assetManager.gameImagaes, TextureAtlas.class);
        this.backgroundTexture = atlas.findRegion("background");
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //TIÊU ĐỀ
        Label title = new Label("GAME OVER", skin, "title");
        title.setFontScale(1.75f);
        title.setColor(Color.RED); // Đỏ
        table.add(title).padBottom(20).row();

        //ĐIỂM SỐ
        Label.LabelStyle ls = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        ls.fontColor = Color.GOLD;
        Label scoreLabel = new Label("Score: " + score, ls);
        scoreLabel.setFontScale(2.5f);
        table.add(scoreLabel).padBottom(25).row();


        //NÚT RETRY
        TextButton retryButton = new TextButton("RETRY", skin);
        retryButton.getLabel().setFontScale(1.1f);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.changeScreen(ScreenManager.APPLICATION);
            }
        });
        table.add(retryButton).width(300).height(70).padBottom(25).row();

        //NÚT MENU
        TextButton menuButton = new TextButton("MENU", skin);
        menuButton.getLabel().setFontScale(1.1f);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.changeScreen(ScreenManager.MENU);
            }
        });
        table.add(menuButton).width(300).height(70).padBottom(25).row();

        //NÚT EXIT
        TextButton exitButton = new TextButton("EXIT GAME", skin);
        exitButton.getLabel().setFontScale(1.1f);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.add(exitButton).width(300).height(70);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1); // Nền tím đậm
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        stage.act(delta);
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
        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        // DỌN DẸP ENTITY
        if (engine != null) {
            engine.removeAllEntities(); // XÓA TẤT CẢ ENTITY
            engine = null;
        }

        // DỌN DẸP WORLD
        if (world != null) {
            world.dispose();
            world = null;
        }
    }

}
