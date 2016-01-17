package by.matveev.rorty;

import by.matveev.rorty.core.Light;
import by.matveev.rorty.entities.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

import static by.matveev.rorty.utils.ColorUtils.colorFrom;

public class WorldBuilder {

    private final World world;
    private final Map map;

    public WorldBuilder(World world, Map map) {
        this.world = world;
        this.map = map;
    }


    public List<Entity> build() {
        final List<Entity> entities = new ArrayList<>();

        buildObstacles();
        buildDoors(entities);
        buildSwitches(entities);
        buildBoxes(entities);
        buildElevators(entities);
        buildTriggers(entities);
        buildFans(entities);
        buildTerminals(entities);
        buildGates(entities);
        buildSensors(entities);

        return entities;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / Cfg.PIXEL_TO_METER_RATIO,
                (rectangle.y + rectangle.height * 0.5f) / Cfg.PIXEL_TO_METER_RATIO);
        polygon.setAsBox(rectangle.width * 0.5f / Cfg.PIXEL_TO_METER_RATIO,
                rectangle.height * 0.5f / Cfg.PIXEL_TO_METER_RATIO,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / Cfg.PIXEL_TO_METER_RATIO);
        circleShape.setPosition(new Vector2(circle.x / Cfg.PIXEL_TO_METER_RATIO, circle.y / Cfg.PIXEL_TO_METER_RATIO));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / Cfg.PIXEL_TO_METER_RATIO;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / Cfg.PIXEL_TO_METER_RATIO;
            worldVertices[i].y = vertices[i * 2 + 1] / Cfg.PIXEL_TO_METER_RATIO;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }


    private void buildSensors(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("sensors");
        if (mapLayer == null) return;

        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();


                final float w = Cfg.toMeters(64);
                final float h = Cfg.toMeters(32);

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());


                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.isSensor = true;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final String name = props.get("name", String.class);
                final String[] targets = props.get("target", String.class).split(",");
                entities.add(new Sensor(body, name, targets));

            }

        }
    }

    private void buildGates(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("gates");
        if (mapLayer == null) return;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();
                final MapProperties props = object.getProperties();


                final String orientation = props.get("orientation",String.class);
                if ("vertical".equals(orientation)) {
                    final VGate gate = new VGate(props.get("name", String.class),
                            world, rect.x, rect.y);
                    final String openProperty = props.get("open", String.class);
                    gate.setInitialState(openProperty != null && Boolean.parseBoolean(openProperty));
                    entities.add(gate);
                } else if ("horizontal".equals(orientation)) {
                    final HGate gate = new HGate(props.get("name", String.class),
                            world, rect.x, rect.y);
                    final String openProperty = props.get("open", String.class);
                    gate.setInitialState(openProperty != null && Boolean.parseBoolean(openProperty));
                    entities.add(gate);
                }




            }
        }
    }

    private void buildSwitches(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("switches");
        if (mapLayer == null) return;

        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();


                final float w = Cfg.toMeters(128);
                final float h = Cfg.toMeters(192);

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());


                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;
                def.position.set(x + hw, y);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.isSensor = true;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final String name = props.get("name", String.class);
                final String target = props.get("target", String.class);

                final String enabledProperty = props.get("enabled", String.class);
                entities.add(new Switch(body, name, target, enabledProperty != null && Boolean.parseBoolean(enabledProperty)));

            }

        }
    }

    private void buildFans(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("fans");
        if (mapLayer == null) return;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                Fan f = new Fan(rect.x, rect.y);
                entities.add(f);
            }
        }
    }

    private void buildTerminals(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("terminals");
        if (mapLayer == null) return;

        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());

                final float w = Cfg.toMeters(rect.width);
                final float h = Cfg.toMeters(rect.height);

                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.KinematicBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.isSensor = true;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final String id = props.get("id", String.class);
                entities.add(new Terminal(id, body));

            }

        }
    }

    private void buildObstacles() {
        MapLayer mapLayer = map.getLayers().get("obstacles");
        if (mapLayer == null) return;
        MapObjects objects = mapLayer.getObjects();


        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object);
            } else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);
            body.createFixture(shape, 1);

            shape.dispose();
        }
    }

    private void buildBoxes(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("boxes");
        if (mapLayer == null) return;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {

            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());

                final float w = Cfg.toMeters(110);
                final float h = Cfg.toMeters(110);

                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

//                final PolygonShape shape = new PolygonShape();
//                shape.setAsBox(hw, hh);

                final CircleShape shape = new CircleShape();
                shape.setRadius(w * 0.5f);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.DynamicBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final Box box = new Box(body, x, y, w, h);
                final String enabledProperty = props.get("enabled", String.class);
                box.setEnabled(enabledProperty != null && Boolean.parseBoolean(enabledProperty));
                entities.add(box);
            }

        }
    }


    private void buildDoors(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("doors");
        if (mapLayer == null) return;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {

            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());

                final float w = Cfg.toMeters(128);
                final float h = Cfg.toMeters(192);

                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.KinematicBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.isSensor = true;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final String levelId = props.get("levelId", String.class);
                if (levelId == null) {
                    throw new IllegalStateException();
                }
                final Door box = new Door(body, levelId, x, y, w, h);
                entities.add(box);
            }

        }
    }

    private void buildElevators(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("elevators");
        if (mapLayer == null) return;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());

                final float w = Cfg.toMeters(128);
                final float h = Cfg.toMeters(48);

                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.KinematicBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                body.createFixture(fix);

                shape.dispose();

                MapProperties props = object.getProperties();
                String direction = props.get("direction", String.class);
                String[] coord = direction.split(",");

                entities.add(new Elevator(props.get("name", String.class),
                        body, new Vector2(Float.parseFloat(coord[0]), Float.parseFloat(coord[1])),
                        Float.parseFloat(props.get("maxDistance", String.class))));

            }

        }
    }


    private void buildTriggers(List<Entity> entities) {
        final MapLayer mapLayer = map.getLayers().get("triggers");
        if (mapLayer == null) return;

        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) object).getRectangle();

                final float x = Cfg.toMeters(rect.getX());
                final float y = Cfg.toMeters(rect.getY());

                final float w = Cfg.toMeters(rect.width);
                final float h = Cfg.toMeters(rect.height);

                final float hw = w * 0.5f;
                final float hh = h * 0.5f;

                final PolygonShape shape = new PolygonShape();
                shape.setAsBox(hw, hh);

                final BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.KinematicBody;
                def.position.set(x + hw, y + hh);

                final Body body = world.createBody(def);

                final FixtureDef fix = new FixtureDef();
                fix.shape = shape;
                fix.isSensor = true;
                body.createFixture(fix);

                shape.dispose();

                final MapProperties props = object.getProperties();
                final String type = props.get("type", String.class);

                final Entity trigger;
