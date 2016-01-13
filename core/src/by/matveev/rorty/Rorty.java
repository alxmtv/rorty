package by.matveev.rorty;

import by.matveev.rorty.core.BaseGame;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.screens.*;

public class Rorty extends BaseGame {

    @Override
    public void create() {
        Prefs.init();

        Screens.set(new GameScreen(Prefs.getString(Prefs.KEY_LEVEL, "6")));
    }

    public static void replaceLevel(String levelId) {
        Screens.replace(new GameScreen(levelId));
    }
}
