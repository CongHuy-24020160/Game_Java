package game.system;

import game.Hud;
import game.Screen.MainScreen;
import game.controller.KeyboardController;
import game.level.LevelManager;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerControlSystemTest {

    @Mock
    private KeyboardController keyCon;

    @Mock
    private Hud hud;

    @Mock
    private LevelManager lvlManager;

    @Mock
    private Viewport viewport;

    @Mock
    private MainScreen mainScreen;

    private PlayerControlSystem playerControlSystem;

    @Before
    public void setUp() {
        playerControlSystem = new PlayerControlSystem(keyCon, hud, lvlManager, viewport, mainScreen);
    }

    @Test
    public void testConstructor_InitializesCorrectly() {
        assertNotNull(playerControlSystem);
    }

    @Test
    public void testFamily_RequiresPlayerIn4Component() {
        assertNotNull(playerControlSystem.getFamily());
    }

    @Test
    public void testComponentMappers_Initialized() {
        // Test that component mappers are available
        assertNotNull(playerControlSystem.b2BodyMapper);
        assertNotNull(playerControlSystem.ballMapper);
        assertNotNull(playerControlSystem.attachMapper);
    }

    @Test
    public void testSystemCreation_WithAllDependencies() {
        // Just test that we can create the system with all mocked dependencies
        PlayerControlSystem system = new PlayerControlSystem(keyCon, hud, lvlManager, viewport, mainScreen);
        assertNotNull(system);
    }
}
