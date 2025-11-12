package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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
        LinkedEntityComponent attachComponent = attachComponentComponentMapper.get(entity);
        if (attachComponent.LinkedEntity == null) return;

        // update the attached entity to match that of the entity
        PhysicsBodyComponent entityB2Body = b2BodyComponentComponentMapper.get(entity);
        PhysicsBodyComponent attachedB2Body = b2BodyComponentComponentMapper.get(attachComponent.LinkedEntity);
        attachedB2Body.body.setTransform(entityB2Body.body.getPosition().x,
            attachedB2Body.body.getPosition().y, attachedB2Body.body.getAngle());
    }
}
