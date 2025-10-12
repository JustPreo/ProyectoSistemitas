/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Levels;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Level {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private ArrayList<Rectangle> colisiones = new ArrayList<>();
    private ArrayList<RectangleMapObject> palancas = new ArrayList<>();
    private ArrayList<MapObject> paredes = new ArrayList<>();
    private ArrayList<RectangleMapObject> ropes = new ArrayList<>();
    private ArrayList<RectangleMapObject> electricidad = new ArrayList<>();
    private ArrayList<RectangleMapObject> WINCHECK = new ArrayList<>();
    private ArrayList<RectangleMapObject> plataformas = new ArrayList<>();
    private ArrayList<RectangleMapObject> pressurePlates = new ArrayList<>();

    private boolean win = false;
    protected GameOverMenu gameOverMenu;

    public Level(String tmxPath) {
        // Carga mapa
        map = new TmxMapLoader().load(tmxPath);
        renderer = new OrthogonalTiledMapRenderer(map);
        loadLayers();

        // Inicializar menú de game over
        gameOverMenu = new GameOverMenu();
    }

    private void loadLayers() {
        // COLL layer -> rectangulos de colision
        MapLayer collisionLayer = map.getLayers().get("Coll");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rec = ((RectangleMapObject) object).getRectangle();
                    colisiones.add(rec);
                }
            }
        }

        // Interactuables - palancas u objetos 
        MapLayer palancaLayer = map.getLayers().get("Interactuables");
        if (palancaLayer != null) {
            for (MapObject object : palancaLayer.getObjects()) {//No solo es palancas si no que es todo 
                if (object instanceof RectangleMapObject) {
                    palancas.add((RectangleMapObject) object);
                }
            }
        }

        // RopesColl
        MapLayer ropeLayer = map.getLayers().get("RopesColl");
        if (ropeLayer != null) {
            for (MapObject object : ropeLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rec = ((RectangleMapObject) object).getRectangle();
                    ropes.add((RectangleMapObject) object);
                    
                }
            }
        }
        //CollElectricidad
        MapLayer electricidadLayer = map.getLayers().get("ElectricidadColl");
        if (electricidadLayer != null) {
            for (MapObject object : electricidadLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    electricidad.add((RectangleMapObject) object);
                }
            }
        }
        //Colisiones de win - fin de mapa
        MapLayer wincheck = map.getLayers().get("WINCHECK");
        if (wincheck != null) {
            for (MapObject object : wincheck.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    WINCHECK.add((RectangleMapObject) object);
                }
            }
        }

        // DoorsColl -Tipo Puerta
        MapLayer doorLayer = map.getLayers().get("DoorsColl");
        if (doorLayer != null) {
            for (MapObject object : doorLayer.getObjects()) {
                String type = object.getProperties().get("type", String.class);
                if ("puerta".equals(type)) {
                    paredes.add(object);
                    if (object instanceof RectangleMapObject) {
                        Rectangle r = ((RectangleMapObject) object).getRectangle();
                        colisiones.add(r);
                        
                    }
                }
            }
        }

        // PlataformasColl
        MapLayer plataformaLayer = map.getLayers().get("PlataformasColl");
        if (plataformaLayer != null) {
            for (MapObject object : plataformaLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    plataformas.add((RectangleMapObject) object);
                }
            }
        }

// PressurePlates
        MapLayer pressurePlateLayer = map.getLayers().get("PressurePlates");
        if (pressurePlateLayer != null) {
            for (MapObject object : pressurePlateLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    pressurePlates.add((RectangleMapObject) object);
                }
            }
        }

    }

    // Getters
    public ArrayList<Rectangle> getColisiones() {
        return colisiones;
    }

    public ArrayList<RectangleMapObject> getPalancas() {
        return palancas;
    }

    public ArrayList<RectangleMapObject> getRopes() {
        return ropes;
    }

    public ArrayList<RectangleMapObject> getElectricidad() {
        return electricidad;
    }

    public ArrayList<RectangleMapObject> getWINCHECK() {
        return WINCHECK;
    }

    public ArrayList<MapObject> getParedes() {
        return paredes;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public TiledMap getMap() {
        return map;
    }

    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
        if (map != null) {
            map.dispose();
        }
    }

    public boolean getWin() {
        return win;
    }

    public void setWin(boolean winCheck) {
        win = winCheck;
    }

    public GameOverMenu getGameOverMenu() {
        return gameOverMenu;
    }

    public ArrayList<RectangleMapObject> getPlataformas() {
        return plataformas;
    }

    public ArrayList<RectangleMapObject> getPressurePlates() {
        return pressurePlates;
    }
}
