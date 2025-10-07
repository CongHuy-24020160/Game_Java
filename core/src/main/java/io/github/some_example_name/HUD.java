package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Timer;

public class HUD {
    private BitmapFont font; // write on the screen
    private int score;
    private int health;
    private int level;
    private int highScore;
    private long startTime;

    public HUD() {
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

    public void render(SpriteBatch batch) {
        // batch : bút vẽ của libgdx
        long elapsed = (TimeUtils.millis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        String time = String.format("%02d:%02d", minutes, seconds);

        font.draw(batch, "SCORE: " + score, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "HEALTH: " + health, 20, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "LEVEL: " + level, 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "HIGHSCORE: " + highScore, 20, Gdx.graphics.getHeight() - 110);
        font.draw(batch, "TIME: " + time, Gdx.graphics.getWidth() - 180, Gdx.graphics.getHeight() - 20);
    }

    public void dispose() {
        font.dispose();
    }
}
