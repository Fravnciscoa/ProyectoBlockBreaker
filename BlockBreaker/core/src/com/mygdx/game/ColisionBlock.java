package com.mygdx.game;

public class ColisionBlock implements EstrategiaColision {

    @Override
    public void manejarColision(PingBall bola, Object objeto) {
        if (!(objeto instanceof Block)) return;
        Block block = (Block) objeto;

        PingBall.CollisionSide side = bola.collidesWith(block);
        if (side != PingBall.CollisionSide.NONE) {
            switch (side) {
                case TOP:
                    bola.setY(block.y + block.height + bola.getSize());
                    bola.setYSpeed(Math.abs(bola.getYSpeed()));
                    break;

                case BOTTOM:
                    bola.setY(block.y - bola.getSize());
                    bola.setYSpeed(-Math.abs(bola.getYSpeed()));
                    break;

                case LEFT:
                    bola.setX(block.x - bola.getSize());
                    bola.setXSpeed(-Math.abs(bola.getXSpeed()));
                    break;

                case RIGHT:
                    bola.setX(block.x + block.width + bola.getSize());
                    bola.setXSpeed(Math.abs(bola.getXSpeed()));
                    break;
            }
            bola.normalizeSpeed();
            block.destroyed = true;
        }
    }
}
