package game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.component.BallComponent;
import game.level.LevelManager;
import game.Screen.ScreenManager;


import java.util.Locale;
import java.util.logging.Logger;

public class Hud implements Disposable {
    private static final Logger logger = Logger.getLogger(Hud.class.getName());
    private static final boolean DEBUG_MODE = true;

    private ArkanoidGame game;
    private Stage stage;
    public Stage getStage(){return stage;}
    private Skin skin;
    private TextureAtlas textures;
    private Viewport viewport;
    private LevelManager levelManager;

    private Dialog dialog;
    public Dialog getDialog(){return dialog;}
    public boolean dialogJustOpened, dialogJustClosed;

    public enum DialogType {NEXT_LEVEL, MENU, FINAL, GAME_OVER}
    public DialogType lastDialogType;

    public enum UserChoice{NEXT_LEVEL, RETRY, MENU, CANCEL, NONE}
    public UserChoice userChoice = UserChoice.NONE;

    // hud table
    private Table table, livesTable;
    private TextureRegion ballTexture;
    private Label scoreLabel, levelLabel, livesLabel;
    private Image ballImage;
    private int score, level, lives;

    public int getScore(){return score;}
    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel(){return level;}
    public void setLevel(int level) {
        this.level = level;
    }

    public int getLives(){return lives;}
    public void setLives(int lives) {
        this.lives = lives;
    }

    public Hud(ArkanoidGame game, LevelManager levelManager){
        if(DEBUG_MODE) logger.info("Constructor");

        this.game = game;
        this.levelManager = levelManager;

        textures = game.assetManager.manager.get(game.assetManager.gameImagaes);
        skin = game.assetManager.manager.get("ui/uiskin.json", Skin.class);
        //Fixme
        ballTexture = new TextureRegion(
            textures.findRegion("ball-sheet-removebg-preview"),
            8, 31, 25, 25
        );
        if (ballTexture.getTexture() == null){
            System.out.println("Ball Texture is null");
        }
        score = 0;
        level = 1;
        lives = 3;

        viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT,
            new OrthographicCamera());
        stage = new Stage(viewport);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        scoreLabel = new Label(String.format(Locale.getDefault(), "Score: %06d", score), new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        levelLabel = new Label("Level 1", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        livesLabel = new Label("Lives: ", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        table.top();

        // add lives
        livesTable = new Table();
        livesTable.setDebug(false);
        livesTable.add(livesLabel);
        for(int i = 0; i < lives; i++){
            ballImage = new Image(ballTexture);
            ballImage.setScale(0.85f);
            livesTable.add(ballImage);
        }

        table.add(levelLabel).left().padLeft(5).padTop(5).expandX();
        table.add(scoreLabel).right().padRight(5).padTop(5).expandX();
        table.row();
        table.add(livesTable).left().padLeft(5);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    // call this method to update the score, lives, level
    public void update(){
        levelLabel.setText("Level " + level);
        scoreLabel.setText(String.format(Locale.getDefault(), "Score: %06d", score));
    }

    public void render(){
        stage.act();
        stage.draw();
    }

    public void updateLives(){
        logger.info("Updating Lives");
        livesTable.clearChildren();
        livesTable.add(livesLabel);
        for(int i = 0; i < lives; i++){
            logger.info("Update Lives: " + lives);
            ballImage = new Image(ballTexture);
            ballImage.setScale(0.85f);
            livesTable.add(ballImage);
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    // opens a generic dialog which will have a confirm, cancel type button
    private void openDialog(String message, String positiveButtonText, String negativeButtonText,
                            ClickListener positiveListener, ClickListener negativeListener, DialogType dialogType){
        dialog = new Dialog("", skin);
        dialog.setModal(true);
        dialog.text(message);

        if(positiveButtonText != null){
            TextButton positiveButton = new TextButton(positiveButtonText, skin);
            positiveButton.addListener(positiveListener);
            dialog.button(positiveButton);
        }

        if(negativeButtonText != null){
            TextButton negativeButton = new TextButton(negativeButtonText, skin);
            negativeButton.addListener(negativeListener);
            dialog.button(negativeButton);
        }

        dialog.show(stage);
        dialog.setVisible(true);
        lastDialogType = dialogType;
        dialogJustOpened = true;
    }

    // Example method to show the level completion dialog
    public void showLevelCompleteDialog() {
        openDialog(
            level < LevelManager.MAX_LEVELS ? "Congratulations! You've completed Level " + level + "." : "Your final score is: " + score,
            level < LevelManager.MAX_LEVELS ? "Next Level" : "Menu",
            null,
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (level < LevelManager.MAX_LEVELS) {
                        levelManager.loadLevel(++level);

                        userChoice = UserChoice.NEXT_LEVEL;
                    }else{
                        // reset game state
                        level = 1;
                        lives = 3;
                        score = 0;

                        // return to screen
                        game.screenManager.changeScreen(ScreenManager.MENU);

                        userChoice = UserChoice.MENU;
                    }

                    handleDialogClosed();
                }
            },
            null,
            DialogType.NEXT_LEVEL
        );
    }

    public void showGameOverDialog(){
        openDialog(
            "Game Over",
            "Retry",
            "Menu",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // reset the current level
                    logger.info("clicked on retry");

                    // reset game state
                    level = 1;
                    lives = 3;
                    score = 0;

                    levelManager.loadLevel(level);

                    handleDialogClosed();
                    userChoice = UserChoice.RETRY;
                    updateLives();
                }
            },

            new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    logger.info("clicked on menu");

                    // reset game state
                    level = 1;
                    lives = 3;
                    score = 0;

                    // take user back to the menu screen
                    game.screenManager.changeScreen(ScreenManager.MENU);

                    handleDialogClosed();
                    userChoice = UserChoice.MENU;
                }
            },
            DialogType.GAME_OVER
        );
    }

    public void showMenuDialog(){
        System.out.println("Show Menu Dialog");
        openDialog(
            "Exit Game?",
            "Confirm",
            "Cancel",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // reset game state
                    level = 1;
                    lives = 3;
                    score = 0;

                    // return to screen
                    game.screenManager.changeScreen(ScreenManager.MENU);
                    handleDialogClosed();

                    userChoice = UserChoice.MENU;
                }
            },
            new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleDialogClosed();
                    userChoice = UserChoice.CANCEL;

//                        lives--;
//                        logger.info("Lives: " + lives);
                }
            },
            DialogType.MENU
        );
    }

    private void handleDialogClosed(){
        logger.info("Dialog Closed");
        Hud.this.dialog.setVisible(false);
        dialogJustClosed = true;
    }

    @Override
    public void dispose() {
        ballTexture.getTexture().dispose();
    }
}
