package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;
import game.Hud;
import game.component.*;

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
    private final ComponentMapper<GameStateComponent> gameStateMapper = ComponentMapper.getFor(GameStateComponent.class);

    // Entity quản lý game state
    private Entity gameStateEntity;

    public PowerUpSystem(Hud hud) {
        super(Family.all(PowerUpComponent.class, PhysicsBodyComponent.class, ColliderComponent.class).get());
        this.hud = hud;
    }

    /**
     * Set game state entity từ bên ngoài
     */
    public void setGameStateEntity(Entity entity) {
        this.gameStateEntity = entity;
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
        if (collider.targetEntity == null) {
            return;
        }

        Entity targetEntity = collider.targetEntity;
        TypeComponent targetType = typeMapper.get(targetEntity);

        // Kiểm tra xem có va chạm với PLAYER không
        if (targetType != null && targetType.type == TypeComponent.PLAYER_TYPE) {
            PowerUpComponent powerUp = powerUpMapper.get(entity);

            if (!powerUp.isActivated) {
                if (powerUp.isPowerUp()) {
                    if (ArkanoidGame.DEBUG_MODE)
                        System.out.println(" ĐÃ ĂN POWER-UP: " + powerUp.powerUpType);
                    activatePowerUp(targetEntity, powerUp.powerUpType);
                } else if (powerUp.isPowerDown()) {
                    if (ArkanoidGame.DEBUG_MODE)
                        System.out.println(" ĐÃ ĂN POWER-DOWN: " + powerUp.powerDownType);
                    activatePowerDown(targetEntity, powerUp.powerDownType);
                }

                powerUp.isActivated = true;
                b2body.setToDestroy = true;
            }
        }

        collider.targetEntity = null;
    }

    /**
     * Kích hoạt hiệu ứng TÍCH CỰC (Power-Up)
     */
    private void activatePowerUp(Entity playerEntity, PowerUpComponent.PowerUpType type) {
        switch (type) {
            case EXTRA_LIFE: {
                hud.setLives(hud.getLives() + 1);
                hud.updateLives();
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("  ➜ +1 Mạng!");
                break;
            }

            case EXPAND_PADDLE: {
                PlayerIn4Component playerInfo = playerInfoMapper.get(playerEntity);
                if (playerInfo != null) {
                    playerInfo.expand(0.25f);
                    if (ArkanoidGame.DEBUG_MODE)
                        System.out.println("  ➜ PADDLE LỚN RA!");
                }
                break;
            }

            case SLOWDOWN_BALL: {
                ImmutableArray<Entity> balls = getEngine().getEntitiesFor(
                    Family.all(BallComponent.class, PhysicsBodyComponent.class).get()
                );

                if (balls.size() > 0) {
                    Entity ball = balls.first();
                    PhysicsBodyComponent ballBody = bodyMapper.get(ball);
                    BallComponent ballComp = ballMapper.get(ball);

                    if (ballBody != null && ballComp != null) {
                        float currentSpeed = ballComp.getBallSpeed();
                        float newSpeed = currentSpeed * 0.7f;
                        newSpeed = Math.max(newSpeed, 0.5f * BallSystem.DEFAULT_BALL_SPEED);
                        ballComp.setBallSpeed(newSpeed);

                        Vector2 currentVel = ballBody.body.getLinearVelocity();
                        currentVel.setLength(newSpeed);
                        ballBody.body.setLinearVelocity(currentVel);

                        if (ArkanoidGame.DEBUG_MODE)
                            System.out.println("  ➜ BÓNG CHẬM LẠI!");
                    }
                }
                break;
            }

            case DOUBLE_SCORE: {
                if (gameStateEntity != null) {
                    GameStateComponent gameState = gameStateMapper.get(gameStateEntity);
                    if (gameState != null) {
                        gameState.activateDoubleScore();
                        if (ArkanoidGame.DEBUG_MODE)
                            System.out.println("  ➜ ĐIỂM NHÂN ĐÔI! (10 giây)");
                    }
                } else {
                    if (ArkanoidGame.DEBUG_MODE)
                        System.err.println("WARNING: gameStateEntity chưa được set!");
                }
                break;
            }
        }
    }

    /**
     * Kích hoạt hiệu ứng TIÊU CỰC (Power-Down)
     */
    private void activatePowerDown(Entity playerEntity, PowerUpComponent.PowerDownType type) {
        switch (type) {
            case LOSE_LIFE: {
                hud.setLives(hud.getLives() - 1);
                hud.updateLives();
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("  ➜ -1 Mạng!");
                break;
            }

            case SHRINK_PADDLE: {
                PlayerIn4Component playerInfo = playerInfoMapper.get(playerEntity);
                if (playerInfo != null) {
                    playerInfo.shrink(0.25f);
                    if (ArkanoidGame.DEBUG_MODE)
                        System.out.println("  ➜ PADDLE NHỎ LẠI!");
                }
                break;
            }

            case SPEEDUP_BALL: {
                ImmutableArray<Entity> balls = getEngine().getEntitiesFor(
                    Family.all(BallComponent.class, PhysicsBodyComponent.class).get()
                );

                if (balls.size() > 0) {
                    Entity ball = balls.first();
                    PhysicsBodyComponent ballBody = bodyMapper.get(ball);
                    BallComponent ballComp = ballMapper.get(ball);

                    if (ballBody != null && ballComp != null) {
                        float currentSpeed = ballComp.getBallSpeed();
                        float newSpeed = currentSpeed * 1.3f;
                        newSpeed = Math.min(newSpeed, 2.5f * BallSystem.DEFAULT_BALL_SPEED);
                        ballComp.setBallSpeed(newSpeed);

                        Vector2 currentVel = ballBody.body.getLinearVelocity();
                        currentVel.setLength(newSpeed);
                        ballBody.body.setLinearVelocity(currentVel);

                        if (ArkanoidGame.DEBUG_MODE)
                            System.out.println("  ➜ BÓNG NHANH LẠI!");
                    }
                }
                break;
            }
        }
    }
}
