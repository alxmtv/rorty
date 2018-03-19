package by.matveev.rorty.entities;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class PhysicsEntity extends Entity {

    public PhysicsEntity(String name) {
        super(name);
    }

    public void onContactStart(PhysicsEntity otherEntity) {
        // do nothing
    }

    public void onContactEnd(PhysicsEntity otherEntity) {
        // do nothing
    }

    public abstract Body getBody();

}