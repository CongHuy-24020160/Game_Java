// import packages;

import com.badlogic.ashlay.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
/**
 * thực thể này có khả năng va chạm
 */
public class colliderComponent implements Component, Pool.Poolable {
    // thực thể mà đối tượng va chạm gần nhất
    public Entity tagertEntity;
    // biến cờ để xác định xem thực thể có thể va chạm hay không
    public boolean isActive = true;
    @Override
    public void reset() {
        tagertEntity = null;
        isActive = true;
    }
}