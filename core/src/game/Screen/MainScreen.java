package game.Screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.*;

import game.LoadAssets.BodyFactory;
import game.Utils.ParticleHandler;
import game.Utils.ScoreManager;
import game.Utils.UtilSound;
import game.component.GameStateComponent;
import game.controller.KeyboardController;
import game.data.GameData;
import game.level.B2dContactListener;
import game.level.LevelManager;
import game.system.*;

public class MainScreen implements Screen, ScoreChangeListener {

    private static final Logger logger = new Logger(MainScreen.class.getName());
    private final ArkanoidGame game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private PooledEngine engine;
    private final LevelManager levelManager;
    private Hud hud;

    private boolean gamePaused = false;

    private GameData loadedData = null;

    public boolean gameOverPending = false;
    public int finalScoreForGameOver = 0;

    private CollisionSystem collisionSystem;
    private PhysicSystem physicSystem;
    private BallSystem ballSystem;
    private PlayerControlSystem playerControlSystem;
    private AttachSystem attachSystem;

    private final InputMultiplexer inputMultiplexer;
    private final KeyboardController keyboardController;
    private World world;
    private SpriteBatch spriteBatch;

    public MainScreen(ArkanoidGame game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Utilities.getPPMWidth(), Utilities.getPPMHeight(),
            camera);
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getScreenHeight());
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);


        keyboardController = new KeyboardController();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new B2dContactListener());
        inputMultiplexer = new InputMultiplexer();

        engine = new PooledEngine();
        levelManager = new LevelManager(game, world, engine, camera);
        hud = new Hud(game, this, levelManager);


        ParticleHandler particleHandler = new ParticleHandler("particles/test.p", "particles");
        particleHandler.resizeAll(1f);
        particleHandler.trigger(10, 20);

        spriteBatch.setProjectionMatrix(camera.combined);
    }

    public MainScreen(ArkanoidGame game, GameData dataToLoad) {
        this(game); // Gọi constructor cũ ở trên
        this.loadedData = dataToLoad; // Lưu lại data
    }

    @Override
    public void show() {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("Hello from MainScreen.java");
        logger.info("show");

        // Kiểm tra xem có phải là "load game" không?
        if (loadedData != null) {
            if (ArkanoidGame.DEBUG_MODE) System.out.println("Đang TẢI game từ dữ liệu đã lưu...");
            levelManager.loadLevel(loadedData.level);
            hud.setLives(loadedData.lives);
            hud.setScore(loadedData.score);
            hud.setLevel(loadedData.level);
        } else {
            if (ArkanoidGame.DEBUG_MODE) System.out.println("Đang TẠO game mới...");
            levelManager.loadLevel(1);
            hud.setLives(5);
            hud.setScore(0);
            hud.setLevel(1);
        }
        hud.updateLives();

        Entity gameStateEntity = engine.createEntity();
        GameStateComponent gameState = engine.createComponent(GameStateComponent.class);
        gameStateEntity.add(gameState);
        engine.addEntity(gameStateEntity);

        physicSystem = new PhysicSystem(world, engine);
        ballSystem = new BallSystem(hud, levelManager, this);
        attachSystem = new AttachSystem();
        playerControlSystem = new PlayerControlSystem(keyboardController, hud, viewport, this);
        collisionSystem = new CollisionSystem(this, world, hud, levelManager, this, game);
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        PowerUpSystem powerUpSystem = new PowerUpSystem(hud);
        powerUpSystem.setGameStateEntity(gameStateEntity);

        engine.addSystem(new GameStateSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(physicSystem);

        engine.addSystem(ballSystem);
        engine.addSystem(attachSystem);

        engine.addSystem(collisionSystem);
        engine.addSystem(powerUpSystem);

        engine.addSystem(playerControlSystem);
        engine.addSystem(new PaddleResizeSystem());

        inputMultiplexer.addProcessor(0, hud.getStage());
        inputMultiplexer.addProcessor(keyboardController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void update(float delta) {
        collisionSystem.particlesManager.update(delta);
        hud.update();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOverPending) {
            gameOverPending = false;
            int score = finalScoreForGameOver;
            Gdx.app.postRunnable(() -> {
                pauseGameSystems();

                if (ScoreManager.getInstance().isHighScore(score)) {
                    game.lastScore = score;
                    ScreenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    game.lastScore = score;
                    ScreenManager.changeScreen(ScreenManager.ENDGAME);
                    UtilSound.getInstance().playGameOver();
                }
            });
            return;
        }

        // Update logic game
        update(delta);

        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);


        levelManager.renderLevel();

        engine.update(delta);

        collisionSystem.particlesManager.render(spriteBatch);


        hud.render();
    }

    /**
     * Tạm dừng các hệ thống logic game chính.
     * Rendering và HUD vẫn chạy.
     */
    public void pauseGameSystems() {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("Hệ thống game đã TẠM DỪNG!");
        if (physicSystem != null) physicSystem.setProcessing(false);
        if (ballSystem != null) ballSystem.setProcessing(false);
        if (playerControlSystem != null) playerControlSystem.setProcessing(false);
        if (attachSystem != null) attachSystem.setProcessing(false);
        if (collisionSystem != null) collisionSystem.setProcessing(false); // Dừng xử lý va chạm mới
        // if (soundSystem != null) soundSystem.setProcessing(false);
        gamePaused = true;
    }

    /**
     * Khởi động lại các hệ thống logic game.
     */
    public void resumeGameSystems() {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("Hệ thống game đã TIẾP TỤC!");
        if (physicSystem != null) physicSystem.setProcessing(true);
        if (ballSystem != null) ballSystem.setProcessing(true);
        if (playerControlSystem != null) playerControlSystem.setProcessing(true);
        if (attachSystem != null) attachSystem.setProcessing(true);
        if (collisionSystem != null) collisionSystem.setProcessing(true);
        gamePaused = false;
    }

    /**
     * Kiểm tra xem game có đang pause (bởi menu) hay không
     */
    public boolean isPaused() {
        return gamePaused;
    }

    @Override
    public void resize(int width, int height) {
        //  Cập nhật viewport cho Game (bóng, gạch...)
        viewport.update(width, height);

        //  Cập nhật viewport cho HUD (nút, điểm số, "Game Paused")
        hud.getStage().getViewport().update(width, height, true);
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
        if (ArkanoidGame.DEBUG_MODE) System.out.println("--- DỌN DẸP MAINSCREEN ---");

        pauseGameSystems();

        //  ĐỢI 1 FRAME ĐỂ SYSTEMS HOÀN THÀNH
        Gdx.app.postRunnable(() -> {
            // BÂY GIỜ MỚI XÓA
            if (collisionSystem != null) {
                collisionSystem.dispose();
                collisionSystem = null;
            }
            if (world != null) {
                world.dispose();
                world = null;
            }
            if (engine != null) {
                engine.removeAllEntities();
                engine.clearPools();
                engine = null;
            }
            if (spriteBatch != null) {
                spriteBatch.dispose();
                spriteBatch = null;
            }
            if (hud != null) {
                hud.dispose();
                hud = null;
            }
            BodyFactory.destroyInstance();
            if (ArkanoidGame.DEBUG_MODE) System.out.println("DỌN DẸP HOÀN TẤT");
        });
    }

    public void onScoreChange(int appendScore) {
        hud.setScore(hud.getScore() + appendScore);
    }

    @Override
    public void onScoreChanged(int i) {
        hud.setScore(hud.getScore() + i);
    }

}
