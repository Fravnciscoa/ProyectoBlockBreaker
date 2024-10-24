package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class PoderDuplicarPuntos extends Poder implements ModificadorPoder {
    private BlockBreakerGame game;

    public PoderDuplicarPuntos(float x, float y, Texture imagenPoder, BlockBreakerGame game, float duracion) {
        super(x, y, imagenPoder, duracion);
        this.game = game;
    }

    @Override
    public void aplicar() {
        if (!activo) {
            game.setMultiplicadorPuntos(2);  // Multiplica por 2 los puntos obtenidos
            activo = true;
        }
    }

    @Override
    public void revertir() {
        if (activo) {
            game.setMultiplicadorPuntos(1);  // Restablece el multiplicador de puntos
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
