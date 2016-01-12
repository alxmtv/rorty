package by.matveev.rorty.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;
import java.util.Map;

public  class AnimationSet {

    public final Map<String, Animation> animations = new HashMap<>();
    public Texture texture;
    public Animation currentAnimation;

    public void add(String key, Animation animation) {
        animations.put(key, animation);
    }

    public void setTexture(String path) {
        setTexture(new Texture(Gdx.files.internal(path)));
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setAnimation(String key) {
        final Animation found = animations.get(key);
        if (found == null) {
            throw new IllegalArgumentException("Could not file animation with key '" + key + "'");
        }
        currentAnimation = found;
    }

    public void draw(Batch batch, float x, float y, float w, float h) {
        currentAnimation.draw(batch, texture, x, y, w, h);
    }

    public void update(float dt) {
        currentAnimation.update(dt);
    }

    public Animation.Frame currentFrame() {
        return currentAnimation.currentFrame;
    }
}