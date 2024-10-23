package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
	private Paddle pad;
	private ArrayList<Block> blocks = new ArrayList<>();
	private ArrayList<PowerUp> powerUps = new ArrayList<>();
	private ArrayList<PingBall> bolas = new ArrayList<>();
	private ArrayList<Block> arcos = new ArrayList<>();

	private int vidas;
	private int puntaje;
	private int nivel;
	private int bloquesDestruidosConsecutivos = 0;
	private int totalBloquesDestruidos = 0;
	private int bloquesParaPowerUp = 3; // Número de bloques para generar un PowerUp

	private boolean pelotaEnLlamasActivo = false;
	private float tiempoPelotaEnLlamas = 0f;
	private float duracionPelotaEnLlamas = 30f; // Duración en segundos

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(3, 2);
		nivel = 1;
		crearBloques(2 + nivel);

		shape = new ShapeRenderer();
		pad = new Paddle(Gdx.graphics.getWidth()/2 - 50, 40, 100, 10);
		bolas.add(new PingBall(pad.getX() + pad.getWidth()/2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true));
		vidas = 3;
		puntaje = 0;
	}

	public void crearBloques(int filas) {
		blocks.clear();
		int blockWidth = 70;
		int blockHeight = 26;
		int y = Gdx.graphics.getHeight();
		for (int cont = 0; cont < filas; cont++) {
			y -= blockHeight + 10;
			for (int x = 5; x < Gdx.graphics.getWidth(); x += blockWidth + 10) {
				blocks.add(new Block(x, y, blockWidth, blockHeight));
			}
		}
	}

	public void dibujaTextos() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, "Puntos: " + puntaje, 10, 25);
		font.draw(batch, "Vidas : " + vidas, Gdx.graphics.getWidth()-150, 25);
		font.draw(batch, "Nivel : " + nivel, Gdx.graphics.getWidth()/2 - 50, 25);
		batch.end();
	}

	private void generarPowerUp(float x, float y) {
		PowerUp.PowerType[] tipos = PowerUp.PowerType.values();
		PowerUp.PowerType tipoAleatorio = tipos[new Random().nextInt(tipos.length)];
		powerUps.add(new PowerUp(x, y, tipoAleatorio));
	}

	private void aplicarPowerUp(PowerUp.PowerType tipo) {
		switch (tipo) {
			case CRECER_PADLE:
				pad.setWidth(pad.getWidth() + 30);
				break;
			case TRIPLICAR_BOLA:
				triplicarBola();
				break;
			case SUMAR_VIDA:
				if (vidas < 3) {
					vidas++;
				} else {
				}
				break;
			case CREAR_ARCO:
				crearArco();
				break;
			case PELOTA_EN_LLAMAS:
				pelotaEnLlamas();
				break;
			case DISMINUIR_PADLE:
				pad.setWidth(Math.max(pad.getWidth() - 30, 50));
				break;
		}
	}

	private void triplicarBola() {
		ArrayList<PingBall> nuevasBolas = new ArrayList<>();
		for (PingBall bolaOriginal : bolas) {
			if (bolas.size() + nuevasBolas.size() >= 10) { // Limitar a 10 bolas como ejemplo
				break;
			}
			PingBall bola1 = new PingBall((int)bolaOriginal.getX(), (int)bolaOriginal.getY(),
					bolaOriginal.getSize(),
					(int)bolaOriginal.getxSpeed(),
					(int)bolaOriginal.getySpeed(),
					bolaOriginal.estaQuieto());
			PingBall bola2 = new PingBall((int)bolaOriginal.getX(), (int)bolaOriginal.getY(),
					bolaOriginal.getSize(),
					(int)-bolaOriginal.getxSpeed(),
					(int)bolaOriginal.getySpeed(),
					bolaOriginal.estaQuieto());
			nuevasBolas.add(bola1);
			nuevasBolas.add(bola2);
		}
		bolas.addAll(nuevasBolas);
	}

	private void crearArco() {
		int arcoWidth = 800; // Ancho del arco
		int arcoHeight = 10; // Alto del arco
		int y = pad.getY() - arcoHeight - 5; // Justo debajo del paddle
		arcos.add(new Block(0, y, arcoWidth, arcoHeight));
	}

	private void pelotaEnLlamas() {
		pelotaEnLlamasActivo = true;
		tiempoPelotaEnLlamas = 0f;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		pad.draw(shape);

		// Actualizar y dibujar todas las bolas
		Iterator<PingBall> bolaIter = bolas.iterator();
		while (bolaIter.hasNext()) {
			PingBall bola = bolaIter.next();
			if (bola.estaQuieto()) {
				bola.setXY(pad.getX() + pad.getWidth()/2 - bola.getSize(), pad.getY() + pad.getHeight() + 11);
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
					bola.setEstaQuieto(false);
				}
			} else {
				bola.update();
			}

			// Verificar si la bola se fue por abajo
			if (bola.getY() < 0) {
				vidas--;
				bola.setXY(pad.getX() + pad.getWidth()/2 - bola.getSize(), pad.getY() + pad.getHeight() + 11);
				bola.setEstaQuieto(true);
			}

			// Dibujar la bola
			bola.draw(shape);
		}

		// Manejar colisiones y dibujar bloques
		for (Block b : blocks) {
			b.draw(shape);
			for (PingBall bola : bolas) {
				if (pelotaEnLlamasActivo) {
					bola.checkCollision(b, true);
				} else {
					bola.checkCollision(b);
				}
			}
		}

		// Dibujar arcos y manejar colisiones
		for (Block arco : arcos) {
			arco.draw(shape);
			for (PingBall bola : bolas) {
				if (pelotaEnLlamasActivo) {
					bola.checkCollision(arco, true);
				} else {
					bola.checkCollision(arco);
				}
			}
		}

		// Actualizar estado de los bloques y generar `PowerUps`
		Iterator<Block> blockIter = blocks.iterator();
		while (blockIter.hasNext()) {
			Block b = blockIter.next();
			if (b.destroyed) {
				puntaje++;
				bloquesDestruidosConsecutivos++;
				totalBloquesDestruidos++;

				blockIter.remove();

				if (bloquesDestruidosConsecutivos >= bloquesParaPowerUp) {
					generarPowerUp(b.x + b.width / 2, b.y);
					bloquesDestruidosConsecutivos = 0;
				}
			} else {
				bloquesDestruidosConsecutivos = 0;
			}
		}

		// Actualizar y dibujar `PowerUps`
		Iterator<PowerUp> puIter = powerUps.iterator();
		while (puIter.hasNext()) {
			PowerUp pu = puIter.next();
			pu.update();
			pu.draw(shape);

			if (pu.isCaught(pad)) {
				aplicarPowerUp(pu.getType());
				puIter.remove();
			} else if (pu.isOffScreen()) {
				puIter.remove();
			}
		}

		// Dibujar y manejar colisiones de las bolas con el paddle
		for (PingBall bola : bolas) {
			bola.checkCollision(pad);
		}

		shape.end();
		dibujaTextos();

		// Actualizar temporizador de `pelotaEnLlamas`
		if (pelotaEnLlamasActivo) {
			tiempoPelotaEnLlamas += Gdx.graphics.getDeltaTime();
			if (tiempoPelotaEnLlamas >= duracionPelotaEnLlamas) {
				pelotaEnLlamasActivo = false;
			}
		}

		// Manejar Game Over
		if (vidas <= 0) {
			resetGame();
		}

		// Manejar nivel completado
		if (blocks.size() == 0) {
			nivel++;
			crearBloques(2 + nivel);
			resetLevel();
		}
	}

	private void resetGame() {
		vidas = 3;
		nivel = 1;
		puntaje = 0;
		bloquesDestruidosConsecutivos = 0;
		totalBloquesDestruidos = 0;
		crearBloques(2 + nivel);
		bolas.clear();
		bolas.add(new PingBall(pad.getX() + pad.getWidth()/2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true));
		powerUps.clear();
		arcos.clear();
		pelotaEnLlamasActivo = false;
	}

	private void resetLevel() {
		bloquesDestruidosConsecutivos = 0;
		totalBloquesDestruidos = 0;
		bolas.clear();
		bolas.add(new PingBall(pad.getX() + pad.getWidth()/2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true));
		powerUps.clear();
		arcos.clear();
		pelotaEnLlamasActivo = false;
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		shape.dispose();
	}
}
