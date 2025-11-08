package game.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BallComponentTest {

    private BallComponent ballComponent;

    @Mock
    private Body mockBody;

    @Before
    public void setUp() {
        ballComponent = new BallComponent();
    }

    @Test
    public void testDefaultValues() {
        // Test giá trị mặc định theo code thực tế
        assertEquals(0f, ballComponent.BallSpeed, 0.01f);
        assertFalse(ballComponent.isDead);
        assertFalse(ballComponent.canBounce); // Mặc định là false
        assertTrue(ballComponent.canLinked);
        assertNull(ballComponent.preSpeed);
    }

    @Test
    public void testDirectionEnum_Values() {
        BallComponent.Direction[] directions = BallComponent.Direction.values();
        assertEquals(3, directions.length);
        assertEquals(BallComponent.Direction.LEFT, BallComponent.Direction.valueOf("LEFT"));
        assertEquals(BallComponent.Direction.RIGHT, BallComponent.Direction.valueOf("RIGHT"));
        assertEquals(BallComponent.Direction.MID, BallComponent.Direction.valueOf("MID"));
    }

    @Test
    public void testSettersAndGetters() {
        // Test BallSpeed
        ballComponent.setBallSpeed(10.5f);
        assertEquals(10.5f, ballComponent.getBallSpeed(), 0.01f);

        // Test isDead
        ballComponent.setDead(true);
        assertTrue(ballComponent.isDead());

        // Test canBounce
        ballComponent.setCanBounce(true);
        assertTrue(ballComponent.isCanBounce());

        // Test canLinked
        ballComponent.setCanLinked(false);
        assertFalse(ballComponent.isCanLinked());

        // Test preSpeed
        Vector2 speed = new Vector2(5f, 3f);
        ballComponent.setPreSpeed(speed);
        assertEquals(speed, ballComponent.getPreSpeed());
    }

    @Test
    public void testReset() {
        // Set some values
        ballComponent.setBallSpeed(15f);
        ballComponent.setDead(true);
        ballComponent.setCanBounce(false);
        ballComponent.setCanLinked(false);
        ballComponent.setPreSpeed(new Vector2(1f, 2f));

        // Reset
        ballComponent.reset();

        // Verify reset to default values - sửa theo code thực tế
        assertEquals(0f, ballComponent.BallSpeed, 0.01f);
        assertFalse(ballComponent.isDead);
        assertTrue(ballComponent.canBounce); // Reset sets canBounce to true
        assertTrue(ballComponent.canLinked); // Reset sets canLinked to true
        assertNull(ballComponent.preSpeed); // Reset sets preSpeed to null
    }

    @Test
    public void testBounceDirection_WhenCanBounceIsFalse_DoesNothing() {
        // Arrange - canBounce mặc định là false
        when(mockBody.getMass()).thenReturn(1f);

        // Act
        ballComponent.bounceDirection(BallComponent.Direction.LEFT, mockBody);

        // Assert - no interactions with body khi canBounce = false
        verify(mockBody, never()).setLinearVelocity(anyFloat(), anyFloat());
        verify(mockBody, never()).applyLinearImpulse(any(Vector2.class), any(Vector2.class), anyBoolean());
    }

    @Test
    public void testReverseX() {
        // Arrange
        when(mockBody.getLinearVelocity()).thenReturn(new Vector2(10f, 5f));
        when(mockBody.getMass()).thenReturn(2f);
        when(mockBody.getWorldCenter()).thenReturn(new Vector2(0, 0));

        // Act
        ballComponent.reverseX(mockBody);

        // Assert - verify impulse calculation and application
        verify(mockBody).applyLinearImpulse(any(Vector2.class), eq(new Vector2(0, 0)), eq(true));
    }

    @Test
    public void testReverseY() {
        // Arrange
        when(mockBody.getLinearVelocity()).thenReturn(new Vector2(10f, 5f));
        when(mockBody.getMass()).thenReturn(2f);
        when(mockBody.getWorldCenter()).thenReturn(new Vector2(0, 0));

        // Act
        ballComponent.reverseY(mockBody);

        // Assert
        verify(mockBody).applyLinearImpulse(any(Vector2.class), eq(new Vector2(0, 0)), eq(true));
    }

    @Test
    public void testComponentImplementsRequiredInterfaces() {
        assertTrue(ballComponent instanceof com.badlogic.ashley.core.Component);
        assertTrue(ballComponent instanceof com.badlogic.gdx.utils.Pool.Poolable);
    }
}
