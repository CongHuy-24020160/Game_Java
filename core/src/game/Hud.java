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

/**
 * Lớp {@code Hud} chịu trách nhiệm hiển thị giao diện HUD (Heads-Up Display)
 * trong game Arkanoid, bao gồm điểm, mạng, cấp độ, và các hộp thoại (dialog)
 * như Game Over, Menu, hoặc Next Level.
 */
public class Hud implements Disposable {
    private static final Logger logger = Logger.getLogger(Hud.class.getName());
    private static final boolean DEBUG_MODE = true;
    // dùng default live để đỡ phải gán nhiều
    private static final int DEFAULT_LIVES = 5;

    /** Tham chiếu đến game chính */
    private ArkanoidGame game;

    /** Stage chứa các actor giao diện HUD */
    private Stage stage;

    /** Trả về Stage để có thể thêm Actor từ bên ngoài */
    public Stage getStage(){return stage;}

    private Skin skin;
    private TextureAtlas textures;
    private Viewport viewport;
    private LevelManager levelManager;

    /** Hộp thoại hiện tại (Menu, Next Level, v.v...) */
    private Dialog dialog;

    /** Trả về dialog hiện tại */
    public Dialog getDialog(){return dialog;}

    /** Biến cờ cho biết dialog vừa được mở hoặc đóng */
    public boolean dialogJustOpened, dialogJustClosed;

    /**
     * Các loại dialog có thể xuất hiện.
     */
    public enum DialogType {NEXT_LEVEL, MENU, FINAL, GAME_OVER}
    public DialogType lastDialogType;

    /**
     * Các lựa chọn của người dùng khi tương tác dialog.
     */
    public enum UserChoice{NEXT_LEVEL, RETRY, MENU, CANCEL, NONE}
    public UserChoice userChoice = UserChoice.NONE;

    // Bảng chứa các thành phần HUD
    private Table table, livesTable;
    private TextureRegion ballTexture;
    private Label scoreLabel, levelLabel, livesLabel;
    private Image ballImage;
    private int score, level, lives;

    /** Getter và setter cho điểm, cấp độ, mạng */
    public int getScore(){return score;}
    public void setScore(int score) { this.score = score; }
    public int getLevel(){return level;}
    public void setLevel(int level) { this.level = level; }
    public int getLives(){return lives;}
    public void setLives(int lives) { this.lives = lives; }

    /**
     * Khởi tạo HUD.
     *
     * @param game Tham chiếu đến game chính.
     * @param levelManager Quản lý các cấp độ của trò chơi.
     */
    public Hud(ArkanoidGame game, LevelManager levelManager){
        if(DEBUG_MODE) logger.info("Constructor");

        this.game = game;
        this.levelManager = levelManager;

        // Lấy atlas và skin từ AssetManager
        textures = game.assetManager.manager.get(game.assetManager.gameImagaes);
        skin = game.assetManager.manager.get("ui/uiskin.json", Skin.class);

        // Lấy texture quả bóng hiển thị mạng sống
        ballTexture = new TextureRegion(
            textures.findRegion("Ball_small-blue"),
            8, 31, 25, 25
        );
        if (ballTexture.getTexture() == null){
            System.out.println("Ball Texture is null");
        }

        // Giá trị khởi tạo
        score = 0;
        level = 1;
        lives = DEFAULT_LIVES ; // tăng mạng để game đỡ khó

        // Khởi tạo viewport và stage cho HUD
        viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT,
            new OrthographicCamera());
        stage = new Stage(viewport);

