package by.matveev.rorty.utils;

import com.badlogic.gdx.graphics.Color;

public final class ColorUtils {

    public ColorUtils() {
    }

    // Expects a hex value as integer and returns the appropriate Color object.
    // @param hex
    //            Must be of the form 0xAARRGGBB
    // @return the generated Color object
    public static Color colorFrom(long hex) {
        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0xFF0000L) >> 16;
        float g = (hex & 0xFF00L) >> 8;
        float b = (hex & 0xFFL);

        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }


    // Expects a hex value as String and returns the appropriate Color object
    // @param s The hex string to create the Color object from
    // @return

    public static Color colorFrom(String s) {
        if (s.startsWith("0x"))
            s = s.substring(2);

        if (s.length() != 8) // AARRGGBB
            throw new IllegalArgumentException("String must have the form AARRGGBB");

        return colorFrom(Long.parseLong(s, 16));
    }
}
