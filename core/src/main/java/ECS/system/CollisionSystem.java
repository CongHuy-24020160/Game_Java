package ECS.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.Hud;
import com.Utilities;
import com.ECS.components.*;
import com.level.LevelManager;
import com.tlisteners.ScoreChangeListener;
import com.utils.ParticlesManager;

/*
    System chịu trách nhiệm xử lý logic *sau khi* các va chạm vật lý xảy ra.
 */
public class CollisionSystem extends IteratingSystem {
    private ComponentMapper<CollisionComponent> collisionC
        = ComponentMapper.getFor(CollisionComponent.class);
    private ComponentMapper<B2BodyComponent> b2BodyC
        = ComponentMapper.getFor(B2BodyComponent.class);
    private ComponentMapper<BallComponent> ballC
        = ComponentMapper.getFor(BallComponent.class);
    private ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<TextureComponent> textureC =
        ComponentMapper.getFor(TextureComponent.class);

    private final ScoreChangeListener scoreChangeListener;
    private final LevelManager levelManager;
    private final Hud hud;
    private final ParticlesManager particlesManager;

    public CollisionSystem(ScoreChangeListener scoreChangeListener, LevelManager levelManager, Hud hud, ParticlesManager particlesManager) {
        // System này chỉ xử lý các Entity có CollisionComponent (tức là vừa xảy ra va chạm).
        super(Family.all(CollisionComponent.class).get());
        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager;
        this.hud = hud;
        this.particlesManager = particlesManager;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Lấy các component cần thiết bằng mapper đã khai báo.
        final CollisionComponent collision = collisionC.get(entity);
        final Entity otherEntity = collision.collisionEntity;

        // Bỏ qua nếu không có va chạm nào được ghi nhận.
        if (otherEntity == null) return;

        // Tạm dừng logic va chạm khi có dialog (ví dụ: menu) đang hiển thị.
        if (hud.isDialogVisible()) return;

        final TypeComponent entityType = typeC.get(entity);

        // Chỉ xử lý va chạm bắt nguồn từ quả bóng
        if (entityType.type == TypeComponent.BALL) {
            handleBallCollision(entity, otherEntity);
        }

        // Đặt lại va chạm để chuẩn bị cho khung hình tiếp theo.
        collision.collisionEntity = null;
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
            case TypeComponent.PLAYER:
                handleBallPlayerCollision(ballEntity, otherEntity);
                break;
            case TypeComponent.BLOCK:
                handleBallBlockCollision(ballEntity, otherEntity);
                break;
        }
    }

    /**
     * tính toán và áp dụng góc nảy cho bóng.
     */
    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
        final B2BodyComponent ballB2body = b2BodyC.get(ballEntity);
        final B2BodyComponent playerB2body = b2BodyC.get(playerEntity);
        final BallComponent ball = ballC.get(ballEntity);

        final Vector2 ballPosition = ballB2body.body.getPosition();
        final Vector2 playerPosition = playerB2body.body.getPosition();
        final float paddleWidth = Utilities.PADDLE_WIDTH;

        // Mặc định nảy thẳng lên.
        float angle = 90;

        // Tính toán vị trí va chạm tương đối trên thanh trượt để quyết định góc nảy.
        if (ballPosition.x < playerPosition.x - paddleWidth / 6) { // Va chạm phần bên trái của thanh trượt
            angle = 135;
        } else if (ballPosition.x > playerPosition.x + paddleWidth / 6) { // Va chạm phần bên phải
            angle = 45;
        }

        // Tạo và áp dụng lực nảy mới cho bóng.
        Vector2 force = new Vector2(0, 1).setAngleDeg(angle).scl(ball.speed);
        ballB2body.body.setLinearVelocity(force);
    }

    /**
     * xử lý hậu quả của việc phá gạch.
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        final B2BodyComponent blockB2Body = b2BodyC.get(blockEntity);

        // Cộng điểm cho người chơi.
        scoreChangeListener.onScoreChanged(100);

        // Giảm số lượng gạch còn lại trên màn chơi.
        levelManager.currentLevel.blockCount--;

        // Kiểm tra điều kiện hoàn thành màn chơi.
        if (levelManager.currentLevel.blockCount <= 0) {
            hud.showLevelCompletedDialog();
        }

        // Tạo hiệu ứng hạt tại vị trí gạch vỡ.
        particlesManager.spawn(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);

        // Đánh dấu viên gạch là "cần hủy" để PhysicsSystem xử lý việc xóa nó.
        blockB2Body.setToDestroy = true;

    }
}
