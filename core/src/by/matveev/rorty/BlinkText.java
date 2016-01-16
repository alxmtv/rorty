package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BlinkText extends Text {

    private float time;
    private float duration;

    public BlinkText(String text) {
        this(text, 0.5f);
    }

    public BlinkText(String text, float duration) {
        super(text);
        this.duration = duration;
        this.time = duration;
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        time += Gdx.graphics.getDeltaTime();
        if (time > duration) {
            time = 0f;
            visible = !visible;
        }

        super.draw(batch, x, y);
    }
}
