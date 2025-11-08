package game.system;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import game.Utilities;
import game.component.PhysicsBodyComponent;
import game.component.TextureComponent;

public class RenderingSystem extends IteratingSystem {
    private final SpriteBatch batch;

    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<PhysicsBodyComponent> b2BodyMapper = ComponentMapper.getFor(PhysicsBodyComponent.class);

    public RenderingSystem(SpriteBatch batch, OrthographicCamera cam) {
        // System này chỉ xử lý các Entity có cả B2BodyComponent và TextureComponent.
        super(Family.all(PhysicsBodyComponent.class, TextureComponent.class).get());
        this.batch = batch;
        Array<Entity> renderQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        // Chúng ta chỉ gọi batch.begin() một lần duy nhất
        // trước khi vẽ tất cả các đối tượng.
//        batch.begin();

        super.update(deltaTime);
        // Sau khi đã vẽ xong tất cả các đối tượng,
        // chúng ta gọi batch.end() một lần duy nhất.
//        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Sử dụng các mapper đã được tối ưu hóa để lấy component.

        // ?? how texture can work
        final TextureComponent texture = textureMapper.get(entity);


        final PhysicsBodyComponent b2body = b2BodyMapper.get(entity);

        // Nếu entity đã bị đánh dấu là "chết" thì không cần vẽ nó nữa.
        if (b2body.isDead) return;

        // Tính toán kích thước và vị trí để vẽ hình ảnh khớp với vật thể vật lý.
        final float width = Utilities.convertToPPM(texture.currImage.getRegionWidth());
        final float height = Utilities.convertToPPM(texture.currImage.getRegionHeight());
        final float originX = width * 0.5f; // Tọa độ tâm X
        final float originY = height * 0.5f; // Tọa độ tâm Y
        batch.begin();
        batch.draw(texture.currImage,
            b2body.body.getPosition().x - originX, // Vị trí X
            b2body.body.getPosition().y - originY, // Vị trí Y
            width, height);
        batch.end();
    }
}
