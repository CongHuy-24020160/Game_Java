package game;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

public class Hud {
    private BitmapFont font; // write on the screen
    private int score;
    private int health;
    private int level;
    private int highScore;
    private long startTime;
    private String mapName;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getMapName() {
        return mapName;
    }

    public Hud(String mapName) {
        this.mapName = mapName;
    }
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Hud() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GUNDAM.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // parameter : size and colour of letters
        parameter.size = 24;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        score = 0;
        health = 3;
        level = 1;
        highScore = 0;
        startTime = TimeUtils.millis();
    }

    public void addScore(int amount) {
        score += amount;
        if (score > highScore) highScore = score;
    }

    public void loseHealth() {
        health--;
    }

    public void nextLevel() {
        level++;
    }

    public void resetTimer() {
        startTime = TimeUtils.millis();
    }
//    public void updateLives(){
//        logger.info("Updating Lives");
//        livesTable.clearChildren();
//        livesTable.add(livesLabel);
//        for(int i = 0; i < lives; i++){
//            logger.info("Update Lives: " + lives);
//            ballImage = new Image(ballTexture);
//            ballImage.setScale(0.85f);
//            livesTable.add(ballImage);
//        }
//    }

    public void render(SpriteBatch batch) {
        // batch : bút vẽ của libgdx
        long elapsed = (TimeUtils.millis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        String time = String.format("%02d:%02d", minutes, seconds);

        font.draw(batch, "Map: " + mapName, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "SCORE: " + score, 20, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "HEALTH: " + health, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "LEVEL: " + level, 20, Gdx.graphics.getHeight() - 110);
        font.draw(batch, "HIGHSCORE: " + highScore, 20, Gdx.graphics.getHeight() - 140);
        font.draw(batch, "TIME: " + time, Gdx.graphics.getWidth() - 180, Gdx.graphics.getHeight() - 20);
    }

    public void dispose() {
        font.dispose();
    }
}
