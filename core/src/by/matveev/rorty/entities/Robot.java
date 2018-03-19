package by.matveev.rorty.entities;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Pools;

public class Robot extends AbstractRobot {

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
    private PhysicsEntity interactEntity;
    private boolean isMoveSoundPlaying;

    public Robot(World world, float mapX, float mapY) {
        super(world, "robot", mapX, mapY);

        body.getPosition().set(Cfg.toMeters(mapX), Cfg.toMeters(mapY));
    }

    @Override
    public void onContactStart(PhysicsEntity otherEntity) {
        if (isFree() && otherEntity instanceof Box) {
            setInteraction(Interaction.BOX, otherEntity);
        }
    }

    @Override
    public void onContactEnd(PhysicsEntity otherEntity) {
        if (isFree() && otherEntity instanceof Box) {
            if (interaction == Interaction.BOX && state == State.CONTROL) {
                setInteraction(Interaction.NONE, null);
            }

            text.setText(null);
        }
    }

    public void setInteraction(Interaction interaction, PhysicsEntity interactEntity) {
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
        circle.setRadius(Cfg.toMeters(32));
        circle.setPosition(new Vector2(0, -0.2f));

        body.createFixture(circle, 0);
        circle.dispose();

        body.setBullet(true);

        return body;
    }

    @Override
    protected Light createLight() {
        final Light light = new Light(Light.Type.SOFT, new Color(1f, 1f, 1f, 0.3f));
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


        final Animation pullRight = new Animation();
        pullRight.setFrameWidth(148);
        pullRight.setFrameHeight(148);
        pullRight.add(new Animation.Frame(0, 2, 0.2f, true, false));
        pullRight.add(new Animation.Frame(1, 2, 0.2f, true, false));
        set.add("pull_right", pullRight);

        final Animation pullLeft = new Animation();
        pullLeft.setFrameWidth(148);
        pullLeft.setFrameHeight(148);
        pullLeft.add(new Animation.Frame(0, 2, 0.2f, false, false));
        pullLeft.add(new Animation.Frame(1, 2, 0.2f, false, false));
        set.add("pull_left", pullLeft);


        final Animation pushRight = new Animation();
        pushRight.setFrameWidth(148);
        pushRight.setFrameHeight(148);
        pushRight.add(new Animation.Frame(0, 3, 0.2f, false, false));
        pushRight.add(new Animation.Frame(1, 3, 0.2f, false, false));
        set.add("push_right", pushRight);


        final Animation pushLeft = new Animation();
        pushLeft.setFrameWidth(148);
        pushLeft.setFrameHeight(148);
        pushLeft.add(new Animation.Frame(0, 3, 0.2f, true, false));
        pushLeft.add(new Animation.Frame(1, 3, 0.2f, true, false));
        set.add("push_left", pushLeft);


        final Animation idleRight = new Animation();
        idleRight.setFrameWidth(148);
        idleRight.setFrameHeight(148);
        idleRight.add(new Animation.Frame(0, 1, 0.3f, false, false));
        idleRight.add(new Animation.Frame(1, 1, 0.3f, false, false));
        set.add("idle_right", idleRight);

        // default animation
        set.setAnimation("idle_right");

        return set;
    }

    public boolean isFree() {
        return currentJoint == null;
    }

    public void update(float delta) {
        super.update(delta);
        animSet.update(delta);

        if (isActive() && isFree() && Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            EventQueue.add(Pools.obtain(FollowEvent.class)
                    .setSender(name)
                    .setReceiver("assistant"));
        }


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

        setPosition(body.getPosition().x, body.getPosition().y);

        light.x = Cfg.toPixels(x) - 400 * 0.5f;
        light.y = Cfg.toPixels(y) - 400 * 0.5f;

        mark.setPosition(x, y + HEIGHT * 0.5f);

    }

    private void updateBoxInteraction() {
        final Box box = (Box) this.interactEntity;
        if (isActive() && box.isEnabled() && Gdx.input.isKeyPressed(Input.Keys.E)) {
            if (isFree() && state == State.CONTROL) {
                startInteractionWithBox(box);
            }
        } else {
            if (state == State.MOVE_BOX) {
                endInteractionWithBox(box);
            }

        }
    }

    private void endInteractionWithBox(Box box) {
        if (currentJoint == null) return;
        setState(State.CONTROL);
        setInteraction(Interaction.NONE, null);

        world.destroyJoint(currentJoint);
        currentJoint = null;
        box.setupHugeMass();
    }

    private void startInteractionWithBox(Box box) {
        if (currentJoint != null) return;
        setState(State.MOVE_BOX);

        final DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.initialize(body, box.getBody(), new Vector2(0f, 0f), new Vector2(1f, 0));
        jointDef.length = 1f;

        currentJoint = world.createJoint(jointDef);

        box.setupLowMass();
    }

    @Override
    public void toggleActive() {
        super.toggleActive();

        if (!isActive() && !isFree()) {
            endInteractionWithBox((Box) this.interactEntity);
        }
    }

    private void updateControlState() {
        final Vector2 vel = body.getLinearVelocity();
        final Vector2 pos = body.getPosition();
        if (Math.abs(vel.x) > MAX_VELOCITY) {
            vel.x = Math.signum(vel.x) * MAX_VELOCITY;
            body.setLinearVelocity(vel.x, vel.y);
        }

        if (!isActive() || (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            body.setLinearVelocity(vel.x * DEFAULT_FRICTION, vel.y);
            stopMoveSound();
        }

        if (isActive()) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && vel.x > -MAX_VELOCITY) {
                playMoveSound();

                body.applyLinearImpulse(-DEFAULT_SPEED, 0, pos.x, pos.y, true);
                if (currentJoint != null) {
                    if (body.getPosition().x < interactEntity.getBody().getPosition().x) {
                        animSet.setAnimation("pull_left");
                    } else {
                        animSet.setAnimation("push_left");
                    }
                } else {
                    animSet.setAnimation("left");
                }

                direction = -1;
            }

            if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT)) && vel.x < MAX_VELOCITY) {
                body.applyLinearImpulse(DEFAULT_SPEED, 0f, pos.x, pos.y, true);
                playMoveSound();
                direction = 1;

                if (currentJoint != null) {
                    if (body.getPosition().x > interactEntity.getBody().getPosition().x) {
                        animSet.setAnimation("pull_right");
                    } else {
                        animSet.setAnimation("push_right");
                    }
                } else {
                    animSet.setAnimation("right");
                }
            }
        }
    }

    private void stopMoveSound() {
        Assets.stopLoopMoveSound();
        isMoveSoundPlaying = false;
    }

    private void playMoveSound() {
        if (!isMoveSoundPlaying) {
            Assets.loopRobotMoveSound();
            isMoveSoundPlaying = true;
        }
    }

    public void draw(Batch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        animSet.draw(batch, x - WIDTH * 0.5f, y - BODY_HEIGHT * 0.5f, WIDTH, HEIGHT);
    }


    public enum State {
        CONTROL, MOVE_BOX
    }

    public enum Interaction {
        NONE, BOX
    }

    public static final class FollowEvent extends Event {

        public FollowEvent() {
        }

        @Override
        public boolean validate() {
            return true;
        }
    }
}
