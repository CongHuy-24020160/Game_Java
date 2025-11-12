package game.LoadAssets;

import com.badlogic.gdx.Gdx;                                      // Truy cập tài nguyên nội bộ của libGDX
import com.badlogic.gdx.assets.AssetManager;                      // Quản lý việc load và unload tài nguyên
import com.badlogic.gdx.assets.loaders.SkinLoader;                // Loader cho giao diện UI Skin
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver; // Dùng để xác định đường dẫn nội bộ
import com.badlogic.gdx.graphics.Color;                           // Dùng khi cần tạo font màu
import com.badlogic.gdx.graphics.g2d.BitmapFont;                  // Font bitmap
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator; // Sinh font từ file TTF
import com.badlogic.gdx.audio.Music;                              // Nhạc nền
import com.badlogic.gdx.audio.Sound;                              // Âm thanh ngắn (hiệu ứng)
import com.badlogic.gdx.graphics.g2d.TextureAtlas;                // Chứa nhiều hình ảnh trong 1 file nén
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader; // Loader tạo font TTF
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader; // Loader cho font FreeType
import com.badlogic.gdx.scenes.scene2d.ui.Skin;                   // Định nghĩa giao diện UI (button, slider, ...)

/**
 * <h2>GameAssetManager</h2>
 *
 * <p>Lớp này chịu trách nhiệm quản lý toàn bộ tài nguyên (hình ảnh, âm thanh, font, skin, nhạc nền)
 * trong trò chơi. Sử dụng mô hình Singleton để đảm bảo chỉ có duy nhất một thể hiện
 * (instance) tồn tại trong suốt vòng đời ứng dụng.</p>
 *
 * <p>Việc quản lý tài nguyên tập trung giúp giảm thiểu lỗi trùng lặp, dễ dàng kiểm soát việc
 * load và giải phóng bộ nhớ khi chuyển cảnh hoặc thoát trò chơi.</p>
 *
 * <p><b>Chức năng chính:</b></p>
 * <ul>
 *     <li>Load hình ảnh (TextureAtlas)</li>
 *     <li>Load font chữ (FreeType/BitmapFont)</li>
 *     <li>Load âm thanh hiệu ứng (Sound)</li>
 *     <li>Load nhạc nền (Music)</li>
 *     <li>Load giao diện người dùng (Skin)</li>
 *     <li>Giải phóng bộ nhớ khi không còn sử dụng</li>
 * </ul>
 */
public class GameAssetManager {
    /**
     * Thể hiện duy nhất (Singleton instance) của GameAssetManager
     */
    private static volatile GameAssetManager instance;
    /**
     * Đối tượng AssetManager chính của libGDX
     */
    public final AssetManager manager;
    /**
     * File chứa tất cả hình ảnh của trò chơi (TextureAtlas)
     */
    public final String gameImagaes = "images/ArkanoidGame.atlas";
    /**
     * Âm thanh khi bóng đập gạch
     */
    public final String hitBrickSound = "sounds/ball_hit_brick.wav";
    /**
     * Âm thanh khi bóng đập tường
     */
    public final String hitWallSound = "sounds/ball_hit_wall.wav";
    /**
     * Âm thanh khi trò chơi kết thúc
     */
    public final String gameOverSound = "sounds/game_over.wav";
    /**
     * Âm thanh khi người chơi đánh trượt bóng
     */
    public final String missBallSound = "sounds/miss_the_ball.wav";
    /**
     * Nhạc nền chính của trò chơi
     */
    public final String backgroundMusic = "sounds/backgroundMusic1.mp3";
    /**
     * Font chữ chính được sử dụng trong trò chơi
     */
    public final String gameFont = "fonts/GUNDAM.ttf";
    /**
     * File định nghĩa giao diện (UI Skin)
     */
    public final String skin = "ui/uiskin.json";

    /**
     * <p>Constructor riêng (private) nhằm ngăn không cho tạo đối tượng từ bên ngoài.</p>
     * <p>Tại đây, AssetManager sẽ được khởi tạo để quản lý tài nguyên.</p>
     */
    private GameAssetManager() {
        manager = new AssetManager();
    }

