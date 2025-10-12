package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.monkey.com.Main.Main;
import com.monkey.com.Main.Menus.GameProgress;
import com.monkey.com.Main.Menus.MenuScreen;
import com.monkey.com.Main.Objetos.Prisionero;

import java.util.ArrayList;

public class Lvl5 implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private final float CamWidth = 800;
    private final float CamHeight = 400;
    private final float CamMargX = 100;
    private final float CamMargY = 100;

    private Prisionero prisionero;
    private float timerWin = 0;

    private Level level;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private ArrayList<Rectangle> colisiones;
    private ArrayList<RectangleMapObject> revisarwin;

    private BitmapFont font;
    private ShapeRenderer shapes;

    // Sistema de endings
    private boolean enZonaDecision = false;
    private boolean decisionActiva = false;
    private boolean endingMostrado = false;
    private int opcionSeleccionada = 0; // 1 = salvar monos, 2 = salir solo
    private float timerEnding = 0;

    private VoiceManager voice;
    private boolean audioIniciado = false;

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

       
        level = new Level("Mapa/Mapa1/Lvl5.tmx");
        renderer = level.getRenderer();
        map = level.getMap();

        colisiones = level.getColisiones();
        revisarwin = level.getWINCHECK();

        
        prisionero = new Prisionero("Humano/Humano.png", 64, 64, 200, colisiones);

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        shapes = new ShapeRenderer();
        prisionero.cambiarEstado(true);

        
        voice = VoiceManager.getInstance();
        voice.reproducirNarracion("lvl5_intro.mp3", false);
        audioIniciado = true;
        
        
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
        if (endingMostrado) {
            mostrarPantallaEnding(delta);
            return;
        }

        
        if (timerWin <= 0.5f) {
            timerWin += delta;
        }

        
        prisionero.update(delta);

        
        camera.position.x = prisionero.getX() + CamMargX;
        camera.position.y = prisionero.getY() + CamMargY;
        camera.update();

        
        renderJuego();

        
        if (timerWin >= 0.5f) {
            timerWin = 0;
            revisarZonaDecision();
        }

        
        if (decisionActiva) {
            mostrarUIDecision();
        }

        
    }

    private void renderJuego() {
        renderer.setView(camera);

       
        int bgIndex = map.getLayers().getIndex("BG");
        int pisoIndex = map.getLayers().getIndex("Piso");
        int decorIndex = map.getLayers().getIndex("Decor");

        if (bgIndex != -1) {
            renderer.render(new int[]{bgIndex});
        }
        if (pisoIndex != -1) {
            renderer.render(new int[]{pisoIndex});
        }
        if (decorIndex != -1) {
            renderer.render(new int[]{decorIndex});
        }

       
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        prisionero.render(batch);
        batch.end();

        
        int foregroundIndex = map.getLayers().getIndex("Foreground");
        if (foregroundIndex != -1) {
            renderer.render(new int[]{foregroundIndex});
        }
    }

    private void revisarZonaDecision() {
        for (RectangleMapObject revisarObj : revisarwin) {
            Rectangle r = revisarObj.getRectangle();

            
            if (prisionero.getHitbox().overlaps(r)) {
                if (!enZonaDecision) {
                    enZonaDecision = true;
                    activarDecision();
                    prisionero.cambiarEstado(false);
                }
                return;
            }
        }
        enZonaDecision = false;
    }

    private void activarDecision() {
        decisionActiva = true;
        
    }

    private void mostrarUIDecision() {
        batch.begin();

        font.setColor(Color.WHITE);

        float screenCenterX = camera.position.x - 150;
        float screenCenterY = camera.position.y + 100;

        font.draw(batch, "=== DECISIÓN FINAL ===", screenCenterX, screenCenterY);
        font.draw(batch, "Presiona 1: SALVAR a los monos", screenCenterX, screenCenterY - 50);
        font.draw(batch, "Presiona 2: SALIR solo", screenCenterX, screenCenterY - 80);

        batch.end();
        
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            opcionSeleccionada = 1;
            activarEnding("des1.mp3");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            opcionSeleccionada = 2;
            activarEnding("des2.mp3");
        }
    }

    private void activarEnding(String audio) {
        endingMostrado = true;
        decisionActiva = false;
        timerEnding = 0;
        voice.detenerNarracion();
        voice.reproducirNarracion(audio, false);
        
    }

   private void mostrarPantallaEnding(float delta) {
        timerEnding += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
        OrthographicCamera cameraHUD = new OrthographicCamera();
        cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(cameraHUD.combined);

        batch.begin();
        font.setColor(Color.WHITE);

        
        float startX = 50;
        float startY = Gdx.graphics.getHeight() - 50;

        if (opcionSeleccionada == 1) {
            font.draw(batch,
                    "FINAL: EVOLUCION COMPARTIDA\n\n"
                    + "Jack libero a los monos, y juntos escaparon del laboratorio.\n"
                    + "El equipo de Project Monkey registro un resultado inesperado:\n"
                    + "cooperacion entre especies.\n"
                    + "El mono principal mostro empatia, estrategia y lenguaje emergente.\n"
                    + "Por primera vez… el experimento demostro que la evolucion puede ser compartida.\n"
                    + "Y Project Monkey… fue abandonado.",
                    startX, startY);
        } else if (opcionSeleccionada == 2) {
            font.draw(batch,
                    "FINAL: SUPERVIVENCIA INDIVIDUAL\n\n"
                    + "Jack huyo y cerro las puertas tras de si.\n"
                    + "Los monos quedaron atrapados… pero no indefensos.\n"
                    + "En las grabaciones finales, uno de ellos - el sujeto 07-observo las camaras.\n"
                    + "Durante horas.\nHasta que sonrio.\n"
                    + "El laboratorio fue encontrado vacio dias despues.\n"
                    + "Project Monkey… continua.",
                    startX, startY);
        }

        batch.end();

        
        if (timerEnding > 12f) {
            GameProgress.resetearProgreso();
            ((Main) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
        }
    }

    private void renderHitboxes() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        prisionero.dispose();
        font.dispose();
        shapes.dispose();
        level.dispose();
        if (voice != null) {
            voice.dispose();
        }
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
}
