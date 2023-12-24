package hr.fer.projektR.math;

public class Vector extends Matrix {
	int n;
	public Vector(int n) {
		super(n,1);
		this.n = n;
		
	}

	public Vector(double[] data) {
		super(data.length,1);
		n = data.length;
		fillWith(data);
	}

	public Vector(Vector v) {
		super(v);
		this.n = v.n;
	}
	public int size() {
		return n;
	}
	
	public void fillWith(double[] data) {
		if (data.length != n) throw new MatrixSizeException();
		for (int i = 0; i < n; i++) {
			super.matrix[i][0] = data[i];
		}
		
	}
}
