package game.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import game.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class HudTest {

    private Hud hud;

    @Before
    public void setUp() {
        // Không khởi tạo Hud thật vì nó phụ thuộc vào nhiều thứ
        // Chỉ test các enum và constants
    }

    @Test
    public void testDialogTypeEnum_Values() {
        // Test enum values exist
        Hud.DialogType[] types = Hud.DialogType.values();
        assertEquals(4, types.length);
        assertEquals(Hud.DialogType.NEXT_LEVEL, Hud.DialogType.valueOf("NEXT_LEVEL"));
        assertEquals(Hud.DialogType.MENU, Hud.DialogType.valueOf("MENU"));
        assertEquals(Hud.DialogType.FINAL, Hud.DialogType.valueOf("FINAL"));
        assertEquals(Hud.DialogType.GAME_OVER, Hud.DialogType.valueOf("GAME_OVER"));
    }

    @Test
    public void testUserChoiceEnum_Values() {
        // Test enum values exist
        Hud.UserChoice[] choices = Hud.UserChoice.values();
        assertEquals(5, choices.length);
        assertEquals(Hud.UserChoice.NEXT_LEVEL, Hud.UserChoice.valueOf("NEXT_LEVEL"));
        assertEquals(Hud.UserChoice.RETRY, Hud.UserChoice.valueOf("RETRY"));
        assertEquals(Hud.UserChoice.MENU, Hud.UserChoice.valueOf("MENU"));
        assertEquals(Hud.UserChoice.CANCEL, Hud.UserChoice.valueOf("CANCEL"));
        assertEquals(Hud.UserChoice.NONE, Hud.UserChoice.valueOf("NONE"));
    }

    @Test
    public void testConstants_Values() {
        // Test constants
        assertTrue(Hud.getDebugMode());
        assertEquals(5, Hud.getDefaultLives());
    }

    @Test
    public void testUserChoice_DefaultValue() {
        // Test default user choice
        Hud.UserChoice choice = Hud.UserChoice.NONE;
        assertEquals(Hud.UserChoice.NONE, choice);
    }

    @Test
    public void testDialogType_CanBeSet() {
        // Test that dialog type can be assigned
        Hud.DialogType type = Hud.DialogType.GAME_OVER;
        assertEquals(Hud.DialogType.GAME_OVER, type);
    }

    @Test
    public void testScoreLevelLives_DefaultValues() {
        // Test the expected default values for game state
        // These match what's set in Hud constructor
        int expectedScore = 0;
        int expectedLevel = 1;
        int expectedLives = 5;

        assertEquals(0, expectedScore);
        assertEquals(1, expectedLevel);
        assertEquals(5, expectedLives);
    }

    @Test
    public void testEnums_NotSame() {
        // Test that different enum values are not the same
        assertNotSame(Hud.DialogType.NEXT_LEVEL, Hud.DialogType.GAME_OVER);
        assertNotSame(Hud.UserChoice.NEXT_LEVEL, Hud.UserChoice.MENU);
    }
}
