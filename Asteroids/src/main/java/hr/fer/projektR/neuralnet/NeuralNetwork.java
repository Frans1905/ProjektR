package hr.fer.projektR.neuralnet;

import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.math.Vector;

public abstract class NeuralNetwork implements Jedinka {
	private Layer[] layers;
	
	
	public NeuralNetwork(Layer[] layers) {
		this.layers = layers;
	}
	
	public NeuralNetwork(int... c) {
		layers = new Layer[c.length - 1];
		for (int i = 0; i < c.length - 1; i++) {
			layers[i] = new Layer(c[i + 1], c[i]);
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
	
	public void mutate(double alpha) {
		for(int i = 0; i < layers.length; i++) {
			layers[i].getWeights().applyToAll((t) -> (Math.random()<alpha)?t+mutateRandom():t);
			layers[i].getBiases().applyToAll((t) -> (Math.random()<alpha)?t+mutateRandom():t);
		}

	}
	
	public void fromParents(NeuralNetwork first, NeuralNetwork second, double alfa) {
		for (int i = 0; i < layers.length; i++) {
			for (int j = 0; j < layers[i].getWeights().getNrow(); j++) {
				for (int k = 0; k < layers[i].getWeights().getNcol(); k++) {
					double len = Math.abs(first.getLayers()[i].getWeights().matrix[j][k] - second.getLayers()[i].getWeights().matrix[j][k]);
					double low =Math.min(first.getLayers()[i].getWeights().matrix[j][k], second.getLayers()[i].getWeights().matrix[j][k]) - alfa*len;
					layers[i].getWeights().matrix[j][k] = len*(1+2*alfa)*Math.random()+low;
				}
			}
			for (int j = 0; j < layers[i].getWeights().getNrow(); j++) {
				double len = Math.abs(first.getLayers()[i].getBiases().matrix[j][0] - second.getLayers()[i].getBiases().matrix[j][0]);
				double low =Math.min(first.getLayers()[i].getBiases().matrix[j][0], second.getLayers()[i].getBiases().matrix[j][0]) - alfa*len;
				layers[i].getBiases().matrix[j][0] = len*(1+2*alfa)*Math.random()+low;
			}
			
		}
	}
	
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
			if (i != layers.length-1) input.applyToAll((t) -> {return 1.0 / (1 + Math.exp(-t));});
		}
		return input;
	}
	
	public void randomize() {
		for(int i = 0; i < layers.length; i++) {
			layers[i].getWeights().applyToAll((t) -> startRandom());
			layers[i].getBiases().applyToAll((t) -> startRandom());
		}
	}

	double startRandom() {
		return Math.random() * 2 - 1;
	}
	double mutateRandom() {
		return Math.random() * 2 - 1;
	}
}
