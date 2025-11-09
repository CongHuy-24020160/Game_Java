package game.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import game.component.*;
import game.Utilities;

public class PaddleResizeSystem extends IteratingSystem {

    private final ComponentMapper<PlayerIn4Component> playerMapper = ComponentMapper.getFor(PlayerIn4Component.class);
    private final ComponentMapper<PhysicsBodyComponent> bodyMapper = ComponentMapper.getFor(PhysicsBodyComponent.class);
    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);

    // Kích thước cơ bản (world unit)
    private static final float BASE_WIDTH = 100f;  // world unit
    private static final float BASE_HEIGHT = 20f;  // world unit

    public PaddleResizeSystem() {
        super(Family.all(
            PlayerIn4Component.class,
            PhysicsBodyComponent.class,
            TextureComponent.class
        ).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerIn4Component player = playerMapper.get(entity);
        PhysicsBodyComponent bodyComp = bodyMapper.get(entity);
        TextureComponent texture = textureMapper.get(entity);

        if (texture == null || bodyComp.body == null) return;

        // TÍNH KÍCH THƯỚC MỚI
        float targetWidth = BASE_WIDTH * player.lengthMultiplier;
        float currentWidth = texture.width / Utilities.PPM;

        // CHỈ RESIZE NẾU CẦN
        if (Math.abs(targetWidth - currentWidth) > 0.01f) {
            resizePaddle(bodyComp.body, targetWidth, texture);
            System.out.println("Paddle resized: " + targetWidth + " (x" + player.lengthMultiplier + ")");
        }
    }

    private void resizePaddle(Body body, float newWidthWorld, TextureComponent texture) {
        texture.width = newWidthWorld;
        texture.height = BASE_HEIGHT;

        // === CẬP NHẬT VẬT LÝ ===
        // Lấy thông tin fixture cũ
        Array<Fixture> fixtures = body.getFixtureList();
        if (fixtures.size == 0) return;

        Fixture oldFixture = fixtures.first();
        FixtureDef fdef = new FixtureDef();
        fdef.density = oldFixture.getDensity();
        fdef.friction = oldFixture.getFriction();
        fdef.restitution = oldFixture.getRestitution();
        fdef.filter.categoryBits = oldFixture.getFilterData().categoryBits;
        fdef.filter.maskBits = oldFixture.getFilterData().maskBits;
        fdef.isSensor = oldFixture.isSensor();

        // Xóa fixture cũ
        while (fixtures.size > 0) {
            body.destroyFixture(fixtures.first());
        }

        // Tạo shape mới
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            Utilities.convertToPPM(newWidthWorld) / 2f,
            Utilities.convertToPPM(BASE_HEIGHT) / 2f
        );

        fdef.shape = shape;
        body.createFixture(fdef);
        shape.dispose();
    }
}
