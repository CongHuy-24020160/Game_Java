// import packages vô đây
package game.component;

import com.badlogic.ashley.core.Component; // mọi component đều phải implement interface Component
import com.badlogic.gdx.physics.box2d.Body; // dùng để gán 1 thực thể vật lý Box2D cho entity
import com.badlogic.gdx.utils.Pool; // tiện ích tái sử dụng đối tượng

/**
 * Lớp {@code PhysicsBodyComponent} biểu diễn một thành phần (component) trong hệ thống ECS (Entity Component System)
 * có nhiệm vụ gắn một đối tượng vật lý {@link Body} của Box2D vào một thực thể (entity) trong trò chơi.
 * <p>
 * Thành phần này cho phép các hệ thống vật lý (Physics System) xử lý chuyển động, va chạm,
 * và các hiệu ứng động học cho thực thể dựa trên cơ chế vật lý thực tế.
 * <p>
 * Lớp này còn quản lý trạng thái sống/chết của thực thể,
 * và được tái sử dụng thông qua cơ chế Pool để tối ưu hiệu năng và bộ nhớ.
 */
public class PhysicsBodyComponent implements Component, Pool.Poolable {

    /**
     * Đối tượng vật lý của Box2D được gán cho thực thể.
     * <p>
     * {@link Body} lưu trữ các thuộc tính vật lý như vị trí, vận tốc, khối lượng,
     * và được Box2D sử dụng để mô phỏng chuyển động của thực thể trong thế giới vật lý.
     * <p>
     * Nếu thực thể không còn tồn tại hoặc chưa được gán vật lý, giá trị này có thể là {@code null}.
     */
    public Body body;

    /**
     * Biến đánh dấu trạng thái “đã chết” của thực thể.
     * <p>
     * Khi {@code isDead = true}, thực thể không còn được xử lý trong game nữa.
     * Biến này thường được sử dụng để ngăn thực thể tương tác với thế giới vật lý
     * hoặc để loại bỏ khỏi hệ thống render.
     */
    public boolean isDead = false;

    /**
     * Biến đánh dấu thực thể sắp bị phá hủy.
     * <p>
     * Khi {@code setToDestroy = true}, thực thể sẽ được xóa trong vòng cập nhật tiếp theo.
     * Điều này giúp tránh việc xóa đối tượng ngay lập tức trong khi hệ thống vật lý đang xử lý va chạm.
     */
    public boolean setToDestroy = false;

    public int lives = 0;

    /**
     * Phương thức {@code reset()} được gọi khi đối tượng này được trả lại vào Pool để tái sử dụng.
     * <p>
     * Mục đích là đặt lại các giá trị của component về trạng thái mặc định,
     * đảm bảo rằng khi lấy ra dùng lại, component không còn giữ dữ liệu cũ.
     * <p>
     * Cụ thể:
     * <ul>
     *   <li>{@code body} được gán về {@code null}</li>
     *   <li>{@code isDead} được đặt về {@code false}</li>
     *   <li>{@code setToDestroy} được đặt về {@code false}</li>
     * </ul>
     */
    @Override
    public void reset() {
        body = null;
        isDead = false;
        setToDestroy = false;
    }
}
