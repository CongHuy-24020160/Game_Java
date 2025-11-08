package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;
import game.component.LinkedEntityComponent;
import game.component.PhysicsBodyComponent;

/*
    System chịu trách nhiệm "gắn" một entity (con) vào một entity khác (cha).
    Trong game này, nó dùng để giữ bóng dính vào thanh trượt trước khi phóng.
 */
public class AttachSystem extends IteratingSystem {
    private final ComponentMapper<LinkedEntityComponent> attachComponentComponentMapper
        = ComponentMapper.getFor(LinkedEntityComponent.class);
    private final ComponentMapper<PhysicsBodyComponent> b2BodyComponentComponentMapper
        = ComponentMapper.getFor(PhysicsBodyComponent.class);

    public AttachSystem() {
        super(Family.all(LinkedEntityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
//        final LinkedEntityComponent attachComponent = attachComponentComponentMapper.get(entity);
//
//        // Thoát sớm nếu không có entity nào được gắn vào.
//        if(attachComponent == null) {
//            return;
//        }
//
//        // Lấy component vật lý của cả hai đối tượng: cha (entity) và con (attachedEntity).
//        if (ArkanoidGame.DEBUG_MODE) {
//            if (attachComponent.LinkedEntity==null){
//                System.out.println("attachComponent.LinkedEntity is null");
//            }
//        }
//        final PhysicsBodyComponent parentB2Body = b2BodyComponentComponentMapper.get(entity);
//        if (ArkanoidGame.DEBUG_MODE) {
//            if (attachComponent.LinkedEntity==null){
//                System.out.println("attachComponent.LinkedEntity 's null");
//            }
//        }
//        final PhysicsBodyComponent childB2Body = b2BodyComponentComponentMapper.get(attachComponent.LinkedEntity);
//
//        // Cập nhật vị trí của đối tượng con theo vị trí của đối tượng cha.
//        final Vector2 parentPosition = parentB2Body.body.getPosition();
//        final float childAngle = childB2Body.body.getAngle();
//        final float childPositionY = childB2Body.body.getPosition().y;
//        // Giữ nguyên vị trí Y của bóng
//
//        /**
//         * Cập nhật vị trí của đối tượng con.
//         * Chỉ cập nhật trục X theo đối tượng cha, giữ nguyên trục Y và góc quay của đối tượng con.
//         * Điều này giúp bóng di chuyển theo thanh trượt nhưng không bị "lún" xuống.
//         */
//        childB2Body.body.setTransform(parentPosition.x, childPositionY, childAngle);
        LinkedEntityComponent attachComponent = attachComponentComponentMapper.get(entity);
        if (attachComponent.LinkedEntity == null) return;

        // update the attached entity to match that of the entity
        PhysicsBodyComponent entityB2Body = b2BodyComponentComponentMapper.get(entity);
        PhysicsBodyComponent attachedB2Body = b2BodyComponentComponentMapper.get(attachComponent.LinkedEntity);
        attachedB2Body.body.setTransform(entityB2Body.body.getPosition().x,
            attachedB2Body.body.getPosition().y, attachedB2Body.body.getAngle());
    }
}
