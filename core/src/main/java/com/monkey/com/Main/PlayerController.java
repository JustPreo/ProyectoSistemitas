/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author user
 */
public class PlayerController {
    //Intentare hacer la logica basica del playercontroller general , tipo mono y jugador?
    private Texture sprite;//Sprite por el momento
    private float x,y;
    private float velocidadX,velocidadY;
    private float gravedad;//SideScroller so ocupa gravedad)?
    private boolean tocandoPiso;//Para revisar si toca el piso o no?
    
    public PlayerController(String texturaPath,float xI , float yI,float velocidad){
    this.sprite = new Texture(texturaPath);
    this.x = xI;
    this.y = yI;
    this.velocidadX = velocidad;
    this.velocidadY = 0;
    this.gravedad = -600;//Aun no se , hay que ver como manejar esto
    this.tocandoPiso = true;
    }
    
    public void update(float delta,int teclaIzq , int teclaDer,int teclaSaltar){
    //Vamos a ver los movimientos
        if (Gdx.input.isKeyPressed(teclaDer)){
        x += velocidadX * delta;//Como el time.deltaTime de unity
        }
        if (Gdx.input.isKeyPressed(teclaIzq))
        {
        x -= velocidadX * delta;
        }
        if (Gdx.input.isKeyJustPressed(teclaSaltar) && tocandoPiso)//Creo que justPressed seria mejor para que no revise doble salto o algo asi
        {
        velocidadY = 200;//Cambiar parametro a futuro
        tocandoPiso = false;
        }
        //Agregar gravedad a la velocidad de y
        velocidadY += gravedad * delta;
        y += velocidadY * delta;
        //Agregar despues lo de saber si esta en suelo o no\
       if (y <= 100){
       y = 100;
       velocidadY =0;
       tocandoPiso = true;
       }
    }
    
    public void render(SpriteBatch batch){//Pedir el batch
    batch.draw(sprite, x, y);
    }
    
    public void dispose(){
    sprite.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
