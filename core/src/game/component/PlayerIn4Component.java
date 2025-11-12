package game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

/**
 * Lớp {@code PlayerIn4Component} biểu diễn thông tin đặc trưng của thực thể người chơi (Player)
 * trong hệ thống ECS (Entity Component System) của trò chơi.
 * <p>
 * Thành phần này lưu trữ dữ liệu liên quan đến người chơi như camera quan sát, kích thước paddle (vợt),
 * hoặc các chỉ số cơ bản khác. Nó không chứa logic xử lý mà chỉ đóng vai trò lưu trữ thông tin.
 * <p>
 * Thông qua hệ thống ECS, các hệ thống (systems) khác có thể đọc dữ liệu trong component này
 * để cập nhật hành vi, hiển thị, hoặc điều chỉnh gameplay của người chơi.
 */
public class PlayerIn4Component implements Component, Pool.Poolable {

    /**
     * Camera gắn liền với người chơi.
     * <p>
     * Camera này theo dõi chuyển động của người chơi trong thế giới game, đảm bảo
     * góc nhìn luôn được căn chỉnh phù hợp. Một số trò chơi có thể sử dụng camera riêng
     * cho từng người chơi (đặc biệt trong chế độ nhiều người).
     */
    public OrthographicCamera camera;

    public float lengthMultiplier = 0.8f;

    public void expand(float amount) {
        this.lengthMultiplier += amount;
        this.lengthMultiplier = MathUtils.clamp(this.lengthMultiplier, 0.3f, 3.0f);
    }

    public void shrink(float amount) {
        this.lengthMultiplier -= amount;
        this.lengthMultiplier = MathUtils.clamp(this.lengthMultiplier, 0.3f, 3.0f);
    }

    public void resetLength() {
        this.lengthMultiplier = 0.8f;
    }

    /**
     * Phương thức lấy (getter) trả về camera đang được gắn cho người chơi.
     *
     * @return đối tượng {@link OrthographicCamera} gắn với người chơi.
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Phương thức thiết lập (setter) gán một camera cụ thể cho người chơi.
     *
     * @param camera đối tượng {@link OrthographicCamera} được gắn cho người chơi.
     */
    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void reset() {
        camera = null;
        lengthMultiplier = 0.8f;
    }


}
