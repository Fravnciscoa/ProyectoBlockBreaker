package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Poder {
    protected float x, y;
    protected Texture imagenPoder;
    protected boolean activo;
    protected float duracion;  // Duración del poder en segundos

    public Poder(float x, float y, Texture imagenPoder, float duracion) {
        this.x = x;
        this.y = y;
        this.imagenPoder = imagenPoder;
        this.duracion = duracion;
        this.activo = false;
    }

    public int getWidth() {
        return imagenPoder.getWidth();
    }

    public int getHeight() {
        return imagenPoder.getHeight();
    }

    // Reiniciar duración del poder.
    public void reiniciarDuracion(float nuevaDuracion) {
        this.duracion = nuevaDuracion;
    }


    // Método abstracto para aplicar el efecto del poder
    public abstract void aplicarEfecto();

    // Método abstracto para revertir el efecto del poder
    public abstract void revertirEfecto();

    // Mueve el poder hacia abajo en el eje Y
    public void moverPoder(float velocidad) {
        this.y -= velocidad;
    }

    // Verifica si el poder colisiona con el paddle
    public boolean verificarColisionConPaddle(Paddle paddle) {
        float poderIzquierda = x;                        // Límite izquierdo del poder
        float poderDerecha = x + getWidth();             // Límite derecho del poder
        float poderArriba = y + getHeight();             // Límite superior del poder
        float poderAbajo = y;                            // Límite inferior del poder

        float paddleIzquierda = paddle.getX();           // Límite izquierdo del paddle
        float paddleDerecha = paddle.getX() + paddle.getWidth(); // Límite derecho del paddle
        float paddleArriba = paddle.getY() + paddle.getHeight(); // Límite superior del paddle
        float paddleAbajo = paddle.getY();               // Límite inferior del paddle

        // Verificar superposición en los ejes X e Y
        boolean colisionX = poderDerecha > paddleIzquierda && poderIzquierda < paddleDerecha;
        boolean colisionY = poderAbajo < paddleArriba && poderArriba > paddleAbajo;

        return colisionX && colisionY;
    }


    // Método para dibujar el poder en la pantalla
    public void render(SpriteBatch batch) {
        batch.draw(imagenPoder, x, y);  // Dibujar la textura del poder en su posición actual
    }



    // Eliminar el poder si no fue capturado
    public boolean desaparecer() {
        return y < 0; // Desaparece cuando sale de la pantalla
    }
}