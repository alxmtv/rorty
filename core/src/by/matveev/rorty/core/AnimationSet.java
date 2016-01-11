package by.matveev.rorty.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        texture = new Texture(Gdx.files.internal(path));
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

    public static AnimationSet load(String jsonData) {
        try {
            final JSONObject parser = new JSONObject(jsonData);

            final AnimationSet set = new AnimationSet();
            set.setTexture(parser.getString("texture"));

            final int frameWidth = parser.getInt("frameWidth");
            final int frameHeight = parser.getInt("frameHeight");

            final JSONArray animationsArray = parser.getJSONArray("animations");
            for (int aix = 0; aix < animationsArray.length(); aix++) {
                final JSONObject animationObject = animationsArray.getJSONObject(aix);

                final Animation animation = new Animation();
                animation.setFrameWidth(frameWidth);
                animation.setFrameHeight(frameHeight);

                final JSONArray framesArray = animationObject.getJSONArray("frames");
                for (int fix = 0; fix < framesArray.length(); fix++) {
                    final JSONObject frameObject = framesArray.getJSONObject(fix);
                    final Animation.Frame frame = new Animation.Frame();
                    frame.setIndexX(frameObject.optInt("indexX", 0));
                    frame.setIndexY(frameObject.optInt("indexY", 0));
                    frame.setFlipX(frameObject.optBoolean("flipX", false));
                    frame.setFlipY(frameObject.optBoolean("flipY", false));
                    frame.setDuration((float) frameObject.getDouble("duration"));
                    animation.add(frame);
                }

                set.add(animationObject.getString("name"), animation);
            }
            set.setAnimation(parser.getString("default"));
            return set;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Animation.Frame currentFrame() {
        return currentAnimation.currentFrame;
    }
}