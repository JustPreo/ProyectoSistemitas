package com.monkey.com.Main;

import com.monkey.com.Main.Levels.Prueba;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monkey.com.Main.Levels.Lvl1;
import com.monkey.com.Main.Menus.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
        //setScreen(new MenuScreen());
        //setScreen(new TestMapa());
        setScreen(new Lvl1());
    }
}
