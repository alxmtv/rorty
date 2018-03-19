package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Animation;
import by.matveev.rorty.core.AnimationSet;
import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.utils.ColorUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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

    private State state = State.FOLLOW;

    private float stateTime;
    private int stateIndex;
    private String[] messages = new String[]{"start", "loading...", "failed"};
    private final Vector2 target = new Vector2();

    public Assistant(World world, Robot robot, float mapX, float mapY) {
        super(world, "assistant", mapX, mapY);
        this.robot = robot;
        body.getPosition().set(mapX, mapY);
        target.set(body.getPosition());
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Robot.FollowEvent) {
            setState(State.FOLLOW);
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

            case CRASH:
                updateCrashState();
                break;
        }

        setPosition(body.getPosition().x, body.getPosition().y);

        light.x = Cfg.toPixels(x) - 128 * 0.5f;
        light.y = Cfg.toPixels(y) - 128 * 0.5f;

        mark.setPosition(x, y + BODY_RADIUS2);

        setSensor(!isActive() && state != State.CRASH);

        if (!isActive()) {
            final Vector2 vel = body.getLinearVelocity();
            vel.x *= DEFAULT_FRICTION;
            vel.y *= DEFAULT_FRICTION;
            body.setLinearVelocity(vel);
        }
    }

    private void updateCrashState() {
        stateTime += Gdx.graphics.getDeltaTime();
        if (stateTime > 1f) {
            stateTime = 0f;
            stateIndex++;
            if (stateIndex > messages.length - 1) {
                stateIndex = 0;
            }
            text.setText(messages[stateIndex]);
            target.set(MathUtils.random(0, 8), y);
        }
        temp.set(target).sub(DEFAULT_OFFSET * direction, 0f);

        final float distance = temp.dst(body.getPosition());
        final float magnitude = distance * 2f;

        final Vector2 newVelocity = temp.sub(body.getPosition()).nor().scl(magnitude);
        newVelocity.y += (MathUtils.sin(rotation += 0.1f) * 0.2f);

        body.setLinearVelocity(newVelocity);

        if (body.getLinearVelocity().x > 0) {
            animSet.setAnimation("right");
        } else if (body.getLinearVelocity().x < 0) {
            animSet.setAnimation("left");
        }
    }

    @Override
    public void postDraw(Batch batch, OrthographicCamera camera) {
        super.postDraw(batch, camera);

        text.setVisible(State.CRASH.equals(state));

    }

    private void setSensor(boolean isSensor) {
        body.getFixtureList().get(0).setSensor(isSensor);
    }

    private void updateControlState() {
        final Vector2 vel = body.getLinearVelocity();
        vel.y += (MathUtils.sin(rotation += 0.1f) * 0.02f);
        body.setLinearVelocity(vel);

        if (!isActive()) return;

        if (Math.abs(vel.x) > MAX_VELOCITY) {
            vel.x = Math.signum(vel.x) * MAX_VELOCITY;
        }

        if (Math.abs(vel.y) > MAX_VELOCITY) {
            vel.y = Math.signum(vel.y) * MAX_VELOCITY;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            vel.x *= DEFAULT_FRICTION;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vel.y *= DEFAULT_FRICTION;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && vel.x > -MAX_VELOCITY) {
            vel.x -= DEFAULT_SPEED;
            animSet.setAnimation("left");
            direction = -1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && vel.x < MAX_VELOCITY) {
            vel.x += DEFAULT_SPEED;
            animSet.setAnimation("right");
            direction = 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && vel.y < MAX_VELOCITY) {
            vel.y += DEFAULT_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && vel.y > -MAX_VELOCITY) {
            vel.y -= DEFAULT_SPEED;
        }

        body.setLinearVelocity(vel);
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
        newVelocity.y += (MathUtils.sin(rotation += 0.1f) * 0.2f);

        body.setLinearVelocity(newVelocity);
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    protected AnimationSet createAnimSet() {
        final AnimationSet set = new AnimationSet();
        set.setTexture(Assets.ASSISTANT);

        final Animation left = new Animation();
        left.setFrameWidth(80);
        left.setFrameHeight(80);
        left.add(new Animation.Frame(2, 0, 2, true, false));
        left.add(new Animation.Frame(2, 1, 0.2f, true, false));

        set.add("left", left);

        final Animation right = new Animation();
        right.setFrameWidth(80);
        right.setFrameHeight(80);
        right.add(new Animation.Frame(2, 0, 2, false, false));
        right.add(new Animation.Frame(2, 1, 0.2f, false, false));

        set.add("right", right);

        set.setAnimation("right");

        return set;
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
        final Color color = ColorUtils.colorFrom(0x1A90CAF9);
        color.a = 0.25f;
        final Light light = new Light(Light.Type.SOFT, color);
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

    public enum State {
        FOLLOW, CONTROL, CRASH
    }
}
