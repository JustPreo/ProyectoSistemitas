/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Objetos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Prisionero extends PlayerController {

    public Prisionero(String texturaPath, float xI, float yI, float velocidad, ArrayList<Rectangle> mapaPlataformas) {
        super(texturaPath, xI, yI, velocidad, mapaPlataformas);
        this.hitbox = new Rectangle(x, y, 32, 64);//Tamano sprite
    }

    public void update(float delta) {
        // Guardar posicion anterior
        delta = Math.min(delta, 0.05f);//limito delta
        float oldX = x;
        float oldY = y;

        // Movimiento horizontal
        if (activo) {
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += velocidadX * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= velocidadX * delta;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tocandoPiso) {
                velocidadY = 150;
                tocandoPiso = false;
            }

        }

        // Aplicar gravedad
        velocidadY += gravedad * delta;
        y += velocidadY * delta;

        // Actualizar hitbox con nueva pos
        hitbox.setPosition(x, y);

        // Reset flag de piso
        tocandoPiso = false;

        // Verificar colisiones
        for (Rectangle plataforma : mapaPlataformas) {
            if (hitbox.overlaps(plataforma)) {

                // Calcular overlap en cada dir
                float overlapLeft = (hitbox.x + hitbox.width) - plataforma.x;
                float overlapRight = (plataforma.x + plataforma.width) - hitbox.x;
                float overlapTop = (hitbox.y + hitbox.height) - plataforma.y;
                float overlapBottom = (plataforma.y + plataforma.height) - hitbox.y;

                // Encontrar el overlap mínimo (indica la dir de la col)
                float minOverlap = Math.min(
                        Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom)
                );

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
            }
        }

        // Si no hay colisiones verticales, no esta en el piso
        if (velocidadY != 0 && !tocandoPiso) {
            tocandoPiso = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {//Pedir el batch
        batch.draw(sprite, x, y, hitbox.width, hitbox.height);//Lo dibuja con el tamano de eso

    }
}
