package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderReducirVelocidadBola extends Poder implements ModificadorPoder {
    private PingBall ball;
    private float reduccionFactor;

    public PoderReducirVelocidadBola(float x, float y, Texture imagenPoder, PingBall ball, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.ball = ball;
        this.reduccionFactor = 0.5f; // Reduce la velocidad al 50%
    }

    @Override
    public void aplicar() {
        if (!activo) {
            ball.addSpeedModifier(reduccionFactor); // Agrega el modificador a la lista
            ball.normalizeSpeed();
            activo = true;
        }
    }

    @Override
    public void revertir() {
        if (activo) {
            ball.removeSpeedModifier(reduccionFactor); // Remueve el modificador de la lista
            activo = false;
        }
    }

    @Override
    public void aplicarEfecto() {
        aplicar();
    }

    @Override
    public void revertirEfecto() {
        revertir();
    }
}


