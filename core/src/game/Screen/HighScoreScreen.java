package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ArkanoidGame;
import game.Utilities;
import game.Utils.ScoreManager;

import java.util.List;

public class HighScoreScreen implements Screen {
    private ArkanoidGame game;
    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private BitmapFont font;

    public HighScoreScreen(ArkanoidGame game) {
        this.game = game;
        this.viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT);
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        this.skin = game.assetManager.manager.get(game.assetManager.skin, Skin.class);
        this.font = game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class);

        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("High Scores", new Label.LabelStyle(font, Color.WHITE));
        table.add(titleLabel).padBottom(40);
        table.row();

        List<ScoreManager.ScoreEntry> scores = ScoreManager.getInstance().loadScores();

        if (scores.isEmpty()) {
            Label noScoreLabel = new Label("Chua co diem nao", new Label.LabelStyle(font, Color.GRAY));
            table.add(noScoreLabel);
        } else {
            Table scoreTable = new Table();
            for (int i = 0; i < scores.size(); i++) {
                ScoreManager.ScoreEntry entry = scores.get(i);

                Label rankLabel = new Label((i + 1) + ".", new Label.LabelStyle(font, Color.WHITE));
                Label nameLabel = new Label(entry.playerName, new Label.LabelStyle(font, Color.YELLOW));
                Label scoreLabel = new Label(String.valueOf(entry.score), new Label.LabelStyle(font, Color.CYAN));

                scoreTable.add(rankLabel).padRight(10).left();
                scoreTable.add(nameLabel).padRight(20).left();
                scoreTable.add(scoreLabel).expandX().right();
                scoreTable.row();
            }
            table.add(scoreTable);
        }

        table.row().padTop(40);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.screenManager.changeScreen(ScreenManager.MENU);
            }
        });
        table.add(backButton).width(200).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        stage.dispose();
    }
}
