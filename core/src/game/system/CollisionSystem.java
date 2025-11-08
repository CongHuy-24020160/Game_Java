package game.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import game.ArkanoidGame;
import game.Hud;
import game.LoadAssets.BodyFactory;
import game.Screen.MainScreen;
import game.Utilities;
import game.component.*;
import game.component.TypeComponent;
import game.level.LevelManager;
import game.ScoreChangeListener;
import game.Utils.ParticleHandler;

// ⭐️ THÊM 2 DÒNG IMPORT BỊ THIẾU ⭐️
import game.Utils.ScoreManager;
import game.Screen.ScreenManager;

/*
    System chịu trách nhiệm xử lý logic *sau khi* các va chạm vật lý xảy ra.
 */
public class CollisionSystem extends IteratingSystem {

    private ArkanoidGame game;
    private PooledEngine engine;
    private World world;
    private BodyFactory bodyFactory;
    // ⭐️ SỬA LỖI: Khai báo 3 biến này ở đây ⭐️
    private ScoreChangeListener scoreChangeListener;
    private LevelManager levelManager;
    private Hud hud;
    public ParticleHandler particlesManager;

    private MainScreen mainScreen;

    // ⭐️ SỬA LỖI: Hàm khởi tạo (Constructor) phải NHẬN và GÁN 3 biến này ⭐️
    public CollisionSystem(MainScreen mainScreen, PooledEngine engine, World world, Hud hud,
                           LevelManager levelManager, ScoreChangeListener scoreChangeListener,ArkanoidGame game) {
        super(Family.all(ColliderComponent.class, PhysicsBodyComponent.class, BallComponent.class).get());

        this.mainScreen = mainScreen;
        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager;
        this.hud = hud;
        this.engine = engine;
        this.world = world;
        this.game = game;
        this.bodyFactory = BodyFactory.getInstance(world);

        particlesManager = new ParticleHandler("particles/block-particle.p", "particles");
        particlesManager.resizeAll(1f);
    }


