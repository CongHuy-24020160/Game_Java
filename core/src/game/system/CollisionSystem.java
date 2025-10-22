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

/*
    System chịu trách nhiệm xử lý logic *sau khi* các va chạm vật lý xảy ra.
 */
public class CollisionSystem extends IteratingSystem {

    public CollisionSystem(Hud hud, LevelManager levelManager, ScoreChangeListener scoreChangeListener) {
        super(Family.all(ColliderComponent.class, PhysicsBodyComponent.class, BallComponent.class).get());

        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager;
        this.hud = hud;
        //Fixme
        particlesManager = new ParticleHandler("particles/block-particle.p", "particles");
        particlesManager.resizeAll(1f);
    }

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

    private ScoreChangeListener scoreChangeListener;
    private LevelManager levelManager;
    private Hud hud;
    public ParticleHandler particlesManager;

    @Override
    protected void processEntity(Entity entity, float v) {
        // Lấy các component cần thiết bằng mapper đã khai báo.
        final ColliderComponent collision = collisionC.get(entity);
        final Entity otherEntity = collision.tagertEntity;

        // Bỏ qua nếu không có va chạm nào được ghi nhận.
        if (otherEntity == null) return;

        // Tạm dừng logic va chạm khi có dialog (ví dụ: menu) đang hiển thị.
//        if (hud.isDialog()) return;
//
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
     * tính toán và áp dụng góc nảy cho bóng.
     */
//    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
//        final PhysicsBodyComponent ballB2body = b2BodyC.get(ballEntity);
//        final PhysicsBodyComponent playerB2body = b2BodyC.get(playerEntity);
//        final BallComponent ball = ballC.get(ballEntity);
//
//        final Vector2 ballPosition = ballB2body.body.getPosition();
//        final Vector2 playerPosition = playerB2body.body.getPosition();
//        final float paddleWidth = Utilities.convertToPPM(Utilities.PADDLE_WIDTH);
//        float relativeIntersectX = ballPosition.x - playerPosition.x ;
//        float normalizedIntersectX = relativeIntersectX / paddleWidth;
//        float maxBoucedAngle = (float)Math.toDegrees(150);
//        // Mặc định nảy thẳng lên.
//        float angle = 90;
//
//        float bounceAngle = normalizedIntersectX * maxBoucedAngle;
//
//        // Tính toán vị trí va chạm tương đối trên thanh trượt để quyết định góc nảy.
//        if (ballPosition.x < playerPosition.x - paddleWidth / 6) { // Va chạm phần bên trái của thanh trượt
//            angle = 135;
//        } else if (ballPosition.x > playerPosition.x + paddleWidth / 6) { // Va chạm phần bên phải
//            angle = 45;
//        }
//        System.out.println("angle: "+bounceAngle);
//        System.out.println("normalizedIntersectX: "+normalizedIntersectX);
//
//        // Tạo và áp dụng lực nảy mới cho bóng.
//        Vector2 force = new Vector2(1, 1).setAngleDeg(bounceAngle).scl(ball.BallSpeed);
//        ballB2body.body.setLinearVelocity(force);
//    }
    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
        final BallComponent ball = ballC.get(ballEntity);
// 1. Chọn hệ số làm chậm (ví dụ: 0.8f = giảm 20% tốc độ sau mỗi lần chạm)
        final float SLOW_DOWN_FACTOR = 0.8f;

// 2. Đặt tốc độ tối thiểu để bóng không dừng hẳn
        final float MIN_BALL_SPEED = 3.5f;

// 3. Giảm tốc độ của bóng
        ball.BallSpeed = ball.BallSpeed * SLOW_DOWN_FACTOR;

// 4. Đảm bảo tốc độ không bao giờ thấp hơn mức tối thiểu
        if (ball.BallSpeed < MIN_BALL_SPEED) {
            ball.BallSpeed = MIN_BALL_SPEED;
        }
        final PhysicsBodyComponent ballB2body = b2BodyC.get(ballEntity);
        final PhysicsBodyComponent playerB2body = b2BodyC.get(playerEntity);


        final Vector2 ballPosition = ballB2body.body.getPosition();
        final Vector2 playerPosition = playerB2body.body.getPosition();
        final float paddleWidth = Utilities.convertToPPM(Utilities.PADDLE_WIDTH);

        // Tính vị trí va chạm tương đối (từ -1 -> 1)
        float relativeIntersectX = ballPosition.x - playerPosition.x;
        float normalizedIntersectX = relativeIntersectX / (paddleWidth / 2f);

        // Giới hạn giá trị để tránh nảy quá gắt
        normalizedIntersectX = MathUtils.clamp(normalizedIntersectX, -1f, 1f);

        // Góc nảy tối đa tính từ phương thẳng đứng (đơn vị: độ)
        float maxBounceAngle = 60f;

        // Góc phản xạ = 90° ± (normalizedIntersectX * maxBounceAngle)
        // (vì 90° là hướng thẳng lên)
        float bounceAngle = 90f - normalizedIntersectX * maxBounceAngle;

        // In ra debug
        if (ArkanoidGame.DEBUG_MODE){
            System.out.println("relativeIntersectX: " + relativeIntersectX);
            System.out.println("normalizedIntersectX: " + normalizedIntersectX);
            System.out.println("bounceAngle: " + bounceAngle);
        }

        // Tính vector vận tốc mới từ góc nảy
        Vector2 velocity = new Vector2(1, 0).setAngleDeg(bounceAngle).scl(ball.BallSpeed);

        // Đảm bảo bóng luôn đi lên (y > 0)
        if (velocity.y < 0) velocity.y *= -1;

        // Gán vận tốc mới
        ballB2body.body.setLinearVelocity(velocity);
    }

