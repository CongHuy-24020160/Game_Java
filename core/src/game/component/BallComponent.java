package game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import game.ArkanoidGame;

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
        if (ArkanoidGame.DEBUG_MODE) System.out.println("Đảo X: " + ballB2body.getLinearVelocity());
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
        if (ArkanoidGame.DEBUG_MODE) System.out.println("Đảo Y: " + ballB2body.getLinearVelocity());
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
        if(ArkanoidGame.DEBUG_MODE) System.out.println("Reset bóng");

        // Gán lại các giá trị mặc định
        BallSpeed = 0;
        isDead = false;
        canBounce = true;
        canLinked = true;
        preSpeed = null;
    }
}
