package game.Screen;

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
import game.ArkanoidGame;

import game.Utilities;

public class MainScreen implements Screen {

    private static final Logger logger = new Logger(MainScreen.class.getName());
    private ArkanoidGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PooledEngine engine;

    private InputMultiplexer inputMultiplexer;
    private World world;
    private SpriteBatch spriteBatch;
    // Particles Manager
    public MainScreen(ArkanoidGame game){
        this.game = game;
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Utilities.getPPMWidth(), Utilities.getPPMHeight(),
            camera);
        camera.setToOrtho(false,viewport.getWorldWidth(), viewport.getScreenHeight());
        camera.position.set(viewport.getWorldWidth()/2,viewport.getWorldHeight()/2,0);
        // control -- here
        world = new World(new Vector2(0,0),true);
        // ContactListener -- here
        inputMultiplexer = new InputMultiplexer();

        engine = new PooledEngine();
        // LeverManager
        // Hud

        spriteBatch.setProjectionMatrix(camera.combined);
    }
    @Override
    public void show(){
        logger.info("show");


    }
    public void update(float delta){

    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        //load level
        engine.update(delta);

        // particles

        // render hud at the end so it overlays on top of everything

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

    public void onScoreChange(int appendScore){
        // hud8
    }
}
