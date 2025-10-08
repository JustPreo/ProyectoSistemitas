package com.monkey.com.Main.Menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.monkey.com.Main.Main;

public class MenuScreen implements Screen {

    private SpriteBatch batch;

    // Texturas
    private Texture fondo;
    private Texture btnJugar, btnJugarHover;
    private Texture btnOpciones, btnOpcionesHover;
    private Texture btnSalir, btnSalirHover;

    // Area de los botones
    private Rectangle jugarBounds;
    private Rectangle opcionesBounds;
    private Rectangle salirBounds;

    // Estados de hover
    private boolean jugarHover = false;
    private boolean opcionesHover = false;
    private boolean salirHover = false;

    private Vector3 touchPos;

    @Override
    public void show() {
        batch = new SpriteBatch();
        touchPos = new Vector3();

        
        fondo = new Texture("Fondo/Fondo.png");
        btnJugar = new Texture("Fondo/Jugar.png");
        btnJugarHover = new Texture("Fondo/Jugar2.png");
        btnOpciones = new Texture("Fondo/Opciones.png");
        btnOpcionesHover = new Texture("Fondo/Opciones2.png");
        btnSalir = new Texture("Fondo/Salir.png");
        btnSalirHover = new Texture("Fondo/Salir2.png");

       
        float baseWidth = Gdx.graphics.getWidth() * 0.35f;
        float baseHeight = baseWidth / 5f; 
        float centerX = (Gdx.graphics.getWidth() - baseWidth) / 2f;
        float spacing = baseHeight * 1.5f; // espacio entre botones

        float centerY = Gdx.graphics.getHeight() / 2.7f;

        jugarBounds = new Rectangle(centerX, centerY + spacing, baseWidth, baseHeight);
        opcionesBounds = new Rectangle(centerX, centerY, baseWidth, baseHeight);
        salirBounds = new Rectangle(centerX, centerY - spacing, baseWidth, baseHeight);
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        touchPos.y = Gdx.graphics.getHeight() - touchPos.y;

        
        jugarHover = jugarBounds.contains(touchPos.x, touchPos.y);
        opcionesHover = opcionesBounds.contains(touchPos.x, touchPos.y);
        salirHover = salirBounds.contains(touchPos.x, touchPos.y);

        // Detectar clicks
        if (Gdx.input.justTouched()) {
            if (jugarHover) iniciarJuego();
            else if (opcionesHover) abrirOpciones();
            else if (salirHover) salirJuego();
        }

        // Dibujar
        batch.begin();

        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.draw(jugarHover ? btnJugarHover : btnJugar,
                jugarBounds.x, jugarBounds.y, jugarBounds.width, jugarBounds.height);

        batch.draw(opcionesHover ? btnOpcionesHover : btnOpciones,
                opcionesBounds.x, opcionesBounds.y, opcionesBounds.width, opcionesBounds.height);

        batch.draw(salirHover ? btnSalirHover : btnSalir,
                salirBounds.x, salirBounds.y, salirBounds.width, salirBounds.height);

        batch.end();
    }

    private void iniciarJuego() {
        System.out.println("Iniciando juego");
        
    }

    private void abrirOpciones() {
        System.out.println("Abriendo opciones");
        ((Main) Gdx.app.getApplicationListener()).setScreen(new PantallaSettings());
    }

    private void salirJuego() {
        System.out.println("Saliendo del juego...");
        Gdx.app.exit();//pa salir
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        btnJugar.dispose();
        btnJugarHover.dispose();
        btnOpciones.dispose();
        btnOpcionesHover.dispose();
        btnSalir.dispose();
        btnSalirHover.dispose();
    }
}
