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
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Sử dụng các mapper đã được tối ưu hóa để lấy component.
        final TextureComponent texture = textureMapper.get(entity);
        final PhysicsBodyComponent b2body = b2BodyMapper.get(entity);

        // Nếu entity đã bị đánh dấu là "chết" thì không cần vẽ nó nữa.
        if (b2body.isDead || texture.currImage == null) return;

        float width, height;

        if (texture.width > 0 && texture.height > 0) {
            // Nếu đã set width/height thì dùng (đây là world unit)
            width = Utilities.convertToPPM(texture.width);
            height = Utilities.convertToPPM(texture.height);
        } else {
            // Nếu chưa set thì dùng kích thước gốc của hình ảnh
            width = Utilities.convertToPPM(texture.currImage.getRegionWidth());
            height = Utilities.convertToPPM(texture.currImage.getRegionHeight());
        }

        final float originX = width * 0.5f;
        final float originY = height * 0.5f;

        batch.begin();
        batch.draw(texture.currImage,
            b2body.body.getPosition().x - originX, // Vị trí X
            b2body.body.getPosition().y - originY, // Vị trí Y
            width, height);
        batch.end();
    }
}
