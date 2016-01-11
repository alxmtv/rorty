package by.matveev.rorty;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class TiledMapUtils {

    public TiledMapUtils() {
    }

    public static Rectangle obtainBounds(Map map) {
        final MapProperties prop = map.getProperties();
        return new Rectangle(0f, 0f,
                prop.get("width", Integer.class) * prop.get("tilewidth", Integer.class),
                prop.get("height", Integer.class) * prop.get("tileheight", Integer.class));
    }

}