        // Tạo table chính chứa các thành phần HUD
        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        // Label điểm, cấp độ, mạng
        scoreLabel = new Label(String.format(Locale.getDefault(), "Score: %06d", score), new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        levelLabel = new Label("Level 1", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        livesLabel = new Label("Lives: ", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));

        table.top();

        // Bảng con hiển thị mạng sống bằng hình bóng
        livesTable = new Table();
        livesTable.setDebug(false);
        livesTable.add(livesLabel);
        for(int i = 0; i < lives; i++){
            ballImage = new Image(ballTexture);
            ballImage.setScale(0.85f);
            livesTable.add(ballImage);
        }

        // Thêm các thành phần vào bảng HUD
        table.add(levelLabel).left().padLeft(5).padTop(5).expandX();
        table.add(scoreLabel).right().padRight(5).padTop(5).expandX();
        table.row();
        table.add(livesTable).left().padLeft(5);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Cập nhật thông tin HUD (điểm, mạng, cấp độ) theo giá trị hiện tại.
     */
    public void update(){
        levelLabel.setText("Level " + level);
        scoreLabel.setText(String.format(Locale.getDefault(), "Score: %06d", score));
    }

    /**
     * Vẽ HUD lên màn hình.
     */
    public void render(){
        stage.act();
        stage.draw();
    }

    /**
     * Cập nhật lại phần hiển thị mạng sống khi người chơi mất hoặc được thêm mạng.
     */
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

    /**
     * Gọi khi kích thước màn hình thay đổi để cập nhật viewport.
     *
     * @param width chiều rộng mới
     * @param height chiều cao mới
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Mở dialog với các nút xác nhận/hủy tuỳ chỉnh.
     *
     * @param message Nội dung thông báo
     * @param positiveButtonText Nhãn nút xác nhận
     * @param negativeButtonText Nhãn nút hủy
     * @param positiveListener Sự kiện khi nhấn xác nhận
     * @param negativeListener Sự kiện khi nhấn hủy
     * @param dialogType Loại dialog đang mở
     */
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

    /**
     * Hiển thị dialog hoàn thành cấp độ.
     */
    public void showLevelCompleteDialog() {
        logger.info("Showing Level Complete Dialog");
        openDialog(
            level < LevelManager.MAX_LEVELS ? "Congratulations! You've completed Level " + level + "." : "Your final score is: " + score,
            level < LevelManager.MAX_LEVELS ? "Next Level" : "Menu",
            null,
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (level < LevelManager.MAX_LEVELS) {
                        levelManager.loadLevel(++level);
                        System.out.println("They clicked");
                        userChoice = UserChoice.NEXT_LEVEL;
                    }else{
                        // reset game state
                        level = 1;
                        lives = DEFAULT_LIVES ;
                        score = 0;

                        // quay về menu
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

    /**
     * Hiển thị dialog "Game Over" khi người chơi thua.
     */
    public void showGameOverDialog(){
        openDialog(
            "Game Over",
            "Retry",
            "Menu",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // reset lại game
                    logger.info("clicked on retry");
                    level = 1;
                    lives = DEFAULT_LIVES ;
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
                    level = 1;
                    lives = DEFAULT_LIVES;
                    score = 0;
                    game.screenManager.changeScreen(ScreenManager.MENU);
                    handleDialogClosed();
                    userChoice = UserChoice.MENU;
                }
            },
            DialogType.GAME_OVER
        );
    }

    /**
     * Hiển thị dialog xác nhận khi người chơi mở menu tạm dừng.
     */
    public void showMenuDialog(){
        System.out.println("Show Menu Dialog");
        openDialog(
            "Exit Game?",
            "Confirm",
            "Cancel",
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // reset game và quay lại menu
                    level = 1;
                    lives = DEFAULT_LIVES;
                    score = 0;
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
                }
            },
            DialogType.MENU
        );
    }

    /**
     * Xử lý khi dialog được đóng, ẩn dialog và cập nhật trạng thái.
     */
    private void handleDialogClosed(){
        logger.info("Dialog Closed");
        Hud.this.dialog.setVisible(false);
        dialogJustClosed = true;
    }

    /**
     * Giải phóng tài nguyên HUD khi không còn sử dụng.
     */
    @Override
    public void dispose() {
        ballTexture.getTexture().dispose();
    }
}
