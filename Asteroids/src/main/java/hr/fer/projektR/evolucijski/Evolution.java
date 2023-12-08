package hr.fer.projektR.evolucijski;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class Evolution<J extends Jedinka>{
	private List<J> generation, sideGeneration;
	private int generationSize;
	private double[] goodnes;
	private double goodnesSum, minGoodnes;
	private int best;
	@SuppressWarnings("unchecked")
	public Evolution(int n, J jed) {
		this.generationSize = n;
		generation = new LinkedList<J>();
		sideGeneration = new LinkedList<J>();
		goodnes = new double[n];
		for (int i = 0; i < n; i++) {
			try {
				J jed2, jed3;
				jed2 = (J) jed.getClass().getDeclaredConstructor().newInstance(jed);
				jed3 = (J) jed.getClass().getDeclaredConstructor().newInstance(jed);
				jed2.randomize();
				jed3.randomize();
				generation.add(jed2);
				sideGeneration.add(jed3);
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
	}
	public void nextGeneration() {
		for (int i = 0; i < generationSize; i++) {
			J par1,par2;
			par1 = selectParent();
			par2 = selectParent();
			sideGeneration.get(i).fromParents(par1, par2);
			sideGeneration.get(i).mutate();
		}
		for (int i = 0; i < generationSize; i++) {
			generation.get(i).copy(sideGeneration.get(i));
		}
		evaluate();
	}
	
	public J selectParent() {
		double X = Math.random()*goodnesSum;
		int i;
		for (i = 0; X > 0; i++) {
			X -= goodnes[i];
		}
		i--;
		return generation.get(i);
	}
	
	public J getBest() {
		return generation.get(best);
	}
	
	public void run(double target, double tresh, int maxIter) {
		int k = 0;
		while(target-goodnes[best]>tresh && k++ < maxIter) {
			nextGeneration();
		}
	}
}
