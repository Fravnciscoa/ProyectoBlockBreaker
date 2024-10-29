package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderReducirVelocidadBola extends Poder implements ModificadorPoder {
    private PingBall ball;
    private float reduccionFactor;

    public PoderReducirVelocidadBola(float x, float y, Texture imagenPoder, PingBall ball, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.ball = ball;
        this.reduccionFactor = 0.5f;  // Reducir la velocidad al 50%
    }

    @Override
    public void aplicar() {
        if (!activo) {
            ball.setSpeedMultiplier(reduccionFactor);  // Aplicar el factor de reducción
            activo = true;
        }
    }

    @Override
    public void revertir() {
        if (activo) {
            ball.setSpeedMultiplier(1.0f);  // Restablecer el multiplicador de velocidad
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
