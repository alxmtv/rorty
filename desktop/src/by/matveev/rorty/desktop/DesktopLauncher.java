package by.matveev.rorty.desktop;

import by.matveev.rorty.Cfg;
import by.matveev.rorty.Rorty;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

    public static void main(String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Cfg.WIDTH;
        config.height = Cfg.HEIGHT;
        config.resizable = false;
        new LwjglApplication(new Rorty(), config);
    }
}
