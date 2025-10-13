package com.taptap.breakout;

/**
 * Lớp Utilities chứa các hằng số và hàm tiện ích chung
 * được sử dụng xuyên suốt trong game (Breakout).
 * Chủ yếu phục vụ cho việc quy đổi đơn vị và tính toán kích thước.
 */
public class Utilities {


    /** Số pixel tương ứng với 1 mét trong Box2D (Pixel Per Meter) */
    public static final float PPM = 100;

    /** Chiều rộng màn hình ảo (đơn vị: pixel) */
    public static final float VIRTUAL_WIDTH = 800;

    /** Chiều cao màn hình ảo (đơn vị: pixel) */
    public static final float VIRTUAL_HEIGHT = 450;

    // =======================
    // 🧱 KÍCH THƯỚC PADDLE
    // =======================

    /** Chiều rộng paddle (đơn vị: pixel) */
    public static final float PADDLE_WIDTH = 100;

    /** Chiều cao paddle (đơn vị: pixel) */
    public static final float PADDLE_HEIGHT = 25;

    /** Khoảng cách giữa paddle và mép dưới màn hình (đơn vị: mét) */
    public static final float PADDLE_PADDING = 0.1f;

    /**
     * Trả về chiều rộng tương ứng trong thế giới Box2D (đơn vị: mét)
     * @return VIRTUAL_WIDTH / PPM
     */
    public static float getPPMWidth() {
        return VIRTUAL_WIDTH / PPM;
    }

    /**
     * Trả về chiều cao tương ứng trong thế giới Box2D (đơn vị: mét)
     * @return VIRTUAL_HEIGHT / PPM
     */
    public static float getPPMHeight() {
        return VIRTUAL_HEIGHT / PPM;
    }

    /**
     * Quy đổi một giá trị từ pixel sang đơn vị PPM (mét trong Box2D)
     * @param num giá trị cần chuyển đổi (pixel)
     * @return num / PPM (đơn vị: mét)
     */
    public static float convertToPPM(float num) {
        return num / PPM;
    }
}
