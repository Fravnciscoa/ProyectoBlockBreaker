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
        // Lógica simple de colisión entre el poder y el paddle
        return (paddle.getX() < x && paddle.getX() + paddle.getWidth() > x &&
                paddle.getY() < y && paddle.getY() + paddle.getHeight() > y);
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