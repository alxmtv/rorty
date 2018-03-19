package by.matveev.rorty.screens;

import by.matveev.rorty.*;
import by.matveev.rorty.core.Callback;
import by.matveev.rorty.entities.Button;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class MenuScreen extends GameScreen {

    private Text text;
    private boolean buttonsAdded;

    public MenuScreen() {
        super("menu");
        text = new BlinkText("press 'space' to start");
        text.setVisible(true);
    }


    @Override
    public void show() {
        super.show();

        setupButtons();
    }

    protected void setupButtons() {
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
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
           Rorty.replaceLevel("1");
        }
    }

    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        super.draw(batch, camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(Assets.ENV,
                (Cfg.WIDTH - 256) * 0.5f - 8, 250,
                256, 77,
                288, 352,
                256, 77,
                false, false);
        text.draw(batch, Cfg.WIDTH * 0.5f - 16, 100);
        batch.end();
    }

    @Override
    public void hide() {
        super.hide();
        entities.clear();
    }
}
