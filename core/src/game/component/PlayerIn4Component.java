package game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool;

/**
 * Component mô tả đặc tính của thực thể người chơi (Player).
 */
public class PlayerIn4Component implements Component, Pool.Poolable {

    // Camera gắn với người chơi
    public OrthographicCamera camera;

    // Mức độ (cấp) của người chơi
    public enum PaddleSize {
        SMALL,
        MEDIUM,
        LARGE
    }

    public PaddleSize sizeLevel = PaddleSize.SMALL;

    //getter và setter
    public OrthographicCamera getCamera() {
        return camera;
    }
    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
    public PaddleSize getSizeLevel() {
        return sizeLevel;
    }
    public void setSizeLevel(PaddleSize sizeLevel) {
        this.sizeLevel = sizeLevel;
    }


    @Override
    public void reset() {
        camera = null;
        sizeLevel = PaddleSize.SMALL;
    }
}
