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
	private EstrategiaColision estrategiaColision; // Estrategia de colisión actual

	public void setY(int y) {
		this.y = y;
	}
	public void setX(int x) {
		this.x = x;
	}

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

	// Métodos relacionados con la velocidad
	public void setSpeedMultiplier(float multiplier) {
		this.speedMultiplier = multiplier;
		normalizeSpeed();
	}

	public float getSpeedMultiplier() {
		return this.speedMultiplier;
	}

	// Métodos relacionados con el estado de la bola
	public boolean estaQuieto() {
		return estaQuieto;
	}

	public void setEstaQuieto(boolean bb) {
		estaQuieto = bb;
	}

	// Métodos relacionados con la posición
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return (int) y;
	}

	public float getX() {
		return x;
	}

	public float getYFloat() {
		return y;
	}

	public int getSize() {
		return size;
	}

	// Métodos para velocidad en cada eje
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

	// Método para cambiar el color
	public void setColor(Color color) {
		this.color = color;
	}

	// Métodos de dibujo y actualización
	public void draw(ShapeRenderer shape) {
		shape.setColor(color);
		shape.circle(x, y, size);
	}

	public void update() {
		if (estaQuieto) return;
		x += xSpeed;
		y += ySpeed;

		// Comprobar bordes de la pantalla
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

	// Métodos relacionados con colisiones
	public void setEstrategiaColision(EstrategiaColision estrategia) {
		this.estrategiaColision = estrategia;
	}

	public void checkCollision(Object objeto) {
		if (estrategiaColision != null) {
			estrategiaColision.manejarColision(this, objeto);
		}
	}

	// Métodos para calcular las colisiones (delegados a las estrategias)
	public CollisionSide collidesWith(Paddle paddle) {
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

	public CollisionSide collidesWith(Block block) {
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

	// Método para normalizar la velocidad
    void normalizeSpeed() {
		float baseSpeed = 7.0f; // Velocidad base deseada
		float desiredSpeed = baseSpeed * speedMultiplier; // Velocidad ajustada por el multiplicador

		float currentSpeed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);

		if (currentSpeed == 0) {
			xSpeed = desiredSpeed * ((Math.random() > 0.5) ? 1 : -1);
			ySpeed = desiredSpeed * ((Math.random() > 0.5) ? 1 : -1);
		} else {
			float factor = desiredSpeed / currentSpeed;
			xSpeed *= factor;
			ySpeed *= factor;
		}
	}
}
