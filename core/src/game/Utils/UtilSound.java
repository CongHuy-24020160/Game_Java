//import package hiện tại vô đây
package game.Utils;

import com.badlogic.gdx.audio.Sound;
// cái này là đường dẫn AppPreferences sau nhớ sửa

import game.GameSettings;
// cái này là đường dẫn B2dAssetManager sau nhớ sửa ( thằng huy code là GameAssetManager)
import game.LoadAssets.GameAssetManager;

// cái này để tạo ra 1 âm thanh duy nhất sử dung singleton pattern


public class UtilSound {
    private static volatile UtilSound instance;
    private GameAssetManager assetManager;

    private UtilSound(){
        assetManager = GameAssetManager.getInstance();
    }

    public static UtilSound getInstance(){
        if(instance == null){
            instance = new UtilSound();
        }
        return instance;
    }
    // We don't use it now
    //    public void playExplosion(){
    //        Sound explosionSound = assetManager.manager.get(assetManager.explosionSound);
    //        explosionSound.play(AppPreferences.getInstance().getSoundVolume());
    //    }

    public void playDingSound1(){
        Sound dingSound1 = assetManager.manager.get(assetManager.hitBrickSound);
        dingSound1.play(GameSettings.getInstance().getSoundVolume());
    }

    public void playDingSound2(){
        Sound dingSound2 = assetManager.manager.get(assetManager.hitWallSound);
        dingSound2.play(GameSettings.getInstance().getSoundVolume());
    }
    public void playGameOver(){
        Sound gameOverSound = assetManager.manager.get(assetManager.gameOverSound);
        gameOverSound.play(GameSettings.getInstance().getSoundVolume());
    }
    public void playMissBallSound(){
        Sound missBallSound = assetManager.manager.get(assetManager.missBallSound);
        missBallSound.play(GameSettings.getInstance().getSoundVolume());
    }
    public void playBackgroundMusic(){
        Sound backgroundMusic = assetManager.manager.get(assetManager.backgroundMusic);
    }
}
