package fer.hr.projektR.game;

public class Player extends GameObject {
	double orient;
	private int focre = 1;
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
		setSpeed(Vector2D.addVector2d(getSpeed(),getForce().scale(dt)));
		super.move(dt);
	}
	
	public Vector2D getForce() {
		return Vector2D.I.rotate(orient).scale(focre);
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
}
