package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pausa {
    private boolean pausado;
    private GlyphLayout layout;

    public Pausa() {
        pausado = false;
        layout = new GlyphLayout();  // Para calcular el tamaño del texto
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

            // Usar GlyphLayout para calcular el tamaño del texto
            layout.setText(font, mensajePausa);
            float textoAncho = layout.width;
            float textoAlto = layout.height;

            // Centramos el texto
            float x = (Gdx.graphics.getWidth() - textoAncho) / 2.2f;
            float y = (Gdx.graphics.getHeight() + textoAlto) / 2;

            // Dibujar texto centrado
            font.draw(batch, mensajePausa, x, y);
            batch.end();
        }
    }
}