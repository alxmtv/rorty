package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.Text;
import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.EventQueue;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pools;

import java.util.Collections;
import java.util.List;

public class Switch extends PhysicsEntity {

    private final Body body;
    private final String target;

    private final Color greenColor = ColorUtils.colorFrom(0x8C81C784); // 25% alpha
    private final Color redColor = ColorUtils.colorFrom(0xBFF44336); // 75%

    private boolean contacted;
    private boolean activated;
    private boolean enabled;
    private Light light;
    private Text text;
    private PhysicsEntity otherEntity;


    public Switch(Body body, String name, String target, boolean enabled) {
        super(name);
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = Cfg.toMeters(64);
        this.height = Cfg.toMeters(64);
        this.target = target;
        this.body = body;
        this.enabled = enabled;
        this.body.setUserData(this);
        this.text = new Text("Press 'E'");
        this.text.setVisible(true);

        greenColor.a = 0.2f;
        redColor.a = 0.2f;

        light = new Light(Light.Type.SOFT, enabled ? greenColor : redColor);
        light.x = Cfg.toPixels(body.getPosition().x) - 256 / 2;
        light.y = Cfg.toPixels(body.getPosition().y) - 256 / 2;
        light.width = light.height = 256;
    }

    @Override
    public Body getBody() {
        return body;
    }


    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (otherEntity instanceof AbstractRobot && ((AbstractRobot) otherEntity).isActive()) {
            this.otherEntity = otherEntity;
            this.contacted = true;
        }
    }


    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (this.otherEntity == otherEntity) {
            this.otherEntity = null;
            this.contacted = false;
            this.activated = false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Sensor.SensorEvent) {
            enabled = ((Sensor.SensorEvent) event).isActive();
        }


    }

    @Override
    public void update(float dt) {
        light.color = enabled ? greenColor : redColor;
        if (otherEntity instanceof AbstractRobot && ((AbstractRobot) otherEntity).isActive()) {
            if (contacted && enabled && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                activated = !activated;
                Assets.playSwitchSound();
                EventQueue.add(Pools.obtain(SwitchEvent.class)
                        .setSender(name)
                        .setReceiver(target));
            }
        }


    }

    @Override
    public List<Light> createLights() {
        return Collections.singletonList(light);
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        if (enabled) {
            batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f, width, height, 320, 0, 64, 64, false, false);
        } else {
            batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f, width, height, 256, 0, 64, 64, false, false);
        }
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        if (enabled && contacted) {
            final float x = body.getPosition().x;
            final float y = body.getPosition().y;
            text.draw(batch, Cfg.toPixels(x), Cfg.toPixels(y) + 48);
        }
    }

    public static final class SwitchEvent extends Event {

        public SwitchEvent() {
        }

        @Override
        public boolean validate() {
            return true;
        }
    }
}
