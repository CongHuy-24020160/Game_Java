package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.GameSettings;
import game.component.ColliderComponent;
import game.component.SoundEffectComponent;
import game.component.TypeComponent;

import game.Utils.UtilSound;

/**
 *   System chịu trách nhiệm phát ra âm thanh dựa trên các sự kiện va chạm.
 */
public class SoundSystem extends IteratingSystem {
    private final GameSettings appPreferences;

    private final ComponentMapper<ColliderComponent> collisionC
        = ComponentMapper.getFor(ColliderComponent.class);
    private final ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);

    public SoundSystem(GameSettings appPreferences) {
        // System này chỉ xử lý các Entity có cả 3 component: Sound, Collision, và Type.
        super(Family.all(SoundEffectComponent.class, ColliderComponent.class, TypeComponent.class).get());
        this.appPreferences = appPreferences;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Kiểm tra cài đặt âm thanh trước tiên. Nếu âm thanh bị tắt, thoát ngay để tiết kiệm xử lý.
        if (!appPreferences.isSoundEnabled()) {
            return;
        }

        final ColliderComponent collisionComponent = collisionC.get(entity);
        final Entity otherEntity = collisionComponent.tagertEntity;

        // Không làm gì nếu không có va chạm nào được ghi nhận.
        if (otherEntity == null) {
            return;
        }

        final TypeComponent entityType = typeC.get(entity);

        // Chỉ phát âm thanh nếu va chạm bắt nguồn từ quả bóng.
        if (entityType.type == TypeComponent.BALL_TYPE) {
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
            case TypeComponent.BLOCK_TYPE:
                UtilSound.getInstance().playDingSound1(); // Âm thanh khi va vào gạch
                break;
            case TypeComponent.PLAYER_TYPE:
                UtilSound.getInstance().playDingSound2(); // Âm thanh khi va vào thanh trượt
                break;
            // thêm các case khác ở đây cho các loại va chạm mới .
        }
    }
}
