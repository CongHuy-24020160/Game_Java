package game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import game.ArkanoidGame;

/**
 * Lớp {@code KeyboardController} chịu trách nhiệm xử lý các sự kiện bàn phím
 * để điều khiển paddle trong game Arkanoid.
 * <p>
 * Paddle chỉ có thể di chuyển sang trái hoặc phải, đồng thời người chơi có thể
 * nhấn phím {@code SPACE} để thả bóng hoặc {@code ESCAPE} để tạm dừng/thoát.
 * </p>
 */
public class KeyboardController implements InputProcessor {

    /**
     * Trạng thái di chuyển sang trái của paddle.
     */
    public boolean left;

    /**
     * Trạng thái di chuyển sang phải của paddle.
     */
    public boolean right;

    /**
     * Trạng thái nhấn phím Space (thả bóng hoặc bắt đầu).
     */
    public boolean space;

    /**
     * Trạng thái nhấn phím Escape (tạm dừng hoặc mở menu).
     */
    public boolean escape;

    /**
     * Vị trí chuột (dành cho trường hợp điều khiển bằng chuột).
     */
    public Vector2 mouseLocation;

    public boolean p_pause;
    /**
     * Khởi tạo bộ điều khiển bàn phím.
     * Mặc định tất cả các phím đều chưa được nhấn.
     */

    public boolean hasMouseMoved = false;

    public boolean mouseClick; // Trạng thái nhấn chuột

    public KeyboardController() {
        mouseLocation = new Vector2();
    }

    /**
     * Đặt lại toàn bộ trạng thái phím về {@code false}.
     * <p>Dùng khi bắt đầu level mới hoặc reset trạng thái điều khiển.</p>
     */
    public void reset() {
        left = false;
        right = false;
        space = false;
        escape = false;
        hasMouseMoved = false;
        mouseClick = false;
        p_pause = false;
    }

    /**
     * Gọi khi người chơi nhấn một phím.
     *
     * @param keycode mã phím được nhấn (định nghĩa trong {@link Input.Keys})
     * @return {@code true} nếu phím đã được xử lý, ngược lại {@code false}
     */
    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;

        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Pressed A/Left");
                left = true;
                keyProcessed = true;
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Pressed D/Right");
                right = true;
                keyProcessed = true;
                break;

            case Input.Keys.SPACE:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Pressed Space");
                space = true;
                keyProcessed = true;
                break;

            case Input.Keys.ESCAPE:
                escape = true;
                keyProcessed = true;
                break;
            case Input.Keys.P:
                p_pause = true;
                keyProcessed = true;
                break;
        }

        return keyProcessed;
    }

    /**
     * Gọi khi người chơi thả phím ra.
     *
     * @param keycode mã phím được thả
     * @return {@code true} nếu phím đã được xử lý
     */
    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;

        switch (keycode) {
            case Input.Keys.A: // A or left  sang trai
            case Input.Keys.LEFT:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Released A/Left");
                left = false;
                keyProcessed = true;
                break;
            // D or righr sang phai
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Released D/Right");
                right = false;
                keyProcessed = true;
                break;
            // space tung bong
            case Input.Keys.SPACE:
                if (ArkanoidGame.DEBUG_MODE)
                    System.out.println("(KeyboardController) Released Space");
                space = false;
                keyProcessed = true;
                break;
            // esc = out game
            case Input.Keys.ESCAPE:
                System.out.println("Releasing Escape key");
                escape = false;
                keyProcessed = true;
                break;
            case Input.Keys.P: // ⭐️ THÊM CASE NÀY
                p_pause = false;
                keyProcessed = true;
                break;
        }

        return keyProcessed;
    }

    /**
     * Gọi khi người chơi nhập ký tự (phím gõ).
     * Trong game này không sử dụng.
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Lưu lại tọa độ pixel (screen coordinates) của chuột
        mouseLocation.set(screenX, screenY);
        // Đặt cờ thành true để PlayerControlSystem biết và xử lý
        hasMouseMoved = true;
        return true; // Trả về true để báo rằng ta đã xử lý sự kiện này
    }


    /**
     * Gọi khi người chơi chạm xuống (dành cho màn hình cảm ứng hoặc chuột).
     * Không dùng trong lớp này.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Kiểm tra xem có phải là chuột phải không
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.LEFT) {
            mouseClick = true; // Đặt cờ thành true
            return true; // Đã xử lý
        }
        return false; // Bỏ qua các nút chuột khác
    }

    /**
     * Gọi khi người chơi nhả chuột hoặc ngón tay.
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Kiểm tra xem có phải là chuột phải không
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.LEFT) {
            mouseClick = false; // Đặt cờ thành false
            return true; // Đã xử lý
        }
        return false; // Bỏ qua các nút chuột khác
    }

    /**
     * Gọi khi thao tác cảm ứng bị hủy (ít khi dùng).
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Gọi khi người chơi kéo chuột (giữ và di chuyển).
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * Gọi khi người chơi cuộn chuột.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
