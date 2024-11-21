package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;

public abstract class JuegoBase extends ApplicationAdapter {
    protected abstract void inicializar();  // Inicializar elementos específicos del juego
    protected abstract void actualizar();   // Lógica principal del juego
    protected abstract void dibujar();      // Renderizar los elementos del juego

    @Override
    public void create() {
        inicializar();  // Llamamos al paso de inicialización
    }

    @Override
    public void render() {
        actualizar();   // Lógica de actualización
        dibujar();      // Renderizado de gráficos
    }
}
