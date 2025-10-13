package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import game.Hud;
import game.Utilities;
import controller.KeyboardController;
import game.component.*;
import game.level.LevelManager;

/*
    System chịu trách nhiệm xử lý các tín hiệu điều khiển từ người chơi (bàn phím).
 */
public class PlayerControlSystem extends IteratingSystem {

    private static final float PADDLE_SPEED = 5f; // Tốc độ di chuyển của thanh trượt
    private static final float LERP_ALPHA = 0.2f; // Độ mượt khi thanh trượt bắt đầu và dừng lại
    private static final float INITIAL_BALL_VELOCITY_Y = 1f; // Vận tốc ban đầu của bóng theo trục Y


    private final ComponentMapper<PhysicsBodyComponent> b2BodyMapper
        = ComponentMapper.getFor(PhysicsBodyComponent.class);
    private final ComponentMapper<BallComponent> ballMapper
        = ComponentMapper.getFor(BallComponent.class);
    private final ComponentMapper<LinkedEntityComponent> attachMapper
        = ComponentMapper.getFor(LinkedEntityComponent.class);

    private final KeyboardController keyCon;
    private final Hud hud;
    private final LevelManager lvlManager;

    public PlayerControlSystem(KeyboardController keyCon, Hud hud, LevelManager lvlManager){
        // System này chỉ xử lý các Entity có PlayerComponent (chính là thanh trượt).
        super(Family.all(PlayerComponent.class).get());
        this.keyCon = keyCon;
        this.hud = hud;
        this.lvlManager = lvlManager;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        // Nếu có dialog đang hiển thị hoặc màn chơi đã kết thúc, vô hiệu hóa điều khiển.
        if(hud.isDialogVisible() || lvlManager.isLevelCompleted()){
            return;
        }

        final PhysicsBodyComponent b2body = b2BodyMapper.get(entity);
        final LinkedEntityComponent attachComponent = attachMapper.get(entity);
        final float width = Utilities.PADDLE_WIDTH; // Giả sử chiều rộng paddle là hằng số

        // Xử lý di chuyển trái/phải
        if (keyCon.left && b2body.body.getPosition().x - width / 2 > Utilities.PADDLE_PADDING) {
            float targetVelocityX = -PADDLE_SPEED;
            // Dùng MathUtils.lerp để tạo ra chuyển động mượt mà hơn là thay đổi vận tốc đột ngột.
            b2body.body.setLinearVelocity(
                MathUtils.lerp(b2body.body.getLinearVelocity().x, targetVelocityX, LERP_ALPHA),
                b2body.body.getLinearVelocity().y
            );
        } else if (keyCon.right && b2body.body.getPosition().x + width / 2 + Utilities.PADDLE_PADDING < Utilities.getPPMWidth()) {
            b2body.body.setLinearVelocity(
                MathUtils.lerp(b2body.body.getLinearVelocity().x, PADDLE_SPEED, LERP_ALPHA),
                b2body.body.getLinearVelocity().y
            );
        } else {
            // Tự động giảm tốc độ khi người chơi không nhấn nút.
            b2body.body.setLinearVelocity(
                MathUtils.lerp(b2body.body.getLinearVelocity().x, 0, LERP_ALPHA),
                b2body.body.getLinearVelocity().y
            );
        }

        // Xử lý phóng bóng
        if (keyCon.space) {
            handleLaunchBall(attachComponent);
        }

        // Xử lý mở menu
        if (keyCon.escape) {
            hud.showMenuDialog();
        }
    }

    //Component chứa thông tin về bóng đang dính vào thanh trượt.
    private void handleLaunchBall(LinkedEntityComponent attachComponent) {
        Entity ballEntity = LinkedEntityComponent.LinkedEntity;

        // Không làm gì nếu không có bóng nào đang dính vào thanh trượt.
        if (ballEntity == null) return;

        PhysicsBodyComponent ballB2Body = b2BodyMapper.get(ballEntity);
        BallComponent ballComponent = ballMapper.get(ballEntity);

        // Cung cấp một vận tốc ban đầu để phóng bóng đi.
        Vector2 initialVelocity = new Vector2(0f, INITIAL_BALL_VELOCITY_Y).nor().scl(ballComponent.BallSpeed);
        ballB2Body.body.setLinearVelocity(initialVelocity);

        // Cập nhật trạng thái: bóng không còn dính vào thanh trượt nữa.
        ballComponent.canLinked = false;

        // Gỡ bỏ liên kết giữa bóng và thanh trượt.
        attachComponent.setLinkedEntity(null);
    }
}
