package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import com.monkey.com.Main.Main;
import com.monkey.com.Main.Menus.MenuScreen;
import com.monkey.com.Main.Objetos.Mono;
import com.monkey.com.Main.Objetos.Prisionero;

import java.util.ArrayList;

public class Lvl2 implements Screen {

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
    private MinijuegoNumeros minijuegoNumeros;

    private ShapeRenderer shapes;

    private Level level;
    private TiledMap map;
    private ArrayList<Rectangle> colisiones;
    private ArrayList<RectangleMapObject> palancas;
    private ArrayList<MapObject> paredes;
    private ArrayList<RectangleMapObject> ropes;
    private ArrayList<RectangleMapObject> electricidad;
    private ArrayList<RectangleMapObject> revisarwin;
    private com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer renderer;

    // Menú
    private GameOverMenu gameOverMenu;
    private boolean juegoEnPausa = false;

    @Override
    public void show() {
        // Inicializar nivel
        level = new Level("Mapa/Mapa1/Lvl2.tmx");
        renderer = level.getRenderer();
        map = level.getMap();

        colisiones = level.getColisiones();
        palancas = level.getPalancas();
        paredes = level.getParedes();
        ropes = level.getRopes();
        electricidad = level.getElectricidad();
        revisarwin = level.getWINCHECK();

        batch = new SpriteBatch();
        prisionero = new Prisionero("Humano/Humano.png", 500, 64, 200, colisiones);
        mono = new Mono("Humano/Humano.png", 500, 64, 200, colisiones);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

        shapes = new ShapeRenderer();

        // Inicializar menú desde Level
        gameOverMenu = level.getGameOverMenu();

        // Input
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (gameOverMenu.estaMostrado()) {
                    return true; // Input es manejado por Stage del menú
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);

        // Si el juego está en pausa por menú
        if (juegoEnPausa) {
            renderJuego();
            gameOverMenu.render(delta);

            // Checar acción del menú
            GameOverMenu.MenuAction accion = gameOverMenu.getAccion();
            if (accion != GameOverMenu.MenuAction.NINGUNA) {
                manejarAccionMenu(accion);
                gameOverMenu.ocultar();
            }
            return;
        }

        // Mini-juegos
        if (enMiniGame) {
            if (cableMiniGame != null) cableMiniGame.render(delta);
            else if (minijuegoNumeros != null) minijuegoNumeros.render(delta);
            return;
        }

        // Timers
        if (timer <= 1.5f) timer += delta;
        if (timerWin <= 0.5f) timerWin += delta;

        // Cambiar personaje
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) switchController();

        prisionero.update(delta);

        // Cámara
        float targetX = activo ? mono.getX() : prisionero.getX();
        float targetY = activo ? mono.getY() : prisionero.getY();
        camera.position.x = targetX + CamMargX;
        camera.position.y = targetY + CamMargY;
        camera.update();

        renderJuego();

        handlePalancas();
        handleRopes(delta);
        handleHitboxes();
        handleElectricidad();
        handleToolbox();

