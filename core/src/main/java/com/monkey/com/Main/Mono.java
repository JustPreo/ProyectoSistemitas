/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Mono extends PlayerController {

    public Mono(String texturaPath, float xI, float yI, float velocidad, ArrayList<Rectangle> mapaPlataformas) {
        super(texturaPath, xI, yI, velocidad, mapaPlataformas);
        this.hitbox = new Rectangle(x, y, 32, 32);//Tamano sprite    
    }
//El movimiento es mas rapido y el puede subir ("Tengo que agregar eso a futuro")- Talvez E para subir o algo asi
    //Tambien tiene que ser mas rapido que el humano )?

    @Override
    public void update(float delta
    ) {
        delta = Math.min(delta, 0.05f);//limito delta
        float oldX = x;
        float oldY = y;
        if (activo) {

            //Vamos a ver los movimientos
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += (velocidadX + 50) * delta;//
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= (velocidadX + 50) * delta;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tocandoPiso)//Creo que justPressed seria mejor para que no revise doble salto o algo asi
            {
                System.out.println("Wtf Mono");
                velocidadY = 200;//Cambiar parametro a futuro (Osea que salte mas)
                tocandoPiso = false;
            }
        }

        //Agregar gravedad a la velocidad de y
        velocidadY += gravedad * delta;
        y += velocidadY * delta;
        //Agregar despues lo de saber si esta en suelo o no\
        hitbox.setPosition(x, y);
        tocandoPiso = false;
        for (Rectangle plataforma : mapaPlataformas) {
            if (hitbox.overlaps(plataforma)) {
                float overlapLeft = (hitbox.x + hitbox.width) - plataforma.x;
                float overlapRight = (plataforma.x + plataforma.width) - hitbox.x;
                float overlapTop = (hitbox.y + hitbox.height) - plataforma.y;
                float overlapBottom = (plataforma.y + plataforma.height) - hitbox.y;

                float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));//Poner las los dos 

                // COL DESDE ARRIBA (cayendo sobre plataforma)
                if (minOverlap == overlapBottom && velocidadY <= 0) {
                    y = plataforma.y + plataforma.height;
                    velocidadY = 0;
                    tocandoPiso = true;
                } // COL DESDE ABAJO (golpear techo)
                else if (minOverlap == overlapTop && velocidadY > 0) {
                    y = plataforma.y - hitbox.height;
                    velocidadY = 0;
                } // COL LATERAL IZQUIERDA
                else if (minOverlap == overlapLeft) {
                    x = plataforma.x - hitbox.width;
                } // COL LATERAL DERECHA
                else if (minOverlap == overlapRight) {
                    x = plataforma.x + plataforma.width;
                }

                // Actualizar hitbox con pos corregida
                hitbox.setPosition(x, y);

                if (velocidadY != 0 && !tocandoPiso) {
                    tocandoPiso = false;
                }

            }
        }

        hitbox.x = x;
        hitbox.y = y;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(sprite, x, y, hitbox.width, hitbox.height);//Lo dibuja con el tamano de eso

    }
}
