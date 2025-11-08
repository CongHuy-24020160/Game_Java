package game.component; // import package vô đây

import com.badlogic.ashley.core.Component;

/**
 * Lớp {@code ScoreComponent} biểu diễn thành phần (component) lưu trữ điểm số (score)
 * của một thực thể trong trò chơi.
 * <p>
 * Thành phần này thường được gắn cho các thực thể như người chơi (Player),
 * hoặc bất kỳ đối tượng nào có khả năng ghi điểm (ví dụ: nhân vật, đội, hoặc vật thể thu thập điểm).
 * <p>
 * Hệ thống (System) trong game có thể đọc hoặc thay đổi giá trị trong {@code ScoreComponent}
 * để cập nhật điểm khi người chơi thực hiện hành động như tiêu diệt kẻ địch,
 * nhặt vật phẩm, hoặc hoàn thành nhiệm vụ.
 */
public class ScoreComponent implements Component {

    /**
     * Biến lưu trữ giá trị điểm số hiện tại của thực thể.
     * <p>
     * Mặc định điểm số khởi tạo là {@code 100},
     * có thể được thay đổi trong quá trình chơi để phản ánh tiến trình của người chơi.
     * <p>
     * Ví dụ:
     * <ul>
     *   <li>Khi người chơi hạ gục quái vật: tăng điểm.</li>
     *   <li>Khi người chơi bị tấn công hoặc thua mạng: giảm điểm.</li>
     * </ul>
     */
    public int Score = 100;

    /**
     * Phương thức lấy (getter) trả về điểm số hiện tại của thực thể.
     *
     * @return giá trị điểm số hiện tại.
     */
    public int getScore() {
        return Score;
    }

    /**
     * Phương thức thiết lập (setter) để thay đổi điểm số của thực thể.
     *
     * @param score giá trị điểm số mới cần gán.
     */
    public void setScore(int score) {
        this.Score = score;
    }

    /**
     * Phương thức tăng điểm số cho thực thể.
     * <p>
     * Dùng khi người chơi đạt được thành tích hoặc thực hiện hành động được thưởng điểm.
     *
     * @param amount số điểm cần cộng thêm.
     */
    public void addScore(int amount) {
        this.Score += amount;
    }

    /**
     * Phương thức giảm điểm số của thực thể.
     * <p>
     * Dùng khi người chơi bị phạt hoặc thất bại trong một thử thách.
     *
     * @param amount số điểm cần trừ đi.
     */
    public void subtractScore(int amount) {
        this.Score -= amount;
    }
}
