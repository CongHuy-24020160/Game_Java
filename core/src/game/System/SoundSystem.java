package game.System;

import AppPreferences;
import ECS.components.CollisionComponent;
import ECS.components.SoundComponent;
import ECS.components.TypeComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import utils.SoundUtil;

/**
 *   System chịu trách nhiệm phát ra âm thanh dựa trên các sự kiện va chạm.
 */
public class SoundSystem extends IteratingSystem {
    private final AppPreferences appPreferences;

    private final ComponentMapper<CollisionComponent> collisionC
        = ComponentMapper.getFor(CollisionComponent.class);
    private final ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);

    public SoundSystem(AppPreferences appPreferences) {
        // System này chỉ xử lý các Entity có cả 3 component: Sound, Collision, và Type.
        super(Family.all(SoundComponent.class, CollisionComponent.class, TypeComponent.class).get());
        this.appPreferences = appPreferences;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Kiểm tra cài đặt âm thanh trước tiên. Nếu âm thanh bị tắt, thoát ngay để tiết kiệm xử lý.
        if (!appPreferences.isSoundEnabled()) {
            return;
        }

        final CollisionComponent collisionComponent = collisionC.get(entity);
        final Entity otherEntity = collisionComponent.collisionEntity;

        // Không làm gì nếu không có va chạm nào được ghi nhận.
        if (otherEntity == null) {
            return;
        }

        final TypeComponent entityType = typeC.get(entity);

        // Chỉ phát âm thanh nếu va chạm bắt nguồn từ quả bóng.
        if (entityType.type == TypeComponent.BALL) {
            final TypeComponent otherEntityType = typeC.get(otherEntity);
            playSoundForCollision(otherEntityType);
        }
    }

    /**
     * phát ra âm thanh phù hợp.
     * otherEntityType Loại của đối tượng mà bóng đã va chạm.
     */
    private void playSoundForCollision(TypeComponent otherEntityType) {
        if (otherEntityType == null) return;

        switch (otherEntityType.type) {
            case TypeComponent.BLOCK:
                SoundUtil.getInstance().playDingSound1(); // Âm thanh khi va vào gạch
                break;
            case Type.PLAYER:
                SoundUtil.getInstance().playDingSound2(); // Âm thanh khi va vào thanh trượt
                break;
            // thêm các case khác ở đây cho các loại va chạm mới .
        }
    }
}
