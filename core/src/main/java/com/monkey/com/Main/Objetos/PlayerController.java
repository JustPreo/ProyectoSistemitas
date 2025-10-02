/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public abstract class PlayerController {
    //Intentare hacer la logica basica del playercontroller general , tipo mono y jugador?
    protected Texture sprite;//Sprite por el momento
    protected float x,y;
    protected float velocidadX,velocidadY;
    protected float gravedad;//SideScroller so ocupa gravedad)?
    public boolean tocandoPiso;//Para revisar si toca el piso o no?
    protected boolean activo;
    protected Rectangle hitbox;//Hitbox
    protected ArrayList<Rectangle> mapaPlataformas;

    
    public PlayerController(String texturaPath,float xI , float yI,float velocidad,ArrayList<Rectangle> mapaPlataformas){
    this.sprite = new Texture(texturaPath);
    this.x = xI;
    this.y = yI;
    this.velocidadX = velocidad;
    this.velocidadY = 0;
    this.gravedad = -500;//Aun no se , hay que ver como manejar esto
    this.tocandoPiso = true;
    this.mapaPlataformas = mapaPlataformas;
    activo = false;
    }
    
    public abstract void update(float delta);
    
    public abstract void render(SpriteBatch batch);
    
    public void dispose(){
    sprite.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public void cambiarEstado(boolean bool){
    activo = bool;//Lo opuesto a lo que esta ahorita
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVelocidadY(float velocidadY) {
        this.velocidadY = velocidadY;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getVelocidadY() {
        return velocidadY;
    }
    
    
}
