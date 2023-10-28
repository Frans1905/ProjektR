package fer.hr.projektR.game;

import java.util.LinkedList;
import java.util.List;

public class Game {
	public static final int W=800, H=600;
	public static final int DT = 10; // !
	private boolean wPressed = false;
	private boolean aPressed = false;
	private boolean dPressed = false;
	private boolean spacePressed = false;
	Player player = new Player();
	List<Asteroid> asteroids = new LinkedList<>();
	
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
		if (wPressed) {
			player.move(DT);
			wPressed = false;
		}
		if (aPressed) {
			player.rotateLeft(DT);
			aPressed = false;
		}
		if (dPressed) {
			player.rotateRight(DT);
			dPressed = false;
		}
		if (spacePressed) {
			Player.Bullet bullet = player.new Bullet();
			bullet.run(asteroids);
			spacePressed = false;
		}
	}
}
