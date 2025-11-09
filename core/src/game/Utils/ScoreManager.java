package game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Quản lý việc đọc và ghi điểm cao vào file "highscores.txt".
 * Được thiết kế theo kiểu Singleton (chỉ có 1 thể hiện duy nhất).
 */
public class ScoreManager {

    private static volatile ScoreManager instance;

    // Tên file lưu điểm
    private static final String HIGH_SCORE_FILE = "highscores.txt";
    // Số lượng điểm cao tối đa muốn lưu (Top 10)
    private static final int MAX_SCORES = 10;

    /**
     * Constructor riêng tư để đảm bảo Singleton.
     */
    private ScoreManager() {
        // (Không cần làm gì ở đây)
    }

    /**
     * Trả về thể hiện duy nhất của ScoreManager.
     */
    public static ScoreManager getInstance() {
        if (instance == null) {
            synchronized (ScoreManager.class) {
                if (instance == null) {
                    instance = new ScoreManager();
                }
            }
        }
        return instance;
    }

    /**
     * Lớp nội bộ đơn giản để lưu 1 mục điểm.
     */
    public static class ScoreEntry {
        public String playerName;
        public int score;

        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
    }

    /**
     * Tải danh sách điểm cao từ file.
     *
     * @return Một List<ScoreEntry> đã sắp xếp.
     */
    public List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        FileHandle file = Gdx.files.local(HIGH_SCORE_FILE); // Dùng 'local' để có thể ghi file

        if (!file.exists()) {
            // File chưa tồn tại, trả về danh sách rỗng
            return scores;
        }

        try {
            String fileContent = file.readString();
            String[] lines = fileContent.split("\\r?\\n"); // Tách theo dòng

            for (String line : lines) {
                if (line.trim().isEmpty()) continue; // Bỏ qua dòng trống

                String[] parts = line.split(","); // Tách bằng dấu phẩy
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    scores.add(new ScoreEntry(name, score));
                }
            }
        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Lỗi khi đọc file điểm cao!", e);
        }

        // Sắp xếp trước khi trả về
        sortScores(scores);
        return scores;
    }

    /**
     * Ghi đè danh sách điểm cao hiện tại vào file.
     *
     * @param scores Danh sách điểm cần lưu.
     */
    private void saveScores(List<ScoreEntry> scores) {
        FileHandle file = Gdx.files.local(HIGH_SCORE_FILE);

        // Sắp xếp lại trước khi lưu
        sortScores(scores);

        // Giới hạn danh sách chỉ MAX_SCORES mục
        List<ScoreEntry> scoresToSave = scores;
        if (scores.size() > MAX_SCORES) {
            scoresToSave = scores.subList(0, MAX_SCORES);
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (ScoreEntry entry : scoresToSave) {
                sb.append(entry.playerName)
                    .append(",")
                    .append(entry.score)
                    .append("\n"); // Thêm ký tự xuống dòng
            }

            // Ghi đè file (tham số false = overwrite)
            file.writeString(sb.toString(), false);

        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Lỗi khi ghi file điểm cao!", e);
        }
    }

    /**
     * Phương thức chính: Thêm một điểm mới và lưu lại.
     *
     * @param playerName Tên người chơi
     * @param newScore   Điểm số mới
     */
    public void addScore(String playerName, int newScore) {
        if (newScore <= 0) return; // Không lưu điểm 0

        List<ScoreEntry> currentScores = loadScores();
        currentScores.add(new ScoreEntry(playerName, newScore));
        saveScores(currentScores); // Hàm saveScores sẽ tự động sắp xếp và cắt top 10
    }

    /**
     * Kiểm tra xem điểm mới có đủ cao để vào Top 10 không.
     *
     * @param newScore Điểm số cần kiểm tra.
     * @return true nếu đủ cao, false nếu không.
     */
    public boolean isHighScore(int newScore) {
        if (newScore <= 0) return false;

        List<ScoreEntry> currentScores = loadScores();

        // Nếu danh sách chưa đủ 10 người, luôn là điểm cao
        if (currentScores.size() < MAX_SCORES) {
            return true;
        }

        // Nếu danh sách đã đủ 10 người, kiểm tra xem có cao hơn người cuối cùng không
        return newScore > currentScores.get(currentScores.size() - 1).score;
    }


    /**
     * Sắp xếp danh sách điểm, từ cao nhất đến thấp nhất.
     */
    private void sortScores(List<ScoreEntry> scores) {
        Collections.sort(scores, new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                // Sắp xếp giảm dần (descending)
                return Integer.compare(o2.score, o1.score);
            }
        });
    }

    public int getHighestScore() {
        List<ScoreEntry> scores = loadScores();
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0).score; // Đã được sort giảm dần
    }
}
