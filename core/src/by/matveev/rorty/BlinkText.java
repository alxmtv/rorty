package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BlinkText extends Text {

    private float time;

    public BlinkText(String text) {
        super(text);
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        time += Gdx.graphics.getDeltaTime();
        if (time > 0.5f) {
            time = 0f;
            visible = !visible;
        }

        super.draw(batch, x, y);
    }
}
