package by.matveev.rorty.screens;

import by.matveev.rorty.Assets;
import by.matveev.rorty.Cfg;
import by.matveev.rorty.core.AbstractScreen;
import by.matveev.rorty.core.Screens;
import by.matveev.rorty.entities.Terminal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TerminalScreen extends AbstractScreen {

    private final Terminal.Type type;
    private float stateTime;
    private boolean visible = true;

    public TerminalScreen(Terminal.Type type) {
        this.type = type;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        if (stateTime > 0.5f) {
            stateTime = 0;
            visible = !visible;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Screens.pop();
        }
    }

    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        // do nothing
    }

    @Override
    public void postDraw(SpriteBatch batch, OrthographicCamera camera) {
        super.postDraw(batch, camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (visible) {
            switch (type) {
                case GOAL:
                    batch.draw(Assets.ENV, (Cfg.WIDTH - 256f) * 0.5f, (Cfg.HEIGHT - 96f) * 0.5f,
                            256, 96, 672, 0, 256, 96, false, false);
                    break;
                case COMPLETE:
                    batch.draw(Assets.ENV, (Cfg.WIDTH - 256f) * 0.5f, (Cfg.HEIGHT - 96f) * 0.5f,
                            256, 96, 672, 96, 256, 96, false, false);
                    break;
            }
        }
        Assets.font.getColor().a = 0.6f;
        Assets.font.draw(batch, "press 'SPACE' to back", 480 * 0.6f, 100);
        Assets.font.getColor().a = 1f;
        batch.end();
    }
}
