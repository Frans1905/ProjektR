package hr.fer.projektR.neuralnet;

import java.util.function.BiConsumer;

import hr.fer.projektR.Utils.SerializableTriConsumer;
import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.math.Vector;

public abstract class NeuralNetwork implements Jedinka, java.io.Serializable {
	private Layer[] layers;
	private BiConsumer<NeuralNetwork, Double> mutationMethod = null;
	private SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> fromParentMethod = null;
	
	public NeuralNetwork(Layer[] layers) {
		this.layers = layers;
	}
	public NeuralNetwork(int... c) {
		this(null, null, c);
	}
	public NeuralNetwork(BiConsumer<NeuralNetwork, Double> mutationMethod, int... c) {
		this(null, mutationMethod, c);
	}
	public NeuralNetwork(SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> fromParentMethod, int... c) {
		this(fromParentMethod, null, c);
	}
	public NeuralNetwork(SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> fromParentMethod, BiConsumer<NeuralNetwork, Double> mutationMethod, int... c) {
		layers = new Layer[c.length - 1];
		for (int i = 0; i < c.length - 1; i++) {
			layers[i] = new Layer(c[i + 1], c[i]);
		}
		this.mutationMethod = mutationMethod;
		this.fromParentMethod = fromParentMethod;
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

	public void mutate(double mutationChance) {
		if (mutationMethod == null) {
			mutate(mutationChance, 0.1, 0.5);
		}
		else {
			mutationMethod.accept(this, mutationChance);
		}
	}
	
	public void mutate(double alpha, double big, double relative) {
		for(int i = 0; i < layers.length; i++) {
			if (Math.random() < alpha) {
				double delat = (Math.random() < big)?bigRandom()*1:smallRandom()*0.5;
				layers[i].getWeights().applyToAll((t) -> (Math.random()<relative)?t+delat:delat);		
			}
			if (Math.random() < alpha) {
				double delat = (Math.random() < big)?bigRandom()*1:smallRandom()*0.5;
				layers[i].getBiases().applyToAll((t) -> (Math.random()<relative)?t+delat:delat);				
			}
		}
	}

	public void fromParents(NeuralNetwork first, NeuralNetwork second) {
		if (fromParentMethod == null) {
			fromParentsAlpha(first, second, 0.1);
		}
		else {
			fromParentMethod.accept(this, first, second);
		}
	}
	
	public void fromParentsAlpha(NeuralNetwork first, NeuralNetwork second, double alfa) {
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
			layers[i].getWeights().applyToAll((t) -> smallRandom());
			layers[i].getBiases().applyToAll((t) -> smallRandom());
		}
	}

	public double smallRandom() {
		return Math.random() * 4 - 1;
	}
	public double bigRandom() {
		return 20 * smallRandom();
	}
}
