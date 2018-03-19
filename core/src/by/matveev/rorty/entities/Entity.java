package by.matveev.rorty.entities;

import by.matveev.rorty.core.Event;
import by.matveev.rorty.core.Light;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Collections;
import java.util.List;

public abstract class Entity {

    public final String name;
    public float x;
    public float y;
    public float width;
    public float height;
    public float rotation;

    public Entity(String name) {
        this.name = name;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
    }

    public void draw(Batch batch, OrthographicCamera camera) {
    }

    public void postDraw(Batch batch, OrthographicCamera camera) {
    }

    public void onEvent(Event event) {
    }

    public List<Light> createLights() {
        return Collections.emptyList();
    }
}