package game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.ArkanoidGame;

public class LoadingScreen implements Screen {
    private static final int NUM_OF_STAGES = 3;

    private ArkanoidGame game;

    // ui
    private Stage stage;
    private Table table;
    private Label loadingTitle;

    // loading state time
    private float loadingStateTimer = 0f;
    private float loadingStateDuration = 0.5f;

    // loading state fake timer (comment for prod)
    private float loadingtimer = 0f;
    private float loadingDuration= 1.5f;

    // loading assets
    private final int IMAGE = 0;
    private final int SKIN = 1;
    private final int SOUND = 2;
    private int currentLoadingStage = 0;

    public LoadingScreen(ArkanoidGame game){
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();

        // add fonts
        game.assetManager.queueAddFonts();
        game.assetManager.manager.finishLoading();

        // loading table
        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        // title label
        loadingTitle = new Label("Loading.", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        table.add(loadingTitle);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1); //  clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(game.assetManager.manager.update()){
            loadingtimer += delta;
            handleLoadingAssets();

            if(currentLoadingStage > NUM_OF_STAGES && loadingtimer >= loadingDuration){

                // change to menu screen
                game.screenManager.changeScreen(ScreenManager.MENU);
            }
        }

        handleLoadingTitle(delta);
        stage.act();
        stage.draw();
    }

    private void handleLoadingTitle(float delta){
        loadingStateTimer += delta;
        if(loadingStateTimer > loadingStateDuration){

            // update loading
            // Loading. (8 chars) -> Loading.. (9 chars) -> Loading... (10 chars)
            if(loadingTitle.getText().length == 8){
                loadingTitle.setText("Loading..");
            }else if(loadingTitle.getText().length == 9){
                loadingTitle.setText("Loading...");
            }else{
                loadingTitle.setText("Loading.");
            }

            // reset timer
            loadingStateTimer = 0;
        }
    }

    private void handleLoadingAssets(){
        switch(currentLoadingStage){
            case IMAGE:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(LoadingScreen) Loading Textures...");
                game.assetManager.queueAddImages();
                break;
            case SKIN:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(LoadingScreen) Loading Skin...");
                game.assetManager.queueLoadSkin();
                break;
            case SOUND:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(LoadingScreen) Loading SoundFX...");
                game.assetManager.queueLoadSound();
                break;
            default:
                break;
        }

        // update currentLoadingStage
        currentLoadingStage++;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
