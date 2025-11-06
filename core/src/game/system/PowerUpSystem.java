package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.Hud;
import game.component.*;
import game.level.LevelManager;

import java.util.ArrayList;
import java.util.EnumSet;

public class PowerUpSystem extends IteratingSystem {

    private final Hud hud;

    // Mappers
    private final ComponentMapper<PhysicsBodyComponent> bodyMapper = ComponentMapper.getFor(PhysicsBodyComponent.class);
    private final ComponentMapper<ColliderComponent> colliderMapper = ComponentMapper.getFor(ColliderComponent.class);
    private final ComponentMapper<PowerUpComponent> powerUpMapper = ComponentMapper.getFor(PowerUpComponent.class);
    private final ComponentMapper<TypeComponent> typeMapper = ComponentMapper.getFor(TypeComponent.class);

    // Mapper để tác động
    private final ComponentMapper<PlayerIn4Component> playerInfoMapper = ComponentMapper.getFor(PlayerIn4Component.class);
    private final ComponentMapper<BallComponent> ballMapper = ComponentMapper.getFor(BallComponent.class);


    public PowerUpSystem(Hud hud) {
        super(Family.all(PowerUpComponent.class, PhysicsBodyComponent.class, ColliderComponent.class).get());
        this.hud = hud;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsBodyComponent b2body = bodyMapper.get(entity);
        ColliderComponent collider = colliderMapper.get(entity);

        // 1. Xử lý rơi ra ngoài màn hình
        if (b2body.body.getPosition().y < 0) {
            b2body.setToDestroy = true;
            return;
        }

        // 2. Xử lý va chạm (Collection)
        if (collider.tagertEntity == null) {
            return;
        }

        Entity targetEntity = collider.tagertEntity;
        TypeComponent targetType = typeMapper.get(targetEntity);

        // Kiểm tra xem có va chạm với PLAYER không
        if (targetType != null && targetType.type == TypeComponent.PLAYER_TYPE) {
            PowerUpComponent powerUp = powerUpMapper.get(entity);

            if (!powerUp.isActivated) {
                // 1. Chọn một loại power-up ngẫu nhiên
                ArrayList<PowerUpComponent.PowerUpType> types = new ArrayList<>(EnumSet.allOf(PowerUpComponent.PowerUpType.class));
                PowerUpComponent.PowerUpType randomType = types.get(MathUtils.random(0, types.size() - 1));

                System.out.println("ĐÃ ĂN! HIỆU ỨNG LÀ: " + randomType);

                // 2. Kích hoạt hiệu ứng ngẫu nhiên đó
                activateEffect(targetEntity, randomType);


                powerUp.isActivated = true;
                b2body.setToDestroy = true;
            }
        }

        collider.tagertEntity = null; // Xử lý xong va chạm
    }

    private void activateEffect(Entity playerEntity, PowerUpComponent.PowerUpType type) {
        switch (type) {
            case ADD_LIFE:
                hud.setLives(hud.getLives() + 1);
                hud.updateLives();
                break;

            case GROW_PADDLE:
                PlayerIn4Component playerInfo = playerInfoMapper.get(playerEntity);
                if (playerInfo != null) {
                    playerInfo.setSizeLevel(PlayerIn4Component.PaddleSize.LARGE);
                    System.out.println("PADDLE LỚN RA!");
                    // (Bạn sẽ cần 1 system khác để ĐỌC trạng thái này và thay đổi vật lý/hình ảnh)
                }
                break;

            case SLOW_BALL:
                // Dùng 'getEngine()' để truy vấn tất cả thực thể có BallComponent
                ImmutableArray<Entity> balls = getEngine().getEntitiesFor(
                    Family.all(BallComponent.class, PhysicsBodyComponent.class).get()
                );

                // Nếu tìm thấy (thường là 1 quả)
                if (balls.size() > 0) {
                    Entity ball = balls.first(); // Lấy quả bóng đầu tiên

                    PhysicsBodyComponent ballBody = bodyMapper.get(ball);
                    BallComponent ballComp = ballMapper.get(ball); // Lấy BallComponent

                    if (ballBody != null && ballComp != null) {
                        // Lấy tốc độ hiện tại từ BallComponent (để đảm bảo nhất quán)
                        float currentSpeed = ballComp.getBallSpeed();
                        float newSpeed = currentSpeed * 0.7f; // Giảm 30% tốc độ

                        // Cập nhật tốc độ trong Component
                        ballComp.setBallSpeed(newSpeed);

                        // Cập nhật tốc độ vật lý (lấy hướng cũ, áp dụng tốc độ mới)
                        Vector2 currentVel = ballBody.body.getLinearVelocity();
                        currentVel.setLength(newSpeed); // Đặt lại độ lớn
                        ballBody.body.setLinearVelocity(currentVel);

                        System.out.println("BÓNG CHẬM LẠI!");
                    }
                }
                break;
        }
    }
}
