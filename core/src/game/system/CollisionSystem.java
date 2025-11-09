package game.system;

// Import các thư viện lõi của Ashley (framework Entity-Component-System)

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.core.PooledEngine; // Engine được tối ưu hóa, sử dụng lại các đối tượng
import com.badlogic.ashley.systems.IteratingSystem; // System tự động lặp qua các entity
// Import thư viện đồ họa 2D của LibGDX
import com.badlogic.gdx.graphics.g2d.TextureRegion;
// Import thư viện toán học (cho việc random và vector)
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
// Import thư viện vật lý Box2D
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
// Import các class chính của game
import game.ArkanoidGame;
import game.Hud;
import game.LoadAssets.BodyFactory;
import game.Screen.MainScreen;
import game.Utilities;
// Import tất cả các component (thành phần) mà system này cần biết
import game.component.*;
import game.component.TypeComponent;
// Import các class quản lý
import game.level.LevelManager;
import game.ScoreChangeListener;
import game.Utils.ParticleHandler;
import game.Utils.ScoreManager;
import game.Screen.ScreenManager;

/**
 * <h2>CollisionSystem (Hệ thống Xử lý Va chạm)</h2>
 * <p>
 * Đây là một trong những System quan trọng nhất. Nó không *phát hiện* va chạm
 * (việc đó là của B2dContactListener), mà nó *xử lý hậu quả* sau khi va chạm xảy ra.
 * </p>
 * <p>
 * Nó kế thừa <b>IteratingSystem</b> và chỉ lặp qua các Entity
 * có chứa <b>BallComponent</b> (vì chúng ta chỉ quan tâm đến va chạm của bóng).
 * </p>
 */
public class CollisionSystem extends IteratingSystem {

    // --- CÁC BIẾN (FIELDS) QUAN TRỌNG ---

    // Tham chiếu đến các đối tượng quản lý cốt lõi của game
    private ArkanoidGame game;
    private PooledEngine engine; // Engine ECS để tạo/xóa entity (ví dụ: power-up)
    private World world; // Thế giới vật lý Box2D
    private BodyFactory bodyFactory; // "Nhà máy" tạo ra các body vật lý
    private ScoreChangeListener scoreChangeListener; // Interface để báo cho HUD/MainScreen khi điểm thay đổi
    private LevelManager levelManager; // Quản lý màn chơi (level)
    private Hud hud; // Quản lý giao diện (điểm, mạng, nút bấm)
    public ParticleHandler particlesManager; // Quản lý hiệu ứng hạt (vụ nổ)

    private MainScreen mainScreen; // Tham chiếu đến màn hình game chính

    /**
     * Hàm khởi tạo (Constructor) của CollisionSystem.
     * <p>
     * Nhận vào tất cả các đối tượng quản lý cần thiết từ MainScreen (Dependency Injection).
     *
     * @param mainScreen          Màn hình game chính
     * @param engine              Engine ECS
     * @param world               Thế giới vật lý Box2D
     * @param hud                 Quản lý giao diện
     * @param levelManager        Quản lý màn chơi
     * @param scoreChangeListener Đối tượng lắng nghe sự kiện thay đổi điểm
     * @param game                Đối tượng ArkanoidGame chính
     */
    public CollisionSystem(MainScreen mainScreen, PooledEngine engine, World world, Hud hud,
                           LevelManager levelManager, ScoreChangeListener scoreChangeListener, ArkanoidGame game) {

        // --- BỘ LỌC (FAMILY) ---
        // Yêu cầu IteratingSystem này chỉ lặp qua các entity
        // PHẢI có cả 3 component sau:
        super(Family.all(
            ColliderComponent.class,    // Phải có component va chạm
            PhysicsBodyComponent.class, // Phải có thân vật lý
            BallComponent.class         // Phải là một quả bóng
        ).get());

        // --- GÁN CÁC BIẾN ---
        // Lưu lại các tham chiếu được truyền vào
        this.mainScreen = mainScreen;
        this.scoreChangeListener = scoreChangeListener;
        this.levelManager = levelManager;
        this.hud = hud;
        this.engine = engine;
        this.world = world;
        this.game = game;

        // Lấy một thể hiện (instance) của BodyFactory
        this.bodyFactory = BodyFactory.getInstance(world);

        // Khởi tạo trình quản lý hiệu ứng hạt cho vụ nổ gạch
        particlesManager = new ParticleHandler("particles/block-particle.p", "particles");
        particlesManager.resizeAll(1f); // Cài đặt tỉ lệ hạt
    }


