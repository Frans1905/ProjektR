package neuralnet;

import hr.fer.projektR.math.Vector;

public abstract class NeuralNetwork {
	private Layer[] layers;
	
	
	public NeuralNetwork(Layer[] layers) {
		this.layers = layers;
	}
	
	public NeuralNetwork(int ... c) {
		layers = new Layer[c.length];
		for (int i = 0; i < c.length - 1; i++) {
			layers[i] = new Layer(c[i], c[i + 1]);
		}
	}
	
	public NeuralNetwork(NeuralNetwork other) {
		this(other.calculateSizes());
		this.copyFrom(other);
	}

	public Layer[] getLayers() {
		return layers;
	}

	public void setLayers(Layer[] layers) {
		this.layers = layers;
	}
	
	public int[] calculateSizes() {
		int[] sizes = new int[this.layers.length];
		for (int i = 0; i < this.getLayers().length; i++) {
			sizes[i] = this.getLayers()[i].getActivations().size();
		}
		return sizes;
	}
	
	public abstract double calculateFitness();
	
	public abstract void mutate(double alpha);
	
	public abstract void fromParents(NeuralNetwork first, NeuralNetwork second);
	
	public void copyFrom(NeuralNetwork other) {
		if (this.getLayers().length != other.getLayers().length) {
			throw new IllegalArgumentException("Copied neural networks must be same size");
		}
		for (int i = 0; i < layers.length; i++) {
			layers[i].getWeights().copy(other.getLayers()[i].getWeights());
			layers[i].getBiases().copy(other.getLayers()[i].getBiases());
			layers[i].getActivations().copy(other.getLayers()[i].getActivations());
		}
	}
	
	public Vector process(Vector input) {
		for (int i = 0; i < layers.length; i++) {
			input = layers[i].propagate(input);
		}
		return input;
	}
	
	public void randomize() {
		for(int i = 0; i < layers.length; i++) {
			layers[i].getWeights().applyToAll((t) -> Math.random() * 2 - 1);
		}
	}
}
