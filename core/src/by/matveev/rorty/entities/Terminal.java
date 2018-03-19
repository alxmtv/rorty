package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.Text;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.screens.TerminalScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

public class Terminal extends PhysicsEntity {

    private final Body body;
    float time;
    int sheetY = 320;
    private boolean active;
    private Text text;

    public enum Type {
        GOAL, COMPLETE
    }
    public Type type;

    public Terminal(String id,String type, Body body) {
        super(id);
        this.body = body;
        this.body.setUserData(this);
        this.type = Type.valueOf(type.toUpperCase());
        this.x =  body.getPosition().x;
        this.y =  body.getPosition().y;
        this.width = Cfg.toMeters(96);
        this.height = Cfg.toMeters(96);
        this.text = new Text("Press 'E'").setVisible(true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        time += dt;
        if (time >= 2f) {
            time = 0;
            sheetY = sheetY == 320 ? 416 : 320;
        }

        if (active && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Screens.push(new TerminalScreen(type));
        }
    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f,
                width, height,
                160, 128,
                96, 96,
                false, false);

        batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.45f,
                width, height,
                160, sheetY,
                96, 96,
                false, false);

        batch.draw(Assets.ENV, x - width * 0.5f, y - height * 0.5f,
                width, height,
                160, 224,
                96, 96,
                false, false);
    }

    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot)
            this.active = true;
    }

    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (otherEntity instanceof Robot)
            this.active = false;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        if (active) {
            text.draw(batch, Cfg.toPixels(x), Cfg.toPixels(y + height * 0.8f));
        }
    }
}
