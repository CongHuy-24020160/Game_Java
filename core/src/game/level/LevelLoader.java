package game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import game.ArkanoidGame;
import game.Utilities;
import game.component.*;
import game.LoadAssets.BodyFactory;
import game.Utils.BallAndPaddle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class LevelLoader implements Disposable {
    private BodyFactory bodyFactory;
    public TmxMapLoader mapLoader;
    public TiledMap map;
    private World world;
    private ArkanoidGame game;
    private PooledEngine en;
    private OrthogonalTiledMapRenderer mapRenderer;

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    private TextureAtlas textures;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public TextureAtlas getTextures() {
        return textures;
    }

    public void setTextures(TextureAtlas textures) {
        this.textures = textures;
    }

    public int numOfBlocksLeft;
    public BallAndPaddle paddleAndBall;


    public static int[][] loadMap(String path) {
        FileHandle file = Gdx.files.internal(path);
        String text = file.readString();
        String[] lines = text.split("\\r?\\n");
        int[][] map = new int[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].trim().split("\\s+");
            map[i] = new int[tokens.length];
            for (int j = 0; j < tokens.length; j++) {
                map[i][j] = Integer.parseInt(tokens[j]);
            }
        }
        return map;
    }


    public LevelLoader(ArkanoidGame game, World world, PooledEngine en, String mapFilePath) {
        this.world = world;
        this.en = en;
        this.game = game;
        bodyFactory = BodyFactory.getInstance(world);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilePath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Utilities.PPM);
        textures = game.assetManager.manager.get(game.assetManager.gameImagaes);
        loadWorld();
    }


    public void loadWorld() {
        renderPlayerAndBall();
//        renderPlayer();
//        renderBall();
        renderBlocks();
    }

    private void renderPlayerAndBall() {
        Entity playerEntity = en.createEntity();
        Entity ballEntity = en.createEntity();
        renderBall(ballEntity);
        renderPlayer(playerEntity, ballEntity);
        paddleAndBall = new BallAndPaddle(playerEntity, ballEntity);
    }

    private void renderPlayer(Entity playerEntity, Entity ballEntity) {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Player");

        PlayerIn4Component pc = en.createComponent(PlayerIn4Component.class);
        TextureComponent tc = en.createComponent(TextureComponent.class);
        MoveComponent tranC = en.createComponent(MoveComponent.class);
        ColliderComponent cc = en.createComponent(ColliderComponent.class);
        TypeComponent typeC = en.createComponent(TypeComponent.class);
        PhysicsBodyComponent b2bodyC = en.createComponent(PhysicsBodyComponent.class);
        LinkedEntityComponent attachC = en.createComponent(LinkedEntityComponent.class);

        // === TẠO BODY VỚI KÍCH THƯỚC GỐC ===
        float baseWidth = Utilities.PADDLE_WIDTH;   // ví dụ: 100
        float baseHeight = Utilities.PADDLE_HEIGHT; // ví dụ: 20

        b2bodyC.body = bodyFactory.makeBoxPolyBody(
            Utilities.getPPMWidth() / 2 - (Utilities.convertToPPM(baseWidth) / 2),
            Utilities.convertToPPM(10),
            Utilities.convertToPPM(baseWidth),
            Utilities.convertToPPM(baseHeight),
            null,
            BodyDef.BodyType.KinematicBody,
            true,
            false
        );

        b2bodyC.body.setUserData(playerEntity);
        typeC.type = TypeComponent.PLAYER_TYPE;
        attachC.setLinkedEntity(ballEntity);

        // === TẢI HÌNH ẢNH + ĐẶT KÍCH THƯỚC ===
        tc.currImage = new TextureRegion(textures.findRegion("Player"), 0, 0, 74, 26);

        // ĐẶT KÍCH THƯỚC PIXEL (ĐỂ VẼ)
        tc.width = baseWidth;
        tc.height = baseHeight;

        // ĐẶT TRANSFORM
        tranC.pos.set(b2bodyC.body.getPosition().x, b2bodyC.body.getPosition().y, 0);

        // === THÊM lengthMultiplier VÀO PlayerIn4Component ===
        pc.lengthMultiplier = 1.0f; // Bắt đầu: 100%

        // === THÊM COMPONENT ===
        playerEntity.add(pc);
        playerEntity.add(tc);
        playerEntity.add(tranC);
        playerEntity.add(cc);
        playerEntity.add(typeC);
        playerEntity.add(b2bodyC);
        playerEntity.add(attachC);

        en.addEntity(playerEntity);
        System.out.println("Player added to engine");
    }

    private void renderBall(Entity ballEntity) {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Ball");
        TextureComponent tc = en.createComponent(TextureComponent.class);
        MoveComponent tranC = en.createComponent(MoveComponent.class);
        ColliderComponent cc = en.createComponent(ColliderComponent.class);
        TypeComponent typeC = en.createComponent(TypeComponent.class);
        PhysicsBodyComponent b2bodyC = en.createComponent(PhysicsBodyComponent.class);
        BallComponent ballC = en.createComponent(BallComponent.class);
        SoundEffectComponent soundComponent = en.createComponent(SoundEffectComponent.class);

        // add sound fx
        soundComponent.soundEffects.put("ding1", (Sound) game.assetManager.manager.get(game.assetManager.hitBrickSound));
        soundComponent.soundEffects.put("ding2", (Sound) game.assetManager.manager.get(game.assetManager.hitWallSound));
        //soundComponent.soundEffects.put("explode", (Sound) game.assetManager.manager.get(game.assetManager.explosionSound));

        ballC.BallSpeed = 3f;

        // load texture
        tc.currImage = new TextureRegion(
            textures.findRegion("Ball_small-blue"),
            0, 0, 13, 12
        );

        // create box2d body
        b2bodyC.body = bodyFactory.makeCirclePolyBody(
            Utilities.getPPMWidth() / 2,
            Utilities.convertToPPM(Utilities.PADDLE_HEIGHT + 25),
            Utilities.convertToPPM(tc.currImage.getRegionWidth()),


            BodyFactory.Material.PLASTIC,

            BodyDef.BodyType.DynamicBody,
            true,
            false
        );

        b2bodyC.body.setUserData(ballEntity);
        typeC.type = TypeComponent.BALL_TYPE;

        // load transform
        tranC.pos.set(b2bodyC.body.getPosition().x, b2bodyC.body.getPosition().y, 0);

        ballEntity.add(tc);
        ballEntity.add(tranC);
        ballEntity.add(cc);
        ballEntity.add(typeC);
        ballEntity.add(b2bodyC);
        ballEntity.add(ballC);
        ballEntity.add(soundComponent);
        en.addEntity(ballEntity);
        System.out.println("Ball added to engine");
    }

    /**
     * This class to create blocks once at leverLoader
     */
    private void renderBlocks() {
        if (ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Blocks");
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Entity blockEntity = en.createEntity();
            // b2Body is the block
            PhysicsBodyComponent b2Body = en.createComponent(PhysicsBodyComponent.class);

            TextureComponent tc = en.createComponent(TextureComponent.class);
            ColliderComponent collision = en.createComponent(ColliderComponent.class);
            TypeComponent type = en.createComponent(TypeComponent.class);
            ScoreComponent scoreComponent = en.createComponent(ScoreComponent.class);

            // get the rectangle object from the map
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            MapProperties properties = object.getProperties();
            String blockType = (String) properties.get("BlockType");

            switch (blockType) {
                case "Brick_1":
                    // set live to block
                    b2Body.lives = 1;
                    // load red block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion(Utilities.getTexureNameForEachLive(b2Body.lives)),
                        0, 0, 40, 15
                    );


                    break;
                case "Brick_2":
                    b2Body.lives = 2;
                    // load purple block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion(Utilities.getTexureNameForEachLive(b2Body.lives)),
                        0, 0, 40, 15
                    );

                    break;
                case "Brick_3":
                    b2Body.lives = 3;
                    // load yellow block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion(Utilities.getTexureNameForEachLive(b2Body.lives)),
                        0, 0, 40, 15
                    );

                    break;
            }


            // create body with body factory
            b2Body.body = bodyFactory.makeBoxPolyBody(
                Utilities.convertToPPM(rect.x),
                Utilities.convertToPPM(rect.y),
                Utilities.convertToPPM(rect.width),
                Utilities.convertToPPM(rect.height),
                BodyFactory.Material.PLASTIC,
                BodyDef.BodyType.StaticBody,
                true,
                false);


            type.type = TypeComponent.BLOCK_TYPE;
            b2Body.body.setUserData(blockEntity);

            blockEntity.add(b2Body);
            blockEntity.add(tc);
            blockEntity.add(collision);
            blockEntity.add(type);
            blockEntity.add(scoreComponent);
            en.addEntity(blockEntity);

            numOfBlocksLeft++;
            System.out.println("Block added to engine");
        }
    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
//        for (Texture t : brickTextures.values()) {
//            t.dispose();
//        }
//        brickTextures.clear();

    }

}