    /**
     * <p>Phương thức truy cập duy nhất đến thể hiện GameAssetManager.</p>
     *
     * <p>Sử dụng mô hình Double-Checked Locking để đảm bảo an toàn đa luồng,
     * giúp việc khởi tạo chỉ diễn ra một lần duy nhất.</p>
     *
     * @return thể hiện duy nhất của GameAssetManager
     */
    public static GameAssetManager getInstance() {
        if (instance == null) { // Kiểm tra lần 1 (tránh khóa thừa)
            synchronized (GameAssetManager.class) {
                if (instance == null) { // Kiểm tra lần 2
                    instance = new GameAssetManager();
                }
            }
        }
        return instance;
    }

    /**
     * <p>Đưa yêu cầu load toàn bộ hình ảnh trò chơi vào hàng đợi của AssetManager.</p>
     * <p>Hình ảnh được lưu trong file TextureAtlas (nhiều hình gộp trong 1 file).</p>
     */
    public void queueAddImages() {
        manager.load(gameImagaes, TextureAtlas.class);
    }

    /**
     * <p>Đưa yêu cầu load font chữ vào hàng đợi.</p>
     *
     * <p>Font chữ trong libGDX có thể gây lỗi nếu không cài đặt loader đúng cách,
     * do đó cần thiết lập loader cho FreeTypeFontGenerator và BitmapFont trước khi load.</p>
     */
    public void queueAddFonts() {
        // Thiết lập loader cho font TrueType (.ttf)
        manager.setLoader(FreeTypeFontGenerator.class,
            new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));

        manager.setLoader(BitmapFont.class, ".ttf",
            new FreetypeFontLoader(new InternalFileHandleResolver()));

        // Tạo tham số cho font nhỏ (dùng cho HUD, menu,...)
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameterSmall =
            new FreetypeFontLoader.FreeTypeFontLoaderParameter();

        fontParameterSmall.fontFileName = gameFont;   // Đường dẫn file font
        fontParameterSmall.fontParameters.size = 27;  // Kích thước chữ
        fontParameterSmall.fontParameters.color = Color.WHITE; // Màu chữ mặc định

        // Đưa font vào hàng đợi load
        manager.load(gameFont, BitmapFont.class, fontParameterSmall);
    }

    /**
     * <p>Đưa tất cả các file âm thanh ngắn (hiệu ứng) vào hàng đợi của AssetManager.</p>
     * <p>Các âm thanh này thường được kích hoạt khi va chạm hoặc sự kiện đặc biệt xảy ra.</p>
     */
    public void queueLoadSound() {
        manager.load(hitBrickSound, Sound.class);
        manager.load(hitWallSound, Sound.class);
        manager.load(gameOverSound, Sound.class);
        manager.load(missBallSound, Sound.class);
    }

    /**
     * <p>Đưa nhạc nền chính của trò chơi vào hàng đợi để load.</p>
     * <p>Nhạc nền thường được phát ở chế độ lặp vô hạn (loop).</p>
     */
    public void queueLoadMusic() {
        manager.load(backgroundMusic, Music.class);
    }

    /**
     * <p>Đưa yêu cầu load skin giao diện UI vào AssetManager.</p>
     * <p>Skin gồm file JSON mô tả cấu trúc và file atlas chứa hình ảnh UI.</p>
     */
    public void queueLoadSkin() {
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("ui/uiskin.atlas");
        manager.load(skin, Skin.class, params);
    }

    /**
     * <p>Giải phóng toàn bộ tài nguyên đã load và xóa thể hiện Singleton hiện tại.</p>
     * <p>Phương thức này nên được gọi khi thoát trò chơi hoặc đổi cảnh.</p>
     */
    public void dispose() {
        if (manager != null) {
            manager.dispose(); // Giải phóng bộ nhớ của AssetManager
        }
        instance = null; // Reset Singleton
    }
}
