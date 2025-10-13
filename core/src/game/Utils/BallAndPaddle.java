// import các package cần thiết
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.taptap.breakout.Utilities;
import com.taptap.breakout.ecs.components.LinkedEntityComponent;
import com.taptap.breakout.ecs.components.PhiysicsBodyComponent;

// class này dùng để quản lý việc gắn quả bóng vào paddle (khi bắt đầu ván hoặc sau khi mất mạng)
public class BallAndPaddle {

    // tạo mapper để lấy component vật lý và liên kết
    private final ComponentMapper<PhiysicsBodyComponent> bodyMapper = ComponentMapper.getFor(PhiysicsBodyComponent.class);
    private final ComponentMapper<LinkedEntityComponent> linkMapper = ComponentMapper.getFor(LinkedEntityComponent.class);

    // lưu thực thể paddle và ball (final vì không thay đổi trong suốt vòng đời của class)
    private final Entity paddle;
    private final Entity ball;

    // constructor
    public BallAndPaddle(Entity paddle, Entity ball) {
        this.paddle = paddle;
        this.ball = ball;
    }

    // hàm gắn quả bóng trở lại paddle
    public void attachBallToPaddle() {
        // lấy component vật lý của bóng và paddle
        PhiysicsBodyComponent ballBody = bodyMapper.get(ball);
        PhiysicsBodyComponent paddleBody = bodyMapper.get(paddle);

        // dừng chuyển động của bóng
        ballBody.body.setLinearVelocity(0, 0);

        // đặt lại vị trí bóng lên phía trên paddle
        float newX = paddleBody.body.getPosition().x;
        float newY = paddleBody.body.getPosition().y + Utilities.convertToPPM(Utilities.PADDLE_HEIGHT + 2);
        ballBody.body.setTransform(newX, newY, 0f);

        // cập nhật liên kết: paddle "giữ" quả bóng
        LinkedEntityComponent link = linkMapper.get(paddle);
        link.setLinkedEntity(ball);
    }
}
