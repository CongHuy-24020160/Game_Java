package ECS.system;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import Utilities;
import ECS.components.B2BodyComponent;
import ECS.components.TextureComponent;
import java.util.Comparator;

public class RenderingSystem extends IteratingSystem {
    private final OrthographicCamera cam;
    private final SpriteBatch batch;
    private final Array<Entity> renderQueue;

    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<B2BodyComponent> b2BodyMapper = ComponentMapper.getFor(B2BodyComponent.class);

    public RenderingSystem(SpriteBatch batch, OrthographicCamera cam){
        // System này chỉ xử lý các Entity có cả B2BodyComponent và TextureComponent.
        super(Family.all(B2BodyComponent.class, TextureComponent.class).get());
        this.batch = batch;
        this.cam = cam;
        this.renderQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        // Chúng ta chỉ gọi batch.begin() một lần duy nhất
        // trước khi vẽ tất cả các đối tượng.
        batch.begin();

        super.update(deltaTime);
        // Sau khi đã vẽ xong tất cả các đối tượng,
        // chúng ta gọi batch.end() một lần duy nhất.
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Sử dụng các mapper đã được tối ưu hóa để lấy component.
        final TextureComponent texture = textureMapper.get(entity);
        final B2BodyComponent b2body = b2BodyMapper.get(entity);

        // Nếu entity đã bị đánh dấu là "chết" thì không cần vẽ nó nữa.
        if (b2body.isDead) return;

        // Tính toán kích thước và vị trí để vẽ hình ảnh khớp với vật thể vật lý.
        final float width = Utilities.convertToPPM(texture.region.getRegionWidth());
        final float height = Utilities.convertToPPM(texture.region.getRegionHeight());
        final float originX = width * 0.5f; // Tọa độ tâm X
        final float originY = height * 0.5f; // Tọa độ tâm Y

        batch.draw(texture.region,
            b2body.body.getPosition().x - originX, // Vị trí X
            b2body.body.getPosition().y - originY, // Vị trí Y
            width, height);
    }
}
