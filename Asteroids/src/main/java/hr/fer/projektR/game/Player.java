package hr.fer.projektR.game;

import java.util.Iterator;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Player extends GameObject {
	double orient;
	public static final double ACCELERATION = 1000, DECELERATION = - 500; 
	private boolean force = false;
	private Vector2D[] shape = {new Vector2D(-10,10),new Vector2D(-10,-10),new Vector2D(20,0)};
	private Vector2D[] thrustShape = {new Vector2D(-10,10 * 0.57745),new Vector2D(-10,-10 * 0.57745),new Vector2D( - 20,0)};
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
		this(x,y,-Math.PI/2);	
	}
	
	public Player() {
		this(0,0);
	}
	
	@Override
	public void move(double dt) {
		Vector2D newSpeed = Vector2D.addVector2d(getSpeed(),getSpeed().unit().scale(DECELERATION).scale(dt));
		if (getSpeed().abs()<Vector2D.I.scale(DECELERATION).scale(dt).abs()) {
			newSpeed= new Vector2D();
		}
		if (force) {
			newSpeed = Vector2D.addVector2d(newSpeed, getForceVector().scale(dt));
		}
		setSpeed(newSpeed);
		super.move(dt);
	}
	
	public void setForce(boolean force) {
		this.force = force;
	}
	
	public Vector2D getForceVector() {
		return Vector2D.I.rotate(orient).scale(ACCELERATION);
	}
	
	public void rotateLeft(double dt) {
		setOrient(getOrient() - 5.0f * dt);
		if (getOrient() < 0) {
			setOrient(getOrient() + 2*Math.PI);
		}
	}
	public void rotateRight(double dt) {
		setOrient(getOrient() + 5.0f * dt);
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
    	private static final int RANGE = 350; // !
    
		public int run(List<Asteroid> asteroids) {
			for (int i = 0; i < RANGE; i++) {
				move(1);
				for (Iterator<?> iter = asteroids.iterator(); iter.hasNext();) {
					Asteroid asteroid = (Asteroid) iter.next();
					if (asteroid.contains(getPos())) {
						iter.remove();
						asteroids.addAll(asteroid.split());
						return asteroid.getSize();
					}
				}
			}
			return 0;
		}

		//----------------------------------------------------------------
		public void draw(GraphicsContext gc) {
			gc.strokeLine(Player.this.getPos().x, Player.this.getPos().y, Vector2D.addVector2d(Player.this.getPos(), Vector2D.I.rotate(getOrient()).scale(2*RANGE)).x, Vector2D.addVector2d(Player.this.getPos(), Vector2D.I.rotate(getOrient()).scale(2*RANGE)).y);
			super.draw(gc);
		}

		Bullet() {
			super(Player.this.getPos(), Vector2D.I.rotate(getOrient()).scale(2));
		}
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		double[] xPoints = new double[shape.length], yPoints  = new double[shape.length];
		int len = 0;
		for (Vector2D v: shape) {
			Vector2D drawV = Vector2D.addVector2d(v.rotate(orient), getPos());
			xPoints[len] = drawV.x;
			yPoints[len] = drawV.y;
			len++;
		}
		gc.strokePolygon(xPoints, yPoints, len);
		if (force) {
			len = 0;
			
			for (Vector2D v: thrustShape) {
				Vector2D drawV = Vector2D.addVector2d(v.rotate(orient), getPos());
				xPoints[len] = drawV.x;
				yPoints[len] = drawV.y;
				len++;
			}
		}
		gc.strokePolygon(xPoints, yPoints, len);
		super.draw(gc);
	}
}
