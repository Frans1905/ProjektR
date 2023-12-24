package hr.fer.projektR.game;

import hr.fer.projektR.evolucijski.Evolution;
import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.evolucijski.JedinkaFactroy;
import hr.fer.projektR.math.Vector;
import hr.fer.projektR.neuralnet.NeuralNetowrkAsteroids;
import hr.fer.projektR.neuralnet.NeuralNetwork;
import hr.fer.projektR.neuralnet.NeuralNetworkSin;

public class asteroidsAI {

	public static void main(String[] args) {
		JedinkaFactroy fact = new JedinkaFactroy() {
			@Override
			public Jedinka create() {
				return new NeuralNetowrkAsteroids(28,40,40,4);
			}
		};
//		NeuralNetworkSin net = (NeuralNetworkSin) fact.create();
//		net.randomize();
//		for (Layer l: net.getLayers()) {
//			System.out.println(l.getWeights());
//			System.out.println(l.getBiases());
//			System.out.println();
//		}
		Evolution darwin = new Evolution(30, fact);
		Vector in = new Vector(27);
		System.out.println(darwin.run(20000, 1, 10000)-1);
		Game game = new Game();
		game.newGame();
		while (!game.isOver()) {
			in.fillWith(game.getData());
			Vector m = ((NeuralNetwork)darwin.getBest()).process(in);
			if (m.matrix[0][0] > 0.5) {
				game.spaceInput();
			}
			boolean wPress = m.matrix[1][0] > 0.5, aPress = m.matrix[2][0] > 0.5, dPress = m.matrix[3][0] > 0.5;
			for (int j = 0; j < 10 && !game.isOver(); j++) {
				if (wPress) game.wInput();
				if (aPress) game.aInput();
				if (dPress) game.dInput();
				game.step();
			}
		}
		System.out.println(game.getScore());
	}

}
