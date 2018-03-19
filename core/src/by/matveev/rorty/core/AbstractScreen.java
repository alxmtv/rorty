package by.matveev.rorty.core;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreen implements Screen, Disposable {

    protected final SpriteBatch batch;
    private final Viewport viewport;
    protected final List<Light> lights = new ArrayList<>();
    private final ShaderProgram shader;
    private FrameBuffer lightBuffer;
    private TextureRegion lightTexture;
    private FrameBuffer mainBuffer;
    private TextureRegion mainTexture;
    private FrameBuffer finalBuffer;
    private TextureRegion finalTexture;
    private Color clearColor = Color.BLACK;
    // private Color ambientColor = new Color(0.36f, 0.36f, 0.36f, 1f);
    private Color ambientColor = new Color(0.4f, 0.4f, 0.4f, 1f);

    public AbstractScreen() {
        batch = new SpriteBatch();
        viewport = new FitViewport(Cfg.WIDTH, Cfg.HEIGHT);

        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Cfg.WIDTH, Cfg.HEIGHT, false);
        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lightTexture = new TextureRegion(lightBuffer.getColorBufferTexture(), 0, 0, Cfg.WIDTH, Cfg.HEIGHT);
        lightTexture.flip(false, true);

        mainBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Cfg.WIDTH, Cfg.HEIGHT, false);
        mainBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mainTexture = new TextureRegion(mainBuffer.getColorBufferTexture(), 0, 0, Cfg.WIDTH, Cfg.HEIGHT);
        mainTexture.flip(false, true);

        finalBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Cfg.WIDTH, Cfg.HEIGHT, false);
        finalBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        finalTexture = new TextureRegion(finalBuffer.getColorBufferTexture(), 0, 0, Cfg.WIDTH, Cfg.HEIGHT);
        finalTexture.flip(false, true);


        shader = new ShaderProgram(
                Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/contrast.frag"));
        if (!shader.isCompiled()) {
            System.err.println(shader.getLog());
            System.exit(0);
        }
        if (shader.getLog().length() != 0)
            System.out.println(shader.getLog());

    }

    public void clearLights() {
        lights.clear();
    }


    public void addLights(List<Light> lights) {
        this.lights.addAll(lights);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    @Override
    public void render(float delta) {
        renderToMainBuffer();
        renderToLightBuffer(delta);
        renderToScreen();

        update(delta);
    }

    private void renderToScreen() {
        finalBuffer.begin();

        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setProjectionMatrix(getCamera().combined);
        batch.disableBlending();
        batch.setColor(1, 1, 1, 1);
        batch.draw(mainTexture, getCamera().position.x - Assets.BACKGROUND.getRegionWidth() * 0.5f,
                getCamera().position.y - Assets.BACKGROUND.getRegionHeight() * 0.5f, 800, 480);
        batch.end();
        batch.enableBlending();

        if (Cfg.LIGHT_ENABLED) {
            batch.begin();
            batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
            batch.draw(lightTexture, getCamera().position.x - Assets.BACKGROUND.getRegionWidth() * 0.5f,
                    getCamera().position.y - Assets.BACKGROUND.getRegionHeight() * 0.5f, 800, 480);
            batch.end();
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        finalBuffer.end();


        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setProjectionMatrix(getCamera().combined);
        if (Cfg.CONTRAST_SHADER)
            batch.setShader(shader);
        batch.draw(finalTexture, getCamera().position.x - Assets.BACKGROUND.getRegionWidth() * 0.5f,
                getCamera().position.y - Assets.BACKGROUND.getRegionHeight() * 0.5f, 800, 480);
        batch.end();

        if (Cfg.CONTRAST_SHADER)
            batch.setShader(null);

        postDraw(batch, getCamera());
    }

    private void renderToLightBuffer(float delta) {
        if (!Cfg.LIGHT_ENABLED) {
            return;
        }

        lightBuffer.begin();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(ambientColor.r, ambientColor.g, ambientColor.b, ambientColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, -1);
        batch.begin();
        batch.setProjectionMatrix(getCamera().combined);

        for (Light light : lights) {
            light.update(delta);
            light.render(batch);
        }
        batch.end();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        lightBuffer.end();
    }

    private void renderToMainBuffer() {
        mainBuffer.begin();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(getCamera().combined);
        draw(batch, getCamera());

        mainBuffer.end();
    }

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch, OrthographicCamera camera);

    public void postDraw(SpriteBatch batch, OrthographicCamera camera) {
        // do nothing
    }

    @Override
    public void resize(final int width, final int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

    }

    public OrthographicCamera getCamera() {
        return (OrthographicCamera) viewport.getCamera();
    }
}