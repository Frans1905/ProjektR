package hr.fer.projektR.neuralnet;

import java.util.Random;
import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.game.Game;
import hr.fer.projektR.math.Vector;

public class NeuralNetowrkAsteroids extends NeuralNetwork  implements java.io.Serializable {
	private Random rand;
	private Game model;
	
	public NeuralNetowrkAsteroids(int... c) {
		super(c);
		rand = new Random();
		model = new Game();
	}

	public NeuralNetowrkAsteroids(NeuralNetwork n) {
		super(n);
		rand = new Random();
	}

	@Override
	public void fromParents(Jedinka parent1, Jedinka parent2) {
		super.fromParents((NeuralNetwork)parent1, (NeuralNetwork)parent2,0.1);

	}

	@Override
	public void mutate() {
		super.mutate(0.1,0.8,0.5);

	}

	@Override
	public void copy(Jedinka source) {
		copyFrom((NeuralNetwork) source);

	}

	@Override
	public double fitness() {
		final int sekundi = 45;
		final Vector in = new Vector(28);
		double fit = 0;
		for (int k = 0; k < 5; k++) {
			
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
			fit += model.getScore()/50 + (10.0*i+1.0*j)/10; 
			
		}
		return fit/5;
	}
	@Override
	double bigRandom() {
		return rand.nextGaussian()*1000;
	}
	
	@Override
	double smallRandom() {
		return super.smallRandom()*250;
	}
	
	@Override
	public Vector process(Vector input) {
		Vector out = super.process(input);
		out.applyToAll((t) -> {return 1.0 / (1 + Math.exp(-t));});
		return out;
	}

}