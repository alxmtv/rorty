package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.AnimationSet;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

public class Assistant extends AbstractRobot {

    private final static float BODY_RADIUS = Cfg.toMeters(24);
    private final static float BODY_RADIUS2 = BODY_RADIUS * 2f;

    private final static float DEFAULT_OFFSET = 0.8f;
    private final static float DEFAULT_SPEED = 0.2f;
    private static final float DEFAULT_FRICTION = 0.9f;
    private final static float MAX_VELOCITY = 1.5f;
    private final Robot robot;
    private State state = State.CONTROL;
    private Interaction interaction = Interaction.NONE;
    private Entity interactEntity;

    public Assistant(World world, Robot robot, float mapX, float mapY) {
        super(world, "assistant", mapX, mapY);
        this.robot = robot;
        body.getPosition().set(mapX, mapY);
    }

    @Override
    public void onContactStart(final Entity otherEntity) {
        if (otherEntity instanceof Box) {
            setInteraction(Interaction.BOX, otherEntity);
        }
    }

    @Override
    public void onContactEnd(Entity otherEntity) {
        if (otherEntity instanceof Box) {
            if (interaction == Interaction.BOX) {
                setInteraction(Interaction.NONE, null);
            }
        }
    }

    public void update(float delta) {
        super.update(delta);

        animSet.update(delta);

        switch (state) {
            case FOLLOW:
                updateFollowState();
                break;

            case CONTROL:
                updateControlState();
                break;

            case SOCKET:
                updateSocketState();
                break;
        }

        switch (interaction) {
            case BOX:
                updateBoxInteraction();
                break;

            case NONE:
            default:
                updateDefaultInteraction();
                break;
        }

        x = body.getPosition().x;
        y = body.getPosition().y;

        light.x = Cfg.toPixels(x) - 128 * 0.5f;
        light.y = Cfg.toPixels(y) - 128 * 0.5f;
    }

    private void updateDefaultInteraction() {
        setSensor(false);
    }

    private void setSensor(boolean isSensor) {
        body.getFixtureList().get(0).setSensor(isSensor);
    }

    private void updateBoxInteraction() {
        if (isActive() && Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            ((Box) interactEntity).toggleActive();

            if (state == State.FOLLOW || state == State.CONTROL) {
                setState(State.SOCKET);
            } else if (state == State.SOCKET) {
                setState(State.CONTROL);
            }
        }
    }

    private void updateControlState() {
        if (!isActive()) return;
        final Vector2 vel = body.getLinearVelocity();
        if (Math.abs(vel.x) > MAX_VELOCITY) {
            vel.x = Math.signum(vel.x) * MAX_VELOCITY;
        }

        if (Math.abs(vel.y) > MAX_VELOCITY) {
            vel.y = Math.signum(vel.y) * MAX_VELOCITY;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            vel.x *= DEFAULT_FRICTION;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
            vel.y *= DEFAULT_FRICTION;
//            vel.y += (MathUtils.sin(angular += 0.1f) * 0.05f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -MAX_VELOCITY) {
            vel.x -= DEFAULT_SPEED;
            animSet.setAnimation("left");
            direction = -1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < MAX_VELOCITY) {
            vel.x += DEFAULT_SPEED;
            animSet.setAnimation("right");
            direction = 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && vel.y < MAX_VELOCITY) {
            vel.y += DEFAULT_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) && vel.y > -MAX_VELOCITY) {
            vel.y -= DEFAULT_SPEED;
        }

        body.setLinearVelocity(vel);
    }

    private void updateSocketState() {
        setSensor(true);
        animSet.setAnimation("idle");
        temp.set(interactEntity.getBody().getPosition());

        float distance = temp.dst(body.getPosition());
        float magnitude = distance * 50f;

        final Vector2 scl = temp.sub(body.getPosition()).nor().scl(magnitude);
        body.setLinearVelocity(scl);
    }

    private void updateFollowState() {
        final Vector2 targetVelocity = robot.getBody().getLinearVelocity();
        final Vector2 targetPosition = robot.getBody().getPosition();

        if (targetVelocity.x > 0) {
            direction = 1;
            animSet.setAnimation("right");
        } else if (targetVelocity.x < 0) {
            direction = -1;
            animSet.setAnimation("left");
        }

        temp.set(targetPosition).sub(DEFAULT_OFFSET * direction, 0f);

        final float distance = temp.dst(body.getPosition());
        final float magnitude = distance * 2f;

        final Vector2 newVelocity = temp.sub(body.getPosition()).nor().scl(magnitude);
        newVelocity.y += (MathUtils.sin(angular += 0.1f) * 0.2f);

        body.setLinearVelocity(newVelocity);
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setInteraction(Interaction interaction, Entity interactEntity) {
        this.interaction = interaction;
        this.interactEntity = interactEntity;
    }

    @Override
    protected AnimationSet createAnimSet() {
        return AnimationSet.load(Gdx.files.internal("anim/assistant.json").readString());
    }

    @Override
    protected Body createBody(float initialX, float initialY) {
        final CircleShape circle = new CircleShape();
        circle.setRadius(BODY_RADIUS);

        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.x = Cfg.toMeters(initialX) + BODY_RADIUS;
        bodyDef.position.y = Cfg.toMeters(initialY) + BODY_RADIUS;
        bodyDef.linearDamping = 0.1f;
        bodyDef.angularDamping = 0.5f;

        final Body body = world.createBody(bodyDef);
        body.setGravityScale(0f);
        body.setUserData(this);
        body.createFixture(circle, 50);

        circle.dispose();

        return body;
    }

    @Override
    protected Light createLight() {
        // 10% alpha
        final Light light = new Light(ColorUtils.colorFrom(0x1A90CAF9), Assets.LIGHT_CIRCLE2);
        light.x = Cfg.toPixels(x) + 48 / 2;
        light.y = Cfg.toPixels(y) - 48 / 2;
        light.width = light.height = 128;
        return light;
    }

    @Override
    public void toggleActive() {
        super.toggleActive();

        if (state == State.FOLLOW) {
            setState(State.CONTROL);
        } else if (state == State.CONTROL) {
            setState(State.CONTROL);
        }
    }

    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);

        animSet.draw(batch, x - BODY_RADIUS, y - BODY_RADIUS, BODY_RADIUS2, BODY_RADIUS2);
    }

    private enum State {
        FOLLOW, CONTROL, SOCKET
    }

    private enum Interaction {
        NONE, BOX
    }

}
