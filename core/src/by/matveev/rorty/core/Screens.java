package by.matveev.rorty.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public final class Screens {

    private static final Screen NULL = new Screen() {
        @Override
        public void show() {

        }

        @Override
        public void render(float delta) {

        }

        @Override
        public void resize(int width, int height) {

        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }

        @Override
        public void hide() {

        }

        @Override
        public void dispose() {

        }
    };

    private static final Stack<Screen> stack = new Stack<Screen>();

    private Screens() {
    }

    public static void set(Screen screen) {
        while (hasScreens()) {
            final Screen old = stack.pop();
            old.hide();
            old.dispose();
        }
        push(screen);
    }


    public static void replace(AbstractScreen screen) {
        if (hasScreens()) {
            final Screen oldScreen = stack.pop();
            oldScreen.hide();
            oldScreen.dispose();

            stack.add(0, screen);
            show(screen, true);
        }
    }

    public static void push(Screen screen) {
        if (hasScreens()) {
            final Screen old = stack.peek();
            old.hide();
        }
        stack.push(screen);

        show(screen, true);
    }

    public static void pop() {
        if (hasScreens()) {
            final Screen oldScreen = stack.pop();
            oldScreen.hide();
            oldScreen.dispose();

            if (hasScreens()) {
                show(stack.peek(), false);
            }
        }
    }

    public static void dispose() {
        while (hasScreens()) {
            stack.pop().dispose();
        }
    }

    public static Screen current() {
        if (hasScreens()) {
            return stack.peek();
        }
        return NULL;
    }

    private static boolean hasScreens() {
        return !stack.isEmpty();
    }

    private static void show(Screen screen, boolean first) {
        if (first) {
            screen.show();
            screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

}
