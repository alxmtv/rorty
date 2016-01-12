package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.Animation;
import by.matveev.rorty.core.AnimationSet;
import by.matveev.rorty.core.Light;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class Robot extends AbstractRobot {

    public static final String NAME = Robot.class.getSimpleName();
    public static final float DEFAULT_SPEED = 0.3f;
    public static final float DEFAULT_FRICTION = 0.9f;
    private static final float WIDTH = Cfg.toMeters(148);
    private static final float HEIGHT = Cfg.toMeters(148);
    private static final float BODY_WIDTH = Cfg.toMeters(80);
    private static final float BODY_HEIGHT = Cfg.toMeters(96);
    private final static float MAX_VELOCITY = 1.5f;
    private Joint currentJoint;
    private State state = State.CONTROL;
    private Interaction interaction = Interaction.NONE;
    private Entity interactEntity;

    public Robot(World world, float mapX, float mapY) {
        super(world, "robot", mapX, mapY);

        body.getPosition().set(Cfg.toMeters(mapX), Cfg.toMeters(mapY));
    }

    @Override
    public void onContactStart(Entity otherEntity) {
        if (otherEntity instanceof Box) {
            setInteraction(Interaction.BOX, otherEntity);
        }
    }

    @Override
    public void onContactEnd(Entity otherEntity) {
        if (otherEntity instanceof Box) {
            if (interaction == Interaction.BOX && state == State.CONTROL) {
                setInteraction(Interaction.NONE, null);
            }

            text.setText(null);
        }
    }

    public void setInteraction(Interaction interaction, Entity interactEntity) {
        this.interaction = interaction;
        this.interactEntity = interactEntity;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    protected Body createBody(float initialX, float initialY) {
        final float hw = BODY_WIDTH * 0.5f;
        final float hh = BODY_HEIGHT * 0.5f;

        final PolygonShape poly = new PolygonShape();
        poly.setAsBox(hw, hh);

        final BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(Cfg.toMeters(initialX) - hw, Cfg.toMeters(initialY) - hh);
        final Body body = world.createBody(def);
        body.setUserData(this);

//        body.createFixture(poly, 0);
        poly.dispose();

//
        final CircleShape circle = new CircleShape();
        circle.setRadius(Cfg.toMeters(40));
        circle.setPosition(new Vector2(0, -0.2f));

        body.createFixture(circle, 0);
        circle.dispose();

        body.setBullet(true);

        return body;
    }

    @Override
    protected Light createLight() {
        final Light light = new Light(new Color(1f, 1f, 1f, 0.3f), Assets.LIGHT_CIRCLE2);
        light.x = Cfg.toPixels(x) + 148 / 2;
        light.y = Cfg.toPixels(y) - 148 / 2;
        light.width = light.height = 400;
        return light;
    }

    @Override
    protected AnimationSet createAnimSet() {
        final AnimationSet set = new AnimationSet();
        set.setTexture(Assets.ROBOT);

        final Animation left = new Animation();
        left.setFrameWidth(148);
        left.setFrameHeight(148);
        left.add(new Animation.Frame(0, 0, 0.2f, true, false));
        left.add(new Animation.Frame(1, 0, 0.2f, true, false));

        set.add("left", left);

        final Animation right = new Animation();
        right.setFrameWidth(148);
        right.setFrameHeight(148);
        right.add(new Animation.Frame(0, 0, 0.2f, false, false));
        right.add(new Animation.Frame(1, 0, 0.2f, false, false));

        set.add("right", right);

        final Animation idleLeft = new Animation();
        idleLeft.setFrameWidth(148);
        idleLeft.setFrameHeight(148);
        idleLeft.add(new Animation.Frame(0, 1, 0.4f, true, false));
        idleLeft.add(new Animation.Frame(1, 1, 0.4f, true, false));

        set.add("idle_left", idleLeft);

        final Animation idleRight = new Animation();
        idleRight.setFrameWidth(148);
        idleRight.setFrameHeight(148);
        idleRight.add(new Animation.Frame(0, 1, 0.4f, false, false));
        idleRight.add(new Animation.Frame(1, 1, 0.4f, false, false));

        set.add("idle_right", idleRight);

        set.setAnimation("idle_right");

        return set;
    }

    public void update(float delta) {
        super.update(delta);
        animSet.update(delta);


        switch (state) {
            case CONTROL:
            case MOVE_BOX:
                updateControlState();
                break;
        }

        switch (interaction) {
            case BOX:
                updateBoxInteraction();
                break;

            case NONE:
            default:
                break;
        }

        if (body.getLinearVelocity().x == 0) {
            animSet.setAnimation(direction > 0 ? "idle_right" : "idle_left");
        }

        x = body.getPosition().x;
        y = body.getPosition().y;

        light.x = Cfg.toPixels(x) - 400 * 0.5f;
        light.y = Cfg.toPixels(y) - 400 * 0.5f;

    }

    private void updateBoxInteraction() {
        if (isActive() && Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            final Box box = (Box) this.interactEntity;

            if (state == State.CONTROL) {
                setState(State.MOVE_BOX);

                final DistanceJointDef jointDef = new DistanceJointDef();
                jointDef.initialize(body, box.getBody(), new Vector2(0f, 0f), new Vector2(1f, 0));
                jointDef.length = 1f;

                currentJoint = world.createJoint(jointDef);

                box.setupLowMass();


            } else if (state == State.MOVE_BOX) {
                setState(State.CONTROL);
                setInteraction(Interaction.NONE, null);

                world.destroyJoint(currentJoint);
                currentJoint = null;
                box.setupHugeMass();
            }
        }
    }

    private void updateControlState() {
        final Vector2 vel = body.getLinearVelocity();
        final Vector2 pos = body.getPosition();
        if (Math.abs(vel.x) > MAX_VELOCITY) {
            vel.x = Math.signum(vel.x) * MAX_VELOCITY;
            body.setLinearVelocity(vel.x, vel.y);
        }

        if (!isActive() || (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D))) {
            body.setLinearVelocity(vel.x * DEFAULT_FRICTION, vel.y);
        }

        if (isActive()) {
            if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -MAX_VELOCITY) {
                body.applyLinearImpulse(-DEFAULT_SPEED, 0, pos.x, pos.y, true);
                animSet.setAnimation("left");
                direction = -1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < MAX_VELOCITY) {
                body.applyLinearImpulse(DEFAULT_SPEED, 0f, pos.x, pos.y, true);
                animSet.setAnimation("right");
                direction = 1;
            }
        }
    }

    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        animSet.draw(batch, x - WIDTH * 0.5f, y - BODY_HEIGHT * 0.5f - 0.12f, WIDTH, HEIGHT);
    }


    public enum State {
        CONTROL, MOVE_BOX
    }

    public enum Interaction {
        NONE, BOX
    }
}
