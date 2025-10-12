package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.Game;
import java.util.Random;

public class LoadingScreen implements Screen {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private final Game game;

    private float progress = 0;
    private String mensaje;
    private Screen siguientePantalla;
    private Texture pixel;

    private final String[] mensajes = {
        "Inyectando al sujeto de prueba...."
    };

    public LoadingScreen(Game game, Screen siguientePantalla) {
        this.game = game;
        this.siguientePantalla = siguientePantalla;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        // Crear textura de 1x1 píxel para la barra
        pixel = crearPixel();

        // Escoger mensaje aleatorio
        mensaje = mensajes[new Random().nextInt(mensajes.length)];

        // Simular carga progresiva
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                progress += 0.2f;
                if (progress >= 6f) {
                    game.setScreen(siguientePantalla);
                } else {
                    this.run();
                }
            }
        }, 3f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Texto principal
        font.getData().setScale(2f);
        font.draw(batch, "CARGANDO...", 640, 400, 0, Align.center, false);

        // Mensaje aleatorio
        font.getData().setScale(1.5f);
        font.draw(batch, mensaje, 640, 300, 0, Align.center, false);

        // Barra de progreso
        float ancho = 600 * progress;
        batch.setColor(Color.CYAN);
        batch.draw(pixel, 340, 200, ancho, 20);
        batch.setColor(Color.WHITE);

        batch.end();
    }

    private Texture crearPixel() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixel.dispose();
    }
}
