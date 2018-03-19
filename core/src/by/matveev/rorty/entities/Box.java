package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.Text;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;

import java.util.Collections;
import java.util.List;

public class Box extends PhysicsEntity {

    private final Body body;
    private final Text text;
    private final MassData data;
    private boolean active;
    private boolean enabled;
    private Light light;

    private  final Color greenColor = ColorUtils.colorFrom(0xff81C784);
    private  final Color redColor = ColorUtils.colorFrom(0xffF44336);

    public Box(Body body, float x, float y, float width, float height) {
        super("box");
        this.body = body;
        body.setUserData(this);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.data = new MassData();
        setupHugeMass();

        greenColor.a = 0.2f;
        redColor.a = 0.2f;

        this.text = new Text("HOLD 'E'\nto pull/push box");

        light = new Light(Light.Type.SOFT, enabled ? greenColor : redColor);
        light.x = Cfg.toPixels(body.getPosition().x) - 128 / 2;
        light.y = Cfg.toPixels(body.getPosition().y) - 128 / 2;
        light.width = light.height = 128;
    }

    @Override
    public void update(float delta) {
        light.color = enabled ? greenColor : redColor;
        light.x = Cfg.toPixels(body.getPosition().x) - 128 / 2;
        light.y = Cfg.toPixels(body.getPosition().y) - 128 / 2;

        setPosition(body.getPosition().x, body.getPosition().y);
    }

    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot) {
            text.setVisible(enabled && ((Robot) otherEntity).isActive() && ((Robot) otherEntity).isFree());
        }
    }

    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (enabled) {
            text.setVisible(false);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void toggleActive() {
        setActive(!isActive());
    }

    public boolean isActive() {
        return active;
    }


    void setupHugeMass() {
        data.mass += 1000;
        body.setMassData(data);
    }

    void setupLowMass() {
        data.mass -= 1000;
        body.setMassData(data);
    }



    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        final float x = body.getPosition().x;
        final float y = body.getPosition().y;
        final float regW = Cfg.toMeters(110);
        final float regH = Cfg.toMeters(110);
        if (enabled) {
            batch.draw(Assets.ENV, x - regW * 0.5f, y - regH * 0.5f, regW, regH, 288, 192, 128, 128, false, false);
        } else {
            batch.draw(Assets.ENV, x - regW * 0.5f, y - regH * 0.5f, regW, regH, 0, 64, 128, 128, false, false);
        }

    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        text.draw(batch, Cfg.toPixels(body.getPosition().x),
                Cfg.toPixels(body.getPosition().y) + Cfg.toPixels(height) * 0.9f);
    }

    @Override
    public Body getBody() {
        return body;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public List<Light> createLights() {
        return Collections.singletonList(light);
    }
}
