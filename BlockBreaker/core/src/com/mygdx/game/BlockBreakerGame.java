package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// Asegúrate de importar las clases necesarias
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BlockBreakerGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shape;
	private PingBall ball;
	private Paddle pad;
	private ArrayList<Block> blocks = new ArrayList<>();
	private ArrayList<Poder> poderes = new ArrayList<>();  // Lista de poderes en pantalla
	private ArrayList<Poder> poderesActivos = new ArrayList<>(); // Lista de poderes activos
	private int vidas;
	private int puntaje;
	private int nivel;
	private Pausa pausa;
	private float ballSpeedX;
	private float ballSpeedY;
	private int multiplicadorPuntos = 1;  // Valor por defecto

	// Mapa para almacenar las texturas de los poderes
	private Map<Class<? extends Poder>, Texture> texturasPoderes;

	// Array de tipos de poderes disponibles
	private Class<? extends Poder>[] tiposDePoderes = new Class[]{
			PoderDuplicarPuntos.class,
			PoderAumentarTamañoPaddle.class,
			PoderReducirVelocidadBola.class
			// Añade aquí otras clases de poderes según las implementes
	};

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(3, 2);
		nivel = 1;
		crearBloques(2 + nivel);

		shape = new ShapeRenderer();
		ball = new PingBall(Gdx.graphics.getWidth() / 2 - 10, 41, 10, 5, 7, true);
		pad = new Paddle(Gdx.graphics.getWidth() / 2 - 50, 40, 100, 10);
		vidas = 3;
		puntaje = 0;
		pausa = Pausa.getInstance();

		// Cargar las texturas de los poderes
		cargarTexturasPoderes();
	}

	private void cargarTexturasPoderes() {
		texturasPoderes = new HashMap<>();
		texturasPoderes.put(PoderAumentarTamañoPaddle.class, new Texture(Gdx.files.internal("poderAumentarPaddle2.png")));
		texturasPoderes.put(PoderReducirVelocidadBola.class, new Texture(Gdx.files.internal("poderDisminuirVelocidad.png")));
		texturasPoderes.put(PoderDuplicarPuntos.class, new Texture(Gdx.files.internal("poderDuplicarPuntos.png")));
		// Añade aquí la carga de nuevas texturas para otros poderes
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
		font.draw(batch, "Vidas: " + vidas, Gdx.graphics.getWidth() - 10, 25);
		font.draw(batch, "Nivel : " + nivel, Gdx.graphics.getWidth()/2 , 25);
		batch.end();
	}

	// Método para ajustar el multiplicador de puntos
	public void setMultiplicadorPuntos(int multiplicador) {
		this.multiplicadorPuntos = multiplicador;
	}

	// Método para obtener los puntos correctamente multiplicados
	public int getPuntajeMultiplicado(int puntos) {
		return puntos * multiplicadorPuntos;
	}

	@Override
	public void render() {
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
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			pad.draw(shape);

			if (ball.estaQuieto()) {
				ball.setXY(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11);
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) ball.setEstaQuieto(false);
			} else {
				ball.update();
			}

			if (ball.getY() < 0) {
				vidas--;
				ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
			}

			if (vidas <= 0) {
				vidas = 3;
				nivel = 1;
				puntaje = 0;
				crearBloques(2 + nivel);
				// Limpiar poderes activos al reiniciar el juego
				poderesActivos.clear();
			}

			if (blocks.size() == 0) {
				nivel++;
				crearBloques(2 + nivel);
				ball = new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, 10, 5, 7, true);
			}

			// Dibujar bloques y manejar colisiones
			for (int i = 0; i < blocks.size(); i++) {
				Block b = blocks.get(i);
				b.draw(shape);
				ball.checkCollision(b);

				if (b.destroyed) {
					puntaje += getPuntajeMultiplicado(1);  // Aplicar el multiplicador de puntos
					blocks.remove(b);
					i--;

					// Generar un poder con probabilidad de 1/8 (12.5%)
					if (Math.random() < 0.7) {
						// Seleccionar aleatoriamente un tipo de poder
						Class<? extends Poder> tipoPoder = tiposDePoderes[new Random().nextInt(tiposDePoderes.length)];
						Texture texturaPoder = texturasPoderes.get(tipoPoder);

						// Calcular la posición central del bloque para posicionar el poder
						float centroX = b.x + (b.width / 2f) - (texturaPoder.getWidth() / 2f);
						float centroY = b.y + (b.height / 2f) - (texturaPoder.getHeight() / 2f);

						// Verificar que el poder no salga de la pantalla en el eje X
						if (centroX < 0) {
							centroX = 0;
						} else if (centroX + texturaPoder.getWidth() > Gdx.graphics.getWidth()) {
							centroX = Gdx.graphics.getWidth() - texturaPoder.getWidth();
						}

						// Verificar que el poder no salga de la pantalla en el eje Y
						if (centroY < 0) {
							centroY = 0;
						} else if (centroY + texturaPoder.getHeight() > Gdx.graphics.getHeight()) {
							centroY = Gdx.graphics.getHeight() - texturaPoder.getHeight();
						}

						// Crear instancia del poder seleccionado con la posición central ajustada
						Poder nuevoPoder = crearPoder(tipoPoder, centroX, centroY, texturaPoder);
						if (nuevoPoder != null) {
							poderes.add(nuevoPoder);
						}
					}
				}
			}

			shape.end();

			// Dibujar y actualizar poderes
			batch.begin();
			for (int i = 0; i < poderes.size(); i++) {
				Poder poder = poderes.get(i);

				poder.moverPoder(2);  // Mover el poder hacia abajo

				// Verificar colisión del poder con el paddle
				if (poder.verificarColisionConPaddle(pad)) {
					// Activar el poder de manera controlada
					activarPoder(poder);

					poderes.remove(i);           // Eliminar de poderes en pantalla
					i--;                         // Ajustar el índice
					continue;
				}

				// Eliminar poder si sale de la pantalla y no ha sido activado
				if (poder.desaparecer()) {
					poderes.remove(i);
					i--;
					continue;
				}

				// Dibujar el poder
				poder.render(batch);
			}
			batch.end();

			// Actualizar poderes activos
			for (int i = 0; i < poderesActivos.size(); i++) {
				Poder poder = poderesActivos.get(i);

				// Reducir la duración del poder activo
				poder.duracion -= Gdx.graphics.getDeltaTime();

				if (poder.duracion <= 0) {
					poder.revertirEfecto();       // Revertir el efecto del poder
					poderesActivos.remove(i);     // Eliminar de la lista de poderes activos
					i--;                          // Ajustar el índice
				}
			}

			shape.begin(ShapeRenderer.ShapeType.Filled);
			ball.checkCollision(pad);
			ball.draw(shape);
			shape.end();

			dibujaTextos();
		} else {
			pausa.dibujarPausa(batch, font);
		}
	}

	private void activarPoder(Poder poder) {
		boolean poderYaActivo = false;

		// Iterar sobre los poderes activos para verificar si ya está activo
		for (Poder activo : poderesActivos) {
			if (activo.getClass().equals(poder.getClass())) {
				activo.reiniciarDuracion(poder.duracion); // Reiniciar la duración
				poderYaActivo = true;
				break;
			}
		}

		// Si el poder no está activo, añadirlo a la lista de poderes activos
		if (!poderYaActivo) {
			poder.aplicarEfecto();
			poderesActivos.add(poder);
		}
	}

	private Poder crearPoder(Class<? extends Poder> tipoPoder, float x, float y, Texture texturaPoder) {
		try {
			if (tipoPoder == PoderDuplicarPuntos.class) {
				return new PoderDuplicarPuntos(x, y, texturaPoder, this, 5);
			} else if (tipoPoder == PoderAumentarTamañoPaddle.class) {
				return new PoderAumentarTamañoPaddle(x, y, texturaPoder, pad, 5);
			} else if (tipoPoder == PoderReducirVelocidadBola.class) {
				return new PoderReducirVelocidadBola(x, y, texturaPoder, ball, 5);
			}
			// Añade aquí más condiciones para nuevos poderes
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		shape.dispose();

		// Liberar las texturas de los poderes
		for (Texture textura : texturasPoderes.values()) {
			textura.dispose();
		}
	}
}
