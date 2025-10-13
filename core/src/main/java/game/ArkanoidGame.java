package game;

import game.LoadAssets.GameAssetManager;
import com.badlogic.gdx.Game;
import game.Screen.ScreenManager;

public class ArkanoidGame extends Game {
    // change this to false to turn of debug mode
    public static final boolean DEBUG_MODE = false;

    public GameAssetManager assetManager;
    private GameSettings gameSettings;

    public ScreenManager screenManager;
    public GameSettings getGameSettings(){
        return gameSettings;
    }
    @Override
    public void create(){
        gameSettings = GameSettings.getInstance();
    }
    @Override
    public void render(){

    }
    @Override
    public void dispose(){

    }
}
