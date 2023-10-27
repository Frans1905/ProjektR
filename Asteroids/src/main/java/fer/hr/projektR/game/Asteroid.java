package fer.hr.projektR.game;

public class Asteroid extends GameObject{
	private int nSize;
	private final int radius = 100;
	public Asteroid(Vector2D pos, Vector2D speed, int size) {
		super(pos, speed);
		nSize = size;
	}
	
	public Asteroid(double x, double y, double dx, double dy, int size) {
		super(x, y, dx, dy);
		nSize = size;
	}
	
}
