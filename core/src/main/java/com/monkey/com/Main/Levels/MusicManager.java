/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;


public class MusicManager {

    private static MusicManager instance;
    private Music musicaActual;
    private float volumen = 0.1f; // Volumen predeterminado

    private MusicManager() { }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

  
     
    public void reproducirMusica(String nombreArchivo, boolean loop) {
        detenerMusica();

        String ruta = nombreArchivo;
        musicaActual = Gdx.audio.newMusic(Gdx.files.internal(ruta));
        musicaActual.setLooping(loop);
        musicaActual.setVolume(volumen);
        musicaActual.play();
    }


    public void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.dispose();
            musicaActual = null;
        }
    }


    public void pausarMusica() {
        if (musicaActual != null && musicaActual.isPlaying()) {
            musicaActual.pause();
        }
    }

  
    public void reanudarMusica() {
        if (musicaActual != null && !musicaActual.isPlaying()) {
            musicaActual.play();
        }
    }


    public void setVolumen(float vol) {
        volumen = Math.max(0f, Math.min(1f, vol));
        if (musicaActual != null) {
            musicaActual.setVolume(volumen);
        }
    }


    public float getVolumen() {
        return volumen;
    }


    public void dispose() {
        detenerMusica();
    }
}