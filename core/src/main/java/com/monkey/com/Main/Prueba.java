/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author user
 */
public class Prueba implements Screen{
    //Voy a llamar los cosos de spritebatch y eso
    private SpriteBatch batch;
    private PlayerController prisionero;//Despues ponerlo en la cosa de prisionero y mono

    @Override
    public void show() {
       batch = new SpriteBatch();
       prisionero = new PlayerController("Humano/Humano.png",100,100,200);
    }

    @Override
    public void render(float f) {
        prisionero.update(f, Input.Keys.A, Input.Keys.D, Input.Keys.W);
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //Inicio batch
        prisionero.render(batch);
        //Fin batch
        batch.end();
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
    }
    
}
