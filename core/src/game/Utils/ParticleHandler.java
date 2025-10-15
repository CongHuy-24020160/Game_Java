//import backage hiệu tại ( util ) vô
package game.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
// import cái này ( code thêm cái utilities nhé )
import game.Utilities;

/**
 * Lớp này dùng để điều khiển các hiệu ứng hạt trong game
 * (nổ, bụi, lửa, tia sáng...) bằng cơ chế tái sử dụng bộ nhớ.
 */
public class ParticleHandler {

    // Hiệu ứng gốc dùng để sao chép ra các instance mới
    private final ParticleEffect templateEffect;
    // Bộ nhớ dùng để quản lý và tái sử dụng hiệu ứng
    private final ParticleEffectPool pool;
    // Danh sách hiệu ứng đang được hiển thị trên màn hình
    private final Array<ParticleEffectPool.PooledEffect> runningEffects;

    public ParticleHandler(String effectPath, String textureFolder) {
        // Nạp hiệu ứng mẫu
        templateEffect = new ParticleEffect();
        templateEffect.load(Gdx.files.internal(effectPath), Gdx.files.internal(textureFolder));

        // Tạo một pool với giới hạn kích thước ban đầu và tối đa là 2 và 25
        pool = new ParticleEffectPool(templateEffect, 2, 25);
        runningEffects = new Array<>();
    }

    /**
     * Kích hoạt một hiệu ứng mới tại vị trí (x, y).
     */
    public void trigger(float x, float y) {
        ParticleEffectPool.PooledEffect newEffect = pool.obtain();
        newEffect.setPosition(x, y);
        runningEffects.add(newEffect);
    }

    /**
     * Cập nhật trạng thái các hiệu ứng.
     */
    public void update(float deltaTime) {
        // Duyệt ngược để tránh lỗi khi loại bỏ phần tử
        for (int i = runningEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect fx = runningEffects.get(i);
            fx.update(deltaTime);

            // Khi hiệu ứng hoàn tất thì trả lại pool
            if (fx.isComplete()) {
                fx.free();
                runningEffects.removeIndex(i);
            }
        }
    }

    /**
     * Vẽ toàn bộ hiệu ứng đang hoạt động.
     */
    public void render(SpriteBatch batch) {
        // Ở đây không begin()/end() để tránh xung đột với các lớp khác
        for (ParticleEffectPool.PooledEffect fx : runningEffects) {
            fx.draw(batch);
        }
    }

    /**
     * Giải phóng bộ nhớ, nên gọi khi thoát màn chơi hoặc thoát game.
     */
    public void destroy() {
        for (ParticleEffectPool.PooledEffect fx : runningEffects) {
            fx.free();
        }
        runningEffects.clear();
        templateEffect.dispose();
    }

    /**
     * Dừng toàn bộ hiệu ứng, để chúng tự hoàn tất rồi biến mất.
     */
    public void stopAll() {
        for (ParticleEffectPool.PooledEffect fx : runningEffects) {
            fx.allowCompletion();
        }
    }

    /**
     * Điều chỉnh tỉ lệ kích thước hiệu ứng.
     */
    public void resizeAll(float ratio) {
        // cái này là bên Utilities nhé đứa nào code mà đổi tên thì sửa lại
        templateEffect.scaleEffect(Utilities.convertToPPM(ratio));
    }
}
