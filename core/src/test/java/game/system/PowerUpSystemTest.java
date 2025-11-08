package game.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.Hud;
import game.component.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PowerUpSystemTest {

    private PowerUpSystem powerUpSystem;
    private Hud hudMock;
    private Engine engineMock;

    private Entity player;
    private Entity powerUp;
    private Entity ball;

    private PhysicsBodyComponent playerBody;
    private PhysicsBodyComponent powerUpBody;
    private ColliderComponent powerUpCollider;
    private PowerUpComponent powerUpComp;
    private TypeComponent playerType;
    private PlayerIn4Component playerInfo;
    private BallComponent ballComp;
    private PhysicsBodyComponent ballBody;

    @Before
    public void setup() {
        hudMock = mock(Hud.class);
        powerUpSystem = new PowerUpSystem(hudMock);

        engineMock = mock(Engine.class);
        powerUpSystem.setEngine(engineMock);

        // Player
        player = new Entity();
        playerType = new TypeComponent();
        playerType.type = TypeComponent.PLAYER_TYPE;
        playerInfo = new PlayerIn4Component();
        player.add(playerType);
        player.add(playerInfo);

        // PowerUp
        powerUp = new Entity();
        powerUpBody = new PhysicsBodyComponent();
        powerUpCollider = new ColliderComponent();
        powerUpComp = new PowerUpComponent();
        powerUp.add(powerUpBody);
        powerUp.add(powerUpCollider);
        powerUp.add(powerUpComp);

        // Ball
        ball = new Entity();
        ballBody = new PhysicsBodyComponent();
        ballBody.body = mock(com.badlogic.gdx.physics.box2d.Body.class);
        ballComp = new BallComponent();
        ballComp.setBallSpeed(10f);
        ball.add(ballBody);
        ball.add(ballComp);
    }

    @Test
    public void testPowerUpFallsOffScreen_isDestroyed() {
        powerUpBody.body = mock(com.badlogic.gdx.physics.box2d.Body.class);
        when(powerUpBody.body.getPosition()).thenReturn(new Vector2(0, -1));

        powerUpSystem.processEntity(powerUp, 0);

        assertTrue(powerUpBody.setToDestroy);
    }

    @Test
    public void testPowerUpCollisionWithPlayer_activatesAndDestroys() {
        // Giả lập engine
        Engine mockEngine = mock(Engine.class);
        powerUpSystem.addedToEngine(mockEngine);  // gán engine cho system
        when(mockEngine.getEntitiesFor(any())).thenReturn(new ImmutableArray<>(new Array<>()));

        // Setup power-up
        powerUpCollider.tagertEntity = player;
        powerUpBody.body = mock(com.badlogic.gdx.physics.box2d.Body.class);
        when(powerUpBody.body.getPosition()).thenReturn(new Vector2(0, 5));

        // Gọi xử lý
        powerUpSystem.processEntity(powerUp, 0);

        // Kiểm tra
        assertTrue(powerUpBody.setToDestroy);
        assertTrue(powerUpComp.isActivated);
    }


    @Test
    public void testActivateEffect_addLife() {
        when(hudMock.getLives()).thenReturn(2);

        powerUpSystem.activateEffect(player, PowerUpComponent.PowerUpType.ADD_LIFE);

        verify(hudMock).setLives(3);
        verify(hudMock).updateLives();
    }

    @Test
    public void testActivateEffect_growPaddle() {
        powerUpSystem.activateEffect(player, PowerUpComponent.PowerUpType.GROW_PADDLE);
        assertEquals(PlayerIn4Component.PaddleSize.LARGE, playerInfo.getSizeLevel());
    }
}
