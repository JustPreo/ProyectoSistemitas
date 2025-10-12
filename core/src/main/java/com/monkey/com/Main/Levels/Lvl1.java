package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Game;
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
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.MENU_PRINCIPAL;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.NINGUNA;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.REINTENTAR;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.SIGUIENTE_NIVEL;
import com.monkey.com.Main.Main;
import com.monkey.com.Main.Menus.GameProgress;
import com.monkey.com.Main.Menus.MenuScreen;
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
    private MinijuegoNumeros minijuegoNumeros;

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

    private GameOverMenu gameOverMenu;
    private boolean juegoEnPausa = false;

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
        prisionero.cambiarEstado(true);
        mono = new Mono("Mono1.png", 300, 64, 200, colisiones);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

        shapes = new ShapeRenderer();

        //  Inicializar el menu
        gameOverMenu = level.getGameOverMenu();

        //  Input para menu
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (gameOverMenu.estaMostrado()) {
                    return true;
                }
                return false;
            }
        });
        VoiceManager voice = VoiceManager.getInstance();
        voice.reproducirNarracion("lvl1_intro.mp3", false);

    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);
        if (enMiniGame && cableMiniGame != null) {
            cableMiniGame.render(delta); // Solo renderiza el minijuego
            return; // Pausa todo lo del Level
        }
        if (enMiniGame && minijuegoNumeros != null) {
            minijuegoNumeros.render(delta);
            return;
        }
        // Renderizar menu si esta en pausa
        if (juegoEnPausa) {
            gameOverMenu.render(delta);

            GameOverMenu.MenuAction accion = gameOverMenu.getAccion();
            if (accion != GameOverMenu.MenuAction.NINGUNA) {
                manejarAccionMenu(accion);
                gameOverMenu.ocultar();
            }
            return;
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

                    //miniJuegoCable();
                    //Para llamar el miiJuegoDeCable
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
        MapLayer collLayer = map.getLayers().get("Coll");
        MapLayer electricidadCollLayer = map.getLayers().get("ElectricidadColl");
        TiledMapTileLayer electricidadLayer = null;

        if (map.getLayers().get("Electricidad") instanceof TiledMapTileLayer) {
            electricidadLayer = (TiledMapTileLayer) map.getLayers().get("Electricidad");
        }

        int tileWidth = 32;
        int tileHeight = 32;

        for (RectangleMapObject palancaObj : palancas) {
            Rectangle palancaRect = palancaObj.getRectangle();

            if (prisionero.getHitbox().overlaps(palancaRect)) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && activo) {
                    String type = palancaObj.getProperties().get("type", String.class);
                    String target = palancaObj.getProperties().get("target", String.class);

                    // Solo para palancas tipo "numeros"
                    if ("numeros".equals(type) && target != null) {

                        // Buscar objeto de electricidad con el mismo id (en ElectricidadColl)
                        RectangleMapObject electricidadABorrar = null;
                        if (electricidadCollLayer != null) {
                            for (MapObject eObj : electricidadCollLayer.getObjects()) {
                                String id = eObj.getProperties().get("id", String.class);
                                if (id != null && id.equals(target) && eObj instanceof RectangleMapObject) {
                                    electricidadABorrar = (RectangleMapObject) eObj;
                                    break;
                                }
                            }
                        }

                        // Si se encontró el objeto de electricidad, borrarlo
                        if (electricidadABorrar != null) {
                            Rectangle r = electricidadABorrar.getRectangle();

                            // Quitar de la lista de colisiones
                            colisiones.remove(r);
                            // Quitar del layer
                            electricidadCollLayer.getObjects().remove(electricidadABorrar);

                            // TAMBIEN borrar visualmente en la capa de tiles
                            if (electricidadLayer != null) {
                                int startX = (int) (r.x / tileWidth);
                                int startY = (int) (r.y / tileHeight);
                                int endX = (int) ((r.x + r.width) / tileWidth);
                                int endY = (int) ((r.y + r.height) / tileHeight);

                                for (int x = startX; x < endX; x++) {
                                    for (int y = startY; y < endY; y++) {
                                        electricidadLayer.setCell(x, y, null);
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

    private void miniJuegoCable() {
        if (!enMiniGame) {
            enMiniGame = true;
            screenAnterior = this; //pantalla actual
            cableMiniGame = new com.monkey.com.Main.Levels.CableMiniGame(() -> {
                enMiniGame = false;
                cableMiniGame.dispose();
                cableMiniGame = null;
                Gdx.input.setInputProcessor(null);

            });
            Gdx.input.setInputProcessor(cableMiniGame.new CableInput());
        }
    }

    private void miniJuegoNumeros() {
        if (!enMiniGame) {
            enMiniGame = true;
            minijuegoNumeros = new MinijuegoNumeros(() -> {
                enMiniGame = false;
                minijuegoNumeros.dispose();
                minijuegoNumeros = null;
                Gdx.input.setInputProcessor(null);

            });
        }
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

    /*private void handleElectricidad() {
        for (RectangleMapObject eObj : electricidad) {
            Rectangle eRect = eObj.getRectangle();
            if ((mono.getHitbox().overlaps(eRect) || prisionero.getHitbox().overlaps(eRect)) && !enMiniGame) {
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
        }
    }*/
    private void handleElectricidad() {
        for (RectangleMapObject eObj : electricidad) {
            Rectangle eRect = eObj.getRectangle();
            if ((mono.getHitbox().overlaps(eRect) || prisionero.getHitbox().overlaps(eRect)) && !enMiniGame) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    enMiniGame = true;

                    screenAnterior = this; // guardo esta pantalla
                    minijuegoNumeros = new MinijuegoNumeros(() -> {
                        enMiniGame = false;
                        minijuegoNumeros.dispose();
                        minijuegoNumeros = null;

                    });
                }
            }
        }
    }

    private void revisarWin() {
        for (RectangleMapObject revisarObj : revisarwin) {
            Rectangle r = revisarObj.getRectangle();
            if (prisionero.getHitbox().overlaps(r) && mono.getHitbox().overlaps(r) && !level.getWin()) {

                level.setWin(true);
                GameProgress.guardarNivel(2);
                mostrarMenuCompletado();
            }
        }
    }

    private void mostrarMenuFallo() {
        juegoEnPausa = true;
        gameOverMenu.mostrar(GameOverMenu.MenuType.NIVEL_FALLIDO);
        VoiceManager voice = VoiceManager.getInstance();
        voice.detenerNarracion();
    }

    private void mostrarMenuCompletado() {
        juegoEnPausa = true;
        gameOverMenu.mostrar(GameOverMenu.MenuType.NIVEL_COMPLETADO);
        VoiceManager voice = VoiceManager.getInstance();
        voice.detenerNarracion();
    }

    private void manejarAccionMenu(GameOverMenu.MenuAction accion) {
        Main mainGame = (Main) Gdx.app.getApplicationListener();

        switch (accion) {
            case REINTENTAR:
                dispose();
                mainGame.setScreen(new Lvl1());//Reintentar
                break;

            case MENU_PRINCIPAL:
                dispose();
                mainGame.setScreen(new MenuScreen());
                break;

            case SIGUIENTE_NIVEL:
                dispose();

                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen((Game) Gdx.app.getApplicationListener(), new Lvl2()));
                break;

            case NINGUNA:
                break;
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
