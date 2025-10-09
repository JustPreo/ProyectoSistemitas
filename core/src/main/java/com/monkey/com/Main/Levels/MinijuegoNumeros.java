package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MinijuegoNumeros {
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    
    private ArrayList<Boton> botones;
    private int numeroActual;
    private boolean completado;
    private Runnable onComplete;
    
    private static final int TOTAL_NUMEROS = 10;
    private static final float BOTON_SIZE = 60;
    private static final float PADDING = 20;
    private static final int COLUMNAS = 5;
    
    public MinijuegoNumeros(Runnable onComplete) {
        this.onComplete = onComplete;
        this.numeroActual = 1;
        this.completado = false;
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        
        inicializarBotones();
    }
    
    private void inicializarBotones() {
        botones = new ArrayList<>();
        ArrayList<Integer> numeros = new ArrayList<>();
        
        
        for (int i = 1; i <= TOTAL_NUMEROS; i++) {
            numeros.add(i);
        }
        
        
        Collections.shuffle(numeros, new Random());
        
        
        float startX = 400 - (COLUMNAS * (BOTON_SIZE + PADDING)) / 2 + PADDING / 2;
        float startY = 300;
        
        
        for (int i = 0; i < TOTAL_NUMEROS; i++) {
            int fila = i / COLUMNAS;
            int columna = i % COLUMNAS;
            
            float x = startX + columna * (BOTON_SIZE + PADDING);
            float y = startY - fila * (BOTON_SIZE + PADDING);
            
            botones.add(new Boton(numeros.get(i), x, y, BOTON_SIZE));
        }
    }
    
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        
        
        if (Gdx.input.justTouched() && !completado) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            
            for (Boton boton : botones) {
                if (!boton.clickeado && boton.contiene(touchPos.x, touchPos.y)) {
                    if (boton.numero == numeroActual) {
                        
                        boton.clickeado = true;
                        numeroActual++;
                        
                        
                        if (numeroActual > TOTAL_NUMEROS) {
                            completado = true;
                        }
                    } else {
                        
                        reiniciar();
                    }
                    break;
                }
            }
        }
        
        
        if (completado) {
            renderCompletado();
            // Esperar 1 segundo antes de salir
            if (Gdx.input.justTouched()) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
            return;
        }
        
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Boton boton : botones) {
            if (boton.clickeado) {
                shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1); 
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1); 
            }
            shapeRenderer.rect(boton.x, boton.y, boton.size, boton.size);
        }
        
        shapeRenderer.end();
        
        // Dibujar bordes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        
        for (Boton boton : botones) {
            if (!boton.clickeado) {
                shapeRenderer.rect(boton.x, boton.y, boton.size, boton.size);
            }
        }
        
        shapeRenderer.end();
        
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (Boton boton : botones) {
            if (!boton.clickeado) {
                font.setColor(Color.WHITE);
                String texto = String.valueOf(boton.numero);
                font.draw(batch, texto, 
                    boton.x + boton.size / 2 - 10, 
                    boton.y + boton.size / 2 + 10);
            }
        }
        
        
        font.setColor(Color.CYAN);
        font.getData().setScale(1.5f);
        font.draw(batch, "Presiona los numeros del 1 al 10 en orden", 150, 380);
        font.draw(batch, "Siguiente: " + numeroActual, 350, 100);
        font.getData().setScale(2f);
        
        batch.end();
    }
    
    private void renderCompletado() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1);
        shapeRenderer.rect(200, 150, 400, 100);
        shapeRenderer.end();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(2.5f);
        font.draw(batch, "COMPLETADO!", 280, 210);
        font.getData().setScale(1.5f);
        font.draw(batch, "Click para continuar", 280, 170);
        font.getData().setScale(2f);
        batch.end();
    }
    
    private void reiniciar() {
        numeroActual = 1;
        for (Boton boton : botones) {
            boton.clickeado = false;
        }
    }
    
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
    
    // Clase para los botones
    private class Boton {
        int numero;
        float x, y;
        float size;
        boolean clickeado;
        
        Boton(int numero, float x, float y, float size) {
            this.numero = numero;
            this.x = x;
            this.y = y;
            this.size = size;
            this.clickeado = false;
        }
        
        boolean contiene(float px, float py) {
            return px >= x && px <= x + size && py >= y && py <= y + size;
        }
    }
}