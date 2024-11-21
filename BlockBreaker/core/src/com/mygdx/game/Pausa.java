package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pausa {
    // Instancia única de la clase (privada y estática)
    private static Pausa instancia;

    private boolean pausado;
    private final GlyphLayout layout;

    // Constructor privado para evitar instanciación externa
    private Pausa() {
        pausado = false;
        layout = new GlyphLayout();
    }

    // Método público estático para obtener la única instancia
    public static Pausa getInstance() {
        if (instancia == null) {
            instancia = new Pausa();  // Crear instancia si no existe
        }
        return instancia;  // Retornar la instancia única
    }

    public void togglePausa() {
        pausado = !pausado;
    }

    public boolean estaPausado() {
        return pausado;
    }

    public void dibujarPausa(SpriteBatch batch, BitmapFont font) {
        if (pausado) {
            batch.begin();
            String mensajePausa = "Juego Pausado - Presiona ESC para continuar";
            layout.setText(font, mensajePausa);
            float textoAncho = layout.width;
            float textoAlto = layout.height;

            float x = (Gdx.graphics.getWidth() - textoAncho) / 2.2f;
            float y = (Gdx.graphics.getHeight() + textoAlto) / 2;

            font.draw(batch, mensajePausa, x, y);
            batch.end();
        }
    }
}