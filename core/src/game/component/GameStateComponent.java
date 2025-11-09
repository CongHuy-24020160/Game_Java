package game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Component lưu trạng thái toàn cục của game
 * Nên attach vào một entity đặc biệt (game manager entity)
 */
public class GameStateComponent implements Component, Pool.Poolable {

    // Hệ số nhân điểm toàn cục
    public float globalScoreMultiplier = 1.0f;

    // Thời gian còn lại của double score (giây)
    public float doubleScoreTimeLeft = 0f;

    // Thời gian hiệu ứng double score (mặc định 10 giây)
    public static final float DOUBLE_SCORE_DURATION = 5f;

    @Override
    public void reset() {
        globalScoreMultiplier = 1.0f;
        doubleScoreTimeLeft = 0f;
    }

    /**
     * Kích hoạt double score
     */
    public void activateDoubleScore() {
        globalScoreMultiplier = 2.0f;
        doubleScoreTimeLeft = DOUBLE_SCORE_DURATION;
    }

    /**
     * Cập nhật timer
     */
    public void update(float deltaTime) {
        if (doubleScoreTimeLeft > 0) {
            doubleScoreTimeLeft -= deltaTime;

            if (doubleScoreTimeLeft <= 0) {
                // Hết hiệu lực
                globalScoreMultiplier = 1.0f;
                doubleScoreTimeLeft = 0f;
            }
        }
    }
    public boolean isDoubleScoreActive() {
        return doubleScoreTimeLeft > 0;
    }
}
