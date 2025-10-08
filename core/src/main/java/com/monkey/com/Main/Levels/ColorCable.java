/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monkey.com.Main.Levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ColorCable {
    public String color;
    public Vector2 start;
    public Vector2 end;
    public Vector2 tempEnd;
    private boolean connected;

    public ColorCable(String color, Vector2 start, Vector2 end) {
        this.color = color;
        this.start = start;
        this.end = end;
        this.tempEnd = new Vector2(start);
    }

    public void draw(SpriteBatch batch, Texture cable, Texture connector) {
        batch.draw(connector, start.x - 16, start.y - 16, 32, 32);
        batch.draw(connector, end.x - 16, end.y - 16, 32, 32);
        batch.draw(cable, start.x, start.y, tempEnd.x - start.x, 4);
    }

    public void tryConnect(Vector2 pos) {
        if (pos.dst(end) < 40) {
            tempEnd.set(end);
            connected = true;
        } else {
            tempEnd.set(start);
        }
    }

    public boolean isConnected() { return connected; }
    public float startDst(Vector2 v) { return v.dst(start); }
}