package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Queue;

public class HintList {

    private Queue<Hint> hints = new Queue<>();

    public void addHint(String text, int keyCode) {
        hints.addLast(new Hint(text, keyCode));
    }

    public void update(float delta) {
        final Hint h =  hints.size > 0 ? hints.first() : null;
        if (h != null) {
            h.update(delta);

            if (h.isShown()) {
                hints.removeFirst();
            }
        }
    }

    public void draw(Batch batch, float x, float y) {
        final Hint h = hints.size > 0 ? hints.first() : null;
        if (h != null) {
            h.draw(batch, x, Cfg.HEIGHT - 50);
        }
    }

    public static class Hint extends Text {

        private final int keyCode;
        private boolean shown;

        public Hint(String text, int keyCode) {
            super(text);
            this.keyCode = keyCode;
            setVisible(true);
        }

        public void update(float delta) {
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                shown = true;
            }
        }

        public boolean isShown() {
            return shown;
        }
    }
}

