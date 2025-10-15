package game.Screen;

import game.ArkanoidGame;

public class ScreenManager {
    private final ArkanoidGame game;

    public static final int MENU = 0;
    public static final int PREFERENCES = 1;
    public static final int APPLICATION = 2;
    public static final int ENDGAME = 3;
    public static final int LOADING = 4;

    // screens to load
    private LoadingScreen loadingScreen;
    //private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;

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
//            case PREFERENCES:
//                if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(game);
//                game.setScreen(preferencesScreen);
//                break;
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
        }
    }
}
