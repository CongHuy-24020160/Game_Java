package game.Utils;
import game.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameSettingsTest {

    @Test
    public void testGetInstance_ReturnsSameInstance() {
        GameSettings instance1 = GameSettings.getInstance();
        GameSettings instance2 = GameSettings.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testSingletonPattern_OnlyOneInstance() {
        GameSettings instance1 = GameSettings.getInstance();
        GameSettings instance2 = GameSettings.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    public void testConstants_Values() {
        assertEquals("ARKANOID", GameSettings.getGameName());
    }

    @Test
    public void testGameSettings_ObjectCreation() {
        GameSettings settings = GameSettings.getInstance();
        assertNotNull(settings);
    }
}
