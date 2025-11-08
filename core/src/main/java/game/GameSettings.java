package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {
    private static volatile GameSettings instance;

    private static final String MUSIC_VOLUME = "volume";
    private static final String MUSIC_ENABLED = "musicEnabled";
    private static final String SOUND_ENABLED = "soundEnabled";
    private static final String SOUND_VOLUME = "volume";
    private static final String GAME_NAME = "ARKANOID";

    public static String getGameName() {
        return GAME_NAME;
    }
    private GameSettings(){
        if (instance != null){
            throw new IllegalStateException("Already initialized");
        }
    }
    public static GameSettings getInstance(){
        if (instance == null){
            synchronized (GameSettings.class){
                if (instance == null){
                    instance = new GameSettings();
                }
            }
        }
        return instance;
    }
    protected Preferences getPrefs(){
        return Gdx.app.getPreferences(GAME_NAME);
    }
    public float getMusicVolume(){
        return getPrefs().getFloat(MUSIC_VOLUME, 0.5f);
    }
    public void setMusicVolume(float volume){
        getPrefs().putFloat(MUSIC_VOLUME, volume);
        getPrefs().flush();
    }
    public boolean isMusicEnabled(){
        return getPrefs().getBoolean(MUSIC_ENABLED, true);
    }
    public void setMusicEnabled(boolean enabled){
        getPrefs().putBoolean(MUSIC_ENABLED, enabled);
        getPrefs().flush();
    }
    public boolean isSoundEnabled(){
        return getPrefs().getBoolean(SOUND_ENABLED, true);
    }
    public void setSoundEnabled(boolean enabled){
        getPrefs().putBoolean(SOUND_ENABLED, enabled);
        getPrefs().flush();
    }
    public float getSoundVolume(){
        return getPrefs().getFloat(SOUND_VOLUME, 0.5f);
    }

}
