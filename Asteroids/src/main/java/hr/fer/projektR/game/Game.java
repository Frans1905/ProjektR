package hr.fer.projektR.game;

import java.util.LinkedList;
import java.util.List;

import javax.imageio.plugins.tiff.ExifGPSTagSet;

import javafx.scene.canvas.GraphicsContext;

public class Game implements Drawable {
	public static final int W=960, H=760;
	public static final double DT = 10; // ms
	private static final int MAX_ASTEROID_MATERIAL = 50;
	private boolean wPressed;
	private boolean aPressed;
	private boolean dPressed;
	private boolean spacePressed;
	private long score;
	private Player player;
	private List<Asteroid> asteroids;
	private int asteroidAlarm;
	private Player.Bullet bullet;
	public Game() {
		asteroids = new LinkedList<>();
		newGame();
	}
	
	private void newGame() {
		player = new Player(W/2, H/2);
		asteroids.clear();
		spawnAsteroids(10); // !
		score = 0;
		wPressed = false;
		aPressed = false;
		dPressed = false;
		spacePressed = false;
	}
	
	private void setAsteroidAlarm() {
		asteroidAlarm = (int)Math.random() * 50 + 100;
	}
	
	public void testGame() {
		newGame();
		asteroids.clear();
//		asteroids.add(new Asteroid(W/2,100,0,0,2));
	}
	
	private void spawnAsteroids(int number) {
		for (int i = 0; i < number; i++) {
			double x = 0;
			double y = 0;
			int side = (int) (Math.random() * 4);
			switch (side) {
				case 0:
					x = Math.random() * W;
					y = 0;
					break;
				case 1:
					x = W;
					y = Math.random() * H;
					break;
				case 2:
					x = Math.random() * W;
					y = H;
					break;
				case 3:
					x = 0;
					y = Math.random() * H;
					break;
			}
			Vector2D speed = Vector2D.I.rotate(Math.random() * 2 * Math.PI).scale(Math.random() * 25 + 50); // !
			int size = 1<<((int) (Math.random() * 2.99)) ;
			System.out.println(asteroids.size());
			if (asteroids.size() < MAX_ASTEROID_MATERIAL)
				asteroids.add(new Asteroid(new Vector2D(x, y), speed, size));
		}
	}

	public void wInput() {
		wPressed = true;
	}
	public void aInput() {
		aPressed = true;
	}
	public void dInput() {
		dPressed = true;
	}
	public void spaceInput() {
		spacePressed = true;
	}

	public void step() {
		//check input
		player.setForce(false);
		if (wPressed) {
			player.setForce(true);
			wPressed = false;
		}
		if (aPressed) {
			player.rotateLeft(DT/1000);
			aPressed = false;
		}
		if (dPressed) {
			player.rotateRight(DT/1000);
			dPressed = false;
		}
		
		
		if (asteroidAlarm < 0) {
			setAsteroidAlarm();
			spawnAsteroids(1);
		}asteroidAlarm--;

		player.move(DT/1000);
		for (Asteroid asteroid: asteroids) {
			asteroid.move(DT/1000);
			if (player.isHitBy(asteroid)) {
				//System.exit(1); // privremeno
				newGame();
				return;
			}
		}
		bullet = null;
		if (spacePressed) {
			bullet = player.new Bullet();
			int size = bullet.run(asteroids);
			this.score += size * 50; // privremeno
			spacePressed = false;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public List<Asteroid> getAsteroids() {
		return asteroids;
	}

	@Override
	public void draw(GraphicsContext gc) {
		player.draw(gc);
		for (Asteroid asteroid: asteroids) {
			asteroid.draw(gc);
			
		}
		if(bullet != null) {
			bullet.draw(gc);
		}
		
		gc.strokeText("Score: " + String.valueOf(score),100,100);
	}
	public static void main(String[] args) {
		Game game = new Game();
		game.testGame();
		game.spaceInput();
		game.step();
		System.out.println(game.score);
	}
}
