package game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
// import lớp Utilities để hỗ trợ các phép chuyển đổi hoặc tính toán chung trong game
import game.Utilities;

/**
 * ParticleHandler
 * <p>
 * Lớp này chịu trách nhiệm quản lý, cập nhật và vẽ các hiệu ứng hạt (Particle
 * Effect) trong game.
 * Các hiệu ứng này có thể bao gồm: vụ nổ, khói, lửa, bụi, tia sáng, hoặc bất kỳ
 * hiệu ứng hình ảnh nào khác
 * được tạo bằng hệ thống hạt của LibGDX.
 * <p>
 * Mục tiêu của lớp là tái sử dụng bộ nhớ hiệu quả thông qua
 * {@link ParticleEffectPool},
 * tránh việc tạo mới hiệu ứng liên tục gây hao tài nguyên và giảm hiệu năng.
 */
public class ParticleHandler {

    /**
     * Hiệu ứng gốc (templateEffect) được tải từ file — dùng làm bản mẫu để sao chép
     * ra các instance mới.
     */
    private final ParticleEffect templateEffect;

    /**
     * Bộ quản lý vùng nhớ (pool) để tái sử dụng các hiệu ứng hạt thay vì tạo mới
     * mỗi lần.
     * Cấu hình mặc định: kích thước ban đầu = 2, tối đa = 25.
     */
    private final ParticleEffectPool pool;

    /**
     * Danh sách các hiệu ứng hạt hiện đang hoạt động trên màn hình (đang được
     * render và update).
     */
    private final Array<ParticleEffectPool.PooledEffect> runningEffects;

    /**
     * Khởi tạo đối tượng ParticleHandler.
     *
     * @param effectPath    đường dẫn tới file .p (định nghĩa hiệu ứng hạt trong
     *                      LibGDX)
     * @param textureFolder thư mục chứa các texture liên quan đến hiệu ứng.
     *
     *                      <p>
     *                      Ví dụ:
     *
     *                      <pre>
     *                                                                                                          new ParticleHandler("effects/explosion.p", "effects");
     *                                                                                                          </pre>
     */
    public ParticleHandler(String effectPath, String textureFolder) {
        // Nạp hiệu ứng mẫu từ file cấu hình
        templateEffect = new ParticleEffect();
        templateEffect.load(Gdx.files.internal(effectPath), Gdx.files.internal(textureFolder));

        // Khởi tạo pool để quản lý và tái sử dụng hiệu ứng, giúp tiết kiệm bộ nhớ
        pool = new ParticleEffectPool(templateEffect, 2, 25);

        // Tạo danh sách trống để chứa các hiệu ứng đang hoạt động
        runningEffects = new Array<>();
    }

    /**
     * Kích hoạt một hiệu ứng hạt mới tại vị trí (x, y).
     *
     * @param x hoành độ nơi xuất hiện hiệu ứng.
     * @param y tung độ nơi xuất hiện hiệu ứng.
     *
     *          <p>
     *          Hiệu ứng được lấy từ pool, đặt vị trí và thêm vào danh sách đang
     *          hoạt động.
     */
    public void trigger(float x, float y) {
        ParticleEffectPool.PooledEffect newEffect = pool.obtain();
        newEffect.setPosition(x, y);
        runningEffects.add(newEffect);
    }

    /**
     * Cập nhật trạng thái của tất cả hiệu ứng đang hoạt động.
     *
     * @param deltaTime thời gian trôi qua giữa hai khung hình (frame), thường lấy
     *                  từ Gdx.graphics.getDeltaTime().
     *
     *                  <p>
     *                  Khi hiệu ứng hoàn tất, nó sẽ được trả về pool để tái sử
     *                  dụng.
     */
    public void update(float deltaTime) {
        // Duyệt ngược để tránh lỗi ConcurrentModification khi loại bỏ phần tử trong
        for (int i = runningEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect fx = runningEffects.get(i);
            fx.update(deltaTime);

            // Khi hiệu ứng hoàn tất, trả lại pool và loại bỏ khỏi danh sách
            if (fx.isComplete()) {
                fx.free();
                runningEffects.removeIndex(i);
            }
        }
    }

    /**
     * Vẽ toàn bộ hiệu ứng đang hoạt động lên màn hình.
     *
     * @param batch SpriteBatch dùng để render.
     *              <p>
     *              Lưu ý: phương thức này tự gọi begin() và end() để đảm bảo an
     *              toàn.
     *              Nếu tích hợp với các hệ thống khác (ví dụ render nhân vật), có
     *              thể bỏ begin()/end() để tối ưu.
     */
    public void render(SpriteBatch batch) {
        batch.begin();
        for (ParticleEffectPool.PooledEffect fx : runningEffects) {
            fx.draw(batch);
        }
        batch.end();
    }

    /**
     * Giải phóng tài nguyên khi thoát game hoặc đổi màn chơi.
     * <p>
     * Gồm:
     * - Giải phóng toàn bộ hiệu ứng đang hoạt động.
     * - Dọn dẹp danh sách.
     * - Hủy (dispose) hiệu ứng mẫu.
     */
    public void destroy() {
        for (ParticleEffectPool.PooledEffect fx : runningEffects) {
            fx.free();
        }
        runningEffects.clear();
        templateEffect.dispose();
    }

    /**
     * Điều chỉnh kích thước (scale) của toàn bộ hiệu ứng hạt.
     *
     * @param ratio tỉ lệ thay đổi kích thước mong muốn.
     *              <p>
     *              Hàm sử dụng {@link Utilities#convertToPPM(float)} để quy đổi
     *              sang đơn vị nội bộ của game.
     *              <p>
     *              Lưu ý: nếu lớp Utilities bị đổi tên, hãy sửa lại import tương
     *              ứng.
     */
    public void resizeAll(float ratio) {
        templateEffect.scaleEffect(Utilities.convertToPPM(ratio));
    }
}
