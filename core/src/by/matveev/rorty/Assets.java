package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class Assets {

    private Assets() {
    }

    public static final BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/visitor.fnt"));

    public static final Texture LIGHTS = new Texture(Gdx.files.internal("lights.png"));
    public static final Texture ENV = new Texture(Gdx.files.internal("env.png"));
    public static final Texture ROBOT = new Texture(Gdx.files.internal("robot.png"));
    public static final Texture ASSISTANT = new Texture(Gdx.files.internal("assistant.png"));


    public static final Sound MOVE = Gdx.audio.newSound(Gdx.files.internal("sounds/robot_move2.mp3"));
    public static final Sound ELEVATOR = Gdx.audio.newSound(Gdx.files.internal("sounds/elevator.wav"));
    public static final Sound GATE = Gdx.audio.newSound(Gdx.files.internal("sounds/gate.mp3"));
    public static final Sound SENSOR = Gdx.audio.newSound(Gdx.files.internal("sounds/sensor.mp3"));
    public static final Sound SWITCH = Gdx.audio.newSound(Gdx.files.internal("sounds/switch.mp3"));

    public static final TextureRegion BACKGROUND = createRegion("background.png");

    static  {
        ENV.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private static TextureRegion createRegion(String name) {
        return createRegion(name, false, false);
    }

    private static TextureRegion createRegion(String name, boolean flipX, boolean flipY) {
        final Texture texture = new Texture(Gdx.files.internal(name));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        final TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.flip(flipX, flipY);
        return textureRegion;
    }

    private static TextureRegion createRegion(String name, float x, float y, float w, float h) {
        return new TextureRegion(new Texture(Gdx.files.internal(name)), x, y, w, h);
    }
}
