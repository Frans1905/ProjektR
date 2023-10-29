package fer.hr.projektR.game;

import java.util.Iterator;
import java.util.List;

public class Player extends GameObject {
	double orient;
	public static final double Accelerate = 1, Decelerate = - 0.2; 
	private double force = Player.Decelerate;
	Vector2D[] shape = {new Vector2D(1,0),new Vector2D(-1,0),new Vector2D(0,-5)};
	public Player(Vector2D pos, Vector2D speed, Vector2D force, double orient) {
		super(pos, speed);
		this.orient = orient;
	}

	public Player(double x, double y, double dx, double dy, double ddx, double ddy, double orient) {
		this(new Vector2D(x,y),new Vector2D(dx,dy), new Vector2D(ddx,ddy), orient);	
	}
	
	public Player(double x, double y, double orient) {
		this(x,y,0,0,0,0,orient);	
	}
	
	public Player(double x, double y) {
		this(x,y,0);	
	}
	
	public Player() {
		this(0,0);
	}
	
	@Override
	public void move(double dt) {
		setSpeed(Vector2D.addVector2d(getSpeed(),getForceVector().scale(dt)));
		super.move(dt);
	}
	
	public void setForce(double force) {
		this.force = force;
	}
	
	public Vector2D getForceVector() {
		return Vector2D.I.rotate(orient).scale(force);
	}
	
	void rotateLeft(double dt) {
		setOrient(getOrient() - 1.0f * dt);
		if (getOrient() < 0) {
			setOrient(getOrient() + 2*Math.PI);
		}
	}
	void rotateRight(double dt) {
		setOrient(getOrient() + 1.0f * dt);
		if (getOrient() >= 2*Math.PI) {
			setOrient(getOrient() - 2*Math.PI);
		}
	}

	public double getOrient() {
		return orient;
	}

	public void setOrient(double orient) {
		this.orient = orient;
	}

	public class Bullet extends GameObject {
    	private static final int range = 100; // !
    
		public int run(List<Asteroid> asteroids) {
			for (int i = 0; i < range; i++) {
				move(1);
				for (Iterator<?> iter = asteroids.iterator(); iter.hasNext();) {
					Asteroid asteroid = (Asteroid) iter.next();
					if (asteroid.contains(getPos())) {
						asteroids.addAll(asteroid.split());
						iter.remove();
						return asteroid.getSize();
					}
				}
			}
			return 0;
		}

		Bullet() {
			super(Player.this.getPos(), Vector2D.I.rotate(getOrient()).scale(2));
		}
	}
}
