package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PingBall {
	private float x;
	private float y;
	private int size;
	private float xSpeed;
	private float ySpeed;
	private Color color = Color.WHITE;
	private boolean estaQuieto;
	private float speedMultiplier = 1.0f; // Factor de velocidad inicial


	public enum CollisionSide {
		NONE, TOP, BOTTOM, LEFT, RIGHT
	}

	public PingBall(int x, int y, int size, int xSpeed, int ySpeed, boolean iniciaQuieto) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		estaQuieto = iniciaQuieto;
	}

	// Método para ajustar el multiplicador de velocidad
	public void setSpeedMultiplier(float multiplier) {
		this.speedMultiplier = multiplier;
		normalizeSpeed(); // Normalizar la velocidad cada vez que se cambia el multiplicador
	}

	// Método para obtener el multiplicador de velocidad
	public float getSpeedMultiplier() {
		return this.speedMultiplier;
	}

	public boolean estaQuieto() {
		return estaQuieto;
	}

	public void setEstaQuieto(boolean bb) {
		estaQuieto = bb;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return (int) y;
	}

	public void draw(ShapeRenderer shape){
		shape.setColor(color);
		shape.circle(x, y, size);
	}

	public void update() {
		if (estaQuieto) return;
		x += xSpeed;
		y += ySpeed;
		if (x - size < 0) {
			x = size;
			xSpeed = Math.abs(xSpeed);
			normalizeSpeed();
		} else if (x + size > Gdx.graphics.getWidth()) {
			x = Gdx.graphics.getWidth() - size;
			xSpeed = -Math.abs(xSpeed);
			normalizeSpeed();
		}
		if (y + size > Gdx.graphics.getHeight()) {
			y = Gdx.graphics.getHeight() - size;
			ySpeed = -Math.abs(ySpeed);
			normalizeSpeed();
		}
	}

	public void checkCollision(Paddle paddle) {
		CollisionSide side = collidesWith(paddle);
		if (side != CollisionSide.NONE) {
			color = Color.GREEN;

			switch (side) {
				case TOP:
					y = paddle.getY() + paddle.getHeight() + size;
					ySpeed = Math.abs(ySpeed);

					// Variar xSpeed según la posición de impacto
					float paddleCenter = paddle.getX() + paddle.getWidth() / 2f;
					float distanceFromCenter = (x - paddleCenter) / (paddle.getWidth() / 2f);
					xSpeed += distanceFromCenter * 2;
					break;

				case BOTTOM:
					y = paddle.getY() - size;
					ySpeed = -Math.abs(ySpeed);
					break;

				case LEFT:
					x = paddle.getX() - size;
					xSpeed = -Math.abs(xSpeed);
					break;

				case RIGHT:
					x = paddle.getX() + paddle.getWidth() + size;
					xSpeed = Math.abs(xSpeed);
					break;
			}
			normalizeSpeed();
		} else {
			color = Color.WHITE;
		}
	}

	private CollisionSide collidesWith(Paddle paddle) {
		if ((paddle.getX() + paddle.getWidth() >= x - size) && (paddle.getX() <= x + size) &&
				(paddle.getY() + paddle.getHeight() >= y - size) && (paddle.getY() <= y + size)) {

			float overlapLeft = (x + size) - paddle.getX();
			float overlapRight = (paddle.getX() + paddle.getWidth()) - (x - size);
			float overlapTop = (paddle.getY() + paddle.getHeight()) - (y - size);
			float overlapBottom = (y + size) - paddle.getY();

			float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

			if (minOverlap == overlapLeft) {
				return CollisionSide.LEFT;
			} else if (minOverlap == overlapRight) {
				return CollisionSide.RIGHT;
			} else if (minOverlap == overlapTop) {
				return CollisionSide.TOP;
			} else {
				return CollisionSide.BOTTOM;
			}
		}
		return CollisionSide.NONE;
	}

	public void checkCollision(Block block) {
		CollisionSide side = collidesWith(block);
		if (side != CollisionSide.NONE) {
			switch (side) {
				case TOP:
					y = block.y + block.height + size;
					ySpeed = Math.abs(ySpeed);
					break;

				case BOTTOM:
					y = block.y - size;
					ySpeed = -Math.abs(ySpeed);
					break;

				case LEFT:
					x = block.x - size;
					xSpeed = -Math.abs(xSpeed);
					break;

				case RIGHT:
					x = block.x + block.width + size;
					xSpeed = Math.abs(xSpeed);
					break;
			}
			normalizeSpeed();
			block.destroyed = true;
		}
	}

	private CollisionSide collidesWith(Block block) {
		if ((block.x + block.width >= x - size) && (block.x <= x + size) &&
				(block.y + block.height >= y - size) && (block.y <= y + size)) {

			float overlapLeft = (x + size) - block.x;
			float overlapRight = (block.x + block.width) - (x - size);
			float overlapTop = (block.y + block.height) - (y - size);
			float overlapBottom = (y + size) - block.y;

			float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

			if (minOverlap == overlapLeft) {
				return CollisionSide.LEFT;
			} else if (minOverlap == overlapRight) {
				return CollisionSide.RIGHT;
			} else if (minOverlap == overlapTop) {
				return CollisionSide.TOP;
			} else {
				return CollisionSide.BOTTOM;
			}
		}
		return CollisionSide.NONE;
	}

	private void normalizeSpeed() {
		float baseSpeed = 7.0f; // Velocidad base deseada
		float desiredSpeed = baseSpeed * speedMultiplier; // Velocidad ajustada por el multiplicador

		float currentSpeed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);

		if (currentSpeed == 0) {
			// Asignar una velocidad aleatoria si la bola está quieta
			xSpeed = desiredSpeed * ((Math.random() > 0.5) ? 1 : -1);
			ySpeed = desiredSpeed * ((Math.random() > 0.5) ? 1 : -1);
		} else {
			float factor = desiredSpeed / currentSpeed;
			xSpeed *= factor;
			ySpeed *= factor;
		}
	}

	public void setXSpeed(float xSpeed) {
		this.xSpeed = xSpeed;
	}

	public void setYSpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public float getXSpeed() {
		return xSpeed;
	}

	public float getYSpeed() {
		return ySpeed;
	}

}