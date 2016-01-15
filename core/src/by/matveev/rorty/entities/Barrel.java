package by.matveev.rorty.entities;

import com.badlogic.gdx.physics.box2d.Body;

public class Barrel extends Entity {

    public Barrel(String name) {
        super(name);
    }

    @Override
    public Body getBody() {
        return null;
    }
}
