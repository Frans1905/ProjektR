package hr.fer.projektR.math;

import java.util.function.DoubleUnaryOperator;

public class Matrix implements java.io.Serializable {
	private final int nrow,ncol;
	public final double[][] matrix;
	public Matrix(int rows, int ncol) {
		if (rows < 1 || ncol < 1) {
			throw new MatrixSizeException();
		}
		this.nrow = rows;
		this.ncol = ncol;
		matrix = new double[rows][ncol];
		clear();
	}
	public Matrix(double[][] data) {
		if (data.length < 1 || data[0].length < 1) {
			throw new MatrixSizeException();
		}
		this.nrow = data.length;
		this.ncol = data[0].length;
		matrix = new double[nrow][ncol];
		fillWith(data);
		
	}
	public Matrix(Matrix m) {
		this.nrow = m.nrow;
		this.ncol = m.ncol;
		matrix = new double[nrow][ncol];
		copy(m);
	}
	
	public static Matrix matrixLike(Matrix m) {
		return new Matrix(m.nrow, m.ncol);
	}
	
	public void copy(Matrix m) {
		if (!checkDim(m.nrow, m.ncol)) throw new MatrixSizeException();
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				matrix[i][j] = m.matrix[i][j];
			}
		}
	}
	
	private boolean checkDim(int row, int col) {
		return (nrow == row) && (ncol == col);
	}

	private boolean canMultiply(Matrix other) {
		return this.ncol == other.nrow;
	}
	
	private void clear() {
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				matrix[i][j] = 0;
			}
		}
	}
	public void fillWith(double[][] data) {
		if (data.length<1 || !checkDim(data.length, data[0].length)) throw new MatrixSizeException();
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				matrix[i][j] = data[i][j];
			}
		}
		
	}
	public void multiply(final Matrix other, Matrix resault) {
		if (!this.canMultiply(other) || !resault.checkDim(this.nrow, other.ncol)) {
			throw new MatrixOperationException();
		}
		if (resault == this) throw new IllegalArgumentException();
		resault.clear();
		for (int i = 0; i < this.nrow; i++) {
			for (int j = 0; j < this.ncol; j++) {
				for (int k = 0; k < other.ncol; k++) {
//					System.out.println(String.format("%d %d %d", i, j, k));
					resault.matrix[i][k] += this.matrix[i][j]*other.matrix[j][k];
				}
			}
		}
	}
	public void add(final Matrix other, Matrix resault) {
		if (!checkDim(other.nrow, other.ncol) || !checkDim(resault.nrow, resault.ncol)) {
			throw new MatrixOperationException();
		}
		
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				resault.matrix[i][j] = this.matrix[i][j] + other.matrix[i][j];
			}
		}
	}
	public Matrix add(final Matrix other) {
		Matrix resault = matrixLike(this);
		this.add(other,resault);
		return resault;
	}
	
	public Matrix multiply(final Matrix other) {
		Matrix resault = new Matrix(this.nrow, other.ncol);
		this.multiply(other,resault);
		return resault;
	}
	
	public void applyToAll(DoubleUnaryOperator opp) {
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				matrix[i][j] = opp.applyAsDouble(matrix[i][j]);
			}
		}
	}
	
	public void transpose(Matrix resault) {
		if (!resault.checkDim(this.ncol, this.nrow)) {
			throw new MatrixOperationException();
		}
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				resault.matrix[j][i] = matrix[i][j];
			}
		}
	}
	public Matrix transpose() {
		Matrix resault = new Matrix(ncol,nrow);
		this.transpose(resault);
		return resault;
	}
	public int getNrow() {
		return nrow;
	}
	public int getNcol() {
		return ncol;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nrow; i++) {
			sb.append("[");
			for (int j = 0; j < ncol; j++) {
				sb.append(matrix[i][j]);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public void scale(double scalar) {
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				matrix[i][j] = matrix[i][j]*scalar;
			}
		}
	}
}
