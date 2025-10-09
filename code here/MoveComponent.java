// import backage vô đây

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

// thực thể có khả năng thay đổi tọa độ của nói
public class MoveComponent implements Component, Pool.Poolable {
    // tọa độ trong oxyz của nó
    public final Vector3 pos = new Vector3();
    // tỉ lệ phóng to theo 2 trục
    public final Vector2 acceleration = new Vector2(1,1);
    // Góc quay của entity (đơn vị: độ)
    public float angle = 0f;

    // Đánh dấu có được hiển thị hay không
    public boolean hidden = false;


    @Override

    public void reset() {
        pos.set(0f, 0f, 0f);
        acceleration.set(1f, 1f);
        angle = 0f;
        hidden = false;
    }
    }
