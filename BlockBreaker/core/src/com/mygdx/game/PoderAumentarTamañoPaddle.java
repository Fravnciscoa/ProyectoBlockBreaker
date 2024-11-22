package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PoderAumentarTamañoPaddle extends Poder implements ModificadorPoder {
    private Paddle paddle;
    private int incrementoTamaño;

    public PoderAumentarTamañoPaddle(float x, float y, Texture imagenPoder, Paddle paddle, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.paddle = paddle;
        this.incrementoTamaño = 50;  // Aumentar 50 pixeles el ancho del paddle
    }

    public void aplicar() {
        if (!activo) {
            float incremento = incrementoTamaño;
            float nuevaAnchura = paddle.getWidth() + incremento;

            // Verificar si el paddle se saldría del límite derecho
            float pantallaAncho = Gdx.graphics.getWidth();
            if (paddle.getX() + nuevaAnchura > pantallaAncho) {
                // Ajustar el incremento para no salir del límite derecho
                incremento = pantallaAncho - paddle.getX() - paddle.getWidth();
            }

            paddle.setWidth((int) (paddle.getWidth() + incremento));  // Aumenta el tamaño ajustado
            activo = true;
        }
    }

    @Override
    public void revertir() {
        if (activo) {
            paddle.setWidth(paddle.getWidth() - incrementoTamaño);  // Revertir el tamaño del paddle
            activo = false;
        }
    }

    @Override
    public void aplicarEfecto() {
        aplicar();
    }

    @Override
    public void revertirEfecto() {
        revertir();  // Llamamos a revertir el efecto cuando se termine el tiempo del poder
    }
}