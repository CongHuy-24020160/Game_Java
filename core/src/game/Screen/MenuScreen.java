package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ArkanoidGame;
import game.data.GameData;
import game.Utilities;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class MenuScreen implements Screen {
    private ArkanoidGame game;
    private Viewport viewport;
    private Stage stage;

    // Các biến này sẽ được khởi tạo trong show()
    private TextureRegion backgroundTexture;
    private Table table;
    private Skin skin;
    private Label title;
    private TextButton startGame, settings, exit, highScores, continueButton;
    private Music backgroundMusic;


    /**
     * Hàm khởi tạo chỉ nên làm những việc cơ bản nhất,
     * không lấy tài nguyên ở đây.
     */
    public MenuScreen(ArkanoidGame game) {
        this.game = game;
        this.viewport = new FitViewport(Utilities.VIRTUAL_WIDTH, Utilities.VIRTUAL_HEIGHT);
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        // --- LẤY TÀI NGUYÊN Ở ĐÂY ---
        // Đây là thời điểm an toàn nhất để lấy tài nguyên đã được tải
        this.skin = game.assetManager.manager.get("ui/uiskin.json", Skin.class);
        // 1. Lấy atlas
        TextureAtlas atlas = game.assetManager.manager.get(game.assetManager.gameImagaes, TextureAtlas.class);
        // 2. Tìm ảnh nền "background" (tên file .png) bên trong atlas
        this.backgroundTexture = atlas.findRegion("background");
        // --- KẾT THÚC THÊM ---
        this.backgroundMusic = game.assetManager.manager.get(game.assetManager.backgroundMusic, Music.class);

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(game.getGameSettings().getMusicVolume());
        if (game.getGameSettings().isMusicEnabled()) {
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }

        // --- Bắt đầu xây dựng UI ---
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        title = new Label("ARKANOID", skin);

        final GameData savedGame = GameData.load(); // Gọi hàm load ở Bước 1
        boolean hasSave = (savedGame != null);
        continueButton = new TextButton("Continue", skin);

        continueButton.setDisabled(!hasSave); // Vô hiệu hóa nút nếu không có save
        if (!hasSave) {
            continueButton.setColor(Color.GRAY); // Làm cho nó mờ đi
        }

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (savedGame != null) { // Chỉ chạy nếu nút được kích hoạt
                    // 1. Đặt cờ hiệu thành TRUE
                    ArkanoidGame.IS_LOADING_SAVE_GAME = true;

                    // 2. Đổi màn hình như bình thường
                    game.screenManager.changeScreen(ScreenManager.APPLICATION);
                }
            }
        });


        // Nút Start Game
        startGame = new TextButton("New Game", skin);
        startGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameData.clear(); // Xóa file save khi bắt đầu game mới
                ArkanoidGame.IS_LOADING_SAVE_GAME = false;
                game.screenManager.changeScreen(ScreenManager.APPLICATION);
            }
        });

        // Nút Settings
        settings = new TextButton("Settings", skin);
        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.screenManager.changeScreen(ScreenManager.PREFERENCES);
            }
        });

        // Nút Exit
        exit = new TextButton("Exit", skin);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

// ... (code nút exit)


        highScores = new TextButton("High Scores", skin);
        highScores.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.screenManager.changeScreen(ScreenManager.HIGHSCORE);
            }
        });

        // Thêm các thành phần vào table
        table.add(title).expandX().padBottom(50);
        table.row();
        table.add(continueButton).width(300).height(60).pad(10);
        table.row();
        table.add(startGame).width(300).height(60).pad(10);
        table.row();
        table.add(settings).width(300).height(60).pad(10);
        table.row();
        table.add(highScores).width(300).height(60).pad(10);
        table.row();
        table.add(exit).width(300).height(60).pad(10);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Gdx.gl.glClearColor(0, 0, 0, 1);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //  stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        //  stage.draw();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Cập nhật viewport của stage
        stage.getViewport().apply();

        // 2. Lấy batch (cọ vẽ) của stage và thiết lập
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        // 3. Bắt đầu vẽ
        stage.getBatch().begin();

        // 4. VẼ ẢNH NỀN (vừa với kích thước ảo)
        stage.getBatch().draw(backgroundTexture,
            0, 0,
            Utilities.VIRTUAL_WIDTH,
            Utilities.VIRTUAL_HEIGHT);

        // 5. Kết thúc vẽ batch
        stage.getBatch().end();

        // 6. Vẽ các nút bấm (UI) đè lên trên ảnh nền
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
        // Giải phóng tài nguyên khi không cần nữa
        stage.dispose();
    }
}
