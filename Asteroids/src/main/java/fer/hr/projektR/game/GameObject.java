package fer.hr.projektR.game;

public class GameObject {
	private Vector2D pos, speed;
	public GameObject(Vector2D pos, Vector2D speed) {
		this.pos = pos;
		this.speed = speed;
	}
	public GameObject(double x, double y, double dx, double dy) {
		this(new Vector2D(x,y),new Vector2D(dx,dy));
	}
	public GameObject() {
		this(0,0,0,0);
	}
	public void move(double dt) {
		setPos(Vector2D.addVector2d(getPos(), getSpeed().scale(dt)));
		if (pos.x > Game.W) pos.x -= Game.W;
		if (pos.x < 0) pos.x += Game.W;
		if (pos.x > Game.H) pos.x -= Game.H;
		if (pos.x < 0 ) pos.x += Game.H;
	}
	public Vector2D getPos() {
		return pos;
	}
	public void setPos(Vector2D pos) {
		this.pos = pos;
	}
	public void setPos(double x, double y) {
		setPos(new Vector2D(x,y));
	}
	public Vector2D getSpeed() {
		return speed;
	}
	public void setSpeed(Vector2D speed) {
		this.speed = speed;
	}
	public void setSpeed(double dx, double dy) {
		setPos(new Vector2D(dx, dy));
	}
	
	
}
