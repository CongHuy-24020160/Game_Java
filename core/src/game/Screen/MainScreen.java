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
import game.component.GameStateComponent;
import game.controller.KeyboardController;
import game.data.GameData;
import game.level.B2dContactListener;
import game.level.LevelManager;
import game.system.*;

public class MainScreen implements Screen, ScoreChangeListener {

    private static final Logger logger = new Logger(MainScreen.class.getName());
    private ArkanoidGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PooledEngine engine;
    private LevelManager levelManager;
    private Hud hud;

    private boolean gamePaused = false;

    private GameData loadedData = null;

    public boolean gameOverPending = false;
    public int finalScoreForGameOver = 0;
    public int livesToSubtract = 0;

    private CollisionSystem collisionSystem;
    private PhysicSystem physicSystem;
    private BallSystem ballSystem;
    private PlayerControlSystem playerControlSystem;
    private AttachSystem attachSystem;
    private SoundSystem soundSystem;
    private RenderingSystem renderingSystem;
    private PowerUpSystem powerUpSystem;

    private InputMultiplexer inputMultiplexer;
    private KeyboardController keyboardController;
    private World world;
    private SpriteBatch spriteBatch;
    private ParticleHandler particleHandler;

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


        particleHandler = new ParticleHandler("particles/test.p", "particles");
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
        System.out.println("Hello from MainScreen.java");
        logger.info("show");

        // Kiểm tra xem có phải là "load game" không?
        if (loadedData != null) {
            // == LOAD GAME ==
            System.out.println("Đang TẢI game từ dữ liệu đã lưu...");
            levelManager.loadLevel(loadedData.level);
            hud.setLives(loadedData.lives);
            hud.setScore(loadedData.score);
            hud.setLevel(loadedData.level);
        } else {
            // == NEW GAME == (Như code cũ của bạn)
            System.out.println("Đang TẠO game mới...");
            levelManager.loadLevel(1);
            hud.setLives(5); // (Hoặc Hud.DEFAULT_LIVES)
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
        soundSystem = new SoundSystem(game.getGameSettings());
        playerControlSystem = new PlayerControlSystem(keyboardController, hud, levelManager, viewport, this);
        collisionSystem = new CollisionSystem(this, engine, world, hud, levelManager, this, game);
        renderingSystem = new RenderingSystem(spriteBatch, camera);
        powerUpSystem = new PowerUpSystem(hud);
        powerUpSystem.setGameStateEntity(gameStateEntity);

        engine.addSystem(new GameStateSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(physicSystem);

        engine.addSystem(ballSystem);
        engine.addSystem(attachSystem);

        engine.addSystem(collisionSystem);
        engine.addSystem(powerUpSystem);

        engine.addSystem(soundSystem);
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
                    game.screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    game.lastScore = score;
                    game.screenManager.changeScreen(ScreenManager.ENDGAME);
                }
            });
            return; // THOÁT RENDER ĐỂ TRÁNH UPDATE SAU ĐÓ
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
        System.out.println("Hệ thống game đã TẠM DỪNG!");
        if (physicSystem != null) physicSystem.setProcessing(false);
        if (ballSystem != null) ballSystem.setProcessing(false);
        if (playerControlSystem != null) playerControlSystem.setProcessing(false);
        if (attachSystem != null) attachSystem.setProcessing(false);
        if (collisionSystem != null) collisionSystem.setProcessing(false); // Dừng xử lý va chạm mới
        if (soundSystem != null) soundSystem.setProcessing(false);
        gamePaused = true;
    }

    /**
     * Khởi động lại các hệ thống logic game.
     */
    public void resumeGameSystems() {
        System.out.println("Hệ thống game đã TIẾP TỤC!");
        if (physicSystem != null) physicSystem.setProcessing(true);
        if (ballSystem != null) ballSystem.setProcessing(true);
        if (playerControlSystem != null) playerControlSystem.setProcessing(true);
        if (attachSystem != null) attachSystem.setProcessing(true);
        if (collisionSystem != null) collisionSystem.setProcessing(true);
        if (soundSystem != null) soundSystem.setProcessing(true);
        gamePaused = false;
    }

    /**
     * Kiểm tra xem game có đang pause (bởi menu) hay không
     */
    public boolean isPaused() {
        return gamePaused;
    }

    // ĐÃ XÓA PHẦN BỊ XUNG ĐỘT (CONFLICT)

    @Override
    public void resize(int width, int height) {
        // 1. Cập nhật viewport cho Game (bóng, gạch...)
        viewport.update(width, height);

        // 2. Cập nhật viewport cho HUD (nút, điểm số, "Game Paused")
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
        System.out.println("--- DỌN DẸP MAINSCREEN ---");

        pauseGameSystems();

        // ⭐️ ĐỢI 1 FRAME ĐỂ SYSTEMS HOÀN THÀNH
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
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
                if (levelManager != null) {
                    levelManager.dispose();
                    levelManager = null;
                }
                BodyFactory.destroyInstance();
                System.out.println("--- DỌN DẸP HOÀN TẤT ---");
            }
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
