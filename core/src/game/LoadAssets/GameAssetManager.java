package game.LoadAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {

    private static volatile GameAssetManager instance;
    public final AssetManager manager;
    // Load background

    public final String gameImagaes = "images/ArkanoidGame.atlas";

    // Load sound
    public final String hitBrickSound = "sounds/ball_hit_brick.wav";
    public final String hitWallSound = "sounds/ball_hit_wall.wav";
    public final String gameOverSound = "sounds/game_over.wav";
    public final String missBallSound = "sounds/miss_the_ball.wav";

    // Load music
    public final String backgroundMusic = "sounds/backgroundMusic1.mp3";

    // Load font
    public final String gameFont = "fonts/GUNDAM.ttf";
    //Load skin
    public final String skin = "ui/uiskin.json";
    // Private constructor
    private GameAssetManager() {
        manager = new AssetManager();
    }

    public static GameAssetManager getInstance() {
        if (instance == null) {
            synchronized (GameAssetManager.class) {
                if (instance == null) {
                    instance = new GameAssetManager();
                }
            }
        }
        return instance;
    }

    public void queueAddImages(){
        manager.load(gameImagaes, TextureAtlas.class);
    }
    // Fonts can cause errors
    public void queueAddFonts(){
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(new InternalFileHandleResolver()));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameterSmall = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameterSmall.fontFileName = gameFont;
        fontParameterSmall.fontParameters.size = 18;
        manager.load(gameFont, BitmapFont.class, fontParameterSmall);
    }

    public void queueLoadSound(){
        manager.load(hitBrickSound, Sound.class);
        manager.load(hitWallSound, Sound.class);
        manager.load(gameOverSound, Sound.class);
        manager.load(missBallSound, Sound.class);
    }
    public void queueLoadMusic(){
        manager.load(backgroundMusic, Music.class);
    }
    public void queueLoadSkin(){
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("ui/uiskin.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void dispose(){
        if (manager != null){
            manager.dispose();
        }
        instance = null;
    }





}
