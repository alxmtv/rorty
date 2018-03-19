package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.EventQueue;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pools;

public class Sensor extends PhysicsEntity {

    private final Body body;
    private final String name;
    private final String[] targets;
    private PhysicsEntity otherEntity;
    private float regionHeight;

    public Sensor(Body body, String name, String[] targets) {
        super(name);
        this.x = body.getPosition().x;
        this.y = body.getPosition().y + /*yellow button height*/Cfg.toMeters(5);
        this.width = Cfg.toMeters(64);
        this.height = Cfg.toMeters(42);
        this.regionHeight = Cfg.toMeters(64);
        this.body = body;
        this.body.setUserData(this);
        this.name = name;
        this.targets = targets;
    }

    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot || otherEntity instanceof Box) {
            this.otherEntity = otherEntity;

            Assets.playSensorSound();

            for (String target : targets) {
                EventQueue.add(Pools.obtain(SensorEvent.class)
                        .setState(Boolean.TRUE)
                        .setSender(name)
                        .setReceiver(target));
            }
        }
    }

    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (this.otherEntity == otherEntity) {
            this.otherEntity = null;
            Assets.playSensorSound();
            for (String target : targets) {
                EventQueue.add(Pools.obtain(SensorEvent.class)
                        .setState(Boolean.FALSE)
                        .setSender(name)
                        .setReceiver(target));
            }
        }
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        if (otherEntity != null) {
            batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f, width, regionHeight, 0, 256, 64, 64, false, false);
        } else {
            batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f, width, regionHeight, 0, 320, 64, 64, false, false);
        }
    }

    public static final class SensorEvent extends Event {

        private Boolean active;

        public SensorEvent() {
        }

        public SensorEvent setState(Boolean active) {
            this.active = active;
            return this;
        }

        public boolean isActive() {
            return Boolean.TRUE.equals(active);
        }

        @Override
        public void reset() {
            super.reset();
            active = null;
        }

        @Override
        public boolean validate() {
            return active != null;
        }
    }
}
