package game.Utils;//import package hiện tại vô đây


import com.badlogic.gdx.audio.Sound;
// cái này là đường dẫn AppPreferences sau nhớ sửa

import game.GameSettings;
// cái này là đường dẫn B2dAssetManager sau nhớ sửa ( thằng huy code là GameAssetManager)
import game.LoadAssets.GameAssetManager;

// cái này để tạo ra 1 âm thanh duy nhất sử dung singleton pattern


public class UtilSound {
    private static volatile UtilSound instance;
    private final GameAssetManager assetManager;

    private UtilSound(){
        assetManager = GameAssetManager.getInstance();
    }

    public static UtilSound getInstance(){
        if(instance == null){
            instance = new UtilSound();
        }
        return instance;
    }

    public void playExplosion(){
        Sound explosionSound = assetManager.manager.get(assetManager.explosionSound);
        explosionSound.play(GameSettings.getInstance().getSoundVolume());
    }

    public void playDingSound1(){
        Sound dingSound1 = assetManager.manager.get(assetManager.ding1Sound);
        dingSound1.play(GameSettings.getInstance().getSoundVolume());
    }

    public void playDingSound2(){
        Sound dingSound2 = assetManager.manager.get(assetManager.ding2Sound);
        dingSound2.play(GameSettings.getInstance().getSoundVolume());
    }
}
