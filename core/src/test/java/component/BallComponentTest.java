package component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import game.component.BallComponent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test BallComponent mà không dùng BeforeEach/AfterEach
 */
class BallComponentTest {

    @Test
    void testBounceDirection() {
        // Tạo BallComponent mới
        BallComponent ball = new BallComponent();
        ball.setBallSpeed(10f);
        ball.setCanBounce(true);

        // Tạo mock Body
        Body mockBody = Mockito.mock(Body.class);
        when(mockBody.getWorldCenter()).thenReturn(new Vector2(0,0));

        // Chặn các phương thức applyLinearImpulse/setLinearVelocity
        doNothing().when(mockBody).setLinearVelocity(anyFloat(), anyFloat());
        doNothing().when(mockBody).applyLinearImpulse(any(Vector2.class), any(Vector2.class), anyBoolean());

        // Gọi phương thức
        ball.bounceDirection(BallComponent.Direction.MID, mockBody);

        // Kiểm tra applyLinearImpulse và setLinearVelocity được gọi
        verify(mockBody).setLinearVelocity(0,0);
        verify(mockBody).applyLinearImpulse(any(Vector2.class), eq(new Vector2(0,0)), eq(true));

        // canBounce bị false sau khi bounce
        assertFalse(ball.isCanBounce());
    }

    @Test
    void testReverseX() {
        BallComponent ball = new BallComponent();

        Body mockBody = Mockito.mock(Body.class);
        Vector2 velocity = new Vector2(5, 3);
        when(mockBody.getLinearVelocity()).thenReturn(velocity);
        when(mockBody.getMass()).thenReturn(2f);
        when(mockBody.getWorldCenter()).thenReturn(new Vector2(0,0));

        doNothing().when(mockBody).applyLinearImpulse(any(Vector2.class), any(Vector2.class), anyBoolean());

        ball.reverseX(mockBody);

        verify(mockBody).applyLinearImpulse(any(Vector2.class), eq(new Vector2(0,0)), eq(true));
    }

    @Test
    void testReverseY() {
        BallComponent ball = new BallComponent();

        Body mockBody = Mockito.mock(Body.class);
        Vector2 velocity = new Vector2(2, -4);
        when(mockBody.getLinearVelocity()).thenReturn(velocity);
        when(mockBody.getMass()).thenReturn(1.5f);
        when(mockBody.getWorldCenter()).thenReturn(new Vector2(0,0));

        doNothing().when(mockBody).applyLinearImpulse(any(Vector2.class), any(Vector2.class), anyBoolean());

        ball.reverseY(mockBody);

        verify(mockBody).applyLinearImpulse(any(Vector2.class), eq(new Vector2(0,0)), eq(true));
    }

    @Test
    void testReset() {
        BallComponent ball = new BallComponent();
        ball.setBallSpeed(50f);
        ball.setDead(true);
        ball.setCanBounce(false);
        ball.setCanLinked(false);
        ball.setPreSpeed(new Vector2(1,1));

        ball.reset();

        assertEquals(0, ball.getBallSpeed());
        assertFalse(ball.isDead());
        assertTrue(ball.isCanBounce());
        assertTrue(ball.isCanLinked());
        assertNull(ball.getPreSpeed());
    }
}
