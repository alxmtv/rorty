package by.matveev.rorty.client;

import by.matveev.rorty.Cfg;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import by.matveev.rorty.Rorty;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Cfg.WIDTH, Cfg.HEIGHT);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new Rorty();
        }
}