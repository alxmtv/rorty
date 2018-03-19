package by.matveev.rorty.desktop;

import by.matveev.rorty.Cfg;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TileEditor extends ApplicationAdapter {
    private Texture tileset;
    private SpriteBatch batch;
    private TileMap tileMap;
    private Viewport viewport;

    @Override
    public void create() {
        tileset = new Texture(Gdx.files.local("tileset.png"));
        batch = new SpriteBatch();
        tileMap = new TileMap(Cfg.WIDTH / 64, Cfg.HEIGHT / 64);
        viewport = new FitViewport(Cfg.WIDTH, Cfg.HEIGHT);

        Gdx.input.setInputProcessor(new InputAdapter() {
            private Vector3 tmp = new Vector3();

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return handleEvent(screenX, screenY);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return handleEvent(screenX, screenY);
            }

            private boolean handleEvent(int screenX, int screenY) {
                tmp.set(screenX, screenY, 0f);
                viewport.unproject(tmp);

                int x = (int) (tmp.x / 64);
                int y = (int) (tmp.y / 64);

                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    tileMap.setTile(x, y, 0);
                } else {
                    tileMap.setTile(x, y, 1);
                }
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (int x = 0; x < tileMap.getWidth(); x++) {
            for (int y = 0; y < tileMap.getHeight(); y++) {
                final int tile = tileMap.getTile(x, y);
                if (tile > 0) {
                    int north = tileMap.getTile(x, y + 1);
                    int west = tileMap.getTile(x - 1, y);
                    int east = tileMap.getTile(x + 1, y);
                    int south = tileMap.getTile(x, y - 1);

                    int tileIndex = north + 2 * west + 4 * east + 8 * south;

                    int tileX = tileIndex % 4;
                    int tileY = tileIndex / 4;

                    batch.draw(tileset,
                            // x,y
                            x * 64, y * 64,
                            // w,h
                            64, 64,
                            //ox,oy
                            tileX * 64, tileY * 64,
                            //ow,oh
                            64, 64,
                            false, false);
                }
            }
        }
        batch.end();


    }

    static class TileMap {
        private final int[][] tiles;

        TileMap(int width, int height) {
            this.tiles = new int[width][height];
        }

        void setTile(int x, int y, int tile) {
            if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
                return;
            }
            tiles[x][y] = tile;
        }

        int getTile(int x, int y) {
            if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
                return 0;
            }
            return tiles[x][y];
        }

        int getWidth() {
            return tiles.length;
        }

        int getHeight() {
            return tiles[0].length;
        }
    }

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Cfg.WIDTH;
        config.height = Cfg.HEIGHT;
        config.resizable = false;
        new LwjglApplication(new TileEditor(), config);
    }
}
