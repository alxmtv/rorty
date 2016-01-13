package by.matveev.rorty.screens;

import by.matveev.rorty.*;
import by.matveev.rorty.core.AbstractScreen;
import by.matveev.rorty.core.EventQueue;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.entities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.List;

public class GameScreen extends AbstractScreen {

    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final float CAMERA_SPEED = 3.0f;

    private final String levelId;

    private World box2dWorld;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthographicCamera box2DCamera;
    private float accumulator = 0;

    private TiledMap tileMap;
    private Rectangle tileMapBounds;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public Robot robot;
    public Assistant assistant;

    private ShapeRenderer debugRenderer;
    private List<Entity> entities;

    private final FPSLogger fps = new FPSLogger();

    public GameScreen(String levelId) {
        this.levelId = levelId;
    }

    @Override
    public void show() {
        tileMap = new TmxMapLoader().load("maps/" + levelId + ".tmx");
        tileMapBounds = TiledMapUtils.obtainBounds(tileMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);

        final MapLayer player = tileMap.getLayers().get("player");
        RectangleMapObject r;
        if (player != null) {
            r = (RectangleMapObject) player.getObjects().get(0);
        } else {
            r = new RectangleMapObject(200, 200, 0, 0);
        }

        box2dWorld = new World(new Vector2(0f, -9.8f), true);
        box2dWorld.setContactFilter(new ContactFilter() {
            @Override
            public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
                if ((fixtureA.getBody().getUserData() instanceof Robot && fixtureB.getBody().getUserData() instanceof Assistant) ||
                        (fixtureB.getBody().getUserData() instanceof Robot && fixtureB.getBody().getUserData() instanceof Assistant)) {
                    return false;
                }
                return true;
            }
        });

        box2dWorld.setContactListener(new EntityContactResolver());

        box2DDebugRenderer = new Box2DDebugRenderer();
        box2DCamera = new OrthographicCamera(Cfg.toMeters(Cfg.WIDTH), Cfg.toMeters(Cfg.HEIGHT));

        debugRenderer = new ShapeRenderer();

        robot = new Robot(box2dWorld, r.getRectangle().x, r.getRectangle().y);
        robot.toggleActive();
        addLight(robot.getLight());

//        assistant = new Assistant(box2dWorld, robot, r.getRectangle().x, r.getRectangle().y);
//        addLight(assistant.getLight());


        setupWorld();
        setupLights();
    }

    private void setupWorld() {
        final WorldBuilder builder = new WorldBuilder(box2dWorld, tileMap);

        final List<Light> lights = builder.buildLights();
        for (Light l : lights) {
            addLight(l);
        }
        entities = builder.build();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        fps.log();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0);
        box2DCamera.setToOrtho(false, Cfg.toMeters(width), Cfg.toMeters(height));
    }

    private void setupLights() {
        for (Entity e : entities) {
            final List<Light> lights = e.createLights();
            for (Light l : lights) {
                addLight(l);
            }
        }
    }

    @Override
    public void update(float delta) {
        final float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            box2dWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }

        robot.update(delta);
        assistant.update(delta);

        for (Entity e : entities) {
            e.update(delta);
        }

        EventQueue.dispatch(entities);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switchRobots();
        }

        updateCamera(delta);


    }

    private void switchRobots() {
        robot.toggleActive();
        assistant.toggleActive();
    }

    private void updateCamera(float dt) {
        final float robotX;
        final float robotY;
        if (robot.isActive()) {
            robotX = Cfg.toPixels(robot.x);
            robotY = Cfg.toPixels(robot.y);
        } else {
            robotX = Cfg.toPixels(assistant.x);
            robotY = Cfg.toPixels(assistant.y);
        }

//        camera.position.set(robotX, robotY, 0f);

        camera.position.x += (robotX - camera.position.x) * CAMERA_SPEED * dt;
        camera.position.y += (robotY - camera.position.y) * CAMERA_SPEED * dt;

        camera.position.x = MathUtils.round(camera.position.x);
        camera.position.y = MathUtils.round(camera.position.y);

        if (tileMapBounds.width < camera.viewportWidth) {
            camera.position.x = camera.position.x + camera.viewportWidth * 0.5f;
        } else if ((camera.position.x - camera.viewportWidth * 0.5f) <= 0) {
            camera.position.x = camera.viewportWidth * 0.5f;
        } else if ((camera.position.x + camera.viewportWidth * 0.5f) > tileMapBounds.width) {
            camera.position.x = tileMapBounds.width - camera.viewportWidth * 0.5f;
        }

        if (tileMapBounds.height < camera.viewportHeight) {
            camera.position.y = tileMapBounds.height * 0.5f;
        } else if ((camera.position.y - camera.viewportHeight * 0.5f) <= 0) {
            camera.position.y = camera.viewportHeight * 0.5f;
        } else if ((camera.position.y + camera.viewportHeight * 0.5f) >= tileMapBounds.height) {
            camera.position.y = tileMapBounds.height - camera.viewportHeight * 0.5f;
        }

        box2DCamera.position.set(Cfg.toMeters(camera.position.x), Cfg.toMeters(camera.position.y), 0f);

        camera.update();
        box2DCamera.update();
    }

    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(Assets.BACKGROUND,
                camera.position.x - Assets.BACKGROUND.getRegionWidth() * 0.5f,
                camera.position.y - Assets.BACKGROUND.getRegionHeight() * 0.5f);
        batch.end();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.begin();
        batch.setProjectionMatrix(box2DCamera.combined);

        for (Entity e : entities) {
            e.draw(batch, box2DCamera);
        }

        robot.draw(batch, box2DCamera);
        assistant.draw(batch, box2DCamera);

        batch.end();
    }

    @Override
    public void postDraw(SpriteBatch batch, OrthographicCamera camera) {
        super.postDraw(batch, camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity e : entities) {
            e.postDraw(batch);
        }

        robot.postDraw(batch);
        assistant.postDraw(batch);

        batch.end();


        if (Cfg.BOX2D_DEBUG) {
            box2DDebugRenderer.render(box2dWorld, box2DCamera.combined);
        }

        if (Cfg.LIGHT_DEBUG) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (Light light : lights) {
                debugRenderer.circle(light.x + light.width * 0.5f, light.y + light.height * 0.5f, light.width * 0.5f);
            }
            debugRenderer.end();
        }
    }


}
