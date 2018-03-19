package by.matveev.rorty.entities;

import by.matveev.rorty.Cfg;
import by.matveev.rorty.Text;
import by.matveev.rorty.core.AnimationSet;
import by.matveev.rorty.core.Light;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class AbstractRobot extends PhysicsEntity {

    protected final Vector2 temp = new Vector2();


    protected final World world;
    protected final Body body;
    protected final Light light;
    protected final AnimationSet animSet;
    protected final Mark mark;

    private boolean active;

    // default movement direction (right)
    protected int direction = 1;

    protected final Text text;

    public AbstractRobot(World world, String name, float initialX, float initialY) {
        super(name);
        this.world = world;
        this.body = createBody(initialX, initialY);
        this.light = createLight();
        this.animSet = createAnimSet();
        this.mark = new Mark();
        this.text = new Text();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        mark.update(dt);
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        super.postDraw(batch, camera);
        text.draw(batch, Cfg.toPixels(body.getPosition().x), Cfg.toPixels(body.getPosition().y + Cfg.toMeters(48)));

        if (isActive()) {
            mark.postDraw(batch, camera);
        }
    }

    public void toggleActive() {
        this.active = !active;

        if (active) {
            mark.show();
        }
    }

    public boolean isActive() {
        return active;
    }

    protected abstract Body createBody(float initialX, float initialY);

    protected abstract Light createLight();

    protected abstract AnimationSet createAnimSet();

    public Light getLight() {
        return light;
    }

    @Override
    public Body getBody() {
        return body;
    }
}
