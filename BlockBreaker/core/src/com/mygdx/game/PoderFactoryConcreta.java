package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderFactoryConcreta implements PoderFactory {
    private Paddle paddle;
    private PingBall ball;
    private BlockBreakerGame game;
    private Texture texturaAumentarTamaño;
    private Texture texturaDuplicarPuntos;
    private Texture texturaReducirVelocidad;

    public PoderFactoryConcreta(Paddle paddle, PingBall ball, BlockBreakerGame game) {
        this.paddle = paddle;
        this.ball = ball;
        this.game = game;

        // Cargar texturas
        this.texturaAumentarTamaño = new Texture("poderAumentarPaddle2.png");
        this.texturaDuplicarPuntos = new Texture("poderDuplicarPuntos.png");
        this.texturaReducirVelocidad = new Texture("poderDisminuirVelocidad.png");
    }

    @Override
    public Poder crearPoderAumentarTamaño(float x, float y) {
        return new PoderAumentarTamañoPaddle(x, y, texturaAumentarTamaño, paddle, 5);
    }

    @Override
    public Poder crearPoderDuplicarPuntos(float x, float y) {
        return new PoderDuplicarPuntos(x, y, texturaDuplicarPuntos, game, 5);
    }

    @Override
    public Poder crearPoderReducirVelocidad(float x, float y) {
        return new PoderReducirVelocidadBola(x, y, texturaReducirVelocidad, ball, 5);
    }

    public void disposeTexturas() {
        texturaAumentarTamaño.dispose();
        texturaDuplicarPuntos.dispose();
        texturaReducirVelocidad.dispose();
    }
}

