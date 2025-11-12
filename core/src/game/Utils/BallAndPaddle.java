package game.Utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import game.Utilities;
import game.component.PhysicsBodyComponent;
////import com.taptap.breakout.ecs.components.PhiysicsBodyComponent;
import game.component.LinkedEntityComponent;

/**
 * BallAndPaddle
 * <p>
 * Lớp này chịu trách nhiệm quản lý việc "gắn" quả bóng (ball) vào thanh gạt
 * (paddle).
 * Tình huống sử dụng điển hình là khi bắt đầu ván mới hoặc sau khi người chơi
 * mất mạng,
 * ta cần dừng quả bóng và đặt nó trở lại vị trí phía trên paddle để chuẩn bị
 * cho lần phát tiếp theo.
 *
 * <p>
 * Ghi chú thiết kế:
 * - Lớp này KHÔNG thay đổi logic vật lý hay tạo body mới — nó chỉ thao tác trực
 * tiếp
 * trên các PhysicsBodyComponent và LinkedEntityComponent đã tồn tại trên các
 * Entity được truyền vào.
 * - Các Entity (paddle và ball) được giữ ở dạng immutable (final) trong suốt
 * vòng đời của đối tượng
 * BallAndPaddle để tránh thay đổi tham chiếu bất ngờ.
 */
public class BallAndPaddle {

    /**
     * ComponentMapper dùng để truy xuất PhysicsBodyComponent từ một Entity.
     * <p>
     * ComponentMapper cung cấp cách truy xuất component nhanh và an toàn hơn so với
     * tìm trực tiếp
     * trên entity mỗi lần cần dùng — nó tối ưu hóa cho ECS (Entity Component
     * System).
     */
    private final ComponentMapper<PhysicsBodyComponent> bodyMapper = ComponentMapper.getFor(PhysicsBodyComponent.class);

    /**
     * ComponentMapper dùng để truy xuất LinkedEntityComponent từ một Entity.
     * LinkedEntityComponent thường lưu tham chiếu đến một Entity khác (ví dụ:
     * paddle liên kết đến ball khi "giữ" bóng).
     */
    private final ComponentMapper<LinkedEntityComponent> linkMapper = ComponentMapper
        .getFor(LinkedEntityComponent.class);

    /**
     * Tham chiếu đến Entity đại diện cho paddle (thanh gạt).
     * Khai báo final vì tham chiếu đến paddle không thay đổi trong suốt vòng đời
     * của đối tượng này.
     */
    private final Entity paddle;

    /**
     * Tham chiếu đến Entity đại diện cho ball (quả bóng).
     * Khai báo final vì tham chiếu đến ball không thay đổi trong suốt vòng đời của
     * đối tượng này.
     */
    private final Entity ball;

    /**
     * Constructor của BallAndPaddle.
     *
     * @param paddle Entity đại diện cho paddle. Giả định rằng Entity này đã có sẵn
     *               PhysicsBodyComponent
     *               và LinkedEntityComponent (nếu cần) trước khi truyền vào.
     * @param ball   Entity đại diện cho ball. Giả định rằng Entity này đã có sẵn
     *               PhysicsBodyComponent.
     *               <p>
     *               Lưu ý: constructor không thực hiện kiểm tra null để tránh
     *               overhead trong runtime; nếu muốn
     *               thêm lớp kiểm tra, có thể bổ sung trước khi gọi constructor.
     */
    public BallAndPaddle(Entity paddle, Entity ball) {
        this.paddle = paddle;
        this.ball = ball;
    }

    /**
     * Gắn quả bóng vào paddle.
     * <p>
     * Mô tả hành động:
     * 1. Lấy PhysicsBodyComponent của ball và paddle thông qua bodyMapper.
     * 2. Dừng chuyển động của quả bóng bằng cách đặt vận tốc tuyến tính = (0,0).
     * 3. Tính vị trí mới cho quả bóng: cùng x của paddle, y là phía trên paddle một
     * khoảng bằng
     * chiều cao paddle (được quy đổi sang đơn vị PPM nếu cần) cộng thêm 2 (là
     * khoảng đệm nhỏ).
     * Việc sử dụng Utilities.convertToPPM đảm bảo chuyển đổi đúng giữa các đơn vị
     * màn hình và physics.
     * 4. Dịch chuyển (transform) body của quả bóng tới vị trí mới và xoay góc = 0f.
     * 5. Lấy LinkedEntityComponent của paddle và set linked entity thành ball, tức
     * paddle "giữ" quả bóng.
     * <p>
     * Ghi chú quan trọng về an toàn và giả định:
     * - Giả định các component cần thiết tồn tại. Nếu component không có, gọi
     * mapper.get(...) có thể trả về null
     * và gây NullPointerException khi truy xuất tiếp. Nếu muốn an toàn hơn, có thể
     * kiểm tra null trước khi thao tác.
     * - Việc đặt linear velocity về (0,0) là cách đơn giản và trực tiếp để dừng
     * chuyển động. Trong một số engine,
     * có thể cần reset cả awake/sleep state của body nếu muốn tránh body tiếp tục
     * nhận lực.
     */
    public void attachBallToPaddle() {
        // Lấy component vật lý của bóng và paddle từ các Entity tương ứng.
        PhysicsBodyComponent ballBody = bodyMapper.get(ball);
        PhysicsBodyComponent paddleBody = bodyMapper.get(paddle);

        // Dừng chuyển động của bóng
        ballBody.body.setLinearVelocity(0, 0);

        //  Ngủ body để KHÔNG xử lý va chạm
        ballBody.body.setAwake(false);

        // Đặt lại vị trí bóng lên phía trên paddle
        float newX = paddleBody.body.getPosition().x;
        float newY = paddleBody.body.getPosition().y + Utilities.convertToPPM(Utilities.PADDLE_HEIGHT + 2);

        ballBody.body.setTransform(newX, newY, 0f);

        // Cập nhật liên kết
        LinkedEntityComponent link = linkMapper.get(paddle);
        link.setLinkedEntity(ball);
    }
}
