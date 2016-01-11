package by.matveev.rorty.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Light {

    public Color color;
    public TextureRegion mask;
    public float x;
    public float y;
    public float width;
    public float height;

    public Light(Color color, TextureRegion mask) {
        this.color = color;
        this.mask = mask;
    }

    public void render(Batch batch) {
        batch.setColor(color.r, color.g, color.b, color.a);
        batch.draw(mask, x, y, width, height);
    }

    public void update(float delta) {

    }
}
