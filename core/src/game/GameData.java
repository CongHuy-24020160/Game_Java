package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Lớp tĩnh (static) để quản lý việc Save/Load
 */
public class GameData {

    private static final String PREFS_NAME = "ArkanoidSaveData";
    private static final String KEY_HAS_SAVE = "hasSave";
    private static final String KEY_SCORE = "score";
    private static final String KEY_LIVES = "lives";
    private static final String KEY_LEVEL = "level";

    private static Preferences getPrefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    /**
     * LƯU game
     * @param score Điểm hiện tại
     * @param lives Mạng hiện tại
     * @param level Màn chơi hiện tại
     */
    public static void save(int score, int lives, int level) {
        Preferences prefs = getPrefs();

        prefs.putBoolean(KEY_HAS_SAVE, true); // Đánh dấu là có file save
        prefs.putInteger(KEY_SCORE, score);
        prefs.putInteger(KEY_LIVES, lives);
        prefs.putInteger(KEY_LEVEL, level);

        prefs.flush(); // Bắt buộc phải gọi 'flush()' để lưu file
        System.out.println("GAME ĐÃ ĐƯỢC LƯU! Level: " + level + ", Score: " + score);
    }

    /**
     * XÓA file save (ví dụ: khi người chơi "New Game")
     */
    public static void clear() {
        Preferences prefs = getPrefs();
        prefs.clear();
        prefs.flush();
        System.out.println("Đã xóa file save.");
    }

    /**
     * TẢI game (Hàm này sẽ đọc file save)
     * @return một đối tượng GameData nếu có file, hoặc null nếu không
     */
    public static GameData load() {
        Preferences prefs = getPrefs();

        // Kiểm tra xem có file save không
        if (!prefs.getBoolean(KEY_HAS_SAVE, false)) {
            System.out.println("Không tìm thấy file save.");
            return null; // Không có file save, trả về null
        }

        // Nếu có, đọc dữ liệu
        GameData savedData = new GameData();
        savedData.score = prefs.getInteger(KEY_SCORE, 0);
        savedData.lives = prefs.getInteger(KEY_LIVES, 5); // 5 là giá trị mặc định nếu lỗi
        savedData.level = prefs.getInteger(KEY_LEVEL, 1);

        System.out.println("GAME ĐÃ ĐƯỢC TẢI! Level: " + savedData.level + ", Score: " + savedData.score);
        return savedData;
    }

    public int score;
    public int lives;
    public int level;
}