    // --- BỘ TRUY XUẤT COMPONENT (MAPPERS) ---
    // ComponentMapper giúp lấy component từ entity một cách nhanh chóng

    // Lấy ColliderComponent (chứa thông tin va chạm)
    private final ComponentMapper<ColliderComponent> collisionC
        = ComponentMapper.getFor(ColliderComponent.class);
    // Lấy PhysicsBodyComponent (chứa body Box2D)
    private final ComponentMapper<PhysicsBodyComponent> b2BodyC
        = ComponentMapper.getFor(PhysicsBodyComponent.class);
    // Lấy BallComponent (chứa thông tin tốc độ của bóng)
    private final ComponentMapper<BallComponent> ballC
        = ComponentMapper.getFor(BallComponent.class);
    // Lấy TypeComponent (cho biết entity là PLAYER, BLOCK, hay BALL)
    private final ComponentMapper<TypeComponent> typeC
        = ComponentMapper.getFor(TypeComponent.class);
    // Lấy TextureComponent (để thay đổi hình ảnh gạch khi bị va chạm)
    private final ComponentMapper<TextureComponent> textureC
        = ComponentMapper.getFor(TextureComponent.class);


    /**
     * Hàm này được gọi mỗi khung hình (frame) cho TỪNG entity
     * khớp với bộ lọc (Family) đã định nghĩa (tức là cho từng quả bóng).
     *
     * @param entity Entity (quả bóng) đang được xử lý
     * @param v      Thời gian delta (không dùng ở đây)
     */
    @Override
    protected void processEntity(Entity entity, float v) {
        // Lấy component va chạm (Collider) từ quả bóng
        final ColliderComponent collision = collisionC.get(entity);
        // Lấy entity MÀ quả bóng đã va chạm (được gán từ B2dContactListener)
        final Entity otherEntity = collision.targetEntity;

        // Nếu không va chạm với gì cả, thoát hàm
        if (otherEntity == null) {
            return;
        }

        // Lấy loại (Type) của entity này (chắc chắn là BALL_TYPE)
        final TypeComponent entityType = typeC.get(entity);

        // Kiểm tra lại (cho chắc) xem có đúng là bóng không
        if (entityType.type == TypeComponent.BALL_TYPE) {
            // Gửi va chạm đến hàm xử lý trung tâm
            handleBallCollision(entity, otherEntity);
        }

        // Xử lý va chạm xong, xóa cờ hiệu va chạm để chuẩn bị cho frame tiếp theo
        collision.targetEntity = null;
    }

    /**
     * Hàm trung tâm điều phối, quyết định xem bóng va chạm với cái gì.
     *
     * @param ballEntity  Entity của quả bóng
     * @param otherEntity Entity đã va chạm (Player hoặc Block)
     */
    private void handleBallCollision(Entity ballEntity, Entity otherEntity) {

        // Lấy loại của đối tượng kia
        final TypeComponent otherType = typeC.get(otherEntity);

        // Nếu đối tượng kia không có loại (ví dụ: tường), thì bỏ qua
        if (otherType == null) {
            return;
        }

        // Sử dụng switch-case để gọi hàm xử lý tương ứng
        switch (otherType.type) {
            case TypeComponent.PLAYER_TYPE:
                // Nếu là thanh trượt
                handleBallPlayerCollision(ballEntity, otherEntity);
                break;
            case TypeComponent.BLOCK_TYPE:
                // Nếu là gạch
                handleBallBlockCollision(ballEntity, otherEntity);
                break;
        }
    }

