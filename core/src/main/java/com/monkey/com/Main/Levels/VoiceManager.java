/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;


public class VoiceManager {

    private static VoiceManager instance;


    private Music narracionActual;
    private final HashMap<String, Sound> clipsCortos = new HashMap<>();

    private float volumen = 1f;

    private VoiceManager() {
    }

    public static VoiceManager getInstance() {
        if (instance == null) {
            instance = new VoiceManager();
        }
        return instance;
    }

    
    public void reproducirNarracion(String nombreArchivo, boolean loop) {
        detenerNarracion();

        String ruta = "dialogos/" + nombreArchivo;
        narracionActual = Gdx.audio.newMusic(Gdx.files.internal(ruta));
        narracionActual.setLooping(loop);
        narracionActual.setVolume(volumen);
        narracionActual.play();
    }
    
    public void detenerNarracion() {
        if (narracionActual != null) {
            narracionActual.stop();
            narracionActual.dispose();
            narracionActual = null;
        }
    }

    /**
     * Pausa      */
    public void pausarNarracion() {
        if (narracionActual != null && narracionActual.isPlaying()) {
            narracionActual.pause();
        }
    }

    
    public void reanudarNarracion() {
        if (narracionActual != null && !narracionActual.isPlaying()) {
            narracionActual.play();
        }
    }
    public void cargarClip(String nombreArchivo) {
        if (!clipsCortos.containsKey(nombreArchivo)) {
            String ruta = "dialogos/" + nombreArchivo;
            clipsCortos.put(nombreArchivo, Gdx.audio.newSound(Gdx.files.internal(ruta)));
        }
    }

   
    public void reproducirClip(String nombreArchivo) {
        Sound clip = clipsCortos.get(nombreArchivo);
        if (clip != null) {
            clip.play(volumen);
        } else {
            Gdx.app.error("VoiceManager", "Clip no encontrado: " + nombreArchivo);
        }
    }

    
    public void setVolumen(float vol) {
        volumen = Math.max(0f, Math.min(1f, vol));
        if (narracionActual != null) {
            narracionActual.setVolume(volumen);
        }
    }

    public float getVolumen() {
        return volumen;
    }

    
    public void dispose() {
        detenerNarracion();
        for (Sound s : clipsCortos.values()) {
            s.dispose();
        }
        clipsCortos.clear();
    }
}