//                switch (type) {
//                    case "elevator":
//                        trigger = new ElevatorTrigger("elevatorTrigger",
//                                body, props.get("elevatorId", String.class), w, h);
//                        break;
//                    default:
//                        throw new IllegalArgumentException();
//                }
//                entities.add(trigger);

            }

        }
    }

    public List<Light> buildLights() {
        final List<Light> lights = new ArrayList<>();
        final MapLayer mapLayer = map.getLayers().get("lights");
        if (mapLayer == null) return lights;
        final MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                final RectangleMapObject lightObject = (RectangleMapObject) object;
                final MapProperties lightProperties = lightObject.getProperties();

                final Color color = colorFrom(lightProperties.get("color", String.class));
                if (lightProperties.containsKey("alpha")) {
                    color.a = Float.parseFloat(lightProperties.get("alpha", String.class));
                }

                final Light.Type type = Light.Type.from(lightProperties.get("type", String.class));
                final float width = Float.parseFloat(lightProperties.get("width", String.class));
                final float height = Float.parseFloat(lightProperties.get("height", String.class));


                final Rectangle bounds = lightObject.getRectangle();
                final Light light = new Light(type, color);
                light.x =  bounds.x - width * 0.5f;
                light.y = bounds.y - height * 0.5f;
                light.width = width;
                light.height = height;
                light.name = object.getName();

                final String openProperty = lightProperties.get("disabled", String.class);
                light.disabled = openProperty != null && Boolean.parseBoolean(openProperty);


                lights.add(light);
            }

        }
        return lights;
    }
}
