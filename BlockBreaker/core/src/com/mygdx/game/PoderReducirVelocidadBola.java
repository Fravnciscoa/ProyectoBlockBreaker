package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderReducirVelocidadBola extends Poder implements ModificadorPoder {
    private PingBall ball;
    private float reduccionVelocidad;

    public PoderReducirVelocidadBola(float x, float y, Texture imagenPoder, PingBall ball, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.ball = ball;
        this.reduccionVelocidad = 2.0f;  // Reducir la velocidad en un factor de 2
    }

    @Override
    public void aplicar() {
        if (!activo) {
            ball.setXSpeed(ball.getXSpeed() / reduccionVelocidad);
            ball.setYSpeed(ball.getYSpeed() / reduccionVelocidad);
            activo = true;
        }
    }

    @Override
    public void revertir() {
        if (activo) {
            ball.setXSpeed(ball.getXSpeed() * reduccionVelocidad);
            ball.setYSpeed(ball.getYSpeed() * reduccionVelocidad);
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
