package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.core.Callback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class Button extends PhysicsEntity {

    private final float offsetX;
    private final float offsetY;
    private final Type on;
    private final Type off;
    private final Callback<Boolean> action;

    public enum Type {
        MUSIC_ON(256, 448, 32, 32),
        MUSIC_OFF(288, 448, 32, 32),
        SOUND_ON(256, 480, 32, 32),
        SOUND_OFF(288, 480, 32, 32),
        RESTART(319, 448, 32, 32);

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
    }

    private Vector3 temp = new Vector3();
    private boolean enabled = true;

    public Button setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Button(float offsetX, float offsetY, Type on, Type off, Callback<Boolean> action) {
        super("button");
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.on = on;
        this.off = off;
        this.action = action;
        this.width = this.height = 64f;
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        super.draw(batch, camera);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        temp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(temp);

        final boolean contains = contains(temp.x, temp.y);

        if (Gdx.input.justTouched() && contains) {
            enabled = !enabled;
            if (action != null) {
                action.call(enabled);
            }
        }

        this.x = camera.position.x + offsetX;
        this.y = camera.position.y + offsetY;

        final Type type = enabled ? on : off;
        batch.draw(Assets.ENV,
                x, y,
                12, 12,
                24, 24,
                contains ? 1.2f : 1f,
                contains ? 1.2f : 1f,
                0f,
                type.x, type.y,
                type.w, type.h,
                false, false);
    }

    private boolean contains(float x, float y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }
}
