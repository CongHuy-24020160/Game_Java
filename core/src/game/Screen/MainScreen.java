package game.Screen;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ArkanoidGame;

import game.Hud;
import game.ScoreChangeListener;
import game.Utilities;
import game.Utils.ParticleHandler;
import game.controller.KeyboardController;
import game.level.B2dContactListener;
import game.level.LevelManager;
import game.system.*;

public class MainScreen implements Screen,ScoreChangeListener {

    private static final Logger logger = new Logger(MainScreen.class.getName());
    private ArkanoidGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PooledEngine engine;
    private LevelManager levelManager;
    private Hud hud;

    private CollisionSystem collisionSystem;
    private PhysicSystem physicSystem;
    private BallSystem ballSystem;
    private PlayerControlSystem playerControlSystem;
    private AttachSystem attachSystem;
    private SoundSystem soundSystem;
    private RenderingSystem renderingSystem;

    private InputMultiplexer inputMultiplexer;
    private KeyboardController keyboardController;
    private World world;
    private SpriteBatch spriteBatch;
    private ParticleHandler particleHandler;

    public MainScreen(ArkanoidGame game){
        this.game = game;
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Utilities.getPPMWidth(), Utilities.getPPMHeight(),
            camera);
        camera.setToOrtho(false,viewport.getWorldWidth(), viewport.getScreenHeight());
        camera.position.set(viewport.getWorldWidth()/2,viewport.getWorldHeight()/2,0);


        keyboardController = new KeyboardController();
        world = new World(new Vector2(0,0),true);
        world.setContactListener(new B2dContactListener());
        inputMultiplexer = new InputMultiplexer();

        engine = new PooledEngine();
        levelManager = new LevelManager(game,world,engine,camera);
        hud = new Hud(game, this, levelManager);



        particleHandler = new ParticleHandler("particles/test.p", "particles");
        particleHandler.resizeAll(1f);
        particleHandler.trigger(10, 20);

        spriteBatch.setProjectionMatrix(camera.combined);
    }
    @Override
    public void show(){
        System.out.println("Hello from MainScreen.java");
        logger.info("show");
        levelManager.loadLevel(1);

        hud.setLives(5);
        hud.setScore(0);
        hud.setLevel(1);
        hud.updateLives();

        physicSystem = new PhysicSystem(world, engine);
        ballSystem = new BallSystem(hud, levelManager);
        attachSystem = new AttachSystem();
        soundSystem = new SoundSystem(game.getGameSettings());
        playerControlSystem = new PlayerControlSystem(keyboardController, hud, levelManager,viewport);
        collisionSystem = new CollisionSystem(this, hud, levelManager, this);
        renderingSystem = new RenderingSystem(spriteBatch, camera);
        engine.addSystem(renderingSystem);
        engine.addSystem(physicSystem);

        engine.addSystem(ballSystem);
        engine.addSystem(attachSystem);

        engine.addSystem(collisionSystem);
        engine.addSystem(soundSystem);
        engine.addSystem(playerControlSystem);

        inputMultiplexer.addProcessor(hud.getStage());
        inputMultiplexer.addProcessor(keyboardController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }
    public void update(float delta){
        collisionSystem.particlesManager.update(delta);
        hud.update();
    }

    @Override
    public void render(float delta){
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        // hud resize
    }

    @Override
    public  void pause(){

    }

    @Override
    public void resume(){

    }
    @Override
    public void hide(){

    }
    @Override
    public void dispose(){
        // level dispose
    }

    public void onScoreChange(int appendScore) {
        hud.setScore(hud.getScore() + appendScore);
    }

    @Override
    public void onScoreChanged(int i) {
        hud.setScore(hud.getScore() + i);
    }
}
