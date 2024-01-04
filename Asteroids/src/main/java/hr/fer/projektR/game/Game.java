package hr.fer.projektR.game;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import hr.fer.projektR.game.comparators.DangerComparator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Game implements Drawable, java.io.Serializable {
	public static final int W=960, H=760;
	public static final double DT = 10; // ms
	private static final int MAX_ASTEROID_MATERIAL = 100;
	private boolean wPressed;
	private boolean aPressed;
	private boolean dPressed;
	private boolean spacePressed;
	private long score;
	private Player player;
	private List<Asteroid> asteroids;
	private int asteroidAlarm;
	private Player.Bullet bullet;
	// !!!
	List<Asteroid> closestDistance;
	List<Asteroid> closestAngle;
	// !!!
	int asteroidsN;

	private boolean gameOver;
//	private int tmptimer;
	
	public Game() {
		this(5);
	}

	public Game(int n) {
		asteroids = new LinkedList<>();
		asteroidsN = n;
		newGame();
	}
	
	public void newGame() {
		player = new Player(W/2, H/2);
		asteroids.clear();
		spawnAsteroids(10); // !
		score = 0;
		wPressed = false;
		aPressed = false;
		dPressed = false;
		spacePressed = false;
		gameOver = false;
		
//		tmptimer = 0;
	
	}

	public List<Asteroid> filterAsteroids(Comparator<? super Asteroid> c, int n) {
		asteroids.sort(c);
		return new LinkedList<>(asteroids.subList(0, n));
	}
	
	private void setAsteroidAlarm() {
		asteroidAlarm = (int)Math.random() * 2000 + 1000;
	}
	
	public void testGame() {
		newGame();
		asteroids.clear();
		asteroids.add(new Asteroid(W/2, 300, 0, 0, 1));
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
			Vector2D pos = new Vector2D(x, y);
			Vector2D speed = Vector2D.I.rotate(Math.random() * 2 * Math.PI).scale(Math.random() * 50 + 50);				
			if (i == 0) {
				speed = Vector2D.subVector2d(player.getPos(), pos).unit().scale(100);
			}
			int size = 1<<((int) (Math.random() * 2.99)) ;
//			System.out.println(asteroids.size());
			if (asteroids.size() < MAX_ASTEROID_MATERIAL)
				asteroids.add(new Asteroid(pos, speed, size));
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
//		tmptimer++;
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
			spawnAsteroids(5);
		}asteroidAlarm--;

		player.move(DT/1000);
		for (Asteroid asteroid: asteroids) {
			asteroid.move(DT/1000);
			if (player.isHitBy(asteroid)) {
				//System.exit(1); // privremeno
				gameOver = true;
//				System.out.println(tmptimer);
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
		// !!!
//		closestDistance = filterAsteroids(new ClosestDistanceComparator(player), Math.min(asteroids.size(), 3));
//		closestAngle = filterAsteroids(new ClosestAngleComparator(player), Math.min(asteroids.size(), 3));
		// !!!
		player.draw(gc);
//		for (Asteroid asteroid: asteroids) {a
		// !!!
		// samo da najblizi asteroidi budu vizualno oznaceni
//		if (closestDistance != null) {
//			for (Asteroid a: closestDistance) {
//				gc.setStroke(Paint.valueOf("RED"));
//				a.draw(gc);
//				gc.setStroke(Paint.valueOf("WHITE"));
//			}
//		}
//
//		if (closestAngle != null) {
//			for (Asteroid a: closestAngle) {
//				gc.setStroke(Paint.valueOf("BLUE"));
//				a.draw(gc);
//				gc.setStroke(Paint.valueOf("WHITE"));
//			}
//		}


		for (Asteroid asteroid: asteroids) {
			asteroid.draw(gc);
		}

		List<Asteroid> dangerous =  filterAsteroids((new DangerComparator(player)).reversed(), Math.min(asteroids.size(), asteroidsN));
		for (Asteroid a: dangerous) {
			gc.setStroke(Paint.valueOf("BLUE"));
			a.draw(gc);
			gc.setStroke(Paint.valueOf("WHITE"));
		}
		// !!!
		if(bullet != null) {
			bullet.draw(gc);
		}
		
		gc.strokeText("Score: " + String.valueOf(score),100,100);
	}
	public static void main(String[] args) {
//		Game game = new Game();
//		game.testGame();
//		game.spaceInput();
//		game.step();
//		System.out.println(game.score);
	}
	
	public double[] getData() {
		double[] data = new double[asteroidsN * 5];
		int i = 0;
		// data[i++] = player.getOrient();
		// data[i++] = player.getForceVector().x;
		// data[i++] = player.getForceVector().y;
		List<Asteroid> dangerous =  filterAsteroids((new DangerComparator(player)).reversed(), Math.min(asteroids.size(), asteroidsN));
		for (Asteroid asteroid : dangerous) {
			Vector2D relativePos = Vector2D.subVector2d(player.getPos(),asteroid.getPos());
			Vector2D relativeSpeed = Vector2D.addVector2d(asteroid.getSpeed(), player.getSpeed());

			data[i++] = angleFromPlayer(relativePos);
			data[i++] = relativePos.abs();
			data[i++] = speedAngleFromPlayer(relativePos, relativeSpeed);
			data[i++] = relativeSpeed.abs();
			data[i++] = asteroid.size();

			// data[i++] = relativePos.x;
			// data[i++] = relativePos.y;
			// data[i++] = relativeSpeed.x;
			// data[i++] = relativeSpeed.y;
			// data[i++] = asteroid.size();
		}
		if (asteroids.size() < asteroidsN) {
			while (i < asteroidsN * 5) {
				data[i++] = Math.PI;
				data[i++] = Double.POSITIVE_INFINITY;
				data[i++] = Math.PI;
				data[i++] = 0;
				data[i++] = 0;

				// data[i++] = Double.POSITIVE_INFINITY;
				// data[i++] = Double.POSITIVE_INFINITY;
				// data[i++] = 0;
				// data[i++] = 0;
				// data[i++] = 0;				
			}
		}
		return data;
	}

	private double angleFromPlayer(Vector2D relativePos) {
		double angle = Math.atan2(relativePos.y, relativePos.x);
		return angle - player.getOrient();
	}

	private double speedAngleFromPlayer(Vector2D relativePos, Vector2D relativeSpeed) {
		double angle = Math.atan2(relativePos.y, relativePos.x);
		double speedAngle = Math.atan2(relativeSpeed.y, relativeSpeed.x);
		return angle + speedAngle;
	}
	
	public boolean isOver() {
		return gameOver;
	}
	public long getScore() {
		return score;
	}
}

// mvn exec:java -Dexec.mainClass="hr.fer.projektR.app.AsteroidsGame"


