package game.component;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PowerUpComponent implements Component, Pool.Poolable {

    // Enum định nghĩa tất cả các loại power-up
    public enum PowerUpType {
        ADD_LIFE,
        EXPAND_PADDLE,
        SLOW_BALL,
        //MULTI_BALL,
        //MAGNET_PADDLE,
        //DOUBLE_SCORE,
        SHRINK_PADDLE,
        // (Thêm các loại khác sau...
    }

    // Loại của power-up này
    public PowerUpType type = PowerUpType.ADD_LIFE;

    // Cờ đánh dấu power-up đã được kích hoạt
    public boolean isActivated = false;

    @Override
    public void reset() {
        type = PowerUpType.ADD_LIFE;
        isActivated = false;
    }
}