    /**
     * xử lý hậu quả của việc phá gạch.
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        final PhysicsBodyComponent blockB2Body = b2BodyC.get(blockEntity);
        // final PhysicsBodyComponent ballB2Body = b2BodyC.get(ballEntity);
        //final BallComponent ball = ballC.get(ballEntity);
        //final TextureComponent blockTexture = textureC.get(blockEntity);

        //Vector2 ballPosition = ballB2Body.body.getPosition();
        //Vector2 blockPosition = blockB2Body.body.getPosition();
        //ballB2Body.body.setLinearVelocity(0, 0);
        //Vector2 force = new Vector2(0, 0);
        //float angle = 0;

        //if(ballPosition.x <=
            //blockPosition.x - Utilities.convertToPPM((float) blockTexture.currImage.getRegionWidth() /2)){ // LEFT
            //force = new Vector2(-1, 0);
            //angle = MathUtils.random(-150, 150);
        //}else if(ballPosition.x >=
            //blockPosition.x + Utilities.convertToPPM((float) blockTexture.currImage.getRegionWidth() /2)){ // RIGHT
            // force = new Vector2(1, 0);
            // angle = MathUtils.random(-30, 30);
       // }

        // if(ballPosition.y >=
            // blockPosition.y - Utilities.convertToPPM((float) blockTexture.currImage.getRegionWidth() /2)){ // TOP
            // force = new Vector2(0, 1);
           //  angle = MathUtils.random(60, 120);
      // }else if(ballPosition.y <=
           // blockPosition.y + Utilities.convertToPPM((float) blockTexture.currImage.getRegionWidth() /2)){ // DOWN
           // force = new Vector2(0, -1);
           // angle = MathUtils.random(-120, -60);
      //  }

        //force.nor().scl(ball.BallSpeed);
        //force.setAngleDeg(angle);
        //ballB2Body.body.applyLinearImpulse(force, ballB2Body.body.getWorldCenter(), true);
        // Cộng điểm cho người chơi.
        scoreChangeListener.onScoreChanged(100);

        // Giảm số lượng gạch còn lại trên màn chơi.
        levelManager.currentLevel.numOfBlocksLeft--;

        // Kiểm tra điều kiện hoàn thành màn chơi.
        if (levelManager.currentLevel.numOfBlocksLeft <= 0) {
           hud.showLevelCompleteDialog();
          //  levelManager.currentLevel.isL = true;
        }

        // Tạo hiệu ứng hạt tại vị trí gạch vỡ.
        particlesManager.trigger(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);

        // Đánh dấu viên gạch là "cần hủy" để PhysicsSystem xử lý việc xóa nó.
        blockB2Body.setToDestroy = true;

    }
    }
