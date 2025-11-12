package game.component;

import com.badlogic.ashley.core.Component; // Mọi Component trong ECS (Entity Component System) đều phải implement interface này
import com.badlogic.gdx.math.MathUtils;    // Dùng cho các hàm toán học ngẫu nhiên và lượng giác
import com.badlogic.gdx.math.Vector2;      // Vector 2D (dùng biểu diễn vận tốc, hướng, vị trí)
import com.badlogic.gdx.physics.box2d.Body; // Đại diện cho một vật thể vật lý trong thế giới Box2D
import com.badlogic.gdx.utils.Pool;        // Dùng để quản lý bộ nhớ, tái sử dụng đối tượng

/**
 * Lớp BallComponent đại diện cho "bóng" trong trò chơi.
 *
 * <p>Đây là một thành phần (component) trong mô hình ECS (Entity Component System)
 * được sử dụng bởi thư viện Ashley. Lớp này lưu trữ thông tin vật lý, trạng thái,
 * và hành vi cơ bản của quả bóng như tốc độ, hướng, khả năng bật, v.v...</p>
 *
 * <p>Ngoài ra, lớp còn cung cấp các phương thức để xử lý va chạm,
 * bật bóng, đảo hướng di chuyển theo trục X/Y và reset trạng thái khi cần.</p>
 */
public class BallComponent implements Component, Pool.Poolable {

    /**
     * Enum Direction biểu diễn 3 hướng bật cơ bản của bóng.
     * <ul>
     *     <li>LEFT - Bật sang trái</li>
     *     <li>RIGHT - Bật sang phải</li>
     *     <li>MID - Bật gần như thẳng đứng</li>
     * </ul>
     */
    public enum Direction {LEFT, RIGHT, MID}

    /**
     * Tốc độ hiện tại của bóng (đơn vị tùy theo thế giới vật lý Box2D)
     */
    public float BallSpeed = 0;

    /**
     * Xác định xem bóng đã "chết" (rơi khỏi màn hình hoặc ra ngoài bản đồ) hay chưa
     */
    public boolean isDead = false;

    /**
     * Biến cờ cho phép bóng bật lại sau khi va chạm
     */
    public boolean canBounce = false;

    /**
     * Biến cờ cho biết bóng có thể liên kết với vật khác (ví dụ paddle, brick, ...) hay không
     */
    public boolean canLinked = true;

    /**
     * Vận tốc trước khi bóng bị dừng hoặc va chạm
     */
    public Vector2 preSpeed;

    /**
     * Xử lý khi bóng bật lại từ thanh đỡ (paddle) hoặc tường.
     *
     * <p>Phương thức này sẽ chọn ngẫu nhiên một góc bật phù hợp dựa trên hướng
     * (LEFT, RIGHT hoặc MID), sau đó tính toán vector vận tốc mới cho bóng.</p>
     *
     * @param direction hướng bật của bóng (LEFT, RIGHT, MID)
     * @param ballBody  đối tượng vật lý (Body) đại diện cho bóng trong thế giới Box2D
     */
    public void bounceDirection(Direction direction, Body ballBody) {
        // Nếu cờ canBounce = false thì không xử lý bật
        if (!canBounce) {
            return;
        }

        // In hướng bật ra console (dùng cho debug)
        System.out.println("Hướng bật là: " + direction);

        // Góc bật theo độ (0-180)
        float angle;

        // Xác định góc ngẫu nhiên tùy theo hướng bật
        if (direction == Direction.LEFT) {
            // Khi bóng bật sang trái → chọn ngẫu nhiên từ 119 đến 149 độ
            angle = MathUtils.random(119, 149);
        } else if (direction == Direction.RIGHT) {
            // Khi bóng bật sang phải → chọn ngẫu nhiên từ 31 đến 59 độ
            angle = MathUtils.random(31, 59);
        } else {
            // Khi bóng bật giữa (MID) → chọn ngẫu nhiên trong khoảng trung tâm 60-120 độ
            angle = MathUtils.random(61, 119);
        }

        // Reset vận tốc hiện tại để tránh cộng dồn xung lực
        ballBody.setLinearVelocity(0, 0);

        // Tạo một vector mới (1,1) sau đó xoay theo góc vừa chọn
        Vector2 velocity = new Vector2(1, 1);
        velocity.setAngleDeg(angle); // Thiết lập góc của vector theo độ
        velocity = velocity.nor().scl(BallSpeed); // Chuẩn hóa và nhân với tốc độ hiện tại

        // Áp dụng xung lực để bóng bay theo hướng vừa được xác định
        ballBody.applyLinearImpulse(velocity, ballBody.getWorldCenter(), true);

        // In vận tốc mới ra console để kiểm tra
        System.out.println("Vận tốc mới của bóng: " + ballBody.getLinearVelocity());

        // Ngăn bóng bật liên tục (chỉ bật lại sau khi chạm một vật khác)
        canBounce = false;
    }

    /**
     * Đảo hướng chuyển động của bóng theo trục X (thường khi bóng đập vào tường bên).
     *
     * <p>Phương thức này không thay đổi tốc độ mà chỉ đổi hướng chuyển động.</p>
     *
     * @param ballB2body đối tượng vật lý của bóng
     */
    public void reverseX(Body ballB2body) {
        // Lấy vector vận tốc hiện tại
        Vector2 velocity = ballB2body.getLinearVelocity();

        // Tính toán xung lực cần thiết để đảo chiều (gấp đôi vận tốc hiện tại nhưng ngược dấu)
        float impulse = -2 * velocity.x * ballB2body.getMass();

        // Áp dụng xung lực ngược trên trục X
        ballB2body.applyLinearImpulse(new Vector2(impulse, 0), ballB2body.getWorldCenter(), true);

        // In ra vận tốc mới sau khi đảo
        System.out.println("Đảo X: " + ballB2body.getLinearVelocity());
    }

    /**
     * Đảo hướng chuyển động của bóng theo trục Y (ví dụ khi bóng đập vào trần hoặc sàn).
     *
     * @param ballB2body đối tượng vật lý của bóng
     */
    public void reverseY(Body ballB2body) {
        // Lấy vận tốc hiện tại
        Vector2 velocity = ballB2body.getLinearVelocity();

        // Tính toán xung lực đảo chiều theo khối lượng
        float impulse = -2 * velocity.y * ballB2body.getMass();

        // Áp dụng xung lực ngược hướng theo trục Y
        ballB2body.applyLinearImpulse(new Vector2(0, impulse), ballB2body.getWorldCenter(), true);

        // In vận tốc sau khi đảo
        System.out.println("Đảo Y: " + ballB2body.getLinearVelocity());
    }

    /**
     * @return tốc độ hiện tại của bóng
     */
    public float getBallSpeed() {
        return BallSpeed;
    }

    /**
     * @param ballSpeed đặt tốc độ mới cho bóng
     */
    public void setBallSpeed(float ballSpeed) {
        BallSpeed = ballSpeed;
    }

    /**
     * @return true nếu bóng đã "chết"
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * @param dead đặt trạng thái chết cho bóng
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * @return true nếu bóng được phép bật
     */
    public boolean isCanBounce() {
        return canBounce;
    }

    /**
     * @param canBounce bật/tắt khả năng bật
     */
    public void setCanBounce(boolean canBounce) {
        this.canBounce = canBounce;
    }

    /**
     * @return true nếu bóng có thể liên kết với vật khác
     */
    public boolean isCanLinked() {
        return canLinked;
    }

    /**
     * @param canLinked bật/tắt khả năng liên kết
     */
    public void setCanLinked(boolean canLinked) {
        this.canLinked = canLinked;
    }

    /**
     * @return vận tốc trước khi bóng bị dừng
     */
    public Vector2 getPreSpeed() {
        return preSpeed;
    }

    /**
     * @param preSpeed thiết lập vận tốc trước khi dừng
     */
    public void setPreSpeed(Vector2 preSpeed) {
        this.preSpeed = preSpeed;
    }

    /**
     * Đặt lại toàn bộ trạng thái của bóng về mặc định.
     *
     * <p>Phương thức này được gọi tự động khi sử dụng Pool (để tái sử dụng đối tượng mà không cần cấp phát mới).</p>
     */
    @Override
    public void reset() {
        System.out.println("Reset bóng");

        // Gán lại các giá trị mặc định
        BallSpeed = 0;
        isDead = false;
        canBounce = true;
        canLinked = true;
        preSpeed = null;
    }
}