    // (Các ComponentMapper giữ nguyên)
    private final ComponentMapper<ColliderComponent> collisionC
        = ComponentMapper.getFor(ColliderComponent.class);
    private final ComponentMapper<PhysicsBodyComponent> b2BodyC
        = ComponentMapper.getFor(PhysicsBodyComponent.class);
    private final ComponentMapper<BallComponent> ballC
        = ComponentMapper.getFor(BallComponent.class);
    private final ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);
    private final ComponentMapper<TextureComponent> textureC
        = ComponentMapper.getFor(TextureComponent.class);

    // (Đã chuyển 3 biến lên trên)

    @Override
    protected void processEntity(Entity entity, float v) {
        // Lấy các component cần thiết bằng mapper đã khai báo.
        final ColliderComponent collision = collisionC.get(entity);
        final Entity otherEntity = collision.targetEntity;

        if (otherEntity == null) return;

        final TypeComponent entityType = typeC.get(entity);

        // Chỉ xử lý va chạm bắt nguồn từ quả bóng
        if (entityType.type == TypeComponent.BALL_TYPE) {
            handleBallCollision(entity, otherEntity);
        }

        // Đặt lại va chạm để chuẩn bị cho khung hình tiếp theo.
        collision.targetEntity = null;
    }

    /**
     * @param ballEntity  Entity của quả bóng.
     * @param otherEntity Entity mà quả bóng đã va chạm.
     */
    private void handleBallCollision(Entity ballEntity, Entity otherEntity) {

        final TypeComponent otherType = typeC.get(otherEntity);

        if (otherType == null) return;

        // Phân loại va chạm dựa trên loại của đối tượng kia.
        switch (otherType.type) {
            case TypeComponent.PLAYER_TYPE:
                handleBallPlayerCollision(ballEntity, otherEntity);
                break;
            case TypeComponent.BLOCK_TYPE:
                handleBallBlockCollision(ballEntity, otherEntity);
                break;
        }
    }

    /**
     * (Hàm này của bạn, giữ nguyên)
     */
    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
        final BallComponent ball = ballC.get(ballEntity);
        final float SLOW_DOWN_FACTOR = 0.8f;
        final float MIN_BALL_SPEED = 3.5f;
        ball.BallSpeed = ball.BallSpeed * SLOW_DOWN_FACTOR;

        if (ball.BallSpeed < MIN_BALL_SPEED) {
            ball.BallSpeed = MIN_BALL_SPEED;
        }
        final PhysicsBodyComponent ballB2body = b2BodyC.get(ballEntity);
        final PhysicsBodyComponent playerB2body = b2BodyC.get(playerEntity);


        final Vector2 ballPosition = ballB2body.body.getPosition();
        final Vector2 playerPosition = playerB2body.body.getPosition();
        final float paddleWidth = Utilities.convertToPPM(Utilities.PADDLE_WIDTH);

        float relativeIntersectX = ballPosition.x - playerPosition.x;
        float normalizedIntersectX = relativeIntersectX / (paddleWidth / 2f);
        normalizedIntersectX = MathUtils.clamp(normalizedIntersectX, -1f, 1f);
        float maxBounceAngle = 60f;
        float bounceAngle = 90f - normalizedIntersectX * maxBounceAngle;

        if (ArkanoidGame.DEBUG_MODE) {
            System.out.println("relativeIntersectX: " + relativeIntersectX);
            System.out.println("normalizedIntersectX: " + normalizedIntersectX);
            System.out.println("bounceAngle: " + bounceAngle);
        }

        Vector2 velocity = new Vector2(1, 0).setAngleDeg(bounceAngle).scl(ball.BallSpeed);
        if (velocity.y < 0) velocity.y *= -1;
        ballB2body.body.setLinearVelocity(velocity);
    }

    /**
     * xử lý hậu quả của việc phá gạch.
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        // blockB2Body is a block that collides with the ball
        final PhysicsBodyComponent blockB2Body = b2BodyC.get(blockEntity);


        // Change the texture
        blockB2Body.lives--;
        TextureComponent texture = textureC.get(blockEntity);
        texture.currImage = levelManager.currentLevel.getTextures().findRegion(Utilities.getTexureNameForEachLive(blockB2Body.lives));


        // Decrease the number of block if a block is destroyed
        if (blockB2Body.lives > 0) {
            return;
        }

        System.out.println("ĐÃ PHÁ GẠCH!");
        levelManager.currentLevel.numOfBlocksLeft--;
        // Cộng điểm
        scoreChangeListener.onScoreChanged(100);

        blockB2Body.setToDestroy = true;


        // Tạo hiệu ứng hạt (Giữ nguyên)
        particlesManager.trigger(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);

        //  BẮT ĐẦU LOGIC THẮNG (Đã sửa lỗi chính tả)

        // Kiểm tra điều kiện hoàn thành màn chơi.
        if (levelManager.currentLevel.numOfBlocksLeft <= 0 && !levelManager.isLevelCompleted) {

            levelManager.isLevelCompleted = true; // Đánh dấu là đã hoàn thành

            // Kiểm tra xem đây có phải màn CUỐI CÙNG không?
            if (levelManager.currentLevelNumber >= LevelManager.MAX_LEVELS) {

                // *** ĐÂY LÀ MÀN CUỐI CÙNG -> LƯU KỈ LỤC ***
                int finalScore = hud.getScore();

                mainScreen.gameOverPending = true;
                mainScreen.finalScoreForGameOver = finalScore;
                if (ScoreManager.getInstance().isHighScore(finalScore)) {
                    game.lastScore = finalScore;
                    //levelManager.getGame().screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                    game.screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    //levelManager.getGame().screenManager.changeScreen(ScreenManager.ENDGAME);
                    game.screenManager.changeScreen(ScreenManager.ENDGAME);
                }

            } else {

                if (levelManager.currentLevel.numOfBlocksLeft <= 0) {
                    // *** CHƯA PHẢI MÀN CUỐI -> HIỆN BẢNG "NEXT LEVEL" ***
                    hud.showLevelCompleteDialog();
                    mainScreen.pauseGameSystems(); // Tạm dừng hệ thống game khi thang 1 level
                }
            }

            return; // Đã xử lý xong, thoát hàm
        }
        // KẾT THÚC LOGIC THẮNG
        // Tỉ lệ 20% rơi ra power-up (bạn có thể thay đổi số 5)
        //if (MathUtils.random(1, 5) == 1)
        spawnPowerUp(blockB2Body.body.getPosition());
    }

    private void spawnPowerUp(Vector2 position) {
        // Lấy engine từ hệ thống (cần khai báo 'engine' và 'bodyFactory' ở constructor)
        // GIẢ SỬ: bạn cần sửa constructor của CollisionSystem để nhận 'PooledEngine' và 'BodyFactory'
        // Hoặc, một cách đơn giản hơn, hãy lấy chúng từ 'levelManager' nếu có thể
        // VÍ DỤ: (Giả sử bạn đã truyền 'PooledEngine' vào constructor của CollisionSystem)
        // PooledEngine engine = getEngine();
        // BodyFactory bodyFactory = BodyFactory.getInstance(world); // (Giả sử bạn có 'world')

        /*
         * GHI CHÚ QUAN TRỌNG:
         * CollisionSystem của bạn không có tham chiếu đến 'engine' hoặc 'bodyFactory'.
         * Bạn CẦN phải truyền 'PooledEngine' và 'World' vào constructor của CollisionSystem,
         * sau đó lấy 'BodyFactory' bằng 'BodyFactory.getInstance(world)'.
         *
         * Giả sử bạn đã làm điều đó:
         * (private PooledEngine engine; private BodyFactory bodyFactory;)
         */

        // Lấy engine và body factory (BẠN CẦN TRUYỀN CHÚNG VÀO CONSTRUCTOR)
        System.out.println("ĐANG TẠO POWER-UP");
        PooledEngine engine = (PooledEngine) getEngine();

        // Tạo Entity mới cho power-up
        Entity powerUpEntity = engine.createEntity();

        // 1. Tạo PhysicsBodyComponent
        PhysicsBodyComponent b2body = engine.createComponent(PhysicsBodyComponent.class);
        b2body.body = this.bodyFactory.makeBoxPolyBody(
            position.x, position.y,
            Utilities.convertToPPM(30), // Kích thước power-up (ví dụ 30 pixel)
            Utilities.convertToPPM(15),
            BodyFactory.Material.PLASTIC,
            BodyDef.BodyType.DynamicBody,
            false,
            true // ĐẶT LÀ SENSOR (để nó đi xuyên qua nhau, chỉ bắt va chạm)
        );
        b2body.body.setGravityScale(0.5f); // Cho nó rơi chậm
        b2body.body.setLinearVelocity(0, -1.5f); // Rơi xuống
        b2body.body.setUserData(powerUpEntity); // Rất quan trọng!

        // 2. Tạo TextureComponent
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // Lấy hình ảnh từ atlas
        TextureRegion tex = levelManager.currentLevel.getTextures().findRegion("power_up");

        //Nếu không tìm thấy ảnh, dùng tạm ảnh quả bóng để test
        if (tex == null) {
            System.out.println("LỖI: Không tìm thấy hình 'power_up'. Dùng tạm hình bóng.");
            tex = levelManager.currentLevel.getTextures().findRegion("Ball_small-blue");
        }


        texture.currImage = tex;

        // 3. Tạo TypeComponent
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.POWERUP_TYPE;

        // 4. Tạo PowerUpComponent
        PowerUpComponent powerUp = engine.createComponent(PowerUpComponent.class);

        // 5. Tạo ColliderComponent
        ColliderComponent collider = engine.createComponent(ColliderComponent.class);

        // Thêm tất cả component vào entity
        powerUpEntity.add(b2body);
        powerUpEntity.add(texture);
        powerUpEntity.add(type);
        powerUpEntity.add(powerUp);
        powerUpEntity.add(collider);

        // Thêm entity vào engine
        engine.addEntity(powerUpEntity);
        System.out.println("ĐÃ TẠO XONG POWER-UP!");
    }

    /**
     * Dọn dẹp các tài nguyên (như hạt) mà System này đã tạo ra.
     */
    public void dispose() {
        System.out.println("--- DỌN DẸP CollisionSystem (Hạt) ---");
        if (particlesManager != null) {
            particlesManager.destroy();
        }
    }
}
