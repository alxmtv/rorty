package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.Text;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;

public class Box extends Entity {

    private final Body body;
    private final Text text;
    private final MassData data;
    private boolean active;
    private Boolean enabled;

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

        this.text = new Text("Press 'E'");
    }

    @Override
    public void update(float delta) {
        if (isActive()) {
            body.getLinearVelocity().y += 10 * (MathUtils.sin(angular += 0.1f));
        }


        x = body.getPosition().x;
        y = body.getPosition().y;
    }

    @Override
    public void onContactStart(Entity otherEntity) {
        if (otherEntity instanceof Robot) {
            text.setVisible(enabled && ((Robot) otherEntity).isActive() && ((Robot) otherEntity).isFree());
        }
    }

    @Override
    public void onContactEnd(Entity otherEntity) {
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
        final float regW = Cfg.toMeters(128);
        final float regH = Cfg.toMeters(128);
        batch.draw(Assets.ENV, x - regW * 0.5f, y - regH * 0.5f, regW, regH, 0, 64, 128, 128, false, false);
    }

    @Override
    public void postDraw(Batch batch) {
        text.draw(batch, Cfg.toPixels(body.getPosition().x),
                Cfg.toPixels(body.getPosition().y) + Cfg.toPixels(height) * 0.8f);
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
}
