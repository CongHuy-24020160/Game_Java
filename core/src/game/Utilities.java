package game;

public class Utilities {
    public static final float PPM = 100;
    public static final float VIRTUAL_WIDTH = 640;
    public static final float VIRTUAL_HEIGHT = 480;
    public static final float PADDLE_WIDTH = 100;
    public static final float PADDLE_HEIGHT = 25;
    public static final float PADDLE_PADDING = 0.1f;

    public static float getPPMWidth() {
        return VIRTUAL_WIDTH / PPM;
    }

    public static float getPPMHeight() {
        return VIRTUAL_HEIGHT / PPM;
    }

    public static float convertToPPM(float num) {
        return num / PPM;
    }

    public static String getTexureNameForEachLive(int lives) {
        switch (lives) {
            case 1:
                return "Brick2";
            case 2:
                return "Brick1";
            case 3:
                return "Brick3";
            default:
                return "Brick1";
        }
    }
}
