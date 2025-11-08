package game.Screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import game.ArkanoidGame;
import game.Utilities;

public class EndScreen implements Screen {
    private ArkanoidGame game;
    private Stage stage;
    private Skin skin;
    private int score;
    private World world;
    private Engine engine;

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
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // === TIÊU ĐỀ ===
        Label title = new Label("GAME OVER", skin, "title");
        title.setFontScale(2.2f);
        title.setColor(1, 0.3f, 0.3f, 1); // Đỏ
        table.add(title).padBottom(50).row();

        // === ĐIỂM SỐ ===
        Label scoreLabel = new Label("Score: " + score, skin);
        scoreLabel.setFontScale(1.6f);
        table.add(scoreLabel).padBottom(80).row();

        // === NÚT RETRY ===
        TextButton retryButton = new TextButton("RETRY", skin);
        retryButton.getLabel().setFontScale(1.4f);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.changeScreen(ScreenManager.APPLICATION);
            }
        });
        table.add(retryButton).width(320).height(90).padBottom(25).row();

        // === NÚT MENU ===
        TextButton menuButton = new TextButton("MENU", skin);
        menuButton.getLabel().setFontScale(1.4f);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.changeScreen(ScreenManager.MENU);
            }
        });
        table.add(menuButton).width(320).height(90);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1); // Nền tím đậm
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

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
