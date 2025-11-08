package game.Utils;

import game.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameDataTest {

    @Test
    public void testGameDataFields_CanBeSetAndRetrieved() {
        // Test the data class itself - không cần mock Gdx
        GameData gameData = new GameData();
        gameData.score = 1000;
        gameData.lives = 5;
        gameData.level = 1;

        assertEquals(1000, gameData.score);
        assertEquals(5, gameData.lives);
        assertEquals(1, gameData.level);
    }

    @Test
    public void testGameData_DefaultValues() {
        GameData gameData = new GameData();

        // Test default values
        assertEquals(0, gameData.score);
        assertEquals(0, gameData.lives);
        assertEquals(0, gameData.level);
    }

    @Test
    public void testGameData_ObjectCreation() {
        // Test that we can create multiple instances
        GameData data1 = new GameData();
        GameData data2 = new GameData();

        assertNotSame(data1, data2);
        assertNotNull(data1);
        assertNotNull(data2);
    }
    @Test
    public void testConstants() {
        // Test constants are defined
        assertEquals("ArkanoidSaveData", GameData.PREFS_NAME);
    }

    @Test
    public void testGameDataStructure() {
        // Test that GameData is a simple data class
        GameData data = new GameData();
        data.score = 500;
        data.lives = 3;
        data.level = 2;

        // Verify all fields are public and accessible
        assertEquals(500, data.score);
        assertEquals(3, data.lives);
        assertEquals(2, data.level);
    }

    @Test
    public void testMultipleInstances() {
        GameData data1 = new GameData();
        data1.score = 100;

        GameData data2 = new GameData();
        data2.score = 200;

        // Each instance should have independent data
        assertEquals(100, data1.score);
        assertEquals(200, data2.score);
    }

}
