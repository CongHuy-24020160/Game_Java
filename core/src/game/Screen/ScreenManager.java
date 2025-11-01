package game.Screen;

import game.ArkanoidGame;

public class ScreenManager {
    private final ArkanoidGame game;

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
    private HighScoreScreen highScoreScreen;       // ⭐️ THÊM DÒNG NÀY ⭐️
    private EnterHighScoreScreen enterHighScoreScreen;

    public ScreenManager(ArkanoidGame game){this.game = game;}

    public void changeScreen(int screen){
        switch(screen){
            case MENU:
                if (ArkanoidGame.DEBUG_MODE){
                    System.out.println("ScreenManager.java) Changing to Menu Screen");
                }
                if(menuScreen == null) menuScreen = new MenuScreen(game);
                game.setScreen(menuScreen);
                break;
            case PREFERENCES:
                if(preferencesScreen == null) preferencesScreen = new PreferenceScreen(game);
                game.setScreen(preferencesScreen);
                break;
            case APPLICATION:
                if (ArkanoidGame.DEBUG_MODE){
                    System.out.println("ScreenManager.java) Changing to Main Screen");
                }
                if(mainScreen == null) mainScreen = new MainScreen(game);
                game.setScreen(mainScreen);
                break;
            case ENDGAME:
                if (ArkanoidGame.DEBUG_MODE){
                    System.out.println("ScreenManager.java) Changing to End Screen");
                }
                if(endScreen == null) endScreen = new EndScreen(game);
                game.setScreen(endScreen);
                break;
            case LOADING:
                if(loadingScreen == null) loadingScreen = new LoadingScreen(game);
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(ScreenManager.java) Changing to Loading Screen");
                game.setScreen(loadingScreen);
                break;
            case HIGHSCORE: // ⭐️ THÊM KHỐI NÀY ⭐️
                if(highScoreScreen == null) highScoreScreen = new HighScoreScreen(game);
                game.setScreen(highScoreScreen);
                break;
            case ENTER_HIGHSCORE: // ⭐️ THÊM KHỐI NÀY ⭐️
                // Luôn tạo mới để nó lấy điểm 'lastScore' mới nhất
                enterHighScoreScreen = new EnterHighScoreScreen(game);
                game.setScreen(enterHighScoreScreen);
                break;
        }
    }
}
