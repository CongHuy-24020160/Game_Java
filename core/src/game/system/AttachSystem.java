package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import game.component.AttachComponent;
import game.component.B2BodyComponent;

/*
    System chịu trách nhiệm "gắn" một entity (con) vào một entity khác (cha).
    Trong game này, nó dùng để giữ bóng dính vào thanh trượt trước khi phóng.
 */
public class AttachSystem extends IteratingSystem {
    private final ComponentMapper<AttachComponent> attachComponentComponentMapper
        = ComponentMapper.getFor(AttachComponent.class);
    private final ComponentMapper<B2BodyComponent> b2BodyComponentComponentMapper
        = ComponentMapper.getFor(B2BodyComponent.class);

    public AttachSystem(){
        super(Family.all(AttachComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        final AttachComponent attachComponent = attachComponentComponentMapper.get(entity);

        // Thoát sớm nếu không có entity nào được gắn vào.
        if(attachComponent.attachedEntity == null) {
            return;
        }

        // Lấy component vật lý của cả hai đối tượng: cha (entity) và con (attachedEntity).
        final B2BodyComponent parentB2Body = b2BodyComponentComponentMapper.get(entity);
        final B2BodyComponent childB2Body = b2BodyComponentComponentMapper.get(attachComponent.attachedEntity);

        // Cập nhật vị trí của đối tượng con theo vị trí của đối tượng cha.
        final Vector2 parentPosition = parentB2Body.body.getPosition();
        final float childAngle = childB2Body.body.getAngle();
        final float childPositionY = childB2Body.body.getPosition().y;
        // Giữ nguyên vị trí Y của bóng

        /**
         * Cập nhật vị trí của đối tượng con.
         * Chỉ cập nhật trục X theo đối tượng cha, giữ nguyên trục Y và góc quay của đối tượng con.
         * Điều này giúp bóng di chuyển theo thanh trượt nhưng không bị "lún" xuống.
         */
        childB2Body.body.setTransform(parentPosition.x, childPositionY, childAngle);
    }
}
