package hr.fer.projektR.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Asteroid extends GameObject{
	private int nSize;
	private final int radius = 20;
	public static final int maxSize = 4; // !
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
		if (nSize > 1) {
			Vector2D speed1 = this.getSpeed().rotate(Math.random() * 2 * Math.PI).scale(2);
			Vector2D speed2 = this.getSpeed().rotate(Math.random() * 2 * Math.PI).scale(2);
			Asteroid asteroid1 = new Asteroid(this.getPos(), speed1, this.getSize() / 2);
			Asteroid asteroid2 = new Asteroid(this.getPos(), speed2, this.getSize() / 2);
			smallerAsteroid.add(asteroid1);
			smallerAsteroid.add(asteroid2);			
		}
		return smallerAsteroid;
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		gc.strokeOval(getPos().x-size(), getPos().y-size(), 2*size(), 2*size() );
		super.draw(gc);
	}
}
