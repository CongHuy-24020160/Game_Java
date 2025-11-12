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

    private final ArkanoidGame game;

    // ui
    private Stage stage;
    private Label loadingTitle;

    public LoadingScreen(ArkanoidGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();

        //  Chỉ queue font, KHÔNG gọi finishLoading()
        game.assetManager.queueAddFonts();

        // Chờ cho font tải xong để có thể dùng cho Label
        game.assetManager.manager.finishLoading();

        if(ArkanoidGame.DEBUG_MODE) System.out.println("(LoadingScreen) Queuing all assets...");
        game.assetManager.queueAddImages();
        game.assetManager.queueLoadSkin();
        game.assetManager.queueLoadSound();
        game.assetManager.queueLoadMusic();

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        loadingTitle = new Label("Loading.", new Label.LabelStyle(
            game.assetManager.manager.get(game.assetManager.gameFont, BitmapFont.class), Color.WHITE));
        table.add(loadingTitle);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // manager.update() sẽ trả về true khi TẤT CẢ đã tải xong.
        if (game.assetManager.manager.update()) {
            // Đã tải xong, chuyển màn hình
            ScreenManager.changeScreen(ScreenManager.MENU);
        }

        // (Tùy chọn) Hiển thị tiến trình thực tế
        float progress = game.assetManager.manager.getProgress();
        handleLoadingTitle(progress); // Truyền progress vào để hiển thị

        stage.act(delta);
        stage.draw();
    }

    // Sửa lại hàm này để hiển thị %
    private void handleLoadingTitle(float progress) {
        loadingTitle.setText("Loading... " + (int) (progress * 100) + "%");
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
