//Nuevo
package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class PowerUp {
    public enum PowerType {
        CRECER_PADLE,
        TRIPLICAR_BOLA,
        SUMAR_VIDA,
        CREAR_ARCO,
        PELOTA_EN_LLAMAS,
        DISMINUIR_PADLE
    }

    private float x, y;
    private float size = 20;
    private float speed = 2;
    private PowerType type;
    private Color color;

    public PowerUp(float x, float y, PowerType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = determinarColor(type);
    }

    private Color determinarColor(PowerType type) {
        switch (type) {
            case CRECER_PADLE:
                return Color.GREEN;
            case TRIPLICAR_BOLA:
                return Color.ORANGE;
            case SUMAR_VIDA:
                return Color.PINK;
            case CREAR_ARCO:
                return Color.CYAN;
            case PELOTA_EN_LLAMAS:
                return Color.RED;
            case DISMINUIR_PADLE:
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }

    public void update() {
        y -= speed;
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(x, y, size / 2);
    }

    public boolean isCaught(Paddle paddle) {
        return (x > paddle.getX() && x < paddle.getX() + paddle.getWidth()) &&
                (y - size / 2 < paddle.getY() + paddle.getHeight());
    }

    public PowerType getType() {
        return type;
    }

    public boolean isOffScreen() {
        return y + size / 2 < 0;
    }
}
