package game.component; // import packages;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * Lớp {@code ColliderComponent} biểu diễn một thành phần (component) trong hệ thống ECS (Entity Component System)
 * có nhiệm vụ xác định khả năng va chạm của một thực thể (entity) trong trò chơi.
 * <p>
 * Mỗi thực thể trong trò chơi có thể được gắn thành phần này để tham gia vào hệ thống xử lý va chạm.
 * Thành phần này lưu trữ thông tin về đối tượng mà thực thể hiện tại đang va chạm gần nhất,
 * cũng như trạng thái kích hoạt va chạm (có thể va chạm hay không).
 * <p>
 * Lớp này triển khai giao diện {@link Pool.Poolable} để có thể tái sử dụng đối tượng,
 * giúp giảm tải việc cấp phát bộ nhớ liên tục trong quá trình chạy game.
 */
public class ColliderComponent implements Pool.Poolable, Component {

    /**
     * Đại diện cho thực thể mà đối tượng hiện tại đang va chạm hoặc gần nhất với va chạm.
     * <p>
     * Thuộc tính này có thể được hệ thống vật lý (physics system) cập nhật liên tục
     * để xác định xem thực thể này đang tiếp xúc hoặc tương tác với đối tượng nào.
     * Nếu không có va chạm nào đang xảy ra, giá trị này sẽ là {@code null}.
     */
    public Entity tagertEntity;

    /**
     * Biến cờ (flag) xác định xem thực thể này có đang kích hoạt khả năng va chạm hay không.
     * <p>
     * - Nếu {@code true}: thực thể có thể tham gia vào các phép kiểm tra va chạm.
     * - Nếu {@code false}: hệ thống sẽ bỏ qua thực thể này trong các phép kiểm tra va chạm.
     * <p>
     * Biến này giúp kiểm soát linh hoạt trạng thái của va chạm, chẳng hạn khi một thực thể tạm thời vô hiệu hóa va chạm
     * (ví dụ: bị ẩn, bị phá hủy, hoặc đang ở trạng thái miễn nhiễm).
     */
    public boolean isActive = true;

    /**
     * Phương thức lấy (getter) trả về thực thể mà đối tượng hiện tại đang va chạm gần nhất.
     *
     * @return thực thể mà đối tượng hiện tại đang va chạm gần nhất, hoặc {@code null} nếu không có va chạm.
     */
    public Entity getTagertEntity() {
        return tagertEntity;
    }

    /**
     * Phương thức thiết lập (setter) thực thể mà đối tượng hiện tại đang va chạm gần nhất.
     *
     * @param tagertEntity thực thể được gán là đối tượng va chạm hiện tại.
     */
    public void setTagertEntity(Entity tagertEntity) {
        this.tagertEntity = tagertEntity;
    }

    /**
     * Phương thức kiểm tra xem thực thể này có đang kích hoạt khả năng va chạm hay không.
     *
     * @return {@code true} nếu thực thể đang hoạt động va chạm, {@code false} nếu bị vô hiệu hóa.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Phương thức thiết lập trạng thái hoạt động của khả năng va chạm cho thực thể này.
     *
     * @param active {@code true} để kích hoạt va chạm, {@code false} để vô hiệu hóa.
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Phương thức {@code reset()} được gọi khi đối tượng được trả lại vào Pool.
     * <p>
     * Nó giúp đặt lại các giá trị của thành phần về trạng thái mặc định,
     * tránh việc giữ lại dữ liệu cũ khi tái sử dụng đối tượng từ Pool.
     * <p>
     * Trong trường hợp này:
     * - {@code tagertEntity} sẽ được gán là {@code null} để xóa thông tin va chạm cũ.
     * - {@code isActive} sẽ được đặt lại là {@code true} để đảm bảo thực thể có thể va chạm trở lại.
     */
    @Override
    public void reset() {
        tagertEntity = null;
        isActive = true;
    }
}
