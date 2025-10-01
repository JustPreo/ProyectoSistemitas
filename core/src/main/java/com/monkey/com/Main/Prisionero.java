/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 *
 * @author user
 */
public class Prisionero extends PlayerController {

    public Prisionero(String texturaPath, float xI, float yI, float velocidad) {
        super(texturaPath, xI, yI, velocidad);
        this.hitbox = new Rectangle (x,y,32,64);//Tamano sprite
    }

    @Override
    public void update(float delta) {//Hoy si agregar movimiento
        if (activo) {
            
            //Vamos a ver los movimientos
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += velocidadX * delta;//Como el time.deltaTime de unity
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= velocidadX * delta;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tocandoPiso)//Creo que justPressed seria mejor para que no revise doble salto o algo asi
            {
                System.out.println("Wtf prisionero");
                velocidadY = 200;//Cambiar parametro a futuro
                tocandoPiso = false;
            }
            
        }
        //Agregar gravedad a la velocidad de y
        velocidadY += gravedad * delta;
        y += velocidadY * delta;
        //Agregar despues lo de saber si esta en suelo o no\
        
        
        hitbox.x = x;
        hitbox.y = y;

    }

    @Override
    public void render(SpriteBatch batch) {//Pedir el batch
        batch.draw(sprite, x, y,hitbox.width,hitbox.height);//Lo dibuja con el tamano de eso
        
    }
}
