package fer.hr.projektR.game;

import java.util.Collection;

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
		return null;
	}
}
