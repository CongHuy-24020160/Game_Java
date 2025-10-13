package game.controller;

import java.util.EnumMap;
import java.util.EnumSet;
import com.badlogic.gdx.Input; import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.taptap.breakout.BreakoutGame;
public class KeyboardController implements InputProcessor {
    public enum KeyAction { LEFT, RIGHT, SPACE, ESCAPE }

    private final EnumMap<KeyAction, Boolean> keyStates = new EnumMap<>(KeyAction.class);
    private final EnumSet<KeyAction> justPressed = EnumSet.noneOf(KeyAction.class);

    // just in case the user wants to control with mouse
    public Vector2 mouseLocation;

    public KeyboardController(){
        mouseLocation = new Vector2();
    }

    public boolean isPressed(KeyAction action) {
        return keyStates.getOrDefault(action, false);
    }

    public boolean isJustPressed(KeyAction action) {
        return justPressed.contains(action);
    }

    public void clearJustPressed() {
        justPressed.clear();
    }

    private void log(String msg) {
        if (BreakoutGame.DEBUG_MODE) System.out.println("(KeyboardController) " + msg);
    }

    public void reset(){
        keyStates.clear();
        justPressed.clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                log("Pressed LEFT");
                keyStates.put(KeyAction.LEFT, true);
                justPressed.add(KeyAction.LEFT);
                keyProcessed = true;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                log("Pressed RIGHT");
                keyStates.put(KeyAction.RIGHT, true);
                justPressed.add(KeyAction.RIGHT);
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                log("Pressed SPACE");
                keyStates.put(KeyAction.SPACE, true);
                justPressed.add(KeyAction.SPACE);
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                log("Pressed ESCAPE");
                keyStates.put(KeyAction.ESCAPE, true);
                justPressed.add(KeyAction.ESCAPE);
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                log("Released LEFT");
                keyStates.put(KeyAction.LEFT, false);
                keyProcessed = true;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                log("Released RIGHT");
                keyStates.put(KeyAction.RIGHT, false);
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                keyStates.put(KeyAction.SPACE, false);
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                log("Released ESCAPE");
                keyStates.put(KeyAction.ESCAPE, false);
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseLocation.set(screenX, screenY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
