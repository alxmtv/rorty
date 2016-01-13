package by.matveev.rorty.core;

import by.matveev.rorty.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Light {

    public enum Type {
        SOFT(0, 0, 128, 128),
        HARD(256, 0, 128, 128),
        TRIANGLE(128, 0, 128, 128);

        Type(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        public static Type from(String str) {
            for (Type type : values()) {
                if (type.toString().equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return Type.SOFT;
        }
    }

    private final Type type;
    public Color color;
    public float x;
    public float y;
    public float width;
    public float height;
    public boolean disabled;

    public Light(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public void render(Batch batch) {
        if (!disabled) {
            batch.setColor(color.r, color.g, color.b, color.a);
            batch.draw(Assets.LIGHTS, x, y, width, height, type.x, type.y, type.w, type.h, false, false);
        }
    }

    public void update(float delta) {

    }
}
