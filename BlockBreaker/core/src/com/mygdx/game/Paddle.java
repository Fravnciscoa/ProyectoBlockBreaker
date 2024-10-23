package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Paddle {
    private int x;
    private int y;
    private int width;
    private int height;

    public Paddle(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        width = ancho;
        height = alto;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void draw(ShapeRenderer shape, boolean juegoPausado) {
        shape.setColor(Color.BLUE);

        if (!juegoPausado) {
            int deltaX = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) deltaX = -15;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) deltaX = 15;

            if (x + deltaX >= 0 && x + deltaX + width <= Gdx.graphics.getWidth()) {
                x += deltaX;
            }
        }

        shape.rect(x, y, width, height);
    }

}