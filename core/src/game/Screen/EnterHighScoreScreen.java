package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ArkanoidGame;
import game.Utilities;
import game.Utils.ScoreManager;

public class EnterHighScoreScreen implements Screen {
    private ArkanoidGame game;
    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private BitmapFont font;
    private Table table;

    private Label titleLabel;
    private Label scoreLabel;
    private TextField nameField;
    private TextButton submitButton;

    private int currentScore;

    public EnterHighScoreScreen(ArkanoidGame game) {
        this.game = game;
        // Dùng kích thước ảo (VIRTUAL_WIDTH/HEIGHT) giống như MenuScreen của bạn
        this.viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT);
        this.stage = new Stage(viewport);
        
        // Lấy điểm số cuối cùng từ biến tạm (chúng ta sẽ tạo biến này sau)
        this.currentScore = game.lastScore;
    }

    @Override
    public void show() {
        this.skin = game.assetManager.manager.get(game.assetManager.skin, Skin.class);
        this.font = game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class);

        Gdx.input.setInputProcessor(stage);
        stage.clear();

        table = new Table();
        table.setFillParent(true);

        titleLabel = new Label("New High Score!", new Label.LabelStyle(font, Color.YELLOW));
        scoreLabel = new Label("Your Score: " + currentScore, new Label.LabelStyle(font, Color.WHITE));

        nameField = new TextField("", skin);
        nameField.setMessageText("Enter Your Name");
        
        submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleSubmit();
            }
        });

        table.add(titleLabel).padBottom(20);
        table.row();
        table.add(scoreLabel).padBottom(40);
        table.row();
        table.add(nameField).width(300).padBottom(20);
        table.row();
        table.add(submitButton).width(200).height(50);

        stage.addActor(table);
    }

    private void handleSubmit() {
        String playerName = nameField.getText();
        
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "PLAYER";
        }

        ScoreManager.getInstance().addScore(playerName, this.currentScore);
        game.screenManager.changeScreen(ScreenManager.HIGHSCORE);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}