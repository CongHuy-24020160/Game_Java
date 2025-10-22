package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import game.Hud;
import game.Utilities;
import game.component.PhysicsBodyComponent;
import game.component.BallComponent;
import game.level.LevelManager;
import game.Utils.UtilSound;

/*
    System quản lý các logic đặc thù của quả bóng,
    chủ yếu là va chạm với các cạnh màn hình và điều kiện thua cuộc.
 */
public class BallSystem extends IteratingSystem {
    // tốc độ mặc định của bóng.
    private static final float DEFAULT_BALL_SPEED = 5f;

    private ComponentMapper<BallComponent> ballMapper = ComponentMapper.getFor(BallComponent.class);
    private ComponentMapper<PhysicsBodyComponent> b2bodyMapper = ComponentMapper.getFor(PhysicsBodyComponent.class);

    private final Hud hud;
    private final LevelManager levelManager;

    public BallSystem(Hud hud, LevelManager levelManager) {
        super(Family.all(BallComponent.class, PhysicsBodyComponent.class).get());
        this.hud = hud;
        this.levelManager = levelManager;
    }

    @Override



    protected void processEntity(Entity entity, float v) {
        final BallComponent ballC = ballMapper.get(entity);
        final PhysicsBodyComponent ballB2body = b2bodyMapper.get(entity);

        // Bỏ qua nếu bóng đã bị hủy (ví dụ: khi tải màn chơi mới).
        if (ballB2body.isDead)
            return;

        handleScreenBoundaryCollisions(ballC, ballB2body);
        handleOutOfBounds(ballC, ballB2body);
    }


    /**
     * xử lý va chạm với 3 cạnh trên, trái, và phải của màn hình.
     */
    private void handleScreenBoundaryCollisions(BallComponent ballC, PhysicsBodyComponent ballB2body) {
        final Vector2 ballPosition = ballB2body.body.getPosition();
        final float ballRadius = ballB2body.body.getFixtureList().get(0).getShape().getRadius();

        // Va chạm với cạnh trên
        if (ballPosition.y + ballRadius >= Utilities.getPPMHeight()) {
            ballC.reverseY(ballB2body.body);
            ballC.canBounce = true;
        }

        // Va chạm với cạnh trái hoặc phải
        if (ballPosition.x + ballRadius >= Utilities.getPPMWidth() || ballPosition.x - ballRadius <= 0) {
            ballC.reverseX(ballB2body.body);
            ballC.canBounce = true;
        }
    }

    /**
     * xử lý khi bóng rơi ra khỏi cạnh dưới.
     */
    private void handleOutOfBounds(BallComponent ballC, PhysicsBodyComponent ballB2body) {
        final Vector2 ballPosition = ballB2body.body.getPosition();
        final float ballRadius = ballB2body.body.getFixtureList().get(0).getShape().getRadius();

        // Chỉ thực hiện khi bóng rơi qua cạnh dưới và chưa bị xử lý trước đó.
        if(ballPosition.y - ballRadius <= 0 && !ballC.isDead){
            UtilSound.getInstance().playMissBallSound();

            // Giảm mạng sống và cập nhật hiển thị.
            hud.setLives(hud.getLives() - 1);
            hud.updateLives();

             // Kiểm tra điều kiện thua cuộc.
           if(hud.getLives() <= 0){
               hud.showGameOverDialog();
           }

            // Đánh dấu bóng là đã "chết" trong lượt này để tránh xử lý nhiều lần.
            ballC.isDead = true;

            // Đặt lại vị trí của bóng về thanh trượt.
            levelManager.currentLevel.paddleAndBall.attachBallToPaddle();

            // Đặt lại các thuộc tính của bóng cho lượt chơi tiếp theo.
            ballC.reset();
            ballC.setBallSpeed(DEFAULT_BALL_SPEED);
        }
    }
}
