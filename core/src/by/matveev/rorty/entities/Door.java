package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.Rorty;
import by.matveev.rorty.Text;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;
import java.util.List;

public class Door extends PhysicsEntity {

    private final Body body;
    private final String levelId;
    private final Text text;
    private boolean contacted;

    public Door(Body body, String levelId, final float x, float y, float w, float h) {
        super("door");
        this.body = body;
        this.body.setUserData(this);
        this.levelId = levelId;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.text = new Text("Press 'E'");
        this.text.setVisible(true);
    }

    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot && ((Robot) otherEntity).isFree()) {
            this.contacted = true;
        }
    }

    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot) {
            this.contacted = false;
        }
    }
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.ENV, x , y, width, height, 512, 0, 128, 192, false, false);
    }

    @Override
    public List<Light> createLights() {
        final Color greenColor = ColorUtils.colorFrom(0xff81C784);
        greenColor.a = 0.1f;

        final List<Light> lights = new ArrayList<>();

        Light light = new Light(Light.Type.SOFT, greenColor);
        light.x = Cfg.toPixels(body.getPosition().x) - 256 / 2;
        light.y = Cfg.toPixels(body.getPosition().y) - 256 / 2;
        light.width = light.height = 256;
        lights.add(light);

        light = new Light(Light.Type.SOFT, greenColor);
        light.x = Cfg.toPixels(body.getPosition().x) - 128 / 2;
        light.y = Cfg.toPixels(body.getPosition().y) ;
        light.width = light.height = 128;
        lights.add(light);

        return lights;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (contacted && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Rorty.replaceLevel(levelId);
        }
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        if (contacted) {
            text.draw(batch, Cfg.toPixels(body.getPosition().x),
                    Cfg.toPixels(body.getPosition().y) + Cfg.toPixels(height) * 0.8f);
        }
    }

    @Override
    public Body getBody() {
        return body;
    }
}
