package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

public class ColisionPaddle implements EstrategiaColision {

    @Override
    public void manejarColision(PingBall bola, Object objeto) {
        if (!(objeto instanceof Paddle)) return;
        Paddle paddle = (Paddle) objeto;

        PingBall.CollisionSide side = bola.collidesWith(paddle);
        if (side != PingBall.CollisionSide.NONE) {
            bola.setColor(Color.GREEN);

            switch (side) {
                case TOP:
                    bola.setY(paddle.getY() + paddle.getHeight() + bola.getSize());
                    bola.setYSpeed(Math.abs(bola.getYSpeed()));

                    // Variar xSpeed según la posición de impacto
                    float paddleCenter = paddle.getX() + paddle.getWidth() / 2f;
                    float distanceFromCenter = (bola.getX() - paddleCenter) / (paddle.getWidth() / 2f);
                    bola.setXSpeed(bola.getXSpeed() + distanceFromCenter * 2);
                    break;

                case BOTTOM:
                    bola.setY(paddle.getY() - bola.getSize());
                    bola.setYSpeed(-Math.abs(bola.getYSpeed()));
                    break;

                case LEFT:
                    bola.setX(paddle.getX() - bola.getSize());
                    bola.setXSpeed(-Math.abs(bola.getXSpeed()));
                    break;

                case RIGHT:
                    bola.setX(paddle.getX() + paddle.getWidth() + bola.getSize());
                    bola.setXSpeed(Math.abs(bola.getXSpeed()));
                    break;
            }
            bola.normalizeSpeed();
        } else {
            bola.setColor(Color.WHITE);
        }
    }
}
