package game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;

public class KeyboardController implements InputProcessor {
    // paddle can only move left and right
    public boolean left, right;

    public boolean space, escape;

    // just in case the user wants to control with mouse
    public Vector2 mouseLocation;

    public KeyboardController(){
        mouseLocation = new Vector2();
    }

    public void reset(){
        left = false;
        right = false;
        space = false;
        escape = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch(keycode){
            case Input.Keys.A:
            case Input.Keys.LEFT:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Pressed A/Left");
                left = true;
                keyProcessed = true;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Pressed D/Right");
                right = true;
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Pressed Space");
                space = true;
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                escape = true;
                keyProcessed = true;
                break;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch(keycode){
            case Input.Keys.A:
            case Input.Keys.LEFT:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Released A/Left");
                left = false;
                keyProcessed = true;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Released D/Right");
                right = false;
                keyProcessed = true;
                break;
            case Input.Keys.SPACE:
                if(ArkanoidGame.DEBUG_MODE) System.out.println("(KeyboardController) Released Space");
                space = false;
                keyProcessed = true;
                break;
            case Input.Keys.ESCAPE:
                System.out.println("Releasing Escape key");
                escape = false;
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
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
