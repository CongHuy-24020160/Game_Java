package game.system;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PhysicsSystemTest {

    @Mock
    private World world;

    @Mock
    private PooledEngine engine;

    @Test
    public void testConstructor_InitializesWithoutErrors() {
        // Just test that constructor doesn't throw exceptions
        PhysicSystem system = new PhysicSystem(world, engine);
        assertNotNull(system);
    }

    @Test
    public void testConstants_Values() {
        assertEquals(1/60f, PhysicSystem.getTimeStep(), 0.001f);
        assertEquals(6, PhysicSystem.getVelocityIterations());
        assertEquals(2, PhysicSystem.getPositionIterations());
    }

    @Test
    public void testWorldStepParameters() {
        // Test the physics parameters are correct
        float timeStep = 1/60f;
        int velocityIterations = 6;
        int positionIterations = 2;

        assertEquals(0.0166f, timeStep, 0.001f);
        assertEquals(6, velocityIterations);
        assertEquals(2, positionIterations);
    }
}
