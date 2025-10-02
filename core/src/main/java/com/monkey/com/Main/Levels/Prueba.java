package com.monkey.com.Main.Levels;

import com.monkey.com.Main.Objetos.Mono;
import com.monkey.com.Main.Objetos.Prisionero;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Prueba implements Screen {

    //Voy a llamar los cosos de spritebatch y eso
    private SpriteBatch batch;
    private Prisionero prisionero;
    private Mono mono;
    private boolean activo = false; // T = mono // F = Prisionero
    private float timer = 0;

    //Camara para juego
    private OrthographicCamera camera;
    private final float CamWidth = 800;
    private final float CamHeight = 400;
    private final float CamMargX = 100;//Margen H
    private final float CamMargY = 100;

    //El shaperenderer
    ShapeRenderer shapes;

    // Level manager (nuevo)
    private Level level;
    private TiledMap map;
    private ArrayList<Rectangle> colisiones = new ArrayList<>();
    private ArrayList<RectangleMapObject> palancas = new ArrayList<>();
    private ArrayList<MapObject> paredes = new ArrayList<>();
    private ArrayList<RectangleMapObject> ropes = new ArrayList<>();
    private com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer renderer;

    @Override
    public void show() {
        // Cargar Level 
        level = new Level("Mapa/Mapa1/Mapa1.tmx");
        renderer = level.getRenderer();
        map = level.getMap();

        colisiones = level.getColisiones();
        palancas = level.getPalancas();
        paredes = level.getParedes();
        ropes = level.getRopes();

        // Crear batch y personajes
        batch = new SpriteBatch();
        prisionero = new Prisionero("Humano/Humano.png", 350, 100, 200, colisiones);
        mono = new Mono("Humano/Humano.png", 300, 100, 200, colisiones);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

        // ShapeRenderer para debug
        shapes = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);

        // Timer para cooldown de cambio de personaje
        if (timer <= 1.5f) {
            timer += delta;
        }

        // Cambio de personaje con TAB
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            switchController();
        }

        prisionero.update(delta);

        float targetX = activo ? mono.getX() : prisionero.getX();
        float targetY = activo ? mono.getY() : prisionero.getY();
        camera.position.x = targetX + CamMargX;
        camera.position.y = targetY + CamMargY;
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        prisionero.render(batch);
        mono.render(batch);
        batch.end();

        MapLayer collLayer = map.getLayers().get("Coll");
        TiledMapTileLayer doorLayer = null;
        if (map.getLayers().get("Doors") instanceof TiledMapTileLayer) {
            doorLayer = (TiledMapTileLayer) map.getLayers().get("Doors");
        }

        int tileWidth = 32;
        int tileHeight = 32;

        for (RectangleMapObject palancaObj : palancas) {
            Rectangle palancaRect = palancaObj.getRectangle();

            if (mono.getHitbox().overlaps(palancaRect)) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && activo) {//!Activo significa que ese el mono
                    String target = palancaObj.getProperties().get("target", String.class);

                    MapObject paredABorrar = null;
                    for (MapObject pared : paredes) {
                        String id = pared.getProperties().get("id", String.class);
                        if (id != null && id.equals(target)) {
                            paredABorrar = pared;
                            break;
                        }
                    }

                    if (paredABorrar != null && collLayer != null) {
                        // Quitar de la capa de col
                        collLayer.getObjects().remove(paredABorrar);

                        // Quitar de colisiones activas
                        if (paredABorrar instanceof RectangleMapObject) {
                            Rectangle r = ((RectangleMapObject) paredABorrar).getRectangle();
                            colisiones.remove(r);

                            // Quitar todas las celdas visuales que ocupa la puerta (si existe doorLayer)
                            if (doorLayer != null) {
                                int startX = (int) (r.x / tileWidth);
                                int startY = (int) (r.y / tileHeight);
                                int endX = (int) ((r.x + r.width) / tileWidth);
                                int endY = (int) ((r.y + r.height) / tileHeight);

                                for (int x = startX; x < endX; x++) {
                                    for (int y = startY; y < endY; y++) {
                                        doorLayer.setCell(x, y, null);
                                    }
                                }
                            }
                        }

                        System.out.println("Palanca activada, puerta eliminada: " + target);
                    }
                }
            }
        }
        mono.setEnEscalera(false);
        for (RectangleMapObject ropesObj : ropes) {
            Rectangle ropesRect = ropesObj.getRectangle();
            if (mono.getHitbox().overlaps(ropesRect)) {
                mono.setEnEscalera(true);
            }
        }

        mono.update(delta);
        mono.subirEscalera(delta);

        // Dibujar hitbox para debug
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.RED);
        if (activo) {
            shapes.rect(mono.getHitbox().x, mono.getHitbox().y,
                    mono.getHitbox().getWidth(), mono.getHitbox().getHeight());
        } else {
            shapes.rect(prisionero.getHitbox().x, prisionero.getHitbox().y,
                    prisionero.getHitbox().getWidth(), prisionero.getHitbox().getHeight());
        }
        shapes.end();
    }

    private void switchController() {
        if (!activo && timer >= 1) {
            mono.cambiarEstado(true);
            prisionero.cambiarEstado(false);
            activo = !activo;
            timer = 0;
            Gdx.input.setOnscreenKeyboardVisible(false);
            System.out.println("Mono caminando");
        } else if (activo && timer >= 1) {
            prisionero.cambiarEstado(true);
            mono.cambiarEstado(false);
            activo = !activo;
            timer = 0;
            System.out.println("Prisionero caminando");
            Gdx.input.setOnscreenKeyboardVisible(false);
        }
    }

    @Override
    public void resize(int i, int i1) {
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
        batch.dispose();
        prisionero.dispose();
        mono.dispose();
        shapes.dispose();
        if (level != null) {
            level.dispose();
        }
    }
}
