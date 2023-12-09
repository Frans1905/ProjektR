package neuralnet;

import hr.fer.projektR.math.Matrix;
import hr.fer.projektR.math.Vector;

public class Layer {
	private Matrix weights;
	private Vector biases;
	private Vector activations;
	
	public Layer(int curLayerSize, int prevLayerSize) {
		weights = new Matrix(curLayerSize, prevLayerSize);
		biases = new Vector(curLayerSize);
		activations = new Vector(curLayerSize);
	}
	
	public Layer(Matrix weights, Vector biases) {
		super();
		this.weights = weights;
		this.biases = biases;
	}
	

	public Layer(Layer other) {
		this.weights = other.getWeights();
		this.biases = other.getBiases();
	}
	
	public Matrix getWeights() {
		return weights;
	}

	public void setWeights(Matrix weights) {
		this.weights = weights;
	}

	public Vector getBiases() {
		return biases;
	}

	public void setBiases(Vector biases) {
		this.biases = biases;
	}
	
	
	public Vector getActivations() {
		return activations;
	}

	public void setActivations(Vector activations) {
		this.activations = activations;
	}

	public Vector propagate(Vector activations) {
		activations.multiply(weights, this.activations);
		this.activations.add(biases, this.activations);
		this.activations.applyToAll((t) -> {return 1.0 / (1 + Math.exp(-t));});
		return this.activations;
	}
	
}
