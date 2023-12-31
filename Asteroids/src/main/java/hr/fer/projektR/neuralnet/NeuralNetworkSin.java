package hr.fer.projektR.neuralnet;

import java.util.Random;

import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.math.Vector;

public class NeuralNetworkSin extends NeuralNetwork {
	private Random rand;
	public NeuralNetworkSin(int... c) {
		super(c);
		rand = new Random();
	}
	public NeuralNetworkSin(NeuralNetworkSin n) {
		super(n);
		rand = new Random();
	}
	@Override
	public void fromParents(Jedinka parent1, Jedinka parent2) {
		super.fromParentsAlpha1((NeuralNetwork)parent1, (NeuralNetwork)parent2,0.1);
	}

	@Override
	public void copy(Jedinka source) {
		copyFrom((NeuralNetwork) source);
		
	}
	
	@Override
	public double fitness() {
		final Vector in = new Vector(1);
		double fit = 0;
		for (double x = 0; x < 2*Math.PI; x+=2*Math.PI/20) {
			in.matrix[0][0]=x;
			double rez = process(in).matrix[0][0];
			fit += Math.pow(rez - Math.sin(x),2);
		}
		return -fit;
	}

	@Override
	public void mutate() {
		super.mutate(7e-3,0.8,0.9);
	}
	
	@Override
	double bigRandom() {
		return rand.nextGaussian()*50;
	}
	
	@Override
	double smallRandom() {
		return super.smallRandom();
	}

}
