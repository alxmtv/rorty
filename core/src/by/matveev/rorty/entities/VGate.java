package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

import static by.matveev.rorty.Cfg.toMeters;
import static by.matveev.rorty.Cfg.toPixels;

public class VGate extends PhysicsEntity {

    private static final Color GREEN = ColorUtils.colorFrom(0xff81C784);
    private static final Color RED = ColorUtils.colorFrom(0xffF44336);

    private final static float MAX_DISTANCE = 0.68f;

    // bounds
    private final float boxWidth;
    private final float boxHeight;
    private final float partWidth;
    private final float partHeight;
    private final float fullHeight;
    private final Body bottomPart;
    private final Body topPart;
    private final float topOpenedY;
    private final float topClosedY;
    private final float bottomOpenedY;
    private final float bottomClosedY;
    private List<Light> lights;
    private boolean isOpen;
    private boolean isInitOpen;
    private boolean isSoundPlaying;

    public VGate(String name, World world, float mapX, float mapY) {
        super(name);
        this.x = toMeters(mapX);
        this.y = toMeters(mapY);
        this.boxWidth = toMeters(64);
        this.boxHeight = toMeters(64);
        this.partWidth = toMeters(96);
        this.partHeight = toMeters(32);
        this.fullHeight = toMeters(192);


        GREEN.a = 0.2f;
        RED.a = 0.2f;

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(partWidth * 0.5f, partHeight * 0.5f, new Vector2(0f, 0f), 90 * MathUtils.degreesToRadians);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(x + boxWidth * 0.5f, y + (fullHeight - partHeight - toMeters(16)));
        topPart = world.createBody(def);
        topPart.setUserData(this);
        topPart.createFixture(poly, 0);
        topPart.setUserData(this);
        poly.dispose();

        poly = new PolygonShape();
        poly.setAsBox(partWidth * 0.5f, partHeight * 0.5f, new Vector2(0f, 0f), 90 * MathUtils.degreesToRadians);
        def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(x + boxWidth * 0.5f, y + partHeight + toMeters(16));
        bottomPart = world.createBody(def);
        bottomPart.setUserData(this);
        bottomPart.createFixture(poly, 0);
        poly.dispose();


        bottomClosedY =  y + (fullHeight - partHeight - toMeters(16));
        bottomOpenedY = bottomClosedY + MAX_DISTANCE;

        topClosedY =  y + partHeight + toMeters(16);
        topOpenedY =  topClosedY - MAX_DISTANCE;

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
            if (bottomPart.getPosition().y > topOpenedY) {
                bottomPart.setLinearVelocity(0f, -1f);
            } else {
                bottomPart.setLinearVelocity(0f, 0f);
            }

            if (topPart.getPosition().y < bottomOpenedY) {
                topPart.setLinearVelocity(0f, 1f);
            } else {
                topPart.setLinearVelocity(0f, 0f);
            }
        } else {
            if (bottomPart.getPosition().y < topClosedY) {
                bottomPart.setLinearVelocity(0, 1f);
            } else {
                bottomPart.setLinearVelocity(0f, 0f);
            }

            if (topPart.getPosition().y > bottomClosedY) {
                topPart.setLinearVelocity(0f, -1f);
            } else {
                topPart.setLinearVelocity(0f, 0f);
            }
        }
    }


    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);


        // top side partition
        batch.draw(Assets.ENV,
                topPart.getPosition().x - partWidth * 0.5f,
                topPart.getPosition().y - partHeight * 0.5f,
                partWidth * 0.5f, partHeight * 0.5f,
                partWidth, partHeight,
                1f, 1f, 90f,
                320, 64,
                96, 32, true, false);

        // top side box
        batch.draw(Assets.ENV,
                x + toMeters(16),
                y + fullHeight,
                boxWidth * 0.5f, boxHeight * 0.5f,
                boxWidth, boxHeight,
                1f, 1f, 90f,
                256, isOpen ? 128 : 64,
                64, 64, false, false);

        // bottom side partition
        batch.draw(Assets.ENV,
                bottomPart.getPosition().x - partWidth * 0.5f,
                bottomPart.getPosition().y - partHeight * 0.5f,
                partWidth * 0.5f, partHeight * 0.5f,
                partWidth, partHeight,
                1f, 1f, 90f,
                320, 64,
                96, 32, false, false);

        // bottom side box
        batch.draw(Assets.ENV,
                x + toMeters(16),
                y - boxHeight,
                boxWidth * 0.5f, boxHeight * 0.5f,
                boxWidth, boxHeight,
                1f, 1f, 90f,
                256, isOpen ? 128 : 64,
                64, 64, false, false);



    }

    @Override
    public List<Light> createLights() {
        if (lights == null) {
            lights = new ArrayList<>();

            // top
            Light light = new Light(Light.Type.SOFT, Color.BLACK);
            light.x = toPixels(topPart.getPosition().x) - 128 * 0.5f;
            light.y = toPixels(topPart.getPosition().y) - 128 * 0.5f + 80;
            light.width = light.height = 128;

            lights.add(light);

            // bottom
            light = new Light(Light.Type.SOFT, Color.BLACK);
            light.x = Cfg.toPixels(bottomPart.getPosition().x) - 128 * 0.5f;
            light.y = Cfg.toPixels(bottomPart.getPosition().y
                    - partHeight - boxHeight * 0.5f) - 16 - 128 * 0.5f ;
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
