package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

public class Fan extends PhysicsEntity {

    private static final float FAN_SPEED = 500;

    private float angular;
    private float centerX;
    private float centerY;

    public Fan(float x, float y) {
        super("fan");
        this.x = Cfg.toMeters(x);
        this.y = Cfg.toMeters(y);
        this.width = Cfg.toMeters(64);
        this.height = Cfg.toMeters(64);
        this.centerX = width * 0.5f;
        this.centerY = height * 0.5f;

    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void update(float dt) {
        angular += FAN_SPEED * dt;
        if (angular > 360) angular = 0;
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.ENV, x, y,
                centerX, centerY,
                width, height, 1f, 1f,
                angular,
                160, 64,
                64, 64,
                false, false);

        batch.draw(Assets.ENV, x, y,
                width, height,
                160, 0,
                64, 64,
                false, false);
    }
}
