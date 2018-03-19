package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Event;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Elevator extends PhysicsEntity {

    private final Body body;

    private final Vector2 position = new Vector2();
    private final Vector2 direction = new Vector2();

    private float distance;
    private float maxDistance;
    private boolean active;
    private boolean isSoundPlaying;

    public Elevator(final String id, Body body, Vector2 direction, float maxDistance) {
        super(id);
        this.body = body;
        this.maxDistance = maxDistance;
        this.direction.set(direction);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Switch.SwitchEvent) {
            setActive(!active);
        }
    }

    public void update(float delta) {
        if (active) {
            if (!isSoundPlaying) {
                Assets.playElevatorSound();
                isSoundPlaying = true;
            }
            distance += direction.len() * delta;
            if (distance > maxDistance) {
                direction.scl(-1f);
                distance = 0;
                setActive(false);

            }
            body.setLinearVelocity(direction);
        } else {
            if (isSoundPlaying) {
                Assets.stopEvevatorSound();
                isSoundPlaying = false;
            }
            body.setLinearVelocity(Vector2.Zero);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active)
            body.setLinearVelocity(direction);
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        final float x = body.getPosition().x;
        final float y = body.getPosition().y;
        final float width = Cfg.toMeters(128);
        final float height = Cfg.toMeters(60);
        batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f, width, height, 0, 0, 128, 48, false, false);
    }

    @Override
    public Body getBody() {
        return body;
    }
}