    /**
     * Xử lý logic khi bóng va chạm với thanh trượt (Player).
     * <p>
     * Tính toán góc nảy của bóng dựa trên vị trí va chạm trên thanh trượt.
     * </p>
     *
     * @param ballEntity   Entity của bóng
     * @param playerEntity Entity của thanh trượt
     */
    private void handleBallPlayerCollision(Entity ballEntity, Entity playerEntity) {
        // Lấy component của bóng
        final BallComponent ball = ballC.get(ballEntity);

        // --- Logic làm chậm bóng (có thể bỏ nếu muốn) ---
        final float SLOW_DOWN_FACTOR = 0.8f; // Giảm 20% tốc độ
        final float MIN_BALL_SPEED = 3.5f;   // Tốc độ tối thiểu
        ball.BallSpeed = ball.BallSpeed * SLOW_DOWN_FACTOR;
        if (ball.BallSpeed < MIN_BALL_SPEED) {
            ball.BallSpeed = MIN_BALL_SPEED;
        }
        // --- Kết thúc logic làm chậm ---

        // Lấy body vật lý của bóng và thanh trượt
        final PhysicsBodyComponent ballB2body = b2BodyC.get(ballEntity);
        final PhysicsBodyComponent playerB2body = b2BodyC.get(playerEntity);

        // Lấy vị trí (tọa độ thế giới Box2D)
        final Vector2 ballPosition = ballB2body.body.getPosition();
        final Vector2 playerPosition = playerB2body.body.getPosition();
        // Lấy chiều rộng của thanh trượt (đã chuyển sang đơn vị mét)
        final float paddleWidth = Utilities.convertToPPM(Utilities.PADDLE_WIDTH);

        // --- Tính toán góc nảy ---

        // 1. Tính vị trí va chạm tương đối (từ -nửa_thanh đến +nửa_thanh)
        float relativeIntersectX = ballPosition.x - playerPosition.x;
        // 2. Chuẩn hóa về khoảng [-1, 1] (từ trái qua phải)
        float normalizedIntersectX = relativeIntersectX / (paddleWidth / 2f);
        // 3. Giới hạn giá trị trong khoảng [-1, 1] để tránh lỗi
        normalizedIntersectX = MathUtils.clamp(normalizedIntersectX, -1f, 1f);

        // 4. Đặt góc nảy tối đa (ví dụ: 60 độ)
        float maxBounceAngle = 60f;
        // 5. Tính góc nảy mới (90 độ = thẳng đứng, càng gần mép càng nghiêng)
        float bounceAngle = 90f - normalizedIntersectX * maxBounceAngle;

        // In ra log nếu ở chế độ DEBUG
        if (ArkanoidGame.DEBUG_MODE) {
            System.out.println("relativeIntersectX: " + relativeIntersectX);
            System.out.println("normalizedIntersectX: " + normalizedIntersectX);
            System.out.println("bounceAngle: " + bounceAngle);
        }

        // 6. Tạo vector vận tốc mới
        Vector2 velocity = new Vector2(1, 0) // Vector ngang
            .setAngleDeg(bounceAngle)       // Xoay vector theo góc nảy
            .scl(ball.BallSpeed);         // Đặt tốc độ

        // 7. Đảm bảo bóng luôn bay lên (không bao giờ bay xuống)
        if (velocity.y < 0) {
            velocity.y *= -1;
        }

        // 8. Áp dụng vận tốc mới cho body vật lý của bóng
        ballB2body.body.setLinearVelocity(velocity);
    }

