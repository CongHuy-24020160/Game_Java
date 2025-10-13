package game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;

/**
 * Component quản lý hiệu ứng âm thanh trong game.
 */
public class SoundEffectComponent implements Component, Pool.Poolable {
    // danh sách hiệu ứng âm thanh, sử dụng HashMap để dễ dàng truy cập theo tên
    public HashMap<String, Sound> soundEffects = new HashMap<>();

    /**
     * Giải phóng tất cả tài nguyên âm thanh khi không còn dùng
     */
    public void dispose() {
        for (Sound s : soundEffects.values()) {
            s.dispose();
        }
        soundEffects.clear();
    }


    @Override
    public void reset() {
        dispose();
    }
}
