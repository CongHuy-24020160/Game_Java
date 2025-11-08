package game.Screen;

import com.badlogic.gdx.Screen;
import game.ArkanoidGame;
import game.GameData;

public class ScreenManager {
    public final ArkanoidGame game;

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
    private Screen currentScreen;

    public ScreenManager(ArkanoidGame game) {
        this.game = game;
    }

    /**
     * Hàm nội bộ để dọn dẹp màn hình cũ và đặt màn hình mới.
     */
    private void setScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.dispose(); //  Dọn dẹp màn hình cũ
        }
        currentScreen = screen; //  Đặt màn hình mới
        game.setScreen(currentScreen);
    }

    public void changeScreen(int screen) {
        switch (screen) {
            case MENU:
                if (ArkanoidGame.DEBUG_MODE) {
                    System.out.println("ScreenManager.java) Changing to Menu Screen");
                }
                //if (menuScreen == null) menuScreen = new MenuScreen(game);
                setScreen(new MenuScreen(game));
                break;
            case PREFERENCES:
                //if (preferencesScreen == null) preferencesScreen = new PreferenceScreen(game);
                setScreen(new PreferenceScreen(game));
                break;
            case APPLICATION:
                if (ArkanoidGame.IS_LOADING_SAVE_GAME) {

                    // Nếu là LOAD:
                    GameData savedData = GameData.load(); // Tải dữ liệu
                    setScreen(new MainScreen(game, savedData));

                } else {

                    // Nếu là NEW GAME:
                    setScreen(new MainScreen(game));
                }

                // Reset cờ hiệu (dù là load hay new)
                ArkanoidGame.IS_LOADING_SAVE_GAME = false;
                break;
            case ENDGAME:
                if (ArkanoidGame.DEBUG_MODE) {
                    System.out.println("ScreenManager.java) Changing to End Screen");
                }
                //if (endScreen == null) endScreen = new EndScreen(game);
                setScreen(new EndScreen(game));
                break;
            case LOADING:
                //if (loadingScreen == null) loadingScreen = new LoadingScreen(game);
                if (ArkanoidGame.DEBUG_MODE) System.out.println("(ScreenManager.java) Changing to Loading Screen");
                setScreen(new LoadingScreen(game));
                break;
            case HIGHSCORE: // ⭐️ THÊM KHỐI NÀY ⭐️
                //if (highScoreScreen == null) highScoreScreen = new HighScoreScreen(game);
                setScreen(new HighScoreScreen(game));
                break;
            case ENTER_HIGHSCORE: // ⭐️ THÊM KHỐI NÀY ⭐️
                // Luôn tạo mới để nó lấy điểm 'lastScore' mới nhất
                EnterHighScoreScreen enterHighScoreScreen = new EnterHighScoreScreen(game);
                setScreen(enterHighScoreScreen);
                break;
        }
    }
}
