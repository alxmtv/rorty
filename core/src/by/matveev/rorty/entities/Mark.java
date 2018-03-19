package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

public class Mark extends PhysicsEntity {

    private static final float DEFAULT_DURATION = 1.5f;

    private float time;
    private float alpha;

    public Mark() {
        super("mark");
    }

    public void show() {
        time = DEFAULT_DURATION;
    }

    @Override
    public void update(float dt) {
//        time -= dt;
//        if (time < 0) time = 0;
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
//        if (time > 0) {
            final float markX = Cfg.toPixels(x) - 8;
            final float markY = Cfg.toPixels(y) + 8 * MathUtils.sin(alpha += 0.1f);

            batch.draw(Assets.ENV, markX, markY, 16, 16, 0, 224, 32, 32, false, false);
//        }
    }

    @Override
    public Body getBody() {
        return null;
    }
}
