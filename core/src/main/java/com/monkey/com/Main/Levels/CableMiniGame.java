/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;

public class CableMiniGame implements Screen {

    private OrthographicCamera camera;
    private ShapeRenderer shapes;
    private ArrayList<ColorCable> cables;
    private boolean completed = false;
    private Runnable onComplete;
    private Texture fondo;
    private SpriteBatch batch;

    public CableMiniGame(Runnable onComplete) {

        batch = new SpriteBatch();
        this.onComplete = onComplete;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        shapes = new ShapeRenderer();
        fondo = new Texture("Fondo/FONDOTOOLBOX.png");
        cables = new ArrayList<>();
        String[] colors = {"red", "blue", "yellow", "green"};
        for (int i = 0; i < colors.length; i++) {
            cables.add(new ColorCable(colors[i], new Vector2(150, 400 - i * 100), new Vector2(650, 400 - i * 100)));
        }
        Collections.shuffle(cables);
        Gdx.input.setInputProcessor(new CableInput());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.7f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        for (ColorCable c : cables) {
            // Cable
            switch (c.color) {
                case "red":
                    shapes.setColor(1, 0, 0, 1);
                    break;
                case "blue":
                    shapes.setColor(0, 0, 1, 1);
                    break;
                case "yellow":
                    shapes.setColor(1, 1, 0, 1);
                    break;
                case "green":
                    shapes.setColor(0, 1, 0, 1);
                    break;
            }
            shapes.rectLine(c.start, c.tempEnd, 6);

            // Conectores
            shapes.setColor(1, 1, 1, 1);
            shapes.circle(c.start.x, c.start.y, 10);
            shapes.circle(c.end.x, c.end.y, 10);
        }

        shapes.end();

        if (!completed && allConnected()) {
            completed = true;
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    private boolean allConnected() {
        for (ColorCable c : cables) {
            if (!c.isConnected()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
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

    public class CableInput extends InputAdapter {

        private ColorCable selected;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector2 touch = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
            for (ColorCable c : cables) {
                if (c.start.dst(touch) < 30) {
                    selected = c;
                }
            }
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (selected != null) {
                selected.tempEnd.set(screenX, Gdx.graphics.getHeight() - screenY);
            }
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (selected != null) {
                Vector2 release = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
                selected.tryConnect(release);
                selected = null;
            }
            return true;
        }
    }
}
