package by.matveev.rorty.screens;

import by.matveev.rorty.*;
import by.matveev.rorty.core.AbstractScreen;
import by.matveev.rorty.core.Light;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.entities.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.List;


public class MenuScreen extends GameScreen {

    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private List<Entity> entities;
    private OrthographicCamera entityCamera;
    private Music introMusic;
    private Text text;

    public MenuScreen() {
        super("menu");
        text = new BlinkText("press 'space' to start");
        text.setVisible(true);
    }

    @Override
    public void show() {
        super.show();
        introMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro.wav"));
        introMusic.setVolume(0.5f);
        introMusic.setLooping(true);
        introMusic.play();
    }

    @Override
    public void hide() {
        introMusic.stop();
        introMusic.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Rorty.replaceLevel("start");
        }
    }

    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        super.draw(batch, camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(Assets.ENV,
                (Cfg.WIDTH - 256) * 0.5f, 200,
                256,77,
                288, 352,
                256, 77,
                false, false);
        text.draw(batch, Cfg.WIDTH * 0.5f, 100);
        batch.end();
    }
}
