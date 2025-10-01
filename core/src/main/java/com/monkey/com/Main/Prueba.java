/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main;

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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author user
 */
public class Prueba implements Screen {

    //Voy a llamar los cosos de spritebatch y eso
    private SpriteBatch batch;
    private Prisionero prisionero;//Despues ponerlo en la cosa de prisionero y mono
    private Mono mono;
    private boolean activo = false;//T = mono // F = Prisionero
    private float timer = 0;

    //Camara para juego
    private OrthographicCamera camera;
    private final float CamWidth = 800;
    private final float CamHeight = 400;
    private final float CamMargX = 200;//Margen H
    private final float CamMargY = 100;

    //El shaperenderer
    ShapeRenderer shapes;

    //Mapa intento
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    @Override
    public void show() {
        map = new TmxMapLoader().load("Mapa/MapaIntento.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        batch = new SpriteBatch();
        prisionero = new Prisionero("Humano/Humano.png", 100, 100, 200);
        mono = new Mono("Humano/Humano.png", 150, 100, 200);
        //Creamos las camaras
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CamWidth, CamHeight);
        //prisionero.cambiarEstado(true);//Mono camina primero?
        shapes = new ShapeRenderer();

    }

    @Override
    public void render(float f) {
        //Voy a agregarle un timer a esto , como un CD

        if (timer <= 1.5)//1 segundo creo
        {
            timer = timer + f;//Agregar delta
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            switchController();
        }
        prisionero.update(f);
        mono.update(f);
        //Tiene que seguir al activo so consigue su hitbox 

        float targetX = activo ? mono.getX() : prisionero.getX();
        float targetY = activo ? mono.getY() : prisionero.getY();

        camera.position.x = targetX + CamMargX;
        camera.position.y = CamHeight + CamMargY;
        MapLayer collisionLayer = map.getLayers().get("Coll");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();

                    //Col Prisionero
                    if (prisionero.getHitbox().overlaps(rect)) {
                        float floorY = rect.y + rect.height;
                        prisionero.setY(floorY);
                        prisionero.setVelocidadY(0);
                        prisionero.tocandoPiso = true;
                    }

                    // Col Mono
                    if (mono.getHitbox().overlaps(rect)) {
                        float floorY = rect.y + rect.height;
                        mono.setY(floorY);
                        mono.setVelocidadY(0);
                        mono.tocandoPiso = true;
                    }
                }
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            camera.update();
            renderer.setView(camera);
            renderer.render();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            //Inicio batch
            prisionero.render(batch);
            mono.render(batch);
            //Fin batch
            batch.end();

            shapes.setProjectionMatrix(camera.combined);
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.RED);
            if (activo) {
                shapes.rect(mono.getHitbox().x, mono.getHitbox().y, mono.getHitbox().getWidth(), mono.getHitbox().getHeight());
            } else {
                shapes.rect(prisionero.getHitbox().x, prisionero.getHitbox().y, prisionero.getHitbox().getWidth(), prisionero.getHitbox().getHeight());
            }
            shapes.end();

        }
    }

    private void switchController() {
        if (!activo && timer >= 1) {
            mono.cambiarEstado(true);//true
            prisionero.cambiarEstado(false);//false
            activo = !activo;
            timer = 0;
            Gdx.input.setOnscreenKeyboardVisible(false);//Resetear input supongo
            System.out.println("Mono caminando");
        } else if (activo && timer >= 1) {
            prisionero.cambiarEstado(true);//true
            mono.cambiarEstado(false);//false
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
    }

}
