package com.monkey.com.Main.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverMenu {

    public enum MenuType {
        NIVEL_FALLIDO, NIVEL_COMPLETADO
    }

    public enum MenuAction {
        NINGUNA, REINTENTAR, MENU_PRINCIPAL, SIGUIENTE_NIVEL
    }

    private Stage stage;
    private TextButton botonReintentar, botonMenu, botonSiguiente;
    private Table table;
    private Skin skin;
    private boolean mostrar;
    private MenuType tipo;
    private MenuAction accionSeleccionada = MenuAction.NINGUNA;

    public GameOverMenu() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        mostrar = false;

        botonReintentar = new TextButton("Reintentar", skin);
        botonMenu = new TextButton("Menu Principal", skin);
        botonSiguiente = new TextButton("Siguiente Nivel", skin);

        botonReintentar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                accionSeleccionada = MenuAction.REINTENTAR;
            }
        });
        botonMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                accionSeleccionada = MenuAction.MENU_PRINCIPAL;
            }
        });
        botonSiguiente.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                accionSeleccionada = MenuAction.SIGUIENTE_NIVEL;
            }
        });

        
    }

    public void mostrar(MenuType tipo) {
        this.tipo = tipo;
        this.mostrar = true;
        accionSeleccionada = MenuAction.NINGUNA;

        table.clear();
        table.center();

        if (tipo == MenuType.NIVEL_FALLIDO) {
            table.add(botonReintentar).pad(10).row();
            table.add(botonMenu).pad(10).row();
        } else {
            table.add(botonReintentar).pad(10).row();
            table.add(botonMenu).pad(10).row();
            table.add(botonSiguiente).pad(10).row();
        }

        Gdx.input.setInputProcessor(stage);
    }

    public void ocultar() {
        this.mostrar = false;
        Gdx.input.setInputProcessor(null);
        accionSeleccionada = MenuAction.NINGUNA;
    }

    public void render(float delta) {
        if (!mostrar) {
            return;
        }
        stage.act(delta);
        stage.draw();
    }

    public MenuAction getAccion() {
        return accionSeleccionada;
    }

    public boolean estaMostrado() {
        return mostrar;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
