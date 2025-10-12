package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.MENU_PRINCIPAL;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.NINGUNA;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.REINTENTAR;
import static com.monkey.com.Main.Levels.GameOverMenu.MenuAction.SIGUIENTE_NIVEL;
import com.monkey.com.Main.Main;
import com.monkey.com.Main.Menus.GameProgress;
import com.monkey.com.Main.Menus.MenuScreen;
import com.monkey.com.Main.Objetos.Mono;
import com.monkey.com.Main.Objetos.Prisionero;

import java.util.ArrayList;
import java.util.HashMap;

public class Lvl4 extends Level implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private OrthographicCamera camera;

    private final float CamWidth = 800;
    private final float CamHeight = 400;
    private final float CamMargX = 100;
    private final float CamMargY = 100;

    private Prisionero prisionero;
    private Mono mono;
    private boolean activo = false;
    private float timer = 0;
    private float timerWin = 0;

    private final ArrayList<RectangleMapObject> palancas;
    private final ArrayList<MapObject> puertas;
    private final ArrayList<RectangleMapObject> plataformasObjs;
    private final ArrayList<RectangleMapObject> pressurePlates;
    private ArrayList<RectangleMapObject> revisarwin;

    private final HashMap<String, Puerta> mapaPuertas = new HashMap<>();
    private final HashMap<String, Plataforma> mapaPlataformas = new HashMap<>();

    private final int tileWidth;
    private final int tileHeight;
    private final ArrayList<Rectangle> colisionesNivel;

    private GameOverMenu gameOverMenu;
    private boolean juegoEnPausa = false;

    public Lvl4() {
        super("Mapa/Mapa1/Lvl4.tmx");

        palancas = getPalancas();
        puertas = getParedes();
        plataformasObjs = getPlataformas();
        pressurePlates = getPressurePlates();
        colisionesNivel = getColisiones();
        revisarwin = super.getWINCHECK();

        tileWidth = getMap().getProperties().get("tilewidth", Integer.class);
        tileHeight = getMap().getProperties().get("tileheight", Integer.class);

        inicializarPuertas();
        inicializarPlataformas();

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        prisionero = new Prisionero("Humano/Humano.png", 64, 64, 200, colisionesNivel);
        mono = new Mono("Humano/Humano.png", 32, 476, 200, colisionesNivel);
        mono.mejoraMono3();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);

        gameOverMenu = super.getGameOverMenu();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (gameOverMenu.estaMostrado()) {
                    return true; 
                }
                return false;
            }
        });
        
        VoiceManager voice = VoiceManager.getInstance();
        voice.reproducirNarracion("lvl4_intro.mp3", false);
    }

    private void inicializarPuertas() {
        for (MapObject puertaObj : puertas) {
            String id = puertaObj.getProperties().get("id", String.class);
            Rectangle rect = ((RectangleMapObject) puertaObj).getRectangle();
            mapaPuertas.put(id, new Puerta(id, rect, colisionesNivel));
        }
    }

    private void inicializarPlataformas() {
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");

        for (RectangleMapObject plataformaObj : plataformasObjs) {
            String id = plataformaObj.getProperties().get("id", String.class);
            Object tipoObj = plataformaObj.getProperties().get("tipo");
            String tipo = tipoObj != null ? tipoObj.toString() : "";

            Rectangle rect = plataformaObj.getRectangle();
            boolean temporal = "temporal".equals(tipo);

            Plataforma plat = new Plataforma(id, rect, colisionesNivel, temporal);
            plat.guardarCells(tilesLayer);

            
            plat.desactivar(tilesLayer);

            mapaPlataformas.put(id, plat);
        }
    }

    private void update(float delta) {
        timer += delta;
        if (timerWin <= 0.5f) {
            timerWin += delta;
        }

        if (juegoEnPausa) {
            renderJuego();
            gameOverMenu.render(delta);

           
            GameOverMenu.MenuAction accion = gameOverMenu.getAccion();
            if (accion != GameOverMenu.MenuAction.NINGUNA) {
                manejarAccionMenu(accion);
                gameOverMenu.ocultar();
            }
            return;
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) && timer >= 0.5f) {
            if (!activo) {
                mono.cambiarEstado(true);
                prisionero.cambiarEstado(false);
                activo = true;
            } else {
                prisionero.cambiarEstado(true);
                mono.cambiarEstado(false);
                activo = false;
            }
            timer = 0;
        }

        Rectangle jugador = activo ? mono.getHitbox() : prisionero.getHitbox();
        verificarInteraccionPalancas(jugador);
        verificarPressurePlates();
        actualizarPlataformas(delta);

        prisionero.update(delta);
        mono.update(delta);

        if (timerWin >= 0.5f && !super.getWin()) {
            timerWin = 0;
            revisarWin();
        }
    }

    private void verificarInteraccionPalancas(Rectangle jugador) {
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");

        for (RectangleMapObject palanca : palancas) {
            String type = palanca.getProperties().get("type", String.class);
            if (!"palanca".equals(type)) {
                continue; 
            }

            Rectangle rect = palanca.getRectangle();
            String targetType = palanca.getProperties().get("targetType", String.class);
            String target = palanca.getProperties().get("target", String.class);

           
            if (jugador.overlaps(rect) && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                if ("plataforma".equals(targetType)) {
                    activarPlataformasTemporalesPalanca(); // activa todas las temporales
                } else if ("puerta".equals(targetType) && target != null) {
                    alternarPuerta(target);
                }
            }
        }
    }


    private void activarPlataformasTemporalesPalanca() {
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");

        for (Plataforma plat : mapaPlataformas.values()) {
           
            if (plat.isTemporal() && !plat.isActiva()) {
                plat.activar(tilesLayer, 5f);
            }
        }
    }

    private void verificarPressurePlates() {
        Rectangle hitboxPrisionero = prisionero.getHitbox();
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");

        boolean pisando = false;

        for (RectangleMapObject plateObj : pressurePlates) {
            Rectangle plateRect = plateObj.getRectangle();

            if (hitboxPrisionero.overlaps(plateRect)) {
                pisando = true;
                break;
            }
        }

        if (pisando) {
           
            for (Plataforma plat : mapaPlataformas.values()) {
                if (!plat.isTemporal() && !plat.isActiva()) {
                    plat.activar(tilesLayer, 0f);
                }
            }
        } else {
            
            for (Plataforma plat : mapaPlataformas.values()) {
                if (!plat.isTemporal() && plat.isActiva()) {
                    plat.desactivar(tilesLayer);
                }
            }
        }
    }

    private void actualizarPlataformas(float delta) {
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");

        for (Plataforma plat : mapaPlataformas.values()) {
            plat.update(delta, tilesLayer);
        }
    }

    private void activarPlataformaTemporal(String idPlataforma) {
        TiledMapTileLayer tilesLayer = (TiledMapTileLayer) getMap().getLayers().get("TilesColl");
        Plataforma plat = mapaPlataformas.get(idPlataforma);

        if (plat != null && plat.isTemporal()) {
            plat.activar(tilesLayer, 5.0f); 
        }
    }

    @Override
    public void show() {
    }

    @Override
