package game.Screen;

import com.badlogic.gdx.Screen;
import game.ArkanoidGame;
import game.data.GameData;

public class ScreenManager {
    private static ArkanoidGame game;

    public static final int MENU = 0;
    public static final int PREFERENCES = 1;
    public static final int APPLICATION = 2;
    public static final int ENDGAME = 3;
    public static final int LOADING = 4;
    public static final int HIGHSCORE = 5;       // ⭐️ THÊM DÒNG NÀY ⭐️
    public static final int ENTER_HIGHSCORE = 6;

    // screens to load
    private LoadingScreen loadingScreen;
    private PreferenceScreen preferencesScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;
    private HighScoreScreen highScoreScreen;
    private static Screen currentScreen;

    private static ScreenManager instance;

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager(game);
        }
        return instance;
    }

    public ScreenManager(ArkanoidGame game) {
        this.game = game;
    }

    /**
     * Hàm nội bộ để dọn dẹp màn hình cũ và đặt màn hình mới.
     */
    private static void setScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.dispose(); //  Dọn dẹp màn hình cũ
        }
        currentScreen = screen; //  Đặt màn hình mới
        game.setScreen(currentScreen);
    }

    public static void changeScreen(int screen) {
        switch (screen) {
            case MENU:
                if (ArkanoidGame.DEBUG_MODE) {
                    System.out.println("ScreenManager.java) Changing to Menu Screen");
                }
                setScreen(new MenuScreen(game));
                break;
            case PREFERENCES:
                setScreen(new PreferenceScreen(game));
                break;
            case APPLICATION:
                if (ArkanoidGame.IS_LOADING_SAVE_GAME) {
                    GameData savedData = GameData.load(); // Tải dữ liệu
                    setScreen(new MainScreen(game, savedData));

                } else {
                    setScreen(new MainScreen(game));
                }
                // Reset cờ hiệu (dù là load hay new)
                ArkanoidGame.IS_LOADING_SAVE_GAME = false;
                break;
            case ENDGAME:
                if (ArkanoidGame.DEBUG_MODE) {
                    System.out.println("ScreenManager.java) Changing to End Screen");
                }
                setScreen(new EndScreen(game));
                break;
            case LOADING:
                if (ArkanoidGame.DEBUG_MODE) System.out.println("(ScreenManager.java) Changing to Loading Screen");
                setScreen(new LoadingScreen(game));
                break;
            case HIGHSCORE:
                setScreen(new HighScoreScreen(game));
                break;
            case ENTER_HIGHSCORE:
                EnterHighScoreScreen enterHighScoreScreen = new EnterHighScoreScreen(game);
                setScreen(enterHighScoreScreen);
                break;
        }
    }
}
