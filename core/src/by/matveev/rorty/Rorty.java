package by.matveev.rorty;

import by.matveev.rorty.core.BaseGame;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.screens.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Rorty extends BaseGame {

    private static String currentLevelId;

    @Override
    public void create() {
        Prefs.init();

        Assets.platBackgroundMusic();

        Screens.set(new MenuScreen());

    }

    public static void restartCurrentLevel() {
        replaceLevel(currentLevelId);
    }

    @Override
    public void render() {
        super.render();


        // checkDebugTools();
    }

    private void checkDebugTools() {
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
