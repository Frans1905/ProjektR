package hr.fer.projektR.neuralnet;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

import hr.fer.projektR.Utils.SerializableTriConsumer;
import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.game.AsteroidsAI;
import hr.fer.projektR.game.Game;
import hr.fer.projektR.math.Vector;

public class NeuralNetworkAsteroids extends NeuralNetwork  implements java.io.Serializable {
	private Random rand;
	private Game model;
	private int numberOfRepetitions;
	private BinaryOperator<Double> fitnessMethod = null;

	public NeuralNetworkAsteroids(int... c) {
		this(6, c);
	}
	public NeuralNetworkAsteroids(int numberOfRepetitions, int... c) {
		this(null, null, null, numberOfRepetitions, c);
	}
	public NeuralNetworkAsteroids(BinaryOperator<Double> fitnessMethod, BiConsumer<NeuralNetwork, Double> mutationMethod, int numberOfRepetitions, int... c) {
		this(null, fitnessMethod, mutationMethod, numberOfRepetitions, c);
	}
	public NeuralNetworkAsteroids(SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> fromParentMethod, BinaryOperator<Double> fitnessMethod, BiConsumer<NeuralNetwork, Double> mutationMethod, int numberOfRepetitions, int... c) {
		super(fromParentMethod, mutationMethod, c);
		rand = new Random();
		model = new Game(c[0] / 5);
		this.numberOfRepetitions = numberOfRepetitions;
		this.fitnessMethod = fitnessMethod;
	}
	public NeuralNetworkAsteroids(SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> fromParentMethod, BinaryOperator<Double> fitnessMethod, int numberOfRepetitions, int... c) {
		this(fromParentMethod, fitnessMethod, null, numberOfRepetitions, c);
	}
	public NeuralNetworkAsteroids(NeuralNetwork n) {
		super(n);
		rand = new Random();
	}

	@Override
	public void fromParents(Jedinka parent1, Jedinka parent2) {
		super.fromParents((NeuralNetwork) parent1,(NeuralNetwork) parent2);
	}

	@Override
	public void mutate(double mutationChance) {
		super.mutate(mutationChance);
	}

	@Override
	public void copy(Jedinka source) {
		copyFrom((NeuralNetwork) source);
	}

	@Override
	public double fitness() {
		final int sekundi = 45;
		final Vector in = new Vector(getLayers()[0].getWeights().getNcol());
		double fit = 0;
		for (int k = 0; k < numberOfRepetitions; k++) {
			
			model.newGame();
			int i = 0, j = 0;
			for (i = 0; i < sekundi * 10 && !model.isOver(); i++) {
				in.fillWith(model.getData());
				Vector m = process(in);
				if (m.matrix[0][0] > 0.5) {
					model.spaceInput();
				}
				boolean wPress = m.matrix[1][0] > 0.5, aPress = m.matrix[2][0] > 0.5, dPress = m.matrix[3][0] > 0.5;
				for (j = 0; j < 10 && !model.isOver(); j++) {
					if (wPress) model.wInput();
					if (aPress) model.aInput();
					if (dPress) model.dInput();
					model.step();
				}
			}
			if (fitnessMethod == null) {
				fit += model.getScore() + (10.0*i+1.0*j);
			}
			else {
				fit += fitnessMethod.apply((double) model.getScore(), 10.0*i + 1.0*j);
			}
		}
		return fit / (10 * numberOfRepetitions);
	}

	@Override
	public double bigRandom() {
		return rand.nextGaussian()*AsteroidsAI.BIG_RANDOM_RANGE;
	}
	@Override
	public double smallRandom() {
		return super.smallRandom()*AsteroidsAI.SMALL_RANDOM_RANGE;
	}
	
	@Override
	public Vector process(Vector input) {
		Vector out = super.process(input);
		out.applyToAll((t) -> {return 1.0 / (1 + Math.exp(-t));});
		return out;
	}
}
