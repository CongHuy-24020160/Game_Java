package game;

import game.LoadAssets.GameAssetManager;
import com.badlogic.gdx.Game;
import game.Screen.ScreenManager;

public class ArkanoidGame extends Game {
    public static final boolean DEBUG_MODE = true;
    public GameAssetManager assetManager;
    private GameSettings gameSettings;
    public static boolean IS_LOADING_SAVE_GAME = false;
    public int lastScore = 0;

    public ScreenManager screenManager;
    public GameSettings getGameSettings(){
        return gameSettings;
    }
    @Override
    public void create(){
        gameSettings = GameSettings.getInstance();
        screenManager = new ScreenManager(this);
        assetManager = GameAssetManager.getInstance();

        screenManager.changeScreen(ScreenManager.LOADING);

        assetManager.queueLoadMusic();
        assetManager.manager.finishLoading();
    }

    @Override
    public void render(){
        super.render();
    }

    @Override
    public void dispose(){
        if (DEBUG_MODE){
            System.out.println("Disposing game");
        }
        assetManager.dispose();
    }
}
