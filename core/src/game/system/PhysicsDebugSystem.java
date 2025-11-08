package game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/*
    System chỉ dùng cho mục đích gỡ lỗi (debug).
    Nó vẽ các đường viền (wireframe) của tất cả các đối tượng vật lý.
    System này nên được tắt đi trong phiên bản game chính thức.
 */
public class PhysicsDebugSystem extends IteratingSystem {
    private final Box2DDebugRenderer debugRenderer;
    private final World world;
    private final OrthographicCamera cam;

    public PhysicsDebugSystem(World world, OrthographicCamera cam) {
        super(Family.all().get());
        this.world = world;
        this.cam = cam;
        this.debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Yêu cầu debugRenderer vẽ thế giới vật lý lên màn hình.
        // `cam.combined` là ma trận chiếu để đảm bảo các đường viền được vẽ đúng vị trí.
        debugRenderer.render(world, cam.combined);
    }

    @Override
    protected void processEntity(Entity entity, float v) {
    }
}
