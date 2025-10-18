package game.component;
// import packback vô đây sau khi code

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Lớp {@code LinkedEntityComponent} là một thành phần (component) trong hệ thống ECS (Entity Component System)
 * dùng để tạo mối liên kết giữa hai thực thể (entities) trong trò chơi.
 * <p>
 * Khi một thực thể mang thành phần này, nó có thể “gắn kết” hoặc “liên kết” với một thực thể khác,
 * ví dụ như: người chơi cầm một món đồ, quái vật điều khiển một vật thể, hoặc một vũ khí gắn với nhân vật.
 * <p>
 * Thành phần này thường được sử dụng bởi các hệ thống logic khác (system) để xử lý hành vi liên kết,
 * chẳng hạn: di chuyển cùng nhau, chia sẻ vị trí, hoặc cập nhật trạng thái dựa trên nhau.
 */
public class LinkedEntityComponent implements Component {

    /**
     * Biến tham chiếu đến thực thể mà đối tượng hiện tại được liên kết với.
     * <p>
     * Ví dụ:
     * <ul>
     *   <li>Một thực thể “vũ khí” có thể liên kết với thực thể “người chơi”.</li>
     *   <li>Một thực thể “đèn” có thể gắn với “xe ô tô”.</li>
     * </ul>
     * Nếu không có liên kết nào, giá trị sẽ là {@code null}.
     */
    public Entity LinkedEntity;

    /**
     * Phương thức lấy (getter) trả về thực thể mà đối tượng hiện tại đang liên kết với.
     *
     * @return thực thể mà đối tượng này liên kết, hoặc {@code null} nếu chưa liên kết với ai.
     */
    public Entity getLinkedEntity() {
        return LinkedEntity;
    }

    /**
     * Phương thức thiết lập (setter) để liên kết thực thể hiện tại với một thực thể khác.
     * <p>
     * Khi phương thức này được gọi, nó sẽ gán thực thể được truyền vào cho biến {@code LinkedEntity}.
     * Đồng thời, nó in ra thông tin của thực thể được liên kết (dùng cho mục đích debug).
     *
     * @param carry thực thể được gán làm đối tượng liên kết (thực thể mà thực thể hiện tại gắn với).
     */
    public void setLinkedEntity(Entity carry)  {
        System.out.println(carry);
        this.LinkedEntity = carry;
    }
}
