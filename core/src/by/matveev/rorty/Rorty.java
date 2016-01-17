package by.matveev.rorty;

import by.matveev.rorty.core.BaseGame;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.screens.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;

public class Rorty extends BaseGame {

    private Music introMusic;
    private static String currentLevelId;

    @Override
    public void create() {
        Prefs.init();

        introMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro.wav"));
        introMusic.setVolume(0.5f);
        introMusic.setLooping(true);
        introMusic.play();

        Screens.set(new MenuScreen());

    }

    @Override
    public void dispose() {
        super.dispose();
        introMusic.stop();
        introMusic.dispose();
    }

    @Override
    public void render() {
        super.render();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            replaceLevel(currentLevelId);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(final String text) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            replaceLevel(text);
                        }
                    });

                }

                @Override
                public void canceled() {

                }
            }, "Level ID", "", "");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            Cfg.BOX2D_DEBUG = !Cfg.BOX2D_DEBUG;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Cfg.FREE_CAMERA = !Cfg.FREE_CAMERA;
        }

    }

    public static void replaceLevel(String levelId) {
        currentLevelId = levelId;
        Screens.replace(new GameScreen(levelId));
    }
}
