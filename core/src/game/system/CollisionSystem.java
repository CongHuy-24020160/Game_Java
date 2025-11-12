package game.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
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
import game.Utils.ScoreManager;
import game.Screen.ScreenManager;

/**
 * <h2>CollisionSystem (Hệ thống Xử lý Va chạm)</h2>
 * <p>
 * Đây là một trong những System quan trọng nhất. Nó không *phát hiện* va chạm
 * (việc đó là của B2dContactListener), mà nó *xử lý hậu quả* sau khi va chạm xảy ra.
 * </p>
 * <p>
 * Nó kế thừa <b>IteratingSystem</b> và chỉ lặp qua các Entity
 * có chứa <b>BallComponent</b> (vì chúng ta chỉ quan tâm đến va chạm của bóng).
 * </p>
 */
public class CollisionSystem extends IteratingSystem {

    private ArkanoidGame game;
    private PooledEngine engine;
    private World world;
    private BodyFactory bodyFactory;
    private ScoreChangeListener scoreChangeListener;
    private LevelManager levelManager;
    private Hud hud;
    public ParticleHandler particlesManager;

    private MainScreen mainScreen;

    /**
     * Hàm khởi tạo (Constructor) của CollisionSystem.
     * <p>
     * Nhận vào tất cả các đối tượng quản lý cần thiết từ MainScreen (Dependency Injection).
     *
     * @param mainScreen          Màn hình game chính
     * @param engine              Engine ECS
     * @param world               Thế giới vật lý Box2D
     * @param hud                 Quản lý giao diện
     * @param levelManager        Quản lý màn chơi
     * @param scoreChangeListener Đối tượng lắng nghe sự kiện thay đổi điểm
     * @param game                Đối tượng ArkanoidGame chính
     */
    public CollisionSystem(MainScreen mainScreen, PooledEngine engine, World world, Hud hud,
                           LevelManager levelManager, ScoreChangeListener scoreChangeListener, ArkanoidGame game) {
        super(Family.all(
            ColliderComponent.class,
            PhysicsBodyComponent.class,
            BallComponent.class
        ).get());

        this.mainScreen = mainScreen;
        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager;
        this.hud = hud;
        this.engine = engine;
        this.world = world;
        this.game = game;

        // Lấy một thể hiện (instance) của BodyFactory
        this.bodyFactory = BodyFactory.getInstance(world);

        // Khởi tạo trình quản lý hiệu ứng hạt cho vụ nổ gạch
        particlesManager = new ParticleHandler("particles/block-particle.p", "particles");
        particlesManager.resizeAll(1f); // Cài đặt tỉ lệ hạt
    }

    // Lấy ColliderComponent (chứa thông tin va chạm)
    private final ComponentMapper<ColliderComponent> collisionC
        = ComponentMapper.getFor(ColliderComponent.class);
    // Lấy PhysicsBodyComponent (chứa body Box2D)
    private final ComponentMapper<PhysicsBodyComponent> b2BodyC
        = ComponentMapper.getFor(PhysicsBodyComponent.class);
    // Lấy BallComponent (chứa thông tin tốc độ của bóng)
    private final ComponentMapper<BallComponent> ballC
        = ComponentMapper.getFor(BallComponent.class);
    // Lấy TypeComponent (cho biết entity là PLAYER, BLOCK, hay BALL)
    private final ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);
    // Lấy TextureComponent (để thay đổi hình ảnh gạch khi bị va chạm)
    private final ComponentMapper<TextureComponent> textureC
        = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<GameStateComponent> gameStateMapper
        = ComponentMapper.getFor(GameStateComponent.class);


    /**
     * Hàm này được gọi mỗi khung hình (frame) cho TỪNG entity
     * khớp với bộ lọc (Family) đã định nghĩa (tức là cho từng quả bóng).
     *
     * @param entity Entity (quả bóng) đang được xử lý
     * @param v      Thời gian delta (không dùng ở đây)
     */
    @Override
    protected void processEntity(Entity entity, float v) {
        // Lấy component va chạm (Collider) từ quả bóng
        final ColliderComponent collision = collisionC.get(entity);
        // Lấy entity MÀ quả bóng đã va chạm (được gán từ B2dContactListener)
        final Entity otherEntity = collision.targetEntity;

        // Nếu không va chạm với gì cả, thoát hàm
        if (otherEntity == null) {
            return;
        }

        // Lấy loại (Type) của entity này (chắc chắn là BALL_TYPE)
        final TypeComponent entityType = typeC.get(entity);

        // Kiểm tra lại (cho chắc) xem có đúng là bóng không
        if (entityType.type == TypeComponent.BALL_TYPE) {
            // Gửi va chạm đến hàm xử lý trung tâm
            handleBallCollision(entity, otherEntity);
        }

        // Xử lý va chạm xong, xóa cờ hiệu va chạm để chuẩn bị cho frame tiếp theo
        collision.targetEntity = null;
    }

    /**
     * Hàm trung tâm điều phối, quyết định xem bóng va chạm với cái gì.
     *
     * @param ballEntity  Entity của quả bóng
     * @param otherEntity Entity đã va chạm (Player hoặc Block)
     */
    private void handleBallCollision(Entity ballEntity, Entity otherEntity) {

        // Lấy loại của đối tượng kia
        final TypeComponent otherType = typeC.get(otherEntity);

        // Nếu đối tượng kia không có loại (ví dụ: tường), thì bỏ qua
        if (otherType == null) {
            return;
        }

        switch (otherType.type) {
            case TypeComponent.PLAYER_TYPE:
                // Nếu là thanh trượt
                handleBallPlayerCollision(ballEntity, otherEntity);
                break;
            case TypeComponent.BLOCK_TYPE:
                // Nếu là gạch
                handleBallBlockCollision(ballEntity, otherEntity);
                break;
        }
    }

    /**
     * Xử lý logic khi bóng va chạm với thanh trượt (Player).
     * <p>
     * Tính toán góc nảy của bóng dựa trên vị trí va chạm trên thanh trượt.
     * </p>
     *
     * @param ballEntity   Entity của bóng
     * @param playerEntity Entity của thanh trượt
     */
    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
        final BallComponent ball = ballC.get(ballEntity);
        final PhysicsBodyComponent ballB2body = b2BodyC.get(ballEntity);
        final PhysicsBodyComponent playerB2body = b2BodyC.get(playerEntity);

        final Vector2 ballPosition = ballB2body.body.getPosition();
        final Vector2 playerPosition = playerB2body.body.getPosition();
        final float paddleWidth = Utilities.convertToPPM(Utilities.PADDLE_WIDTH);
        final float paddleHeight = Utilities.convertToPPM(Utilities.PADDLE_HEIGHT);

        // KIỂM TRA VA CHẠM TỪ PHÍA TRÊN
        float relativeY = ballPosition.y - playerPosition.y;

        // Nếu bóng ở phía trên paddle (va chạm mặt trên)
        if (relativeY > paddleHeight / 2f * 0.5f) {
            // Logic làm chậm bóng
            final float SLOW_DOWN_FACTOR = 0.8f;
            final float MIN_BALL_SPEED = 3.5f;
            ball.BallSpeed = ball.BallSpeed * SLOW_DOWN_FACTOR;
            if (ball.BallSpeed < MIN_BALL_SPEED) {
                ball.BallSpeed = MIN_BALL_SPEED;
            }

            // Tính góc nảy
            float relativeIntersectX = ballPosition.x - playerPosition.x;
            float normalizedIntersectX = MathUtils.clamp(
                relativeIntersectX / (paddleWidth / 2f), -1f, 1f
            );

            float maxBounceAngle = 60f;
            float bounceAngle = 90f - normalizedIntersectX * maxBounceAngle;

            //  Kiểm tra nếu paddle gần tường
            float screenWidth = Utilities.getPPMWidth();
            float distanceToLeftWall = playerPosition.x - paddleWidth / 2f;
            float distanceToRightWall = screenWidth - (playerPosition.x + paddleWidth / 2f);
            float wallThreshold = paddleWidth * 0.3f; // 30% độ rộng paddle

            // Nếu paddle gần tường trái/phải
            if (distanceToLeftWall < wallThreshold || distanceToRightWall < wallThreshold) {
                // Giới hạn góc nảy để tránh bóng đi dọc tường
                float minSafeAngle = 30f;  // Góc tối thiểu 30°
                float maxSafeAngle = 150f; // Góc tối đa 150°

                bounceAngle = MathUtils.clamp(bounceAngle, minSafeAngle, maxSafeAngle);

                // Nếu gần tường trái, ép bóng bay sang phải
                if (distanceToLeftWall < wallThreshold && normalizedIntersectX < -0.5f) {
                    bounceAngle = 60f; // Bật về góc an toàn
                }

                // Nếu gần tường phải, ép bóng bay sang trái
                if (distanceToRightWall < wallThreshold && normalizedIntersectX > 0.5f) {
                    bounceAngle = 120f; // Bật về góc an toàn
                }
            }

            Vector2 velocity = new Vector2(1, 0)
                .setAngleDeg(bounceAngle)
                .scl(ball.BallSpeed);

            // Đảm bảo bóng luôn bay lên
            if (velocity.y < 0) {
                velocity.y *= -1;
            }

            //  Đảm bảo vận tốc Y tối thiểu
            float minVerticalSpeed = ball.BallSpeed * 0.3f; // Ít nhất 30% tốc độ theo trục Y
            if (Math.abs(velocity.y) < minVerticalSpeed) {
                velocity.y = Math.signum(velocity.y) * minVerticalSpeed;
                // Chuẩn hóa lại tổng tốc độ
                velocity.nor().scl(ball.BallSpeed);
            }

            ballB2body.body.setLinearVelocity(velocity);

        } else {
            // Va chạm cạnh bên - chỉ đảo chiều X, giữ nguyên Y
            Vector2 currentVel = ballB2body.body.getLinearVelocity();

            // Đảo chiều ngang
            if ((ballPosition.x < playerPosition.x && currentVel.x > 0) ||
                (ballPosition.x > playerPosition.x && currentVel.x < 0)) {
                currentVel.x *= -1;
            }

            // Đảm bảo vận tốc X và Y đủ lớn để không bị kẹt
            float minSpeed = 0.8f;
            if (Math.abs(currentVel.x) < minSpeed) {
                currentVel.x = Math.signum(currentVel.x) * minSpeed;
            }
            if (Math.abs(currentVel.y) < minSpeed) {
                currentVel.y = Math.signum(currentVel.y) * minSpeed;
            }

            ballB2body.body.setLinearVelocity(currentVel);
        }
    }

    /**
     * Xử lý logic khi bóng va chạm với gạch (Block).
     *
     * @param ballEntity  Entity của bóng
     * @param blockEntity Entity của gạch
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        // Lấy body vật lý của gạch
        final PhysicsBodyComponent blockB2Body = b2BodyC.get(blockEntity);

        // if the block is unbreakable, do nothing
        if (blockB2Body.lives == 5) {
            return;
        }
        // Trừ 1 "mạng" (lives) của viên gạch
        blockB2Body.lives--;

        // Cập nhật lại hình ảnh (texture) của viên gạch
        TextureComponent texture = textureC.get(blockEntity);
        // (Hàm getTexureNameForEachLive sẽ trả về tên texture dựa trên số "mạng" còn lại)
        texture.currImage = levelManager.currentLevel.getTextures().findRegion(Utilities.getTexureNameForEachLive(blockB2Body.lives));

        // Nếu gạch vẫn còn "mạng" (ví dụ: gạch cứng)
        if (blockB2Body.lives > 0) {
            // Chỉ cập nhật hình ảnh và thoát, không làm gì thêm
            return;
        }

        // --- TỪ ĐÂY TRỞ XUỐNG: Code chỉ chạy khi GẠCH BỊ PHÁ HỦY (lives <= 0) ---

        System.out.println("ĐÃ PHÁ GẠCH!");

        // Giảm số gạch còn lại của màn chơi
        levelManager.currentLevel.numOfBlocksLeft--;
        float multiplier = 1.0f;
        ImmutableArray<Entity> gameStates = getEngine().getEntitiesFor(
            Family.all(GameStateComponent.class).get()
        );

        if (gameStates.size() > 0) {
            GameStateComponent gameState = gameStateMapper.get(gameStates.first());
            if (gameState != null) {
                multiplier = gameState.globalScoreMultiplier;
            }
        }

        int baseScore = 100;
        int finalScoree = (int) (baseScore * multiplier);

        scoreChangeListener.onScoreChanged(finalScoree);

        if (multiplier > 1.0f) {
            System.out.println("🎯 Cộng " + finalScoree + " điểm (x" + multiplier + ")");
        }

        // Đánh dấu body vật lý này là "sẽ bị phá hủy" (sẽ được PhysicSystem dọn dẹp)
        blockB2Body.setToDestroy = true;

        // Kích hoạt hiệu ứng hạt (vụ nổ) tại vị trí của viên gạch
        particlesManager.trigger(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);

        // Kiểm tra xem đã phá hết gạch VÀ màn chơi này chưa được đánh dấu là "hoàn thành"
        if (levelManager.currentLevel.numOfBlocksLeft - levelManager.currentLevel.numOfUnbreakableBlocksLeft <= 0 && !levelManager.isLevelCompleted) {

            // Đánh dấu là đã hoàn thành (để tránh lặp lại logic này)
            levelManager.isLevelCompleted = true;

            // Kiểm tra xem đây có phải màn CUỐI CÙNG không?
            if (levelManager.currentLevelNumber >= LevelManager.MAX_LEVELS) {

                // --- ĐÂY LÀ MÀN CUỐI CÙNG (PHÁ ĐẢO) ---
                int finalScore = hud.getScore(); // Lấy điểm số cuối cùng

                // Đặt cờ hiệu trong MainScreen để chuyển cảnh an toàn
                mainScreen.gameOverPending = true;
                mainScreen.finalScoreForGameOver = finalScore;

                // Kiểm tra xem có phải điểm cao không
                if (ScoreManager.getInstance().isHighScore(finalScore)) {
                    game.lastScore = finalScore; // Lưu điểm để EnterHighScoreScreen lấy
                    game.screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    // Nếu không phải điểm cao, đến màn hình kết thúc
                    game.screenManager.changeScreen(ScreenManager.ENDGAME);
                }

            } else {

                // CHƯA PHẢI MÀN CUỐI (QUA MÀN)
                if (levelManager.currentLevel.numOfBlocksLeft - levelManager.currentLevel.numOfUnbreakableBlocksLeft <= 0) {
                    // Hiển thị dialog "Level Complete!"
                    hud.showLevelCompleteDialog();
                    // Tạm dừng game (dừng bóng, dừng di chuyển)
                    mainScreen.pauseGameSystems();
                }
            }

            return; // Thoát hàm vì đã xử lý xong
        }
        // Tỉ lệ 20% rơi ra power-up (random số từ 1 đến 5, nếu bằng 1 thì rơi)
        if (MathUtils.random(1, 2) == 1)
            // Gọi hàm tạo power-up tại vị trí của gạch
            spawnPowerUp(blockB2Body.body.getPosition());
    }

    /**
     * Hàm tạo ra một Entity Power-up và thả nó vào game.
     *
     * @param position Vị trí (tọa độ) của viên gạch đã vỡ
     */
    private void spawnPowerUp(Vector2 position) {
        System.out.println("ĐANG TẠO POWER-UP");

        // Lấy engine ECS (vì chúng ta đang ở trong 1 system)
        PooledEngine engine = (PooledEngine) getEngine();

        // Tạo một Entity rỗng mới
        Entity powerUpEntity = engine.createEntity();

        //  Tạo PhysicsBodyComponent
        PhysicsBodyComponent b2body = engine.createComponent(PhysicsBodyComponent.class);
        // Dùng BodyFactory để tạo body vật lý
        b2body.body = this.bodyFactory.makeBoxPolyBody(
            position.x, position.y, // Tại vị trí gạch vỡ
            Utilities.convertToPPM(30), // Rộng 30 pixel
            Utilities.convertToPPM(15), // Cao 15 pixel
            BodyFactory.Material.PLASTIC, // Vật liệu (ít quan trọng)
            BodyDef.BodyType.DynamicBody, // Body động (để nó có thể rơi)
            false, // Không phải hình tròn
            true   // LÀ SENSOR (rất quan trọng, để nó đi xuyên gạch/bóng)
        );
        b2body.body.setGravityScale(0.5f); // Cho nó rơi chậm (nửa trọng lực)
        b2body.body.setLinearVelocity(0, -1.5f); // Cho vận tốc rơi ban đầu
        b2body.body.setUserData(powerUpEntity); // Gắn Entity vào body (để B2dContactListener nhận diện)

        //  TextureComponent
        TextureComponent texture = engine.createComponent(TextureComponent.class);

        //  PowerUpComponent (chọn loại)
        PowerUpComponent powerUp = engine.createComponent(PowerUpComponent.class);

        int rand = MathUtils.random(1, 100);

        TextureRegion tex;

        // POWER-UPS (tổng 70%)
        if (rand <= 18) {
            powerUp.powerUpType = PowerUpComponent.PowerUpType.EXTRA_LIFE;
            tex = levelManager.currentLevel.getTextures().findRegion("power_up");
            System.out.println(" EXTRA_LIFE (18%)");

        } else if (rand <= 36) {
            powerUp.powerUpType = PowerUpComponent.PowerUpType.DOUBLE_SCORE;
            tex = levelManager.currentLevel.getTextures().findRegion("power_up");
            System.out.println(" DOUBLE_SCORE (18%)");

        } else if (rand <= 54) {
            powerUp.powerUpType = PowerUpComponent.PowerUpType.EXPAND_PADDLE;
            tex = levelManager.currentLevel.getTextures().findRegion("power_up");
            System.out.println(" EXPAND_PADDLE (18%)");

        } else if (rand <= 70) {
            powerUp.powerUpType = PowerUpComponent.PowerUpType.SLOWDOWN_BALL;
            tex = levelManager.currentLevel.getTextures().findRegion("power_up");
            System.out.println("SLOWDOWN_BALL (16%)");

            // POWER-DOWNS (tổng 30%)
        } else if (rand <= 80) {
            powerUp.powerDownType = PowerUpComponent.PowerDownType.SHRINK_PADDLE;
            tex = levelManager.currentLevel.getTextures().findRegion("power_down");
            System.out.println(" SHRINK_PADDLE (10%)");

        } else if (rand <= 90) {
            powerUp.powerDownType = PowerUpComponent.PowerDownType.SPEEDUP_BALL;
            tex = levelManager.currentLevel.getTextures().findRegion("power_down");
            System.out.println(" SPEEDUP_BALL (10%)");

        } else {
            powerUp.powerDownType = PowerUpComponent.PowerDownType.LOSE_LIFE;
            tex = levelManager.currentLevel.getTextures().findRegion("power_down");
            System.out.println(" LOSE_LIFE (10%)");
        }

        // Fallback texture
        if (tex == null) {
            tex = levelManager.currentLevel.getTextures().findRegion("Ball_small-blue");
        }
        texture.currImage = tex;

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.POWERUP_TYPE;
        ColliderComponent collider = engine.createComponent(ColliderComponent.class);

        powerUpEntity.add(b2body);
        powerUpEntity.add(texture);
        powerUpEntity.add(type);
        powerUpEntity.add(powerUp);
        powerUpEntity.add(collider);

        engine.addEntity(powerUpEntity);
        System.out.println(" HOÀN TẤT!");
    }

    /**
     * Dọn dẹp các tài nguyên mà System này sử dụng.
     */
    public void dispose() {
        System.out.println("DỌN DẸP CollisionSystem");
        // Hủy trình quản lý hạt để tránh rò rỉ bộ nhớ
        if (particlesManager != null) {
            particlesManager.destroy();
        }
    }
}
