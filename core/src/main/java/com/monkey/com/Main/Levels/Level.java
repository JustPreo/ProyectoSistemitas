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

    public Level(String tmxPath) {
        map = new TmxMapLoader().load(tmxPath);
        renderer = new OrthogonalTiledMapRenderer(map);
        loadLayers();
    }

    private void loadLayers() {
        // COLL layer -> rectángulos de colisión
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
            for (MapObject object : palancaLayer.getObjects()) {
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
                    System.out.println("Aa");
                }
            }
        }

        // DoorsColl -Tipo Puerta
        MapLayer doorLayer = map.getLayers().get("DoorsColl");
        if (doorLayer != null) {
            for (MapObject object : doorLayer.getObjects()) {
                // Si en Tiled usaste property "type" = "puerta"
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
    }

    // Getters
    public ArrayList<Rectangle> getColisiones() { return colisiones; }
    public ArrayList<RectangleMapObject> getPalancas() { return palancas; }
    public ArrayList<RectangleMapObject> getRopes() { return ropes; }
    public ArrayList<MapObject> getParedes() { return paredes; }
    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }
    
    public TiledMap getMap() { return map; }

    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();
    }
}
