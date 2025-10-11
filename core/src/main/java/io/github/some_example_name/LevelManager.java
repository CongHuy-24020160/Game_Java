package com.taptap.breakout.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.taptap.breakout.BreakoutGame;
import com.taptap.breakout.ecs.components.B2BodyComponent;
import com.taptap.breakout.utils.PaddleAndBall;

public class LevelManager implements Disposable {
    public static int MAX_LEVELS = 3;

    public enum Level{TEST, LEVEL1, LEVEL2, LEVEL3}

    private World world;
    private OrthographicCamera cam;
    private PooledEngine en;
    private BreakoutGame game;
    public LevelLoader currentLevel;

    public LevelManager(BreakoutGame game, World world, PooledEngine en, OrthographicCamera cam){
        this.world = world;
        this.game = game;
        this.cam = cam;
        this.en = en;
    }

    public void loadLevel(int level) {
        cleanupCurrentLevel();

        String mapPath = "maps/map" + level + ".txt";
        int[][] mapData = LevelLoader.loadMap(mapPath);

        float brickWidth = 1f;
        float brickHeight = 0.5f;

        for (int y = 0; y < mapData.length; y++) {
            for (int x = 0; x < mapData[y].length; x++) {
                int id = mapData[y][x];
                if (id != 0) {
                    Texture tex = LevelLoader.getBrickTexture(id);
                    createBrick(x * brickWidth, (mapData.length - y) * brickHeight, tex, id);
                }
            }
        }

        System.out.println("Level " + level + " loaded with " + mapData.length + " rows");
    }

    private void createBrick(float x, float y, Texture texture, int id) {
        Entity brick = en.createEntity();

        B2BodyComponent body = en.createComponent(B2BodyComponent.class);
        TransformComponent transform = en.createComponent(TransformComponent.class);
        TextureComponent tex = en.createComponent(TextureComponent.class);
        TypeComponent type = en.createComponent(TypeComponent.class);

        // tạo thân vật lý (body)
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.25f); // nửa kích thước vì Box2D

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0f;
        fdef.restitution = 0f;

        body.body = world.createBody(bdef);
        body.body.createFixture(fdef);
        shape.dispose();

        transform.position.set(x, y, 0);
        tex.texture = texture;
        type.type = TypeComponent.BRICK;

        brick.add(body);
        brick.add(transform);
        brick.add(tex);
        brick.add(type);
        en.addEntity(brick);
    }

    public void renderLevel(){
        currentLevel.getMapRenderer().render();
    }

    // this is where you will pass in the level's path
    public String getLevelMapPath(Level level){
        String path = "";

        switch(level){
            case TEST:
                path = "levels/test.tmx";
                break;
            case LEVEL1:
                path = "levels/level1.tmx";
                break;
            case LEVEL2:
                path = "levels/level2.tmx";
                break;
            case LEVEL3:
                path = "levels/level3.tmx";
                break;
        }

        return path;
    }

    private void cleanupCurrentLevel(){
        if (currentLevel == null) return;

        // Dispose the current level
        currentLevel.dispose();

        // Remove all bodies from the current level
        ImmutableArray<Entity> matchingEntities = en.getEntitiesFor(Family.all(B2BodyComponent.class).get());
        for (Entity entity : matchingEntities) {
            B2BodyComponent b2Body = entity.getComponent(B2BodyComponent.class);
            b2Body.setToDestroy = true;
        }

        // Clear the current level
        currentLevel = null;
    }

    @Override
    public void dispose() {
        System.out.println("Calling Level Manager dispose");
        cleanupCurrentLevel();
    }
}
