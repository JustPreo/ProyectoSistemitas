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

public class Lvl1 implements Screen {

    private Screen screenAnterior;

    private SpriteBatch batch;
    private Prisionero prisionero;
    private Mono mono;
    private boolean activo = false;
    private float timer = 0;
    private float timerWin = 0;

    private OrthographicCamera camera;
    private final float CamWidth = 800;
    private final float CamHeight = 400;
    private final float CamMargX = 100;
    private final float CamMargY = 100;

    private boolean enMiniGame = false;
    private CableMiniGame cableMiniGame;

    private ShapeRenderer shapes;

    private Level level;
    private TiledMap map;
    private ArrayList<Rectangle> colisiones = new ArrayList<>();
    private ArrayList<RectangleMapObject> palancas = new ArrayList<>();
    private ArrayList<MapObject> paredes = new ArrayList<>();
    private ArrayList<RectangleMapObject> ropes = new ArrayList<>();
    private ArrayList<RectangleMapObject> electricidad = new ArrayList<>();
    private ArrayList<RectangleMapObject> revisarwin = new ArrayList<>();
    private com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer renderer;

    @Override
    public void show() {
        level = new Level("Mapa/Mapa1/Lvl1.tmx"); // mapa Lvl1
        renderer = level.getRenderer();
        map = level.getMap();

        colisiones = level.getColisiones();
        palancas = level.getPalancas();
        paredes = level.getParedes();
        ropes = level.getRopes();
        electricidad = level.getElectricidad();
        revisarwin = level.getWINCHECK();

        batch = new SpriteBatch();
        prisionero = new Prisionero("Humano/Humano.png", 350, 64, 200, colisiones);
        mono = new Mono("Humano/Humano.png", 300, 64, 200, colisiones);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

        shapes = new ShapeRenderer();

    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);
        if (enMiniGame && cableMiniGame != null) {
            cableMiniGame.render(delta); // Solo renderiza el minijuego
            return; // Pausa todo lo demás del Level
        }

        if (timer <= 1.5f) {
            timer += delta;
        }
        if (timerWin <= 0.5f) {
            timerWin += delta;
        }

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

        // indices de capas
        int bgIndex = map.getLayers().getIndex("BG");
        int pisoIndex = map.getLayers().getIndex("Piso");
        int decorIndex = map.getLayers().getIndex("Decor");
        int doorsIndex = map.getLayers().getIndex("Doors");
        int ropesIndex = map.getLayers().getIndex("Ropes");
        int electricidadIndex = map.getLayers().getIndex("Electricidad");

        renderer.render(new int[]{bgIndex, pisoIndex, doorsIndex, ropesIndex, electricidadIndex, decorIndex});

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        prisionero.render(batch);
        mono.render(batch);
        batch.end();

        int foregroundIndex = map.getLayers().getIndex("Foreground");
        if (foregroundIndex != -1) {
            renderer.render(new int[]{foregroundIndex});
        }

        handlePalancas();
        handleRopes(delta);
        handleHitboxes();
        handleElectricidad();
        if (timerWin >= 0.5f && !level.getWin()) {
            timerWin = 0;
            revisarWin();
        }
    }

    private void switchController() {
        if (!activo && timer >= 1) {
            mono.cambiarEstado(true);
            prisionero.cambiarEstado(false);
            activo = true;
            timer = 0;
        } else if (activo && timer >= 1) {
            prisionero.cambiarEstado(true);
            mono.cambiarEstado(false);
            activo = false;
            timer = 0;
        }
    }

    private void handlePalancas() {
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
                System.out.println("PALANCA");
                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && activo) {
                    String target = palancaObj.getProperties().get("target", String.class);
                    MapObject paredABorrar = null;
                    for (MapObject pared : paredes) {
                        String id = pared.getProperties().get("id", String.class);
                        if (id != null && id.equals(target)) {
                            paredABorrar = pared;
                            break;
                        }
                    }

                    if (!enMiniGame) {
                        enMiniGame = true;
                        screenAnterior = this; // Guardamos la pantalla actual
                        cableMiniGame = new com.monkey.com.Main.Levels.CableMiniGame(() -> {
                            enMiniGame = false;
                            cableMiniGame.dispose();
                            cableMiniGame = null;
                            Gdx.input.setInputProcessor(null);
                            System.out.println("A");
                        });
                        Gdx.input.setInputProcessor(cableMiniGame.new CableInput());
                    }

                    if (paredABorrar != null && collLayer != null) {
                        collLayer.getObjects().remove(paredABorrar);
                        if (paredABorrar instanceof RectangleMapObject) {
                            Rectangle r = ((RectangleMapObject) paredABorrar).getRectangle();
                            colisiones.remove(r);

                            if (doorLayer != null) {
                                int startX = (int) (r.x / tileWidth);
                                int startY = (int) (r.y / tileHeight);
                                int endX = (int) ((r.x + r.width) / tileWidth);
                                int endY = (int) ((r.y + r.height) / tileHeight);

                                for (int x = startX; x < endX; x++) {
                                    for (int y = startY; y < endY; y++) {
                                        System.out.println("doorLayer");
                                        doorLayer.setCell(x, y, null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleRopes(float delta) {
        mono.setEnEscalera(false);
        for (RectangleMapObject ropesObj : ropes) {
            if (mono.getHitbox().overlaps(ropesObj.getRectangle())) {
                mono.setEnEscalera(true);
            }
        }
        mono.update(delta);
        mono.subirEscalera(delta);
    }

    private void handleHitboxes() {
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);

        // Dibuja hitbox del jugador
        shapes.setColor(Color.RED);
        if (activo) {
            shapes.rect(mono.getHitbox().x, mono.getHitbox().y, mono.getHitbox().width, mono.getHitbox().height);
        } else {
            shapes.rect(prisionero.getHitbox().x, prisionero.getHitbox().y, prisionero.getHitbox().width, prisionero.getHitbox().height);
        }

        // Dibuja puertas
        shapes.setColor(Color.BLUE);
        for (Rectangle r : colisiones) {
            shapes.rect(r.x, r.y, r.width, r.height);
        }

        shapes.end();
    }

    private void handleElectricidad() {
        for (RectangleMapObject eObj : electricidad) {
            Rectangle eRect = eObj.getRectangle();
            if ((mono.getHitbox().overlaps(eRect) || prisionero.getHitbox().overlaps(eRect)) && !enMiniGame) {
                /*enMiniGame = true;
                screenAnterior = this; // Guardamos la pantalla actual
                cableMiniGame = new com.monkey.com.Main.Levels.CableMiniGame(() -> {
                    enMiniGame = false;
                    cableMiniGame.dispose();
                    cableMiniGame = null;
                    Gdx.input.setInputProcessor(null);
                    System.out.println("A");
                });
                Gdx.input.setInputProcessor(cableMiniGame.new CableInput());*/
            }
        }
    }

    private void revisarWin() {
        for (RectangleMapObject revisarObj : revisarwin) {
            Rectangle r = revisarObj.getRectangle();
            if (prisionero.getHitbox().overlaps(r) && mono.getHitbox().overlaps(r) && !level.getWin()) {
                System.out.println("WIN");
                level.setWin(true);
            }
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
