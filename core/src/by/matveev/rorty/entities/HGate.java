package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

import static by.matveev.rorty.Cfg.toMeters;

public class HGate extends PhysicsEntity {

    private static final Color GREEN = ColorUtils.colorFrom(0xff81C784);
    private static final Color RED = ColorUtils.colorFrom(0xffF44336);

    private final static float MAX_DISTANCE = 0.68f;

    // bounds
    private final float boxWidth;
    private final float boxHeight;
    private final float partWidth;
    private final float partHeight;
    private final float fullWidth;
    private final Body leftPart;
    private final Body rightPart;
    private final float leftOpenedX;
    private final float leftClosedX;
    private final float rightOpenedX;
    private final float rightClosedX;
    private List<Light> lights;
    private boolean isOpen;
    private boolean isInitOpen;

    public HGate(String name, World world, float mapX, float mapY) {
        super(name);
        this.x = toMeters(mapX);
        this.y = toMeters(mapY);
        this.boxWidth = toMeters(64);
        this.boxHeight = toMeters(64);
        this.partWidth = toMeters(96);
        this.partHeight = toMeters(32);
        this.fullWidth = toMeters(192);

        GREEN.a = 0.2f;
        RED.a = 0.2f;

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(partWidth * 0.5f, partHeight * 0.5f);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(x + partWidth * 0.5f, y + partHeight * 0.5f);
        leftPart = world.createBody(def);
        leftPart.setUserData(this);
        final Fixture leftFixture = leftPart.createFixture(poly, 0);
        poly.dispose();

        poly = new PolygonShape();
        poly.setAsBox(partWidth * 0.5f, partHeight * 0.5f);
        def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(x + fullWidth - partWidth * 0.5f, y + partHeight * 0.5f);
        rightPart = world.createBody(def);
        rightPart.setUserData(this);
        rightPart.createFixture(poly, 0);
        final Fixture rightFixture = rightPart.createFixture(poly, 0);
        rightPart.setUserData(this);
        poly.dispose();

        leftOpenedX = x + partWidth * 0.5f - MAX_DISTANCE;
        leftClosedX = leftOpenedX + MAX_DISTANCE;

        rightClosedX = x + fullWidth - partWidth * 0.5f;
        rightOpenedX = rightClosedX + MAX_DISTANCE;
    }


    @Override
    public void onEvent(Event event) {

        if (event instanceof Sensor.SensorEvent) {
            final boolean active = ((Sensor.SensorEvent) event).isActive();

            if (isInitOpen) {
                isOpen = !active;
            } else {
                isOpen = active;
            }

            Assets.playGatesSound();
        }
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void update(float dt) {
        for (Light l : lights) {
            l.color = isOpen ? GREEN : RED;
        }

        if (isOpen) {
            if (leftPart.getPosition().x > leftOpenedX) {
                leftPart.setLinearVelocity(-1f, 0f);
            } else {
                leftPart.setLinearVelocity(0f, 0f);
            }

            if (rightPart.getPosition().x < rightOpenedX) {
                rightPart.setLinearVelocity(1f, 0f);
            } else {
                rightPart.setLinearVelocity(0f, 0f);
            }
        } else {
            if (leftPart.getPosition().x < leftClosedX) {
                leftPart.setLinearVelocity(1f, 0f);
            } else {
                leftPart.setLinearVelocity(0f, 0f);
            }

            if (rightPart.getPosition().x > rightClosedX) {
                rightPart.setLinearVelocity(-1f, 0f);
            } else {
                rightPart.setLinearVelocity(0f, 0f);
            }
        }
    }


    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);

        // left side partition
        batch.draw(Assets.ENV,
                leftPart.getPosition().x - partWidth * 0.5f,
                leftPart.getPosition().y - partHeight * 0.5f,
                partWidth, partHeight,
                320, 64,
                96, 32, false, false);

        // left side box
        batch.draw(Assets.ENV,
                x - boxWidth,
                y - boxHeight * 0.5f,
                boxWidth, boxHeight,
                256, isOpen ? 128 : 64,
                64, 64, false, false);

        // right side partition
        batch.draw(Assets.ENV,
                rightPart.getPosition().x - partWidth * 0.5f,
                rightPart.getPosition().y - partHeight * 0.5f,
                partWidth, partHeight,
                320, 64,
                96, 32, true, false);

        // right side box
        batch.draw(Assets.ENV,
                x + fullWidth,
                y - boxHeight * 0.5f,
                boxWidth, boxHeight,
                256, isOpen ? 128 : 64,
                64, 64, false, false);


    }

    @Override
    public List<Light> createLights() {
        if (lights == null) {
            lights = new ArrayList<>();

            // left
            Light light = new Light(Light.Type.SOFT, Color.BLACK);
            light.x = Cfg.toPixels(leftPart.getPosition().x) - 80 - 128 / 2;
            light.y = Cfg.toPixels(leftPart.getPosition().y) - 128 / 2;
            light.width = light.height = 128;

            lights.add(light);

            // right
            light = new Light(Light.Type.SOFT, Color.BLACK);
            light.x = Cfg.toPixels(rightPart.getPosition().x) + 80 - 128 / 2;
            light.y = Cfg.toPixels(rightPart.getPosition().y) - 128 / 2;
            light.width = light.height = 128;

            lights.add(light);
        }
        return lights;
    }


    public void setInitialState(boolean isOpenByDefault) {
        this.isInitOpen = isOpenByDefault;
        this.isOpen = isOpenByDefault;
    }
}
