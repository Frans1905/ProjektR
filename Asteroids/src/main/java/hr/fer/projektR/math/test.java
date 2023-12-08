package hr.fer.projektR.math;


public class test {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {
			{1,2,3},{1,3,2},{3,2,1}
		});
		Vector v = new Vector(new double[] {1,0,0});
		Vector v2 = new Vector(new double[] {2,3,4});
		v.add(v2, v2);
		System.out.println(v2);
	}

}