        if (timerWin >= 0.5f && !level.getWin()) {
            timerWin = 0;
            revisarWin();
        }
    }

    private void renderJuego() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);

        // Renderizar capas principales
        int bgIndex = map.getLayers().getIndex("BG");
        int pisoIndex = map.getLayers().getIndex("Piso");
        int decorIndex = map.getLayers().getIndex("Decor");
        int doorsIndex = map.getLayers().getIndex("Doors");
        int ropesIndex = map.getLayers().getIndex("Ropes");
        int electricidadIndex = map.getLayers().getIndex("Electricidad");

        renderer.render(new int[]{bgIndex, pisoIndex, doorsIndex, ropesIndex, electricidadIndex, decorIndex});

        // Renderizar jugadores
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        prisionero.render(batch);
        mono.render(batch);
        batch.end();

        int foregroundIndex = map.getLayers().getIndex("Foreground");
        if (foregroundIndex != -1) renderer.render(new int[]{foregroundIndex});
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

    private void handleToolbox() {
        MapLayer electricidadCollLayer = map.getLayers().get("ElectricidadColl");
        TiledMapTileLayer electricidadLayer = null;
        if (map.getLayers().get("Electricidad") instanceof TiledMapTileLayer) {
            electricidadLayer = (TiledMapTileLayer) map.getLayers().get("Electricidad");
        }

        int tileWidth = 32;
        int tileHeight = 32;

        for (RectangleMapObject palancaObj : palancas) {
            Rectangle palancaRect = palancaObj.getRectangle();

            if (prisionero.getHitbox().overlaps(palancaRect) && Gdx.input.isKeyJustPressed(Input.Keys.E) && !activo) {
                String type = palancaObj.getProperties().get("type", String.class);
                String target = palancaObj.getProperties().get("target", String.class);

                if ("numeros".equals(type) && target != null) {
                    miniJuegoNumeros(target, electricidadCollLayer, electricidadLayer, tileWidth, tileHeight);
                }
            }
        }
    }

    private void miniJuegoNumeros(String target, MapLayer electricidadCollLayer, TiledMapTileLayer electricidadLayer, int tileWidth, int tileHeight) {
        if (!enMiniGame) {
            enMiniGame = true;
            minijuegoNumeros = new MinijuegoNumeros(() -> {
                enMiniGame = false;
                minijuegoNumeros.dispose();
                minijuegoNumeros = null;
                Gdx.input.setInputProcessor(null);
                System.out.println("Mini-juego num completado.");

                RectangleMapObject electricidadABorrar = null;
                if (electricidadCollLayer != null) {
                    for (MapObject obj : electricidadCollLayer.getObjects()) {
                        if (obj instanceof RectangleMapObject) {
                            String id = obj.getProperties().get("id", String.class);
                            if (id != null && id.equals(target)) {
                                electricidadABorrar = (RectangleMapObject) obj;
                                break;
                            }
                        }
                    }
                }

                if (electricidadABorrar != null) {
                    Rectangle r = electricidadABorrar.getRectangle();
                    colisiones.remove(r);
                    electricidad.remove(electricidadABorrar);

                    if (electricidadCollLayer != null) {
                        electricidadCollLayer.getObjects().remove(electricidadABorrar);
                    }

                    if (electricidadLayer != null) {
                        int startX = (int) Math.floor(r.x / tileWidth);
                        int startY = (int) Math.floor(r.y / tileHeight);
                        int endX = (int) Math.ceil((r.x + r.width) / tileWidth);
                        int endY = (int) Math.ceil((r.y + r.height) / tileHeight);

                        for (int x = startX; x < endX; x++) {
                            for (int y = startY; y < endY; y++) {
                                electricidadLayer.setCell(x, y, null);
                            }
                        }
                        System.out.println("Electricidad eliminada: " + target);
                    }
                }
            });
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

        shapes.setColor(Color.RED);
        if (activo) shapes.rect(mono.getHitbox().x, mono.getHitbox().y, mono.getHitbox().width, mono.getHitbox().height);
        else shapes.rect(prisionero.getHitbox().x, prisionero.getHitbox().y, prisionero.getHitbox().width, prisionero.getHitbox().height);

        shapes.setColor(Color.BLUE);
        for (Rectangle r : colisiones) shapes.rect(r.x, r.y, r.width, r.height);

        shapes.end();
    }

    private void handleElectricidad() {
        for (RectangleMapObject eObj : electricidad) {
            Rectangle eRect = eObj.getRectangle();
            if (mono.getHitbox().overlaps(eRect) || prisionero.getHitbox().overlaps(eRect)) {
                mostrarMenuFallo();
                return;
            }
        }
    }

    private void mostrarMenuFallo() {
        juegoEnPausa = true;
        gameOverMenu.mostrar(GameOverMenu.MenuType.NIVEL_FALLIDO);
    }

    private void revisarWin() {
        for (RectangleMapObject revisarObj : revisarwin) {
            Rectangle r = revisarObj.getRectangle();
            if (prisionero.getHitbox().overlaps(r) && mono.getHitbox().overlaps(r) && !level.getWin()) {
                level.setWin(true);
                mostrarMenuCompletado();
                System.out.println("Nivel completado!");
            }
        }
    }

    private void mostrarMenuCompletado() {
        juegoEnPausa = true;
        gameOverMenu.mostrar(GameOverMenu.MenuType.NIVEL_COMPLETADO);
    }

    private void manejarAccionMenu(GameOverMenu.MenuAction accion) {
        Main mainGame = (Main) Gdx.app.getApplicationListener();

        switch (accion) {
            case REINTENTAR:
                dispose();
                mainGame.setScreen(new Lvl2());
                break;

            case MENU_PRINCIPAL:
                dispose();
                mainGame.setScreen(new MenuScreen());
                break;

            case SIGUIENTE_NIVEL:
                dispose();
                System.out.println("Siguiente nivel");
                //mainGame.setScreen(new Lvl3());
                break;

            case NINGUNA:
                break;
        }
    }

    @Override
    public void resize(int i, int i1) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        prisionero.dispose();
        mono.dispose();
        shapes.dispose();
        if (level != null) level.dispose();
        if (cableMiniGame != null) cableMiniGame.dispose();
        if (minijuegoNumeros != null) minijuegoNumeros.dispose();
        if (gameOverMenu != null) gameOverMenu.dispose();
    }
}
