package com.monkey.com.Main.Menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monkey.com.Main.Main;

public class PantallaSettings implements Screen {

    private Stage stage;
    private Skin skin;
    private Texture bg;
    private Texture texturaBoton, texturaBoton2;
    private Button.ButtonStyle estiloBoton;
    private Preferences prefs;

    private Music musica; //Despues

    public PantallaSettings() {}

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        bg = new Texture("Fondo/FondoST.png");
        prefs = Gdx.app.getPreferences("configuracion_juego");

        
        texturaBoton = new Texture(Gdx.files.internal("Fondo/FondoNormal.png"));
        texturaBoton2 = new Texture(Gdx.files.internal("Fondo/FondoNormal2.png"));

        Drawable drawableUp = new TextureRegionDrawable(new TextureRegion(texturaBoton));
        Drawable drawableDown = new TextureRegionDrawable(new TextureRegion(texturaBoton2));

        estiloBoton = new Button.ButtonStyle();
        estiloBoton.up = drawableUp;
        estiloBoton.down = drawableDown;

        crearInterfaz();
    }

    private void crearInterfaz() {
        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();

        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-vaV7.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        BitmapFont pixelFont = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle estiloTexto = new Label.LabelStyle(pixelFont, Color.WHITE);

        
        Label titulo = new Label("Configuracion", estiloTexto);
        tabla.add(titulo).padBottom(40).row();

        
        Label lblVolumen = new Label("Volumen Musica", estiloTexto);
        final Slider sliderVolumen = new Slider(0f, 1f, 0.01f, false, skin);
        sliderVolumen.setValue(prefs.getFloat("volumen", 0.5f));
        tabla.add(lblVolumen).padBottom(10);
        tabla.row();
        tabla.add(sliderVolumen).width(200).padBottom(30);
        tabla.row();

       
        final CheckBox checkFull = new CheckBox(" Pantalla Completa", skin);
        checkFull.setChecked(prefs.getBoolean("fullscreen", false));
        tabla.add(checkFull).padBottom(30).row();

        
        final CheckBox checkFPS = new CheckBox(" Mostrar FPS", skin);
        checkFPS.setChecked(prefs.getBoolean("mostrar_fps", true));
        tabla.add(checkFPS).padBottom(30).row();

       
        Button btnGuardar = new Button(estiloBoton);
        Label labelGuardar = new Label("Guardar", estiloTexto);
        btnGuardar.add(labelGuardar).pad(10);
        btnGuardar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefs.putFloat("volumen", sliderVolumen.getValue());
                prefs.putBoolean("fullscreen", checkFull.isChecked());
                prefs.putBoolean("mostrar_fps", checkFPS.isChecked());
                prefs.flush();

                
                if (checkFull.isChecked()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(1280, 720);
                }

                mostrarMensaje("Configuracion guardada");
            }
        });
        tabla.add(btnGuardar).size(200, 50).padBottom(15).row();

        
        Button btnRestablecer = new Button(estiloBoton);
        Label labelRestablecer = new Label("Restablecer", estiloTexto);
        btnRestablecer.add(labelRestablecer).pad(10);
        btnRestablecer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefs.clear();
                prefs.flush();
                sliderVolumen.setValue(0.5f);
                checkFull.setChecked(false);
                checkFPS.setChecked(true);
                mostrarMensaje("Configuracion restablecida");
            }
        });
        tabla.add(btnRestablecer).size(200, 50).padBottom(15).row();

       
        Button btnVolver = new Button(estiloBoton);
        Label labelVolver = new Label("Volver", estiloTexto);
        btnVolver.add(labelVolver).pad(10);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Main) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        });
        tabla.add(btnVolver).size(200, 50).padBottom(15).row();

        stage.addActor(tabla);
    }

    private void mostrarMensaje(String texto) {
        Label mensaje = new Label(texto, skin);
        mensaje.setColor(Color.GREEN);
        mensaje.setPosition(20, Gdx.graphics.getHeight() - 50);
        stage.addActor(mensaje);

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Gdx.app.postRunnable(mensaje::remove);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        bg.dispose();
        texturaBoton.dispose();
        texturaBoton2.dispose();
    }
}