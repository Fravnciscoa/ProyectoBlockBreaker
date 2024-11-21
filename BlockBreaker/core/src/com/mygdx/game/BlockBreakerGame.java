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
	private ArrayList<Poder> poderes = new ArrayList<>();
	private ArrayList<Poder> poderesActivos = new ArrayList<>();
	private int vidas;
	private int puntaje;
	private int nivel;
	private Pausa pausa;
	private float ballSpeedX;
	private float ballSpeedY;
	private int multiplicadorPuntos = 1;
	private Map<Class<? extends Poder>, Texture> texturasPoderes;
	private Class<? extends Poder>[] tiposDePoderes = new Class[]{
			PoderDuplicarPuntos.class,
			PoderAumentarTamañoPaddle.class,
			PoderReducirVelocidadBola.class
	};

	@Override
	protected void inicializar() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(3, 2);
		shape = new ShapeRenderer();
		ball = new PingBall(Gdx.graphics.getWidth() / 2 - 10, 41, 10, 5, 7, true);
		pad = new Paddle(Gdx.graphics.getWidth() / 2 - 50, 40, 100, 10);
		vidas = 3;
		puntaje = 0;
		nivel = 1;
		pausa = Pausa.getInstance();
		crearBloques(2 + nivel);
		cargarTexturasPoderes();
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
				vidas--;
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
		font.draw(batch, "Vidas: " + vidas, Gdx.graphics.getWidth() - 150, 25);
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
			Class<? extends Poder> tipoPoder = tiposDePoderes[new Random().nextInt(tiposDePoderes.length)];
			Texture textura = texturasPoderes.get(tipoPoder);
			float x = b.x + b.width / 2f - textura.getWidth() / 2f;
			float y = b.y + b.height / 2f - textura.getHeight() / 2f;
			Poder nuevoPoder = crearPoder(tipoPoder, x, y, textura);
			if (nuevoPoder != null) poderes.add(nuevoPoder);
		}
	}

	private void actualizarPoderes() {
		for (int i = 0; i < poderes.size(); i++) {
			Poder poder = poderes.get(i);
			poder.moverPoder(2);
			if (poder.verificarColisionConPaddle(pad)) {
				activarPoder(poder);
				poderes.remove(i);
				i--;
			} else if (poder.desaparecer()) {
				poderes.remove(i);
				i--;
			}
		}
	}

	private void activarPoder(Poder poder) {
		boolean yaActivo = false;
		for (Poder activo : poderesActivos) {
			if (activo.getClass().equals(poder.getClass())) {
				activo.reiniciarDuracion(poder.duracion);
				yaActivo = true;
				break;
			}
		}
		if (!yaActivo) {
			poder.aplicarEfecto();
			poderesActivos.add(poder);
		}
	}

	private Poder crearPoder(Class<? extends Poder> tipo, float x, float y, Texture textura) {
		try {
			if (tipo == PoderDuplicarPuntos.class) return new PoderDuplicarPuntos(x, y, textura, this, 5);
			if (tipo == PoderAumentarTamañoPaddle.class) return new PoderAumentarTamañoPaddle(x, y, textura, pad, 5);
			if (tipo == PoderReducirVelocidadBola.class) return new PoderReducirVelocidadBola(x, y, textura, ball, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void cargarTexturasPoderes() {
		texturasPoderes = new HashMap<>();
		texturasPoderes.put(PoderAumentarTamañoPaddle.class, new Texture(Gdx.files.internal("poderAumentarPaddle2.png")));
		texturasPoderes.put(PoderReducirVelocidadBola.class, new Texture(Gdx.files.internal("poderDisminuirVelocidad.png")));
		texturasPoderes.put(PoderDuplicarPuntos.class, new Texture(Gdx.files.internal("poderDuplicarPuntos.png")));
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
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		shape.dispose();
		for (Texture textura : texturasPoderes.values()) textura.dispose();
	}
}