public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    
    update(delta);

   
    float targetX = activo ? mono.getX() : prisionero.getX();
    float targetY = activo ? mono.getY() : prisionero.getY();
    camera.position.x = targetX + CamMargX;
    camera.position.y = targetY + CamMargY;
    camera.update();

   
    renderJuego();

   
    

    
    if (juegoEnPausa && gameOverMenu != null) {
        gameOverMenu.render(delta);
    }
}

    private void renderJuego() {
        getRenderer().setView(camera);

        int bgIndex = getMap().getLayers().getIndex("BG");
        int pisoIndex = getMap().getLayers().getIndex("Piso");
        int decorIndex = getMap().getLayers().getIndex("Decor");
        int doorsIndex = getMap().getLayers().getIndex("Doors");
        int tilesCollIndex = getMap().getLayers().getIndex("TilesColl");
        int foregroundIndex = getMap().getLayers().getIndex("Foreground");

        getRenderer().render(new int[]{bgIndex, pisoIndex});

        if (tilesCollIndex != -1) {
            getRenderer().render(new int[]{tilesCollIndex});
        }

        getRenderer().render(new int[]{doorsIndex, decorIndex});

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        prisionero.render(batch);
        mono.render(batch);
        batch.end();

        if (foregroundIndex != -1) {
            getRenderer().render(new int[]{foregroundIndex});
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

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        prisionero.dispose();
        mono.dispose();
        super.dispose();
    }

    private void alternarPuerta(String idPuerta) {
        TiledMapTileLayer doorsLayer = (TiledMapTileLayer) getMap().getLayers().get("Doors");

        for (Puerta p : mapaPuertas.values()) {
            if (p.id.equals(idPuerta)) {
                if (!p.abierta) {
                    p.abrir(doorsLayer);
                }
            } else {
                if (p.abierta) {
                    p.cerrar(doorsLayer);
                }
            }
        }
    }

    private void mostrarMenuFallo() {
        juegoEnPausa = true;
        gameOverMenu.mostrar(GameOverMenu.MenuType.NIVEL_FALLIDO);
        VoiceManager voice = VoiceManager.getInstance();
        voice.detenerNarracion();
    }

    private void revisarWin() {
        boolean monoA = false;
        boolean prisioneroA = false;
        for (RectangleMapObject revisarObj : revisarwin) {
            Rectangle r = revisarObj.getRectangle();

            if (prisionero.getHitbox().overlaps(r) && !super.getWin() && !prisioneroA) {
                prisioneroA = true;
            }
            if (mono.getHitbox().overlaps(r) && !super.getWin() && !monoA) {
                monoA = true;
            }
        }
        if (monoA && prisioneroA && !super.getWin()) {
            super.setWin(true);
            mostrarMenuCompletado();
            GameProgress.guardarNivel(5);
            
        }
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
                
                mainGame.setScreen(new Lvl2());
                break;

            case MENU_PRINCIPAL:
                
                mainGame.setScreen(new MenuScreen());
                dispose();
                break;

            case SIGUIENTE_NIVEL:
                
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen((Game) Gdx.app.getApplicationListener(), new Lvl5()));
                break;

            case NINGUNA:
                break;
        }
    }

    
    private static class Puerta {

        String id;
        Rectangle rect;
        boolean abierta = false;
        private final ArrayList<Rectangle> colisiones;
        private TiledMapTileLayer.Cell[] cellsOriginales;

        public Puerta(String id, Rectangle rect, ArrayList<Rectangle> colisiones) {
            this.id = id;
            this.rect = new Rectangle(rect);
            this.colisiones = colisiones;
            this.cellsOriginales = new TiledMapTileLayer.Cell[2];
        }

        public void abrir(TiledMapTileLayer doorsLayer) {
            int tileWidth = (int) doorsLayer.getTileWidth();
            int tileHeight = (int) doorsLayer.getTileHeight();

            int cellX = (int) (rect.x / tileWidth);
            int cellY = (int) (rect.y / tileHeight);

            cellsOriginales[0] = doorsLayer.getCell(cellX, cellY);
            cellsOriginales[1] = doorsLayer.getCell(cellX, cellY + 1);

            doorsLayer.setCell(cellX, cellY, null);
            doorsLayer.setCell(cellX, cellY + 1, null);

            colisiones.remove(rect);
            abierta = true;
        }

        public void cerrar(TiledMapTileLayer doorsLayer) {
            int tileWidth = (int) doorsLayer.getTileWidth();
            int tileHeight = (int) doorsLayer.getTileHeight();

            int cellX = (int) (rect.x / tileWidth);
            int cellY = (int) (rect.y / tileHeight);

            doorsLayer.setCell(cellX, cellY, cellsOriginales[0]);
            doorsLayer.setCell(cellX, cellY + 1, cellsOriginales[1]);

            if (!colisiones.contains(rect)) {
                colisiones.add(rect);
            }
            abierta = false;
        }
    }

    
    private static class Plataforma {

        private String id;
        private Rectangle rect;
        private boolean activa;
        private ArrayList<Rectangle> colisiones;
        private TiledMapTileLayer.Cell[] cellsOriginales;
        private float tiempoRestante;
        private boolean temporal;

        public Plataforma(String id, Rectangle rect, ArrayList<Rectangle> colisiones, boolean temporal) {
            this.id = id;
            this.rect = new Rectangle(rect);
            this.colisiones = colisiones;
            this.activa = true; 
            this.temporal = temporal;
            this.tiempoRestante = 0;

            int numCells = (int) ((rect.width / 32) * (rect.height / 32));
            this.cellsOriginales = new TiledMapTileLayer.Cell[numCells];
        }

        public void activar(TiledMapTileLayer tilesLayer, float duracion) {
            if (activa) {
                return;
            }
            
            int tileWidth = (int) tilesLayer.getTileWidth();
            int tileHeight = (int) tilesLayer.getTileHeight();

            int startX = (int) (rect.x / tileWidth);
            int startY = (int) (rect.y / tileHeight);
            int widthInTiles = (int) (rect.width / tileWidth);
            int heightInTiles = (int) (rect.height / tileHeight);

            int index = 0;
            for (int y = 0; y < heightInTiles; y++) {
                for (int x = 0; x < widthInTiles; x++) {
                    if (index < cellsOriginales.length) {
                        tilesLayer.setCell(startX + x, startY + y, cellsOriginales[index]);
                        index++;
                    }
                }
            }

            if (!colisiones.contains(rect)) {
                colisiones.add(rect);
            }

            activa = true;
            if (temporal) {
                tiempoRestante = duracion;
            }
        }

        public void desactivar(TiledMapTileLayer tilesLayer) {
            if (!activa) {
                return;
            }
            

            int tileWidth = (int) tilesLayer.getTileWidth();
            int tileHeight = (int) tilesLayer.getTileHeight();

            int startX = (int) (rect.x / tileWidth);
            int startY = (int) (rect.y / tileHeight);
            int widthInTiles = (int) (rect.width / tileWidth);
            int heightInTiles = (int) (rect.height / tileHeight);

            for (int y = 0; y < heightInTiles; y++) {
                for (int x = 0; x < widthInTiles; x++) {
                    int index = y * widthInTiles + x;
                    if (index < cellsOriginales.length) {
                        tilesLayer.setCell(startX + x, startY + y, null); 
                    }
                }
            }

            colisiones.remove(rect);
            activa = false;
            tiempoRestante = 0;
        }

        public void guardarCells(TiledMapTileLayer tilesLayer) {
            int tileWidth = (int) tilesLayer.getTileWidth();
            int tileHeight = (int) tilesLayer.getTileHeight();

            int startX = (int) (rect.x / tileWidth);
            int startY = (int) (rect.y / tileHeight);
            int widthInTiles = (int) (rect.width / tileWidth);
            int heightInTiles = (int) (rect.height / tileHeight);


            int index = 0;
            for (int y = 0; y < heightInTiles; y++) {
                for (int x = 0; x < widthInTiles; x++) {
                    if (index < cellsOriginales.length) {
                        cellsOriginales[index] = tilesLayer.getCell(startX + x, startY + y);
                        
                        index++;
                    }
                }
            }
        }

        public void update(float delta, TiledMapTileLayer tilesLayer) {
            if (temporal && activa && tiempoRestante > 0) {
                tiempoRestante -= delta;
                if (tiempoRestante <= 0) {
                    desactivar(tilesLayer);
                }
            }
        }

        public String getId() {
            return id;
        }

        public boolean isActiva() {
            return activa;
        }

        public boolean isTemporal() {
            return temporal;
        }

        public float getTiempoRestante() {
            return tiempoRestante;
        }
    }
}
