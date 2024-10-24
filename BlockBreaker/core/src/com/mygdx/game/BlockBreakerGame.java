package com.mygdx.game;

import java.util.ArrayList;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class BlockBreakerGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shape;
	private PingBall ball;
	private Paddle pad;
	private ArrayList<Block> blocks = new ArrayList<>();
	private int vidas;
	private int puntaje;
	private int nivel;
	private Pausa pausa;
	private float ballSpeedX;
	private float ballSpeedY;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(3, 2);
		nivel = 1;
		crearBloques(2+nivel);

		shape = new ShapeRenderer();
		ball = new PingBall(Gdx.graphics.getWidth()/2-10, 41, 10, 5, 7, true);
		pad = new Paddle(Gdx.graphics.getWidth()/2-50,40,100,10);
		vidas = 3;
		puntaje = 0;
		pausa = new Pausa();
	}
	public void crearBloques(int filas) {
		blocks.clear();
		int blockWidth = 70;
		int blockHeight = 26;
		int y = Gdx.graphics.getHeight();
		for (int cont = 0; cont<filas; cont++ ) {
			y -= blockHeight+10;
			for (int x = 5; x < Gdx.graphics.getWidth(); x += blockWidth + 10) {
				blocks.add(new Block(x, y, blockWidth, blockHeight));
			}
		}
	}
	public void dibujaTextos() {
		//actualizar matrices de la cámara
		camera.update();
		//actualizar
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//dibujar textos
		font.draw(batch, "Puntos: " + puntaje, 10, 25);
		font.draw(batch, "Vidas : " + vidas, Gdx.graphics.getWidth()-20, 25);
		batch.end();
	}

	@Override
	public void render () {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			if (pausa.estaPausado()) {
				// Al reanudar el juego, restaurar las velocidades de la pelota
				ball.setXSpeed(ballSpeedX);
				ball.setYSpeed(ballSpeedY);
			} else {
				// Al pausar el juego, guardar las velocidades actuales y detener la pelota
				ballSpeedX = ball.getXSpeed();
				ballSpeedY = ball.getYSpeed();
				ball.setXSpeed(0);
				ball.setYSpeed(0);
			}
			pausa.togglePausa();
		}

		if (!pausa.estaPausado()) {
			// Lógica normal del juego cuando no está pausado
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			pad.draw(shape);

			// Monitorear inicio del juego
			if (ball.estaQuieto()) {
				ball.setXY(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11);
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) ball.setEstaQuieto(false);
			} else {
				ball.update();  // Solo actualizar si no está quieto y no pausado
			}

			// Verificar si la pelota cayó abajo
			if (ball.getY() < 0) {
				vidas--;
				ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
			}

			// Verificar si se acabaron las vidas
			if (vidas <= 0) {
				vidas = 3;
				nivel = 1;
				puntaje = 0;
				crearBloques(2 + nivel);
			}

			// Verificar si el nivel se completó
			if (blocks.size() == 0) {
				nivel++;
				crearBloques(2 + nivel);
				ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
			}

			// Dibujar los bloques
			for (Block b : blocks) {
				b.draw(shape);
				ball.checkCollision(b);
			}

			// Actualizar el estado de los bloques y el puntaje
			for (int i = 0; i < blocks.size(); i++) {
				Block b = blocks.get(i);
				if (b.destroyed) {
					puntaje++;
					blocks.remove(b);
					i--; // Ajuste para no saltarse un bloque después de eliminar
				}
			}

			// Verificar colisión con el paddle y dibujar la pelota
			ball.checkCollision(pad);
			ball.draw(shape);

			shape.end();
			dibujaTextos();  // Dibujar puntos y vidas en pantalla
		} else {
			// Si el juego está pausado, dibujar mensaje de pausa
			pausa.dibujarPausa(batch, font);
		}
	}




	@Override
	public void dispose () {

	}
}