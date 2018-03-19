package by.matveev.rorty.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Trigger extends PhysicsEntity {

    protected Body body;
    private boolean active;

    public Trigger(String name, Body body) {
        super("trigger");
        this.body = body;
        this.body.setUserData(this);
    }

    public void onEnter() {
        this.active = true;
    }

    public void onExit() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch, OrthographicCamera camera) {
        // do nothing
    }
}
