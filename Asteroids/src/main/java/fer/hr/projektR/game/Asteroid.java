package fer.hr.projektR.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Asteroid extends GameObject{
	private int nSize;
	private final int radius = 100;
	public static final int maxSize = 3; // !
	public Asteroid(Vector2D pos, Vector2D speed, int size) {
		super(pos, speed);
		nSize = size;
	}
	
	public Asteroid(double x, double y, double dx, double dy, int size) {
		super(x, y, dx, dy);
		nSize = size;
	}
	
	public int size() {
		return nSize * radius;
	}
	
	public int getSize() {
		return nSize;
	}
	
	public boolean contains(Vector2D dot) {
		double distance = Vector2D.addVector2d(dot, Vector2D.scalarMulVector2d(getPos(), -1)).abs();
		if (distance <= size()) {
			return true;
		}
		return false;
	}

	public Collection<? extends Asteroid> split() {
		List<Asteroid> smallerAsteroid = new ArrayList<>();
		Vector2D speed1 = this.getSpeed().rotate(Math.random() * 2 * Math.PI).scale(2);
		Vector2D speed2 = this.getSpeed().rotate(Math.random() * 2 * Math.PI).scale(2);
		Asteroid asteroid1 = new Asteroid(this.getPos(), speed1, this.getSize() / 2);
		Asteroid asteroid2 = new Asteroid(this.getPos(), speed2, this.getSize() / 2);
		smallerAsteroid.add(asteroid1);
		smallerAsteroid.add(asteroid2);
		return smallerAsteroid;
	}
}
