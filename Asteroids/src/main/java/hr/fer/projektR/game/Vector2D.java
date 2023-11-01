package hr.fer.projektR.game;

public class Vector2D {
	public double x,y;
	public static final Vector2D I = new Vector2D(1,0);
	public static final Vector2D J = new Vector2D(0,1);
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector2D() {
		this(0, 0);
	}
	public static Vector2D addVector2d(Vector2D vector1, Vector2D vector2) {
		return new Vector2D(vector1.x + vector2.x, vector1.y + vector2.y);
	}
	public static Vector2D scalarMulVector2d(Vector2D vector, double scalar) {
		return new Vector2D(vector.x * scalar, vector.y * scalar);
	}
	public static Vector2D rotatVector2d(Vector2D vector, double tetha) {
		return new Vector2D(vector.x * Math.cos(tetha) - vector.y * Math.sin(tetha),
				vector.x * Math.sin(tetha) + vector.y * Math.cos(tetha));
	}
	public static double dotProduct(Vector2D vector1, Vector2D vector2) {
		return vector1.x*vector2.x + vector1.y*vector2.y;
	}
	public double abs() {
		return Math.sqrt(x*x + y * y);
	}
	
	public Vector2D scale(double scalar) {
		return scalarMulVector2d(this, scalar);
	}
	public Vector2D rotate(double tetha) {
		return rotatVector2d(this, tetha);
	}
	public Vector2D unit() {
		return this.scale(1/this.abs());
	}
}