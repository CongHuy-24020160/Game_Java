// import các package cần thiết
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2; // Vector 2 chiều
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class BallComponent implements Component, Pool.Poolable {

    // Enum mô tả hướng bật bóng
    public enum Direction { LEFT, RIGHT, MID }

    public float BallSpeed = 0;      // Tốc độ bóng
    public boolean isDead = false;   // Cờ kiểm tra bóng "chết" (rơi ra ngoài)
    public boolean canBounce = false;// Có bật được hay không
    public boolean canLinked = false;// Có liên kết với vật khác hay không
    public Vector2 preSpeed;         // Lưu vận tốc trước khi bị dừng

    /**
     * Xử lý khi bóng bật lại từ thanh đỡ hoặc tường
     * @param direction hướng bật (LEFT, RIGHT, MID)
     * @param ballBody đối tượng vật lý của bóng (Box2D Body)
     */
    public void bounceDirection(Direction direction, Body ballBody) {
        if (!canBounce) {
            return; // Nếu không bật được thì bỏ qua
        }

        System.out.println("Hướng bật là: " + direction);

        float angle; // Góc bật theo độ
        if (direction == Direction.LEFT) {
            angle = MathUtils.random(120, 150); // Bật về trái → góc 120-150°
        } else if (direction == Direction.RIGHT) {
            angle = MathUtils.random(30, 60);   // Bật về phải → góc 30-60°
        } else {
            angle = MathUtils.random(60, 120);  // Bật thẳng → góc 60-120°
        }

        // Reset vận tốc hiện tại để tránh cộng dồn
        ballBody.setLinearVelocity(0, 0);

        // Tạo vector vận tốc mới theo góc vừa chọn
        Vector2 velocity = new Vector2(1, 1);
        velocity.setAngleDeg(angle);    // Xoay vector theo góc
        velocity = velocity.nor().scl(BallSpeed); // Chuẩn hóa & nhân tốc độ

        // Áp dụng xung lực để bóng bay theo hướng mới
        ballBody.applyLinearImpulse(velocity, ballBody.getWorldCenter(), true);
        System.out.println("Vận tốc mới của bóng: " + ballBody.getLinearVelocity());

        // Sau khi bật xong thì tạm thời không cho bật tiếp (tránh bật liên tục)
        canBounce = false;
    }

    /**
     * Đảo hướng chuyển động theo trục X (ví dụ: đập tường bên)
     */
    public void reverseX(Body ballB2body) {
        Vector2 velocity = ballB2body.getLinearVelocity();

        // Tính xung lực cần thiết để đảo hướng theo khối lượng
        float impulse = -2 * velocity.x * ballB2body.getMass();

        // Áp dụng xung lực ngược trên trục X
        ballB2body.applyLinearImpulse(new Vector2(impulse, 0), ballB2body.getWorldCenter(), true);
        System.out.println("ReverseX: " + ballB2body.getLinearVelocity());
    }

    /**
     * Đảo hướng chuyển động theo trục Y (ví dụ: bật sàn hoặc trần)
     */
    public void reverseY(Body ballB2body) {
        Vector2 velocity = ballB2body.getLinearVelocity();
        float impulse = -2 * velocity.y * ballB2body.getMass();

        // Áp dụng xung lực ngược trên trục Y
        ballB2body.applyLinearImpulse(new Vector2(0, impulse), ballB2body.getWorldCenter(), true);
        System.out.println("ReverseY: " + ballB2body.getLinearVelocity());
    }

    /**
     * Đặt lại trạng thái bóng về mặc định
     */
    @Override
    public void reset() {
        System.out.println("Reset bóng");
        BallSpeed = 0;
        isDead = false;
        canBounce = true;
        canLinked = false;
        preSpeed = null;
    }
}
