/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Menus;

/**
 *
 * @author user
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameProgress {

    private static final String PREFS_NAME = "MonkeyGamePrefs";
    private static final String KEY_NIVEL = "nivel_actual";


    public static void guardarNivel(int nivel) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(KEY_NIVEL, nivel);
        prefs.flush(); 
    }

 
    public static int cargarNivel() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getInteger(KEY_NIVEL, 1); 
    }

 
    public static void resetearProgreso() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
    }
}