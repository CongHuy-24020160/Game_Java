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
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private World world;
    private ArkanoidGame game;
    private PooledEngine en;
    private OrthogonalTiledMapRenderer mapRenderer;
    public OrthogonalTiledMapRenderer getMapRenderer(){return mapRenderer;}

    private TextureAtlas textures;

    public int numOfBlocksLeft;
    public BallAndPaddle paddleAndBall;

    // Don't understand with this code
//    private static Map<Integer, Texture> brickTextures = new HashMap<>();
//
//    public static void loadTextures() {
//        brickTextures.put(1, new Texture("Brick1_4.png"));
//        brickTextures.put(2, new Texture("Brick2_4.png"));
//        brickTextures.put(3, new Texture("Brick3_4.png"));
//        brickTextures.put(4, new Texture("Brick4_4.png"));
//        brickTextures.put(5, new Texture("Brick5_4.png"));
//        brickTextures.put(6, new Texture("Brick6_4.png"));
//        brickTextures.put(7, new Texture("Brick7_4.png"));
//        brickTextures.put(8, new Texture("Brick8_4.png"));
//        brickTextures.put(9, new Texture("Brick9_4.png"));
//        brickTextures.put(10, new Texture("Brick_unbreakable2.png"));
//    }

//    public static Texture getBrickTexture(int id) {
//        return brickTextures.getOrDefault(id, null);
//    }

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



    public LevelLoader(ArkanoidGame game, World world, PooledEngine en, String mapFilePath){
        this.world = world;
        this.en = en;
        this.game = game;
        bodyFactory = BodyFactory.getInstance(world);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilePath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ Utilities.PPM);
        textures = game.assetManager.manager.get(game.assetManager.gameImagaes);
        loadWorld();
    }


    public void loadWorld(){
        renderPlayerAndBall();
//        renderPlayer();
//        renderBall();
        renderBlocks();
    }

    private void renderPlayerAndBall(){
        Entity playerEntity = en.createEntity();
        Entity ballEntity = en.createEntity();
        renderBall(ballEntity);
        renderPlayer(playerEntity, ballEntity);
        paddleAndBall = new BallAndPaddle(playerEntity, ballEntity);
    }

    private void renderPlayer(Entity playerEntity, Entity ballEntity){
        if(ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Player");
        PlayerIn4Component pc = en.createComponent(PlayerIn4Component.class);
        TextureComponent tc = en.createComponent(TextureComponent.class);
        MoveComponent tranC = en.createComponent(MoveComponent.class);
        ColliderComponent cc = en.createComponent(ColliderComponent.class);
        TypeComponent typeC = en.createComponent(TypeComponent.class);
        PhysicsBodyComponent b2bodyC = en.createComponent(PhysicsBodyComponent.class);
        LinkedEntityComponent attachC = en.createComponent(LinkedEntityComponent.class);

        // create box2d body
        b2bodyC.body = bodyFactory.makeBoxPolyBody(
            Utilities.getPPMWidth() / 2 - (Utilities.convertToPPM(Utilities.PADDLE_WIDTH) / 2),
            Utilities.convertToPPM(10),
            Utilities.convertToPPM(Utilities.PADDLE_WIDTH),
            Utilities.convertToPPM(Utilities.PADDLE_HEIGHT),
            null,
            BodyDef.BodyType.KinematicBody,
            true,
            false
        );

        b2bodyC.body.setUserData(playerEntity);
        typeC.type = TypeComponent.PLAYER_TYPE;
        attachC.setLinkedEntity(ballEntity);
        System.out.println("Player body created");
        // load texture
        tc.currImage = new TextureRegion(
            textures.findRegion("Player"),
            0, 0, 100, 30
        );
        System.out.println("Code run here");

        // load transform
        tranC.pos.set(b2bodyC.body.getPosition().x, b2bodyC.body.getPosition().y, 0);

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

    private void renderBall(Entity ballEntity){
        if(ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Ball");
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

        ballC.BallSpeed = 5f;

        // load texture
        tc.currImage = new TextureRegion(
            textures.findRegion("Ball_small-blue"),
            8, 31, 25, 25
        );
        if (tc.currImage == null) {
            System.out.println("this is null");
        }

        // create box2d body
        b2bodyC.body = bodyFactory.makeCirclePolyBody(
            Utilities.getPPMWidth() / 2,
            Utilities.convertToPPM(Utilities.PADDLE_HEIGHT + 25),
            Utilities.convertToPPM(tc.currImage.getRegionWidth()),
            null,
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

    private void renderBlocks(){
        if(ArkanoidGame.DEBUG_MODE) System.out.println("(LevelLoader) Rendering Blocks");
        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Entity blockEntity = en.createEntity();
            PhysicsBodyComponent b2Body = en.createComponent(PhysicsBodyComponent.class);
            TextureComponent tc = en.createComponent(TextureComponent.class);
            ColliderComponent collision = en.createComponent(ColliderComponent.class);
            TypeComponent type = en.createComponent(TypeComponent.class);
            ScoreComponent scoreComponent = en.createComponent(ScoreComponent.class);

            // get the rectangle object from the map
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            MapProperties properties = object.getProperties();
            String color = (String) properties.get("color");

            switch(color){
                case "red":
                    // load red block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion("Brick1"),
                        1, 2, 34, 32
                    );
                    break;
                case "purple":
                    // load purple block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion("Brick2"),
                        41, 2, 34, 32
                    );
                    break;
                case "yellow":
                    // load yellow block texture
                    tc.currImage = new TextureRegion(
                        textures.findRegion("Brick3"),
                        81, 2, 34, 32
                    );
                    break;
            }


            // create body with bodyfactory
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
        // thêm phần đọc cho txt
//        int[][] mapData = loadMap("maps/level1.txt"); // đường dẫn map txt của bạn
//        float startX = 2;
//        float startY = 150;
//        float blockWidth = 32;
//        float blockHeight = 16;
//
//        for (int row = 0; row < mapData.length; row++) {
//            for (int col = 0; col < mapData[row].length; col++) {
//                int id = mapData[row][col];
//                if (id == 0) continue;
//
//                Texture tex = brickTextures.get(id);
//                if (tex == null) continue;
//
//                Entity blockEntity = en.createEntity();
//                PhysicsBodyComponent b2Body = en.createComponent(PhysicsBodyComponent.class);
//                TextureComponent tc = en.createComponent(TextureComponent.class);
//                ColliderComponent collision = en.createComponent(ColliderComponent.class);
//                TypeComponent type = en.createComponent(TypeComponent.class);
//                ScoreComponent scoreComponent = en.createComponent(ScoreComponent.class);
//
//                tc.currImage = new TextureRegion(tex);
//
//                float x = startX + col * blockWidth;
//                float y = startY - row * blockHeight;
//
//                b2Body.body = bodyFactory.makeBoxPolyBody(
//                    Utilities.convertToPPM(x),
//                    Utilities.convertToPPM(y),
//                    Utilities.convertToPPM(blockWidth),
//                    Utilities.convertToPPM(blockHeight),
//                    BodyFactory.Material.PLASTIC,
//                    BodyDef.BodyType.StaticBody,
//                    true,
//                    false
//                );
//
//                type.type = TypeComponent.BLOCK_TYPE;
//                b2Body.body.setUserData(blockEntity);
//
//                blockEntity.add(b2Body);
//                blockEntity.add(tc);
//                blockEntity.add(collision);
//                blockEntity.add(type);
//                blockEntity.add(scoreComponent);
//                en.addEntity(blockEntity);
//
//                numOfBlocksLeft++;
//            }
//        }
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
