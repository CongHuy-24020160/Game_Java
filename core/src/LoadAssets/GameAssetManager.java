package LoadAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GameAssetManager {

    private static volatile GameAssetManager instance;
    public final AssetManager manager;
    // Load background
    public final String backgroundImage = "images/background1.png";

    // Load sound
    public final String hitBrickSound = "sounds/ball_hit_brick.wav";
    public final String hitWallSound = "sounds/ball_hit_wall.wav";
    public final String gameOverSound = "sounds/game_over.wav";
    public final String missBallSound = "sounds/miss_the_ball.wav";

    // Load music
    public final String backgroundMusic = "sounds/backgroundMusic1.mp3";

    // Load font
    public final String gameFont = "fonts/GUNDAM.ttf";
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
        manager.load(backgroundImage, TextureData.class);
    }

    public void queueAddFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GUNDAM.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
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

    public void dispose(){
        if (manager != null){
            manager.dispose();
        }
        instance = null;
    }





}
