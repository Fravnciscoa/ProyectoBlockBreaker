package com.mygdx.game;

public interface PoderFactory {
    Poder crearPoderAumentarTamaño(float x, float y);
    Poder crearPoderDuplicarPuntos(float x, float y);
    Poder crearPoderReducirVelocidad(float x, float y);
}