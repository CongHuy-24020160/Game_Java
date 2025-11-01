package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;
import game.Hud;
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

    // ⭐️ SỬA LỖI: Khai báo 3 biến này ở đây ⭐️
    private ScoreChangeListener scoreChangeListener;
    private LevelManager levelManager;
    private Hud hud;
    public ParticleHandler particlesManager;

    // ⭐️ SỬA LỖI: Hàm khởi tạo (Constructor) phải NHẬN và GÁN 3 biến này ⭐️
    public CollisionSystem(Hud hud, LevelManager levelManager, ScoreChangeListener scoreChangeListener) {
        super(Family.all(ColliderComponent.class, PhysicsBodyComponent.class, BallComponent.class).get());

        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager; // Gán biến
        this.hud = hud; // Gán biến

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
        final Entity otherEntity = collision.tagertEntity;

        if (otherEntity == null) return;

        final TypeComponent entityType = typeC.get(entity);

        // Chỉ xử lý va chạm bắt nguồn từ quả bóng
        if (entityType.type == TypeComponent.BALL_TYPE) {
            handleBallCollision(entity, otherEntity);
        }

        // Đặt lại va chạm để chuẩn bị cho khung hình tiếp theo.
        collision.tagertEntity = null;
    }

    /**
     * @param ballEntity Entity của quả bóng.
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

        if (ArkanoidGame.DEBUG_MODE){
            System.out.println("relativeIntersectX: " + relativeIntersectX);
            System.out.println("normalizedIntersectX: " + normalizedIntersectX);
            System.out.println("bounceAngle: " + bounceAngle);
        }

        Vector2 velocity = new Vector2(1, 0).setAngleDeg(bounceAngle).scl(ball.BallSpeed);
        if (velocity.y < 0) velocity.y *= -1;
        ballB2body.body.setLinearVelocity(velocity);
    }

    /**
     * ⭐️ ĐÂY LÀ HÀM ĐÃ SỬA LỖI (CHỈ CÓ 1 HÀM NÀY) ⭐️
     * xử lý hậu quả của việc phá gạch.
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        final PhysicsBodyComponent blockB2Body = b2BodyC.get(blockEntity);

        // Cộng điểm
        scoreChangeListener.onScoreChanged(100);

        // Giảm số lượng gạch
        levelManager.currentLevel.numOfBlocksLeft--;

        // Tạo hiệu ứng hạt (Giữ nguyên)
        particlesManager.trigger(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);

        // Đánh dấu gạch là "cần hủy" (Giữ nguyên)
        blockB2Body.setToDestroy = true;

        // ⭐️ BẮT ĐẦU LOGIC THẮNG (Đã sửa lỗi chính tả) ⭐️

        // Kiểm tra điều kiện hoàn thành màn chơi.
        if (levelManager.currentLevel.numOfBlocksLeft <= 0 && !levelManager.isLevelCompleted) {

            levelManager.isLevelCompleted = true; // Đánh dấu là đã hoàn thành

            // Kiểm tra xem đây có phải màn CUỐI CÙNG không?
            if (levelManager.currentLevelNumber >= LevelManager.MAX_LEVELS) {

                // *** ĐÂY LÀ MÀN CUỐI CÙNG -> LƯU KỈ LỤC ***
                int finalScore = hud.getScore();
                if (ScoreManager.getInstance().isHighScore(finalScore)) {
                    levelManager.getGame().lastScore = finalScore;
                    levelManager.getGame().screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    levelManager.getGame().screenManager.changeScreen(ScreenManager.ENDGAME);
                }

            } else {

                // *** CHƯA PHẢI MÀN CUỐI -> HIỆN BẢNG "NEXT LEVEL" ***
                hud.showLevelCompleteDialog();
            }

            return; // Đã xử lý xong, thoát hàm
        }
        // ⭐️ KẾT THÚC LOGIC THẮNG ⭐️
    }
} // <-- Dấu } cuối cùng của Class
