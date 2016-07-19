package by.matveev.rorty.screens;

import by.matveev.rorty.*;
import by.matveev.rorty.core.AbstractScreen;
import by.matveev.rorty.core.Callback;
import by.matveev.rorty.core.EventQueue;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.entities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

public class GameScreen extends AbstractScreen {

    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final float CAMERA_SPEED = 3.0f;

    private final String levelId;

    private World box2dWorld;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Viewport box2DViewport;
    private float accumulator = 0;

    private TiledMap tileMap;
    private Rectangle tileMapBounds;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public Robot robot;
    public Assistant assistant;

    private ShapeRenderer debugRenderer;
    protected List<Entity> entities;
    private HintList hints = new HintList();

    private final Vector3 temp = new Vector3();

    public GameScreen(String levelId) {
        this.levelId = levelId;
    }

    @Override
    public void show() {
        tileMap = new TmxMapLoader().load("maps/" + levelId + ".tmx");
        tileMapBounds = TiledMapUtils.obtainBounds(tileMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
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
        box2DViewport = new FitViewport(Cfg.toMeters(Cfg.WIDTH), Cfg.toMeters(Cfg.HEIGHT));

        debugRenderer = new ShapeRenderer();

        setupWorld();
        setupLights();
        setupRobots();
        setupHints();


        setupButtons();

    }

    protected void setupButtons() {
        entities.add(new Button(70 + 136, 200, Button.Type.RESTART, Button.Type.RESTART, new Callback<Boolean>() {
            @Override
            public void call(Boolean value) {
                Rorty.restartCurrentLevel();
            }
        }));


        entities.add(new Button(70 + 200, 200, Button.Type.MUSIC_ON, Button.Type.MUSIC_OFF, new Callback<Boolean>() {
            @Override
            public void call(Boolean value) {
                Assets.setMusicEnabled(value);
            }
        }).setEnabled(Assets.musicEnabled));


        entities.add(new Button(70 + 264, 200, Button.Type.SOUND_ON, Button.Type.SOUND_OFF, new Callback<Boolean>() {
            @Override
            public void call(Boolean value) {
                Assets.setSoundsEnabled(value);
            }
        }).setEnabled(Assets.soundsEnabled));
    }

    @Override
    public void hide() {
        super.hide();
    }

    private void setupHints() {
        final MapLayer hintsLayer = tileMap.getLayers().get("hints");
        if (hintsLayer != null) {
            final MapObjects objects = hintsLayer.getObjects();
            for (MapObject o : objects) {
                hints.addHint(o.getProperties().get("text", String.class).replace("\\n", "\n"),
                        Integer.parseInt(o.getProperties().get("keyCode", String.class)));
            }
        }
    }

    private void setupRobots() {
        final MapLayer playersLayer = tileMap.getLayers().get("players");
        if (playersLayer == null) throw new IllegalStateException("could not setup: " + levelId);

        final MapObject robotObject = playersLayer.getObjects().get("robot");
        if (robotObject instanceof RectangleMapObject) {
            final Rectangle rect = ((RectangleMapObject) robotObject).getRectangle();
            robot = new Robot(box2dWorld, rect.x + 148 * 0.5f, rect.y + 148 * 0.5f);
            robot.toggleActive();
            addLight(robot.getLight());
            entities.add(robot);
        }

        final MapObject assistantObject = playersLayer.getObjects().get("assistant");
        if (assistantObject instanceof RectangleMapObject) {
            final Rectangle rect = ((RectangleMapObject) assistantObject).getRectangle();
            assistant = new Assistant(box2dWorld, robot, rect.x - 80 * 0.5f, rect.y - 80 * 0.5f);

            final MapProperties props = assistantObject.getProperties();
            final String state = props.get("state", String.class);
            if (state != null) {
                assistant.setState(Assistant.State.valueOf(state.toUpperCase()));
            }


            addLight(assistant.getLight());
            entities.add(assistant);
        }
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
    public void resize(int width, int height) {
        super.resize(width, height);
        box2DViewport.update((int) Cfg.toMeters(width), (int) Cfg.toMeters(height), true);
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

        for (Entity e : entities) {
            e.update(delta);
        }

        EventQueue.dispatch(entities);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switchRobots();
        }

        updateCamera(delta);

        hints.update(delta);
    }

    private void switchRobots() {
        if (assistant != null) {
            robot.toggleActive();
            assistant.toggleActive();
        }
    }

    private void updateCamera(float dt) {
        float robotX;
        float robotY;

        if (robot == null && assistant == null) return;

        if (robot.isActive() || assistant == null) {
            robotX = Cfg.toPixels(robot.x);
            robotY = Cfg.toPixels(robot.y);
        } else {
            robotX = Cfg.toPixels(assistant.x);
            robotY = Cfg.toPixels(assistant.y);
        }

        if (Cfg.FREE_CAMERA) {
            temp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            getCamera().unproject(temp);

            robotX = temp.x;
            robotY = temp.y;
        }

        getCamera().position.x += (robotX - getCamera().position.x) * CAMERA_SPEED * dt;
        getCamera().position.y += (robotY - getCamera().position.y) * CAMERA_SPEED * dt;

        getCamera().position.x = MathUtils.round(getCamera().position.x);
        getCamera().position.y = MathUtils.round(getCamera().position.y);

        if (tileMapBounds.width < getCamera().viewportWidth) {
            getCamera().position.x = getCamera().position.x + getCamera().viewportWidth * 0.5f;
        } else if ((getCamera().position.x - getCamera().viewportWidth * 0.5f) <= 0) {
            getCamera().position.x = getCamera().viewportWidth * 0.5f;
        } else if ((getCamera().position.x + getCamera().viewportWidth * 0.5f) > tileMapBounds.width) {
            getCamera().position.x = tileMapBounds.width - getCamera().viewportWidth * 0.5f;
        }

        if (tileMapBounds.height < getCamera().viewportHeight) {
            getCamera().position.y = tileMapBounds.height * 0.5f;
        } else if ((getCamera().position.y - getCamera().viewportHeight * 0.5f) <= 0) {
            getCamera().position.y = getCamera().viewportHeight * 0.5f;
        } else if ((getCamera().position.y + getCamera().viewportHeight * 0.5f) >= tileMapBounds.height) {
            getCamera().position.y = tileMapBounds.height - getCamera().viewportHeight * 0.5f;
        }

        getBox2DCamera().position.set(Cfg.toMeters(getCamera().position.x), Cfg.toMeters(getCamera().position.y), 0f);

        getCamera().update();
        getBox2DCamera().update();
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
        batch.setProjectionMatrix(getBox2DCamera().combined);

        for (Entity e : entities) {
            e.draw(batch, getBox2DCamera());
        }

        batch.end();
    }

    @Override
    public void postDraw(SpriteBatch batch, OrthographicCamera camera) {
        super.postDraw(batch, camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity e : entities) {
            e.postDraw(batch, camera);
        }

//        Assets.font.draw(batch, "level: " + levelId, camera.position.x - 800 / 2 + 25, camera.position.y + 200);
//        Assets.font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), camera.position.x - 800/2+ 25, camera.position.y + 230);
        hints.draw(batch, camera.position.x, camera.position.y);
        batch.end();


        if (Cfg.BOX2D_DEBUG) {
            box2DDebugRenderer.render(box2dWorld, getBox2DCamera().combined);
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


    public OrthographicCamera getBox2DCamera() {
        return (OrthographicCamera) box2DViewport.getCamera();
    }
}
