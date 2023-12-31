package hr.fer.projektR.game;

import hr.fer.projektR.evolucijski.Evolution;
import hr.fer.projektR.evolucijski.JedinkaFactroy;
import hr.fer.projektR.math.Vector;
import hr.fer.projektR.neuralnet.NeuralNetworkAsteroids;
import hr.fer.projektR.neuralnet.NeuralNetwork;
import java.io.*;

public class asteroidsAI {
	public static void main(String[] args) {
		JedinkaFactroy<NeuralNetworkAsteroids> fact = new JedinkaFactroy<NeuralNetworkAsteroids>() {
			@Override
			public NeuralNetworkAsteroids create() {
				return new NeuralNetworkAsteroids(28,56,28,4);
			}
		};
//		NeuralNetworkSin net = (NeuralNetworkSin) fact.create();
//		net.randomize();
//		for (Layer l: net.getLayers()) {
//			System.out.println(l.getWeights());
//			System.out.println(l.getBiases());
//			System.out.println();
//		}
		Evolution<NeuralNetworkAsteroids> darwin = new Evolution<NeuralNetworkAsteroids>(120, fact);
		Vector in = new Vector(28);
		// System.out.println(darwin.run(20000, 1, 10000)-1); 
		System.out.println(darwin.run(20000, 1, 20000)-1);
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
		saveObject((NeuralNetworkAsteroids) darwin.getBest(), "src/main/resources/NeuralNetworkFile");
		System.out.println(game.getScore());
	}

	public static <T extends Serializable> void saveObject (T best, String name) {
		try {   
        	//Saving of object in a file
            FileOutputStream file = new FileOutputStream(name);
            ObjectOutputStream out = new ObjectOutputStream(file);
             
            // Method for serialization of object
            out.writeObject(best);
             
            out.close();
            file.close();

			System.out.println("Serialized!");
        }
        catch(IOException ex) {
			ex.printStackTrace();
            System.out.println("IOException is caught");
        }
	}
}
