package game.Utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
// Đường dẫn của GameSettings — cần sửa lại nếu package khác
import game.GameSettings;
// Đường dẫn của GameAssetManager — cần sửa lại nếu package khác (ví dụ: game.LoadAssets.GameAssetManager)
import game.LoadAssets.GameAssetManager;

/**
 * UtilSound
 * <p>
 * Lớp này chịu trách nhiệm quản lý và phát các hiệu ứng âm thanh trong game.
 * Được cài đặt theo mô hình Singleton để đảm bảo chỉ tồn tại một thể hiện duy
 * nhất
 * trong suốt vòng đời của ứng dụng (tránh việc tải lại hoặc tạo nhiều đối tượng
 * âm thanh không cần thiết).
 *
 * <p>
 * Chức năng chính:
 * - Quản lý và phát các âm thanh khi người chơi tương tác với vật thể (ví dụ:
 * đập gạch, va tường, mất bóng...)
 * - Đảm bảo mức âm lượng thống nhất, lấy từ {@link GameSettings}.
 * - Tận dụng {@link GameAssetManager} để lấy tài nguyên âm thanh đã được tải
 * sẵn.
 *
 * <p>
 * Lưu ý:
 * - Một số tên file hoặc đường dẫn (ví dụ GameAssetManager, GameSettings) có
 * thể khác tuỳ người phát triển.
 * - Khi đổi package, nhớ cập nhật import tương ứng.
 */
public class UtilSound {

    /**
     * Thể hiện duy nhất (singleton instance) của lớp UtilSound.
     * Dùng từ khóa volatile để đảm bảo tính an toàn trong môi trường đa luồng.
     */
    private static volatile UtilSound instance;

    /**
     * Quản lý tất cả tài nguyên âm thanh được nạp trong game.
     * Sử dụng lớp {@link GameAssetManager} (hoặc tên khác nếu được đổi trong
     * project của bạn).
     */
    private final GameAssetManager assetManager;

    /**
     * Constructor riêng tư để ngăn tạo đối tượng mới từ bên ngoài.
     * Tự động lấy thể hiện của {@link GameAssetManager}.
     */
    private UtilSound() {
        assetManager = GameAssetManager.getInstance();
    }

    /**
     * Trả về thể hiện duy nhất của lớp UtilSound.
     * <p>
     * Đảm bảo rằng trong toàn bộ chương trình chỉ có duy nhất một đối tượng
     * UtilSound được sử dụng.
     *
     * @return thể hiện duy nhất của UtilSound.
     */
    public static UtilSound getInstance() {
        if (instance == null) {
            instance = new UtilSound();
        }
        return instance;
    }
    /**
     * Phát âm thanh khi bóng chạm vào viên gạch.
     * Lấy âm thanh từ assetManager và phát với âm lượng hiện tại trong
     * GameSettings.
     */
    public void playHitBrickSound() {
        Sound hitBrickSound = assetManager.manager.get(assetManager.hitBrickSound);
        if (hitBrickSound == null)
            System.out.println(" Sound hitBrickSound == null!");
        else {
                System.out.println("Sound hitBrickSound loaded OK");
                hitBrickSound.play(GameSettings.getInstance().getSoundVolume());
            }
    }

    /**
     * Phát âm thanh khi bóng chạm vào tường.
     */
    public void playDingSound2() {
        Sound dingSound2 = assetManager.manager.get(assetManager.hitWallSound);
        dingSound2.play(GameSettings.getInstance().getSoundVolume());
    }

    /**
     * Phát âm thanh khi người chơi thua (Game Over).
     */
    public void playGameOver() {
        Sound gameOverSound = assetManager.manager.get(assetManager.gameOverSound);
        gameOverSound.play(GameSettings.getInstance().getSoundVolume());
    }

    public void playLevelComplete() {
        Sound levelCompleteSound = assetManager.manager.get(assetManager.levelcompleteSound);
        levelCompleteSound.play(GameSettings.getInstance().getSoundVolume());
    }

    public void playHitPaddleSound() {
        Sound hitPaddleSound = assetManager.manager.get(assetManager.hitPaddleSound);
        hitPaddleSound.play(GameSettings.getInstance().getSoundVolume());
    }

    /**
     * Phát âm thanh khi bóng bị rơi khỏi màn hình (miss ball).
     */
    public void playMissBallSound() {
        Sound missBallSound = assetManager.manager.get(assetManager.missBallSound);
        missBallSound.play(GameSettings.getInstance().getSoundVolume());
    }

    /**
     * Chuẩn bị âm thanh nền (background music).
     * <p>
     * Lưu ý: hiện tại phương thức chỉ lấy tài nguyên âm thanh nhưng chưa thực sự
     * phát.
     * Để phát nhạc nền lặp lại, cần chuyển sang dùng
     * {@link com.badlogic.gdx.audio.Music} thay vì {@link Sound}.
     */
    public void playBackgroundMusic() {
        Music backgroundMusic = assetManager.manager.get(assetManager.backgroundMusic, Music.class);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(GameSettings.getInstance().getMusicVolume());
        if (GameSettings.getInstance().isMusicEnabled()) {
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }
    }

    public void pauseBackgroundMusic() {
        Music backgroundMusic = assetManager.manager.get(assetManager.backgroundMusic, Music.class);
        backgroundMusic.pause();
    }
}
