package by.matveev.rorty.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.ArrayList;
import java.util.List;

public final class Lights {

    private final List<Light> lights = new ArrayList<>();

    private final FrameBuffer lightBuffer;
    private final TextureRegion lightRegion;

    private Color ambientColor = Color.BLACK;

    public Lights(int width, int height) {
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        lightRegion = new TextureRegion(lightBuffer.getColorBufferTexture(), 0, 0, width, height);
        lightRegion.flip(false, true);
    }

    public void setAmbientColor(Color ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    void update(float delta) {
        for (Light light: lights) {
            light.update(delta);
        }
    }

    public void render(Batch batch) {
        lightBuffer.begin();
        batch.begin();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(ambientColor.r, ambientColor.g, ambientColor.b, ambientColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, -1);

        for (Light light: lights) {
            light.render(batch);
        }

        batch.end();
        lightBuffer.end();



        batch.begin();
        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        batch.draw(lightRegion, 0, 0,800,480);
        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}
