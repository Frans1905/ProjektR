package hr.fer.projektR.game;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Player extends GameObject {
	double orient;
	public static final double ACCELERATION = 1000, DECELERATION = -500; 
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
    	private static final int RANGE = 700; // !
    	private int travelled;
    	private Vector2D wrap(Vector2D vec) {
    		double x = vec.x, y= vec.y;
    		if (x > Game.W) x -= Game.W;
    		if (x < 0) x += Game.W;
    		if (y > Game.H) y -= Game.H;
    		if (y < 0 ) y += Game.H;
    		return new Vector2D(x,y);
    	}
    	private Asteroid intersects(List<Asteroid> asteroids,Vector2D startPoint, Vector2D direction, int range) {
//    		System.out.printf("%d %d%n",(int)startPoint.x, (int)startPoint.y);
    		Vector2D hit = null;
    		Asteroid target = null;
    		for (Asteroid asteroid: asteroids) {
    			Vector2D center = asteroid.getPos();
    			double radius = asteroid.size();
    			Vector2D centerToStart = Vector2D.subVector2d(startPoint, center);
				double b = Vector2D.dotProduct(direction.scale(2), centerToStart);
    			double c = centerToStart.abs()*centerToStart.abs() - radius* radius;
    			double D = b*b - 4*c;
    			if (D>0) {
    				double lambda = (-b - Math.sqrt(D))/2; 
    				if (lambda < 0) lambda+= Math.sqrt(D);
    				if (lambda >= 0) {
    					Vector2D intersectPoint = Vector2D.addVector2d(startPoint, direction.scale(lambda));
    					if (Vector2D.distance(startPoint, intersectPoint) < range) {
    						if (hit == null || Vector2D.distance(startPoint, intersectPoint)<Vector2D.distance(startPoint, hit)) {
    							hit = intersectPoint;
    							target = asteroid;
    						}
    					}
    				}
    			}
    		}
    		if (target != null) {
    			travelled = RANGE - range + (int) Vector2D.distance(startPoint, hit);
    			return target;
    		}
    		double lambdaX = Math.max((0-startPoint.x)/direction.x, (Game.W-startPoint.x)/direction.x);
    		double lambdaY = Math.max((0-startPoint.y)/direction.y, (Game.H-startPoint.y)/direction.y);
    		double travelDistance = Math.min(lambdaX, lambdaY);
    		Vector2D next = Vector2D.addVector2d(startPoint, direction.scale(travelDistance+0.001));
    		if (travelDistance > range) {
    			travelled = RANGE;
    			return null;
    		}
    		next = wrap(next);
//    		System.out.printf("%d %d%n",(int)next.x, (int)next.y);
    		return intersects(asteroids, next, direction, (int) (range-travelDistance));
    	}
    	
		public int run(List<Asteroid> asteroids) {
			Asteroid asteroid = intersects(asteroids, getPos(),  Vector2D.I.rotate(getOrient()), RANGE);
			if (asteroid != null) {
				asteroids.remove(asteroid);
				asteroids.addAll(asteroid.split());				
				return asteroid.getSize();
			}
			return 0;
		}

		public void draw(GraphicsContext gc) {
			double oldWidth = gc.getLineWidth();
			gc.setLineWidth(2);
			Vector2D bulletStart = getPos();
			Vector2D bulletEnd = Vector2D.addVector2d(bulletStart, Vector2D.I.rotate(getOrient()).scale(travelled));
			
			int[] dx = {0,0,0,+Game.W,+Game.W,+Game.W,-Game.W,-Game.W,-Game.W};
			int[] dy = {0,+Game.H,-Game.H,0,+Game.H,-Game.H,0,+Game.H,-Game.H};
			for (int i = 0; i < 9; i++) {
				gc.strokeLine(bulletStart.x+dx[i], bulletStart.y+dy[i], bulletEnd.x+dx[i], bulletEnd.y+dy[i]);				
			}
			gc.setLineWidth(oldWidth);
			super.draw(gc);
		}

		Bullet() {
			super(Player.this.getPos(), Vector2D.I.rotate(getOrient()).scale(2));
			travelled = 0;
		}
	}
	
	public boolean isHitBy(Asteroid asteroid) {
		for (Vector2D p: shape) {
			if (asteroid.contains(Vector2D.addVector2d(getPos(), p))) {
				return true;
			}
		}
		return false;
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
