package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class Assets {

    private Assets() {
    }

    public static final BitmapFont font = generateFont(18);

    public static final TextureRegion LIGHT_CIRCLE = createRegion("light.png");
    public static final TextureRegion LIGHT_CIRCLE2 = createRegion("light1.png");
    public static final Texture ENV = new Texture(Gdx.files.internal("env.png"));
    public static final TextureRegion BACKGROUND = createRegion("background.png");

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

    private static BitmapFont generateFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/visitor.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font8 = generator.generateFont(parameter);
        generator.dispose();
        return font8;
    }
}