    /**
     * Xử lý logic khi bóng va chạm với gạch (Block).
     *
     * @param ballEntity  Entity của bóng
     * @param blockEntity Entity của gạch
     */
    private void handleBallBlockCollision(Entity ballEntity, Entity blockEntity) {
        // Lấy body vật lý của gạch
        final PhysicsBodyComponent blockB2Body = b2BodyC.get(blockEntity);

        // Trừ 1 "mạng" (lives) của viên gạch
        blockB2Body.lives--;

        // Cập nhật lại hình ảnh (texture) của viên gạch
        TextureComponent texture = textureC.get(blockEntity);
        // (Hàm getTexureNameForEachLive sẽ trả về tên texture dựa trên số "mạng" còn lại)
        texture.currImage = levelManager.currentLevel.getTextures().findRegion(Utilities.getTexureNameForEachLive(blockB2Body.lives));

        // Nếu gạch vẫn còn "mạng" (ví dụ: gạch cứng)
        if (blockB2Body.lives > 0) {
            // Chỉ cập nhật hình ảnh và thoát, không làm gì thêm
            return;
        }

        // --- TỪ ĐÂY TRỞ XUỐNG: Code chỉ chạy khi GẠCH BỊ PHÁ HỦY (lives <= 0) ---

        System.out.println("ĐÃ PHÁ GẠCH!");

        // Giảm số gạch còn lại của màn chơi
        levelManager.currentLevel.numOfBlocksLeft--;
        // Cộng 100 điểm
        scoreChangeListener.onScoreChanged(100);

        // Đánh dấu body vật lý này là "sẽ bị phá hủy" (sẽ được PhysicSystem dọn dẹp)
        blockB2Body.setToDestroy = true;

        // Kích hoạt hiệu ứng hạt (vụ nổ) tại vị trí của viên gạch
        particlesManager.trigger(blockB2Body.body.getPosition().x, blockB2Body.body.getPosition().y);


        // --- LOGIC THẮNG CUỘC / QUA MÀN ---

        // Kiểm tra xem đã phá hết gạch VÀ màn chơi này chưa được đánh dấu là "hoàn thành"
        if (levelManager.currentLevel.numOfBlocksLeft <= 0 && !levelManager.isLevelCompleted) {

            // Đánh dấu là đã hoàn thành (để tránh lặp lại logic này)
            levelManager.isLevelCompleted = true;

            // Kiểm tra xem đây có phải màn CUỐI CÙNG không?
            if (levelManager.currentLevelNumber >= LevelManager.MAX_LEVELS) {

                // --- ĐÂY LÀ MÀN CUỐI CÙNG (PHÁ ĐẢO) ---
                int finalScore = hud.getScore(); // Lấy điểm số cuối cùng

                // Đặt cờ hiệu trong MainScreen để chuyển cảnh an toàn
                mainScreen.gameOverPending = true;
                mainScreen.finalScoreForGameOver = finalScore;

                // Kiểm tra xem có phải điểm cao không
                if (ScoreManager.getInstance().isHighScore(finalScore)) {
                    game.lastScore = finalScore; // Lưu điểm để EnterHighScoreScreen lấy
                    game.screenManager.changeScreen(ScreenManager.ENTER_HIGHSCORE);
                } else {
                    // Nếu không phải điểm cao, đến màn hình kết thúc
                    game.screenManager.changeScreen(ScreenManager.ENDGAME);
                }

            } else {

                // --- CHƯA PHẢI MÀN CUỐI (QUA MÀN) ---
                if (levelManager.currentLevel.numOfBlocksLeft <= 0) {
                    // Hiển thị dialog "Level Complete!"
                    hud.showLevelCompleteDialog();
                    // Tạm dừng game (dừng bóng, dừng di chuyển)
                    mainScreen.pauseGameSystems();
                }
            }

            return; // Thoát hàm vì đã xử lý xong
        }
        // KẾT THÚC LOGIC THẮNG
        // --- LOGIC RANDOM POWER-UP ---
        // Tỉ lệ 20% rơi ra power-up (random số từ 1 đến 5, nếu bằng 1 thì rơi)
        if (MathUtils.random(1, 2) == 1) {
            // Gọi hàm tạo power-up tại vị trí của gạch
            spawnPowerUp(blockB2Body.body.getPosition());
        }
    }

    /**
     * Hàm tạo ra một Entity Power-up và thả nó vào game.
     *
     * @param position Vị trí (tọa độ) của viên gạch đã vỡ
     */
    private void spawnPowerUp(Vector2 position) {
        System.out.println("ĐANG TẠO POWER-UP");

        // Lấy engine ECS (vì chúng ta đang ở trong 1 system)
        PooledEngine engine = (PooledEngine) getEngine();

        // Tạo một Entity rỗng mới
        Entity powerUpEntity = engine.createEntity();

        // --- Bắt đầu thêm các Component cho Entity này ---

        // 1. Tạo PhysicsBodyComponent
        PhysicsBodyComponent b2body = engine.createComponent(PhysicsBodyComponent.class);
        // Dùng BodyFactory để tạo body vật lý
        b2body.body = this.bodyFactory.makeBoxPolyBody(
            position.x, position.y, // Tại vị trí gạch vỡ
            Utilities.convertToPPM(30), // Rộng 30 pixel
            Utilities.convertToPPM(15), // Cao 15 pixel
            BodyFactory.Material.PLASTIC, // Vật liệu (ít quan trọng)
            BodyDef.BodyType.DynamicBody, // Body động (để nó có thể rơi)
            false, // Không phải hình tròn
            true   // LÀ SENSOR (rất quan trọng, để nó đi xuyên gạch/bóng)
        );
        b2body.body.setGravityScale(0.5f); // Cho nó rơi chậm (nửa trọng lực)
        b2body.body.setLinearVelocity(0, -1.5f); // Cho vận tốc rơi ban đầu
        b2body.body.setUserData(powerUpEntity); // Gắn Entity vào body (để B2dContactListener nhận diện)

        // 2. Tạo TextureComponent
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // Lấy hình ảnh "power_up" từ atlas
        TextureRegion tex = levelManager.currentLevel.getTextures().findRegion("power_up");

        // Phòng trường hợp load lỗi (không tìm thấy ảnh "power_up")
        if (tex == null) {
            System.out.println("LỖI: Không tìm thấy hình 'power_up'. Dùng tạm hình bóng.");
            // Dùng tạm ảnh quả bóng nếu không thấy
            tex = levelManager.currentLevel.getTextures().findRegion("Ball_small-blue");
        }
        texture.currImage = tex;

        // 3. Tạo TypeComponent
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.POWERUP_TYPE; // Đặt loại là POWERUP

        // 4. Tạo PowerUpComponent
        PowerUpComponent powerUp = engine.createComponent(PowerUpComponent.class);
        // (Component này có thể rỗng, chỉ dùng để đánh dấu)

        // 5. Tạo ColliderComponent
        ColliderComponent collider = engine.createComponent(ColliderComponent.class);
        // (Để nhận va chạm với Player, sẽ được xử lý trong PowerUpSystem)

        // --- Thêm tất cả component vào entity ---
        powerUpEntity.add(b2body);
        powerUpEntity.add(texture);
        powerUpEntity.add(type);
        powerUpEntity.add(powerUp);
        powerUpEntity.add(collider);

        // Thêm entity power-up vào engine để nó bắt đầu được xử lý
        engine.addEntity(powerUpEntity);
        System.out.println("ĐÃ TẠO XONG POWER-UP!");
    }

    /**
     * Dọn dẹp các tài nguyên mà System này sử dụng.
     */
    public void dispose() {
        System.out.println("--- DỌN DẸP CollisionSystem (Hạt) ---");
        // Hủy trình quản lý hạt để tránh rò rỉ bộ nhớ
        if (particlesManager != null) {
            particlesManager.destroy();
        }
    }
}
