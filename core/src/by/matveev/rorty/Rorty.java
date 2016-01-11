package by.matveev.rorty;

import by.matveev.rorty.core.BaseGame;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.screens.*;

public class Rorty extends BaseGame {

    @Override
    public void create() {
        Screens.set(new GameScreen());
    }
}
