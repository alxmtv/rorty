package by.matveev.rorty.editor;

import com.badlogic.gdx.Game;

public class EditorApp extends Game {

    @Override
    public void create() {
        setScreen(new EditorScreen());
    }
}
