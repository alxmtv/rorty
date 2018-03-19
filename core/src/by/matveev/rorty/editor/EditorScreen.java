package by.matveev.rorty.editor;

import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.AbstractScreen;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.entities.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class EditorScreen extends AbstractScreen {

    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private World box2dWorld;
    private Viewport box2DViewport;
    private float box2dAccumulator;

    private ShapeRenderer debugRenderer;

    private List<Entity> entities = new ArrayList<>();

    @Override
    public void show() {
        box2dWorld = new World(new Vector2(0f, -9.8f), true);
        box2DViewport = new FitViewport(Cfg.toMeters(Cfg.WIDTH), Cfg.toMeters(Cfg.HEIGHT));
        debugRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        box2DViewport.update((int) Cfg.toMeters(width), (int) Cfg.toMeters(height), true);
    }

    @Override
    public void update(float delta) {
        final float frameTime = Math.min(delta, 0.25f);
        box2dAccumulator += frameTime;
        while (box2dAccumulator >= TIME_STEP) {
            box2dWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            box2dAccumulator -= TIME_STEP;
        }

        for (Entity e : entities) {
            e.update(delta);
        }

    }

    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        final OrthographicCamera box2dCamera = (OrthographicCamera) box2DViewport.getCamera();
        batch.begin();
        batch.setProjectionMatrix(box2dCamera.combined);
        for (Entity e : entities) {
            e.draw(batch, box2dCamera);
        }
        batch.end();
    }

    @Override
    public void postDraw(SpriteBatch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity e : entities) {
            e.postDraw(batch, camera);
        }
        batch.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Light light : lights) {
            debugRenderer.circle(light.x + light.width * 0.5f, light.y + light.height * 0.5f, light.width * 0.5f);
        }
        debugRenderer.end();

    }
}