package by.matveev.rorty.desktop;

import by.matveev.rorty.Cfg;
import by.matveev.rorty.editor.EditorApp;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class EditorLauncher {
    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Cfg.WIDTH;
        config.height = Cfg.HEIGHT;
        config.resizable = false;
        new LwjglApplication(new EditorApp(), config);
    }
}
