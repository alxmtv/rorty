package by.matveev.rorty.entities;

import by.matveev.rorty.Cfg;
import by.matveev.rorty.Rorty;
import by.matveev.rorty.Text;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

public class Door extends Entity {

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
    public void onContactStart(Entity otherEntity) {
        if (otherEntity instanceof Robot && ((Robot) otherEntity).isFree()) {
            this.contacted = true;
        }
    }

    @Override
    public void onContactEnd(Entity otherEntity) {
        if (otherEntity instanceof Robot) {
            this.contacted = false;
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (contacted && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Rorty.replaceLevel(levelId);
        }
    }

    @Override
    public void postDraw(Batch batch) {
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
