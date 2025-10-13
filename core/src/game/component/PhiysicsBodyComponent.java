// import packages vô đây
package game.component;
import com.badlogic.ashley.core.Component; // mọi component đều phải implement interface Component
import com.badlogic.gdx.physics.box2d.Body; // dùng để gán 1 thực thể vật lý Box2D cho entity
import com.badlogic.gdx.utils.Pool; // tiện ích tái sử dụng đối tượng
// mỗi cái entity có thành phần vật lý thì sẽ có component này
public class PhiysicsBodyComponent implements Component, Pool.Poolable {
    public Body body; // đối tượng vật lý Box2D
    public boolean isDead = false; // đánh dấu thực thể đã chết
    public boolean setToDestroy = false;// đánh dấu thực thể sẽ bị phá hủy

    @Override // phương thức reset để tái sử dụng đối tượng
    public void reset() {
        body = null;
        isDead = false;
        setToDestroy = false;
    }
}
