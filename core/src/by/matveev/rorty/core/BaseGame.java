package by.matveev.rorty.core;

import by.matveev.rorty.core.Screens;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BaseGame extends ApplicationAdapter {
    public BaseGame() {
    }

    @Override
    public void pause() {
        Screens.current().pause();
    }

    @Override
    public void resume() {
        Screens.current().resume();
    }

    @Override
    public void render() {
        Screens.current().render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(final int width, final int height) {
        Screens.current().resize(width, height);
    }
}
