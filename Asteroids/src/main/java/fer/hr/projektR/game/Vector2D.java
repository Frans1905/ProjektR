package fer.hr.projektR.game;

public class Vector2D {
	public double x,y;
	static final Vector2D I = new Vector2D(1,0);
	static final Vector2D J = new Vector2D(0,1);
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector2D() {
		this(0, 0);
	}
	static Vector2D addVector2d(Vector2D vector1, Vector2D vector2) {
		return new Vector2D(vector1.x + vector2.x, vector1.y + vector2.y);
	}
	static Vector2D scalarMulVector2d(Vector2D vector, double scalar) {
		return new Vector2D(vector.x * scalar, vector.y * scalar);
	}
	static Vector2D rotatVector2d(Vector2D vector, double tetha) {
		return new Vector2D(vector.x * Math.cos(tetha) - vector.y * Math.sin(tetha),
				vector.x * Math.sin(tetha) + vector.y * Math.cos(tetha));
	}
	double abs() {
		return Math.sqrt(x*x + y * y);
	}
	
	Vector2D scale(double scalar) {
		return scalarMulVector2d(this, scalar);
	}
	Vector2D rotate(double tetha) {
		return rotatVector2d(this, tetha);
	}
	Vector2D unit() {
		return this.scale(1/this.abs());
	}
}