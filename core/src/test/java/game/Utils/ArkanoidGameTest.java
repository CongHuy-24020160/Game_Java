package game.Utils;

import game.ArkanoidGame;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArkanoidGameTest {

    @Test
    public void testBasicCreation() {
        ArkanoidGame game = new ArkanoidGame();
        assertNotNull(game);
    }

    @Test
    public void testDebugMode() {
        assertTrue(ArkanoidGame.DEBUG_MODE);
    }

    @Test
    public void testLastScoreInitialization() {
        ArkanoidGame game = new ArkanoidGame();
        assertEquals(0, game.lastScore);
    }
}
