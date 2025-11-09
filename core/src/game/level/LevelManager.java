package game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import game.ArkanoidGame;
import game.Hud;
import game.component.PhysicsBodyComponent;

public class LevelManager implements Disposable {
    public static int MAX_LEVELS = 3;

    public enum Level {TEST, LEVEL1, LEVEL2, LEVEL3}

    private World world;
    private OrthographicCamera cam;
    private PooledEngine en;
    private ArkanoidGame game;
    public LevelLoader currentLevel;
    private Hud hud;

    public boolean isLevelCompleted = false;
    public boolean loadNextLevelFlag = false;
    public int currentLevelNumber = 0;

    public LevelManager(ArkanoidGame game, World world, PooledEngine en, OrthographicCamera cam) {
        this.world = world;
        this.game = game;
        this.cam = cam;
        this.en = en;
    }

    public void loadLevel(int level) {
        cleanupCurrentLevel();
        this.currentLevelNumber = level;
        this.isLevelCompleted = false;

        switch (level) {
            case 1:
                currentLevel = new LevelLoader(game, world, en, getLevelMapPath(Level.LEVEL1));
                break;
            case 2:
                currentLevel = new LevelLoader(game, world, en, getLevelMapPath(Level.LEVEL2));
                break;
            case 3:
                currentLevel = new LevelLoader(game, world, en, getLevelMapPath(Level.LEVEL3));
                break;
        }

    }


    public void renderLevel() {
        currentLevel.getMapRenderer().setView(cam);
        currentLevel.getMapRenderer().render();
    }

    // this is where you will pass in the level's path
    public String getLevelMapPath(Level level) {
        String path = "";

        switch (level) {
            case TEST:
                path = "levels/test.tmx";
                break;
            case LEVEL1:
                path = "levels/level0_1.tmx";
                break;
            case LEVEL2:
                path = "levels/level0_2.tmx";
                break;
            case LEVEL3:
                path = "levels/level0_3.tmx";
                break;
        }

        return path;
    }

    private void cleanupCurrentLevel() {
        if (currentLevel == null) return;

        // Dispose the current level
        currentLevel.dispose();

        // Remove all bodies from the current level
        ImmutableArray<Entity> matchingEntities = en.getEntitiesFor(Family.all(PhysicsBodyComponent.class).get());
        for (Entity entity : matchingEntities) {
            PhysicsBodyComponent b2Body = entity.getComponent(PhysicsBodyComponent.class);
            b2Body.setToDestroy = true;
        }

        // Clear the current level
        currentLevel = null;
    }

    public ArkanoidGame getGame() {
        return this.game;
    }

    @Override
    public void dispose() {
        System.out.println("Calling Level Manager dispose");
        //cleanupCurrentLevel();
        if (currentLevel != null) {
            currentLevel.dispose();
        }
    }
}
