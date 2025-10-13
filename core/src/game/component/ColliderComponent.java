package game.component;// import packages;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
/**
 * thực thể này có khả năng va chạm
 */
public class ColliderComponent implements Pool.Poolable, Component {
    // thực thể mà đối tượng va chạm gần nhất
    public Entity tagertEntity;
    // biến cờ để xác định xem thực thể có thể va chạm hay không
    public boolean isActive = true;
    //getter và setter
    public Entity getTagertEntity() {
        return tagertEntity;
    }
    public void setTagertEntity(Entity tagertEntity) {
        this.tagertEntity = tagertEntity;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    // phương thức reset để đặt lại trạng thái ban đầu của thành phần
    @Override
    public void reset() {
        tagertEntity = null;
        isActive = true;
    }
}
