package by.matveev.rorty;

public final class Cfg {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static float PIXEL_TO_METER_RATIO = 100;
    public static boolean BOX2D_DEBUG = true;
    public static boolean LIGHT_DEBUG = false;
    public static boolean LIGHT_ENABLED = true;
    public static boolean CONTRAST_SHADER = true;


    private Cfg() {
    }

    public static float toMeters(float pixels) {
        return pixels / PIXEL_TO_METER_RATIO;
    }

    public static float toPixels(float meters) {
        return meters * PIXEL_TO_METER_RATIO;
    }
}
