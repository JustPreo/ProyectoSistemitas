/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Objetos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Mono extends PlayerController {

    private boolean enEscalera = false;
    private float climbingSpeed = 100;

    // Atributos para mejoras
    private int nivelMejora = 0;
    private float bonusVelocidad = 0;
    private float bonusSalto = 0;

    public Mono(String texturaPath, float xI, float yI, float velocidad, ArrayList<Rectangle> mapaPlataformas) {
        super(texturaPath, xI, yI, velocidad, mapaPlataformas);
        this.hitbox = new Rectangle(x, y, 30, 30); // Tamaño sprite    
    }

    @Override
    public void update(float delta) {
        delta = Math.min(delta, 0.05f); // Limito delta
        float oldX = x;
        float oldY = y;

        if (activo) {
            // Movimiento lateral
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += (velocidadX + 50 + bonusVelocidad) * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= (velocidadX + 50 + bonusVelocidad) * delta;
            }

            // Salto
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tocandoPiso) {
                System.out.println("Wtf Mono");
                velocidadY = 270 + bonusSalto; // El salto aumenta con las mejoras
                tocandoPiso = false;
            }
        }

        if (!enEscalera) {
            // Gravedad
            velocidadY += gravedad * delta;
        }

        y += velocidadY * delta;

        // Actualización de hitbox
        hitbox.setPosition(x, y);
        tocandoPiso = false;

        // Colisiones con plataformas
        for (Rectangle plataforma : mapaPlataformas) {
            if (hitbox.overlaps(plataforma)) {
                float overlapLeft = (hitbox.x + hitbox.width) - plataforma.x;
                float overlapRight = (plataforma.x + plataforma.width) - hitbox.x;
                float overlapTop = (hitbox.y + hitbox.height) - plataforma.y;
                float overlapBottom = (plataforma.y + plataforma.height) - hitbox.y;

                float minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom));

                // Colisión desde arriba (cayendo)
                if (minOverlap == overlapBottom && velocidadY <= 0) {
                    y = plataforma.y + plataforma.height;
                    velocidadY = 0;
                    tocandoPiso = true;
                } // Colisión desde abajo (techo)
                else if (minOverlap == overlapTop && velocidadY > 0) {
                    y = plataforma.y - hitbox.height;
                    velocidadY = 0;
                } // Colisión izquierda
                else if (minOverlap == overlapLeft) {
                    x = plataforma.x - hitbox.width;
                } // Colisión derecha
                else if (minOverlap == overlapRight) {
                    x = plataforma.x + plataforma.width;
                }

                // Actualizar hitbox tras colisión
                hitbox.setPosition(x, y);

                if (velocidadY != 0 && !tocandoPiso) {
                    tocandoPiso = false;
                }
            }
        }

        hitbox.x = x;
        hitbox.y = y;
    }

    public void subirEscalera(float delta) {
        if (!enEscalera) {
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && activo) {
            y += climbingSpeed * delta;
            System.out.println("Subiendo");
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) && activo) {
            y -= climbingSpeed * delta;
            System.out.println("Bajando");
        }
        velocidadY = 0; // Cancela gravedad
        hitbox.setPosition(x, y);
    }

    public void setEnEscalera(boolean bool) {
        enEscalera = bool;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(sprite, x, y, hitbox.width, hitbox.height);
    }

    public void setY(float y) {
        this.y += y;
    }

    
    //                 FUNCIONES DE MEJORA DEL MONO
    

    public void mejoraMono1() {
        
            bonusVelocidad += 60;
            bonusSalto += 15;
           // sprite = new Texture("mono1.png");
            nivelMejora = 1;
            System.out.println("Mono mejorado a nivel 1: more rapido y salta mas alto!");
        
    }

    public void mejoraMono2() {
        
            bonusVelocidad += 80;
            bonusSalto += 30;
           // sprite = new Texture("mono2.png");
            nivelMejora = 2;
            System.out.println("Mono mejorado a nivel 2: ahora aun mas agil y fuerte");
        
    }

    public void mejoraMono3() {
        
            bonusVelocidad += 120;
            bonusSalto += 45;
            //sprite = new Texture("mono3.png");
            nivelMejora = 3;
            System.out.println("Mono alcanzo su forma definitiva!");
        
    }

    public int getNivelMejora() {
        return nivelMejora;
    }
}