package hr.fer.projektR.math;

import hr.fer.projektR.evolucijski.Evolution;
import hr.fer.projektR.evolucijski.Jedinka;
import hr.fer.projektR.evolucijski.JedinkaFactroy;
import hr.fer.projektR.neuralnet.NeuralNetwork;
import hr.fer.projektR.neuralnet.NeuralNetworkSin;

public class test {

	public static void main(String[] args) {
		JedinkaFactroy fact = new JedinkaFactroy() {
			@Override
			public Jedinka create() {
				return new NeuralNetworkSin(1,15,15,1);
			}
		};
//		NeuralNetworkSin net = (NeuralNetworkSin) fact.create();
//		net.randomize();
//		for (Layer l: net.getLayers()) {
//			System.out.println(l.getWeights());
//			System.out.println(l.getBiases());
//			System.out.println();
//		}
		Evolution darwin = new Evolution(65, fact);
		Vector in = new Vector(1);
		System.out.println(darwin.run(0, 0.001, 60000)-1);
		
		for (double x = 0; x < 2*Math.PI; x+=2*Math.PI/20) {
			in.matrix[0][0]=x;
			double rez = ((NeuralNetwork)darwin.getBest()).process(in).matrix[0][0];
			System.out.println(String.format("%.3f\t%.3f\t%.3f", rez, Math.sin(x), Math.abs(rez-Math.sin(x))));
		}
		
	}

}
