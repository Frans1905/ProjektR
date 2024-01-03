package hr.fer.projektR.evolucijski;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

import hr.fer.projektR.game.asteroidsAI;

public class Evolution<T extends Jedinka> {
	private List<T> generation, sideGeneration;
	private int generationSize;
	private double[] goodnes;
	private double goodnesSum, minGoodnes;
	private int best;
	private double firstMedian = -1, firstBest = -1, firstWorst = -1;
	private double bestMedian = -1, bestBest = -1, bestWorst = -1;
	private int bestMedianGeneration = -1, bestBestGeneration = -1, bestWorstGeneration = -1;
	private double mutationChance;

	public Evolution(int n, JedinkaFactroy<? extends T> fact, double mutationChance) {
		this.generationSize = n;
		generation = new LinkedList<T>();
		sideGeneration = new LinkedList<T>();
		goodnes = new double[n];
		for (int i = 0; i < n; i++) {
			T jed2, jed3;
			jed2 = fact.create();
			jed3 = fact.create();
			jed2.randomize();
			jed3.randomize();
			generation.add(jed2);
			sideGeneration.add(jed3);		
		}
		this.mutationChance = mutationChance;
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

	public void nextGenerationOneParent() {
		int firstN = generationSize / 3;
		copyBestN(firstN);
		
		for (int i = firstN; i < generationSize; i++) {
			Jedinka par1;
			// par1 = selectOneParent(firstN);
			par1 = selectParent();
			sideGeneration.get(i).copy(par1);
			sideGeneration.get(i).mutate(mutationChance);
		}
		for (int i = 0; i < generationSize; i++) {
			generation.get(i).copy(sideGeneration.get(i));
		}
		evaluate();
	}

	private Jedinka selectOneParent(int n) {
		int ind = (int) (Math.random() * n);
		return sideGeneration.get(ind);
	}

	public void nextGeneration() {
		int firstN = generationSize / 4;
		copyBestN(firstN);
		
		for (int i = firstN; i < generationSize; i++) {
			Jedinka par1,par2;
			par1 = selectParent();
			par2 = selectParent();
//			System.out.println();
			sideGeneration.get(i).fromParents(par1, par2, mutationChance);
			// sideGeneration.get(i).mutate(); // u pozivu fromParents
		}
		for (int i = 0; i < generationSize; i++) {
			generation.get(i).copy(sideGeneration.get(i));
		}
		evaluate();
	}
	
	// kopira ukupno n jedinki iz bolje polovice u novu generaciju
	// najbolji se uvijek kopira
	private void copyBestN(int n) {
		double[] sortedGoodnes = Arrays.copyOf(goodnes, goodnes.length);
		Arrays.sort(sortedGoodnes);
		double median = sortedGoodnes[generationSize / 2];

		sideGeneration.get(0).copy(getBest());
		for (int i = 1, j = 0; i < n && j < generationSize; j++) {
			if (j == best) {
				continue;
			}
			if (goodnes[j] > median) {
				sideGeneration.get(i++).copy(generation.get(j));
			}
		}
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
		int staysTheSame = 0;
		double lastMedian = -1;
		int k = 0;
		System.out.println("------------------------------------------------------------");
		while(target-goodnes[best]>tresh && k++ < maxIter) {
			nextGenerationOneParent();
			// nextGeneration();
			if (k%10 == 0 || k == 1) {
				double median = calculateAndPrintStatistics(k);

				// ako ne dolazi do promjena u populaciji povecaj vjerojatnost mutacija da se populacija "rasiri"
				if (lastMedian != -1) {
					double percentage = (lastMedian - median) / lastMedian;
					if (-0.08 <= percentage && percentage <= 0.08) {
						staysTheSame++;
					}
					else {
						staysTheSame = 0;
						lastMedian = median;
					}
					if (staysTheSame >= 10 && Math.random() < (1.0 - mutationChance)) {
						mutationChance = Math.min(1.0, mutationChance + 0.25);
						staysTheSame = 0;
						lastMedian = median;
						System.out.println(String.format("Povecana vjerojatnost mutacije! (%.2f)", mutationChance));
						System.out.println("------------------------------------------------------------");
					}
					// postupno smanjuj vjerojatnost mutacije da se populacija moze koncentrira
					// na tocke lokalnih maksimuma
					else if (Math.random() < mutationChance) {
						mutationChance = Math.max(0.05, mutationChance / 2);
						System.out.println(String.format("Smanjena vjerojatnost mutacije! (%.2f)", mutationChance));
						System.out.println("------------------------------------------------------------");
					}
				}
				else {
					lastMedian = median;
				}
				
				
			}
			if (k % 25 == 0 || k == 1) {
				refreshBestScores(k);
				asteroidsAI.<T>saveObject(generation.get(best), "src/main/resources/" + generation.get(best).getClass().toString().substring(generation.get(best).getClass().toString().lastIndexOf(".") + 1) + k);
				System.out.println("============================================================");
			}
		}

		System.out.println("First generation:");
		printStatistics(firstMedian, firstBest, firstWorst, 1);

		System.out.println("Best scores (only every 25th gen):");
		System.out.println("--------------------");
		System.out.println("best median: " + String.format("%.7f (Generation: %d)", bestMedian, bestMedianGeneration));
		System.out.println("Best best: " + String.format("%.7f (Generation: %d)", bestBest, bestBestGeneration));
		System.out.println("best worst: " + String.format("%.7f (Generation: %d)", bestWorst, bestWorstGeneration));
		System.out.println("------------------------------------------------------------");
		return k;
	}

	private void refreshBestScores(int k) {
		double[] sortedGoodnes = Arrays.copyOf(goodnes, goodnes.length);
		Arrays.sort(sortedGoodnes);

		if (bestMedian == -1 && bestBest == -1 && bestWorst == -1) {
			bestBest = goodnes[best];
			bestMedian = sortedGoodnes[sortedGoodnes.length / 2];
			bestWorst = sortedGoodnes[0];

			bestBestGeneration = k;
			bestMedianGeneration = k;
			bestWorstGeneration = k;
		}

		if (bestMedian < sortedGoodnes[sortedGoodnes.length / 2]) {
			bestMedian = sortedGoodnes[sortedGoodnes.length / 2];
			bestMedianGeneration = k;
		}
		if (bestBest < goodnes[best]) {
			bestBest = goodnes[best];
			bestBestGeneration = k;
		}
		if (bestWorst < sortedGoodnes[0]) {
			bestWorst = sortedGoodnes[0];
			bestWorstGeneration = k;
		}
	}

	private void printStatistics(double median, double bestOne, double worst, int k) {
		System.out.println("Generation: " + k);
		System.out.println("--------------------");
		System.out.println("Median: " + String.format("%.7f", median));
		System.out.println("Best: " + String.format("%.7f", bestOne));
		System.out.println("Worst: " + String.format("%.7f", worst));
		System.out.println("------------------------------------------------------------");
	}

	private double calculateAndPrintStatistics(int k) {
		double[] sortedGoodnes = Arrays.copyOf(goodnes, goodnes.length);
		Arrays.sort(sortedGoodnes);

		printStatistics(sortedGoodnes[sortedGoodnes.length / 2], goodnes[best], sortedGoodnes[0], k);

		// first print
		if (firstBest == -1 && firstMedian == -1 && firstWorst == -1) {
			firstBest = goodnes[best];
			firstMedian = sortedGoodnes[sortedGoodnes.length / 2];
			firstWorst = sortedGoodnes[0];
		}

		return sortedGoodnes[sortedGoodnes.length / 2];
	}
	
	public void testPrint() {
	}
}
