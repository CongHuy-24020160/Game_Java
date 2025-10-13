package game;

import game.LoadAssets.GameAssetManager;
import com.badlogic.gdx.Game;
import game.Screen.ScreenManager;

public class ArkanoidGame extends Game {
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
