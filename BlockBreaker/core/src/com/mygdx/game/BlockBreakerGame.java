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
	private ArrayList<Poder> poderes = new ArrayList<>();  // Lista de poderes activos
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
		pausa = new Pausa();

		// Cargar las texturas de los poderes
		cargarTexturasPoderes();
	}

	private void cargarTexturasPoderes() {
		texturasPoderes = new HashMap<>();
		texturasPoderes.put(PoderAumentarTamañoPaddle.class, new Texture(Gdx.files.internal("poderAumentarPaddle.png")));
		texturasPoderes.put(PoderReducirVelocidadBola.class, new Texture(Gdx.files.internal("poderReducirVelocidad.png")));
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
		font.draw(batch, "Vidas: " + vidas, Gdx.graphics.getWidth() - 200, 25);
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

					// Generar un poder con probabilidad de 1/8
					if (Math.random() < 0.125) {
						// Seleccionar aleatoriamente un tipo de poder
						Class<? extends Poder> tipoPoder = tiposDePoderes[new Random().nextInt(tiposDePoderes.length)];
						Texture texturaPoder = texturasPoderes.get(tipoPoder);

						// Crear instancia del poder seleccionado
						Poder nuevoPoder = crearPoder(tipoPoder, b.x, b.y, texturaPoder);
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
					poder.aplicarEfecto();  // Aplicar el efecto del poder
					poder.activo = true;
					poder.duracion = 5 * 60;  // Duración en "frames", suponiendo 60 FPS (5 segundos)
				}

				// Actualizar duración y eliminar si es necesario
				if (poder.activo) {
					poder.duracion--;  // Restar 1 en cada frame
					if (poder.duracion <= 0) {
						poder.revertirEfecto();  // Revertir el efecto después de que el tiempo haya expirado
						poder.activo = false;  // Desactivar el poder
						poderes.remove(poder);  // Ahora eliminamos el poder de la lista
						i--;  // Ajustar el índice si es necesario
						continue;  // Continuamos para evitar errores en el siguiente ciclo
					}
				}

				// Eliminar poder si sale de la pantalla y no ha sido activado
				if (!poder.activo && poder.desaparecer()) {
					poderes.remove(poder);
					i--;
					continue;
				}

				// Dibujar el poder si aún no ha sido activado
				if (!poder.activo) {
					poder.render(batch);
				}
			}




			batch.end();

			shape.begin(ShapeRenderer.ShapeType.Filled);
			ball.checkCollision(pad);
			ball.draw(shape);
			shape.end();

			dibujaTextos();
		} else {
			pausa.dibujarPausa(batch, font);
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