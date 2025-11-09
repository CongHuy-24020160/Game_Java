package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;
import game.Hud;
import game.Screen.MainScreen;
import game.Utilities;
import game.component.PhysicsBodyComponent;
import game.component.BallComponent;
import game.level.LevelManager;
import game.Utils.UtilSound;
import game.Utils.ScoreManager;
import game.Screen.ScreenManager;

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
    private MainScreen mainScreen;


    public BallSystem(Hud hud, LevelManager levelManager,MainScreen mainScreen) {
        super(Family.all(BallComponent.class, PhysicsBodyComponent.class).get());
        this.hud = hud;
        this.levelManager = levelManager;
        this.mainScreen = mainScreen;
    }


    /* protected void processEntity(Entity entity, float v) {

        final BallComponent ballC = ballMapper.get(entity);
        final PhysicsBodyComponent ballB2body = b2bodyMapper.get(entity);
        // Bỏ qua nếu bóng đã bị hủy
        if (ballB2body.isDead)
            return;

        // CHỈ CHẠY CODE KIỂM SOÁT TỐC ĐỘ/CHỐNG KẸT KHI BÓNG ĐÃ RỜI PADDLE
        // (Kiểm tra !ballC.canLinked)
        if (!ballC.canLinked) {

            // --- BẮT ĐẦU CODE CHỐNG KẸT (Nảy vô hạn) & CHỐNG VĂNG ---
            Vector2 currentVelocity = ballB2body.body.getLinearVelocity();
            float desiredSpeed = ballC.BallSpeed;

            // 1. KIỂM TRA VÀ SỬA LỖI "KHÓA NGANG" (vận tốc Y quá nhỏ)
            // Math.abs là hàm lấy giá trị tuyệt đối
            if (Math.abs(currentVelocity.y) < 0.5f) { // Nếu kẹt ngang
                // "Hích" nhẹ cho bóng bay lên (hoặc xuống)
                currentVelocity.y = Math.signum(currentVelocity.y) * 0.5f;
                // Nếu vận tốc y = 0, mặc định cho bay lên
                if (currentVelocity.y == 0) currentVelocity.y = 0.5f;
            }

            // 2. KIỂM TRA VÀ SỬA LỖI "KHÓA DỌC" (vận tốc X quá nhỏ)
            if (Math.abs(currentVelocity.x) < 0.5f) { // Nếu kẹt dọc
                // "Hích" nhẹ cho bóng bay sang ngang
                currentVelocity.x = Math.signum(currentVelocity.x) * 0.5f;
                // Nếu vận tốc x = 0, mặc định cho bay sang phải
                if (currentVelocity.x == 0) currentVelocity.x = 0.5f;
            }

            // Bỏ qua nếu bóng đã bị hủy (ví dụ: khi tải màn chơi mới).
            if (ballB2body.isDead)
                return;

            handleScreenBoundaryCollisions(ballC, ballB2body);
            handleOutOfBounds(ballC, ballB2body);
        }
    } */

    @Override
    protected void processEntity(Entity entity, float v) {
        final BallComponent ballC = ballMapper.get(entity);
        final PhysicsBodyComponent ballB2body = b2bodyMapper.get(entity);

        // Bỏ qua nếu bóng đã bị hủy
        // Fixme
        if (ballB2body.isDead)
            return;

        // 1. CHẠY CÁC HÀM CŨ TRƯỚC
        // Hàm này có thể sẽ gọi reverseX/reverseY và làm hỏng vận tốc Y
        handleScreenBoundaryCollisions(ballC, ballB2body);
        handleOutOfBounds(ballC, ballB2body);


        // 2. CHẠY CODE SỬA LỖI (CHỐNG KẸT/VĂNG) SAU CÙNG
        // Code này sẽ "ghi đè" lên bất kỳ lỗi nào do reverseX gây ra
        if (!ballC.canLinked) {

            Vector2 currentVelocity = ballB2body.body.getLinearVelocity();
            float desiredSpeed = ballC.BallSpeed;
            boolean isStuck = false;

            // 1. KIỂM TRA "KHÓA NGANG"
            if (Math.abs(currentVelocity.y) < 0.5f) {
                currentVelocity.y = Math.signum(currentVelocity.y) * 0.5f;
                if (currentVelocity.y == 0) currentVelocity.y = 0.5f;
                isStuck = true;
            }

            // 2. KIỂM TRA "KHÓA DỌC"
            if (Math.abs(currentVelocity.x) < 0.5f) {
                currentVelocity.x = Math.signum(currentVelocity.x) * 0.5f;
                if (currentVelocity.x == 0) currentVelocity.x = 0.5f;
                isStuck = true;
            }

            // 3. ÁP DỤNG LOGIC
            if (isStuck) {
                // NẾU BỊ KẸT: Áp dụng vận tốc đã "hích".
                // Nó sẽ thoát khỏi vòng lặp vô hạn.
                ballB2body.body.setLinearVelocity(currentVelocity);
            } else {
                // NẾU KHÔNG BỊ KẸT: Chạy code "chống văng" (chuẩn hóa)
                if (currentVelocity.len() > 0 && currentVelocity.len() != desiredSpeed) {
                    ballB2body.body.setLinearVelocity(currentVelocity.nor().scl(desiredSpeed));
                }
            }

        } // Đóng 'if (!ballC.canLinked)'
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
        if (ballPosition.y - ballRadius <= 0 && !ballC.isDead) {
            UtilSound.getInstance().playMissBallSound();

            // Giảm mạng sống và cập nhật hiển thị.
            hud.setLives(hud.getLives() - 1);
            hud.updateLives();

            // Kiểm tra điều kiện thua cuộc.
            if (hud.getLives() <= 0) {

                // ⭐️ BẮT ĐẦU LOGIC BXH MỚI ⭐️

                int finalScore = hud.getScore();
                ballC.isDead = true; // Đánh dấu bóng chết

                //mainScreen.pauseGameSystems();

                mainScreen.gameOverPending = true;
                mainScreen.finalScoreForGameOver = finalScore;

            } else {

                // ⭐️ CHƯA HẾT MẠNG -> RESET BÓNG (Code cũ của bạn) ⭐️
                ballC.isDead = true;
                levelManager.currentLevel.paddleAndBall.attachBallToPaddle();
                ballC.reset();
                ballC.canLinked = true; // báo rằng bóng đã dc gắn lại
                ballC.setBallSpeed(DEFAULT_BALL_SPEED);
                ballB2body.body.setLinearVelocity(0, 0);

            }
        }
    }
}

