package fer.hr.projektR.game;

import java.util.LinkedList;
import java.util.List;

public class Game {
	public static final int W=800, H=600;
	public static final double DT = 10; // ms
	private boolean wPressed;
	private boolean aPressed;
	private boolean dPressed;
	private boolean spacePressed;
	private long score;
	Player player;
	List<Asteroid> asteroids;
	
	public Game() {
		asteroids = new LinkedList<>();
		newGame();
	}
	
	void newGame() {
		player = new Player(W/2, H/2);
		asteroids.clear();
		spawnAsteroids(10); // !
		score = 0;
		wPressed = false;
		aPressed = false;
		dPressed = false;
		spacePressed = false;
	}
	
	void spawnAsteroids(int number) {
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
			Vector2D speed = Vector2D.I.rotate(Math.random() * 2 * Math.PI).scale(Math.random() * 5); // !
			int size = (int) (Math.random() * Asteroid.maxSize) + 1;

			asteroids.add(new Asteroid(new Vector2D(x, y), speed, size));
		}
	}

	void wInput() {
		wPressed = true;
	}
	void aInput() {
		aPressed = true;
	}
	void dInput() {
		dPressed = true;
	}
	void spaceInput() {
		spacePressed = true;
	}

	void step() {
		//check input
		player.setForce(Player.Decelerate);
		if (wPressed) {
			player.setForce(Player.Accelerate);
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
		if (spacePressed) {
			Player.Bullet bullet = player.new Bullet();
			int size = bullet.run(asteroids);
			this.score += size * 50; // privremeno
			spacePressed = false;
		}
		player.move(DT/1000);
		for (Asteroid asteroid: asteroids) {
			asteroid.move(DT/1000);
			if (asteroid.contains(player.getPos())) {
				//System.exit(1); // privremeno
				newGame();
				return;
			}
		}
	}
}
