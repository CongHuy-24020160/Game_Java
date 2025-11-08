package game.component; // import package vào đây

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

/**
 * Lớp MoveComponent thể hiện khả năng di chuyển của một thực thể (entity) trong
 * trò chơi.
 * <p>
 * Đây là component cung cấp thông tin về vị trí, hướng, góc quay và trạng thái
 * hiển thị của đối tượng.
 * Hệ thống xử lý di chuyển (MovementSystem) sẽ dựa vào dữ liệu trong lớp này
 * để cập nhật vị trí và hiển thị của entity trong mỗi khung hình.
 * <p>
 * Việc triển khai {@link Pool.Poolable} cho phép đối tượng được tái sử dụng
 * nhằm tối ưu bộ nhớ và hiệu suất trong quá trình chạy game.
 */
public class MoveComponent implements Component, Pool.Poolable {

    /** Vị trí của entity trong không gian ba chiều (trục Oxyz). */
    public final Vector3 pos = new Vector3();

    /** Tỉ lệ phóng to hoặc thu nhỏ theo hai trục X, Y. */
    public final Vector2 acceleration = new Vector2(1, 1);

    /** Góc quay của entity (đơn vị: độ). */
    public float angle = 0f;

    /** Biến đánh dấu xem thực thể có đang bị ẩn khỏi hiển thị hay không. */
    public boolean hidden = false;

    /**
     * Lấy vị trí hiện tại của entity.
     * 
     * @return đối tượng {@link Vector3} thể hiện tọa độ hiện tại.
     */
    public Vector3 getPos() {
        return pos;
    }

    /**
     * Thiết lập vị trí mới cho entity.
     * 
     * @param x tọa độ theo trục X
     * @param y tọa độ theo trục Y
     * @param z tọa độ theo trục Z
     */
    public void setPos(float x, float y, float z) {
        this.pos.set(x, y, z);
    }

    /**
     * Lấy giá trị tỉ lệ phóng to theo hai trục.
     * 
     * @return {@link Vector2} thể hiện giá trị tỉ lệ.
     */
    public Vector2 getAcceleration() {
        return acceleration;
    }

    /**
     * Thiết lập tỉ lệ phóng to theo trục X và Y.
     * 
     * @param x tỉ lệ theo trục X
     * @param y tỉ lệ theo trục Y
     */
    public void setAcceleration(float x, float y) {
        this.acceleration.set(x, y);
    }

    /**
     * Lấy góc quay hiện tại của entity.
     * 
     * @return góc quay (đơn vị: độ)
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Thiết lập góc quay mới cho entity.
     * 
     * @param angle giá trị góc quay (đơn vị: độ)
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Kiểm tra xem entity có đang bị ẩn hay không.
     * 
     * @return true nếu entity bị ẩn, false nếu hiển thị.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Thiết lập trạng thái hiển thị cho entity.
     * 
     * @param hidden giá trị true để ẩn, false để hiển thị.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Đặt lại toàn bộ trạng thái về giá trị mặc định.
     * Dùng khi component được thu hồi về pool để tái sử dụng.
     */
    @Override
    public void reset() {
        pos.set(0f, 0f, 0f);
        acceleration.set(1f, 1f);
        angle = 0f;
        hidden = false;
    }
}
