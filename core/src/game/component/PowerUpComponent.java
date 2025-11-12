package game.component;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PowerUpComponent implements Component, Pool.Poolable {

    // Enum định nghĩa các loại power-up (hiệu ứng TÍCH CỰC)
    public enum PowerUpType {
        EXTRA_LIFE,
        EXPAND_PADDLE,
        SLOWDOWN_BALL,
        DOUBLE_SCORE
    }

    // Enum định nghĩa các loại power-down (hiệu ứng TIÊU CỰC)
    public enum PowerDownType {
        LOSE_LIFE,
        SHRINK_PADDLE,
        SPEEDUP_BALL
    }

    // Loại của power-up này
    public PowerUpType powerUpType = null;

    // Loại của power-down này
    public PowerDownType powerDownType = null;

    // Cờ đánh dấu đã được kích hoạt
    public boolean isActivated = false;

    // Phương thức kiểm tra loại
    public boolean isPowerUp() {
        return powerUpType != null;
    }

    public boolean isPowerDown() {
        return powerDownType != null;
    }

    @Override
    public void reset() {
        powerUpType = null;
        powerDownType = null;
        isActivated = false;
    }
}
