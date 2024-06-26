package hr.fer.projektR.evolucijski;

import java.util.LinkedList;
import java.util.List;

public class Evolution{
	private List<Jedinka> generation, sideGeneration;
	private int generationSize;
	private double[] goodnes;
	private double goodnesSum, minGoodnes;
	private int best;
	public Evolution(int n, JedinkaFactroy fact) {
		this.generationSize = n;
		generation = new LinkedList<Jedinka>();
		sideGeneration = new LinkedList<Jedinka>();
		goodnes = new double[n];
		for (int i = 0; i < n; i++) {
			Jedinka jed2, jed3;
			jed2 = fact.create();
			jed3 = fact.create();
			jed2.randomize();
			jed3.randomize();
			generation.add(jed2);
			sideGeneration.add(jed3);		
		}
		evaluate();
	}
	private void evaluate() {
		minGoodnes = Double.MAX_VALUE;
		goodnesSum=0;
		for (int i = 0; i < generationSize; i++) {
			goodnes[i] = generation.get(i).fitness();
			minGoodnes = Math.min(minGoodnes, goodnes[i]);
		}
		best=0;
		for (int i = 0; i < generationSize; i++) {
			goodnesSum+=goodnes[i]-minGoodnes;
			if (goodnes[best]<goodnes[i]) {
				best = i;
			}
		}
//		System.out.print(goodnes[best]);
//		System.out.println(minGoodnes);
	}
	public void nextGeneration() {
		sideGeneration.get(0).copy(getBest());
		for (int i = 1; i < generationSize; i++) {
			Jedinka par1,par2;
			par1 = selectParent();
			par2 = selectParent();
//			System.out.println();
			sideGeneration.get(i).fromParents(par1, par2);
			sideGeneration.get(i).mutate();
		}
		for (int i = 0; i < generationSize; i++) {
			generation.get(i).copy(sideGeneration.get(i));
		}
		evaluate();
	}
	
	public Jedinka selectParent() {
		double x = Math.random();
//		System.out.println(x);
		double X = x*goodnesSum;
		int i;
		for (i = 0; X >= 0 && i<generationSize; i++) {
			X -= goodnes[i]-minGoodnes;
		}
		i--;
//		System.out.println(goodnes[i]);
		return generation.get(i);
	}
	
	public Jedinka getBest() {
		return generation.get(best);
	}
	
	public int run(double target, double tresh, int maxIter) {
		int k = 0;
		while(target-goodnes[best]>tresh && k++ < maxIter) {
			nextGeneration();
			if (k%1 == 0) {
				System.out.println(String.format("%.7f", goodnes[best]));
			}
		}
		return k;
	}
	
	public void testPrint() {
	}
}
