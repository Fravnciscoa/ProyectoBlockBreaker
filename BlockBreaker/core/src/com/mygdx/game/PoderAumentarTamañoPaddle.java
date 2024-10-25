package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderAumentarTamañoPaddle extends Poder implements ModificadorPoder {
    private Paddle paddle;
    private int incrementoTamaño;

    public PoderAumentarTamañoPaddle(float x, float y, Texture imagenPoder, Paddle paddle, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.paddle = paddle;
        this.incrementoTamaño = 50;  // Aumentar 50 pixeles el ancho del paddle
    }

    @Override
    public void aplicar() {
        if (!activo) {
            paddle.setWidth(paddle.getWidth() + incrementoTamaño);  // Aumentar el tamaño del paddle
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