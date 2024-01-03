package hr.fer.projektR.neuralnet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.math.Matrix;
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
	
	public void mutate(double alpha, double big, double relative) {
		for(int i = 0; i < layers.length; i++) {
			if (Math.random() < alpha) {
				double delat = (Math.random() < big)?bigRandom():smallRandom();
				layers[i].getWeights().applyToAll((t) -> (Math.random()<alpha)?t+delat:delat);		
			}
			if (Math.random() < alpha) {
				double delat = (Math.random() < big)?bigRandom():smallRandom();
				layers[i].getBiases().applyToAll((t) -> (Math.random()<relative)?t+delat:delat);				
			}
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
			for (int j = 0; j < layers[i].getBiases().getNrow(); j++) {
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

	double smallRandom() {
		return Math.random() * 2 - 1;
	}
	double bigRandom() {
		return 20 * smallRandom();
	}
	
	public void saveTo(String fileName) {
		Path file = Paths.get(fileName);
		if (Files.notExists(file)) {
			try {
				Files.createFile(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new BufferedOutputStream(
								Files.newOutputStream(file)),"UTF-8"))
				) {
			writer.write(String.valueOf(layers.length));
			writer.newLine();
			for (Layer l : layers) {
				writer.write(String.format("%d %d %d", l.getWeights().getNrow(),l.getWeights().getNcol(),l.getBiases().getNrow()));
				writer.newLine();
				for (int i = 0; i < l.getWeights().getNrow(); i++) {
					for (int j = 0; j < l.getWeights().getNcol(); j++) {
						writer.write(String.valueOf(l.getWeights().matrix[i][j]));
						writer.write(" ");
					}
					writer.newLine();
				}
				for (int i = 0; i < l.getBiases().getNrow(); i++) {
					writer.write(String.valueOf(l.getBiases().matrix[i][0]));
					writer.write(" ");
				}
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadFrom(String fileName) {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), Charset.forName("UTF-8"))) {
			int l = Integer.parseInt(reader.readLine().strip());
			layers = new Layer[l];
			for (int i = 0; i < l; i++) {
				String[] str = reader.readLine().split(" ");
				int r = Integer.parseInt(str[0]), c = Integer.parseInt(str[1]), n = Integer.parseInt(str[2]);
				double[][] data = new double[r][c];
				double[] vec = new double[n];
				for (int j = 0; j < r ; j++) {
					String[] ws = reader.readLine().split(" ");
					for (int k = 0; k < c; k++) {
						data[j][k] = Double.parseDouble(ws[k]);
					}
				}
				String[] ws = reader.readLine().split(" ");
				for (int j = 0; j < n ; j++) {
					vec[j] = Double.parseDouble(ws[j]);
				}
				layers[i] = new Layer(new Matrix(data), new Vector(vec));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
