package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BlockBreakerGame extends JuegoBase {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shape;
	private PingBall ball;
	private Paddle pad;
	private ArrayList<Block> blocks = new ArrayList<>();
	private PoderFactory poderFactory;
	private ArrayList<Poder> poderes = new ArrayList<>();
	private ArrayList<Poder> poderesActivos = new ArrayList<>();
	private int vidas;
	private int puntaje;
	private int nivel;
	private Pausa pausa;
	private float ballSpeedX;
	private float ballSpeedY;
	private int multiplicadorPuntos = 1;



	@Override
	protected void inicializar() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(3, 2);
		shape = new ShapeRenderer();
		ball = new PingBall(Gdx.graphics.getWidth() / 2 - 10, 41, 10, 5, 7, true);
		// Configurar estrategias de colisión para la bola
		ball.setEstrategiaColision(new ColisionPaddle()); // Estrategia predeterminada: colisión con paddle
		//ball.setEstrategiaColision(new ColisionBlock());
		pad = new Paddle(Gdx.graphics.getWidth() / 2 - 50, 40, 100, 10);
		vidas = 3;
		puntaje = 0;
		nivel = 1;

		poderFactory = new PoderFactoryConcreta(pad, ball, this);
		pausa = Pausa.getInstance();
		crearBloques(2 + nivel);

	}

	@Override
	protected void actualizar() {
		// Manejo de pausa
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			if (pausa.estaPausado()) {
				ball.setXSpeed(ballSpeedX);
				ball.setYSpeed(ballSpeedY);
			} else {
				ballSpeedX = ball.getXSpeed();
				ballSpeedY = ball.getYSpeed();
				ball.setXSpeed(0);
				ball.setYSpeed(0);
			}
			pausa.togglePausa();
		}

		if (!pausa.estaPausado()) {
			// Lógica de la bola
			if (ball.estaQuieto()) {
				ball.setXY(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11);
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) ball.setEstaQuieto(false);
			} else {
				ball.update();
			}

			// Bola fuera del límite inferior
			if (ball.getY() < 0) {
				vidas--; // Aquí se resta una vida
				if (vidas > 0) {
					ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
				} else {
					reiniciarJuego();
				}
			}


			// Lógica de bloques y poderes
			actualizarColisionesBloques();
			actualizarPoderes();



			// Avanzar de nivel si no quedan bloques
			if (blocks.isEmpty()) {
				nivel++;
				crearBloques(2 + nivel);
				ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
			}

			// Actualizar poderes activos
			for (int i = 0; i < poderesActivos.size(); i++) {
				Poder poder = poderesActivos.get(i);
				poder.duracion -= Gdx.graphics.getDeltaTime();
				if (poder.duracion <= 0) {
					poder.revertirEfecto();
					poderesActivos.remove(i);
					i--;
				}
			}

			// Colisión de la bola con el paddle
			ball.setEstrategiaColision(new ColisionPaddle()); // Cambiar a la estrategia de colisión con el paddle
			ball.checkCollision(pad);
		}
	}

	@Override
	protected void dibujar() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Dibujar paddle y bola
		shape.begin(ShapeRenderer.ShapeType.Filled);
		pad.draw(shape);
		ball.draw(shape);
		for (Block block : blocks) block.draw(shape);
		shape.end();

		// Dibujar poderes
		batch.begin();
		for (Poder poder : poderes) poder.render(batch);
		batch.end();

		// Dibujar texto
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, "Puntos: " + puntaje, 10, 25);
		font.draw(batch, "Vidas: " + vidas, Gdx.graphics.getWidth() - 10, 25);
		font.draw(batch, "Nivel: " + nivel, Gdx.graphics.getWidth() / 2, 25);
		batch.end();

		// Dibujar pausa (si está pausado)
		if (pausa.estaPausado()) {
			pausa.dibujarPausa(batch, font);
		}
	}

	private void crearBloques(int filas) {
		blocks.clear();
		int blockWidth = 70;
		int blockHeight = 26;
		int y = Gdx.graphics.getHeight();
		for (int fila = 0; fila < filas; fila++) {
			y -= blockHeight + 10;
			for (int x = 5; x < Gdx.graphics.getWidth(); x += blockWidth + 10) {
				blocks.add(new Block(x, y, blockWidth, blockHeight));
			}
		}
	}

	private void actualizarColisionesBloques() {
		for (int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			ball.setEstrategiaColision(new ColisionBlock()); // Cambiar a la estrategia de colisión con bloques
			ball.checkCollision(b);
			if (b.destroyed) {
				puntaje += getPuntajeMultiplicado(1);
				blocks.remove(i);
				i--;
				generarPoder(b);
			}
		}
	}


	private void generarPoder(Block b) {
		if (Math.random() < 0.7) {
			float x = b.x + b.width / 2f;
			float y = b.y + b.height / 2f;
			Poder nuevoPoder;

			// Seleccionar un tipo de poder y crearlo usando la fábrica
			int tipo = new Random().nextInt(3);
			switch (tipo) {
				case 0:
					nuevoPoder = poderFactory.crearPoderAumentarTamaño(x, y);
					break;
				case 1:
					nuevoPoder = poderFactory.crearPoderDuplicarPuntos(x, y);
					break;
				case 2:
					nuevoPoder = poderFactory.crearPoderReducirVelocidad(x, y);
					break;
				default:
					nuevoPoder = null;
			}

			if (nuevoPoder != null) {
				poderes.add(nuevoPoder);
			}
		}
	}


	private void actualizarPoderes() {
		for (int i = 0; i < poderes.size(); i++) {
			Poder poder = poderes.get(i);
			poder.moverPoder(2);  // Mueve el poder hacia abajo
			if (poder.verificarColisionConPaddle(pad)) {
				activarPoder(poder);  // Activa el poder si colisiona con el paddle
				poderes.remove(i);   // Remueve el poder activado
				i--;
			} else if (poder.desaparecer()) {
				poderes.remove(i);   // Remueve poderes que salen de la pantalla
				i--;
			}
		}
	}


	private void activarPoder(Poder poder) {
		boolean yaActivo = false;
		for (Poder activo : poderesActivos) {
			if (activo.getClass().equals(poder.getClass())) {
				activo.reiniciarDuracion(poder.duracion);  // Reinicia la duración si ya está activo
				yaActivo = true;
				break;
			}
		}
		if (!yaActivo) {
			poder.aplicarEfecto();  // Aplica el efecto del poder
			poderesActivos.add(poder);  // Agrega el poder a la lista de activos
		}
	}





	public void setMultiplicadorPuntos(int multiplicador) {
		this.multiplicadorPuntos = multiplicador;
	}

	public int getPuntajeMultiplicado(int puntos) {
		return puntos * multiplicadorPuntos;
	}

	private void reiniciarJuego() {
		vidas = 3;
		nivel = 1;
		puntaje = 0;
		crearBloques(2 + nivel);
		poderesActivos.clear();
		ball = new PingBall(Gdx.graphics.getWidth() / 2 - 10, 41, 10, 5, 7, true);


	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		shape.dispose();
		// Liberar texturas de la fábrica
		if (poderFactory instanceof PoderFactoryConcreta) {
			PoderFactoryConcreta concreta = (PoderFactoryConcreta) poderFactory;
			concreta.disposeTexturas();
		}
	}
}
