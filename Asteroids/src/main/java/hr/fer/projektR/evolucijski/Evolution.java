package hr.fer.projektR.evolucijski;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import hr.fer.projektR.game.AsteroidsAI;

public class Evolution {
	private List<Jedinka> generation, sideGeneration;
	private int generationSize;
	private double[] goodness;
	private double[] sideGoodness;
	private double goodnessSum, minGoodness;
	private double sideGoodnessSum, minSideGoodness;
	private int best;
	private double firstMedian = -1, firstBest = -1, firstWorst = -1;
	private double bestMedian = -1, bestBest = -1, bestWorst = -1;
	private int bestMedianGeneration = -1, bestBestGeneration = -1, bestWorstGeneration = -1;
	private double mutationChance, minMutation, mutationReductionFactor, mutationIncrement;
	private double samePercentageTreshold;
	private int staysTheSameMax;
	private boolean oneParent;
	private int firstN;

	public Evolution(int n, int firstN, JedinkaFactroy<? extends Jedinka> fact, double mutationChance, double minMutation, double mutationReductionFactor, double mutationIncrement, double samePercentageTreshold, int staysTheSameMax, boolean oneParent) {
		this.generationSize = n;
		generation = new LinkedList<Jedinka>();
		sideGeneration = new LinkedList<Jedinka>();
		goodness = new double[n];
		sideGoodness = new double[n];
		for (int i = 0; i < n; i++) {
			Jedinka jed2, jed3;
			jed2 = fact.create();
			jed3 = fact.create();
			jed2.randomize();
			jed3.randomize();
			generation.add(jed2);
			sideGeneration.add(jed3);		
		}
		this.mutationChance = mutationChance;
		this.minMutation = minMutation;
		this.mutationReductionFactor = mutationReductionFactor;
		this.mutationIncrement = mutationIncrement;
		this.oneParent = oneParent;
		this.samePercentageTreshold = samePercentageTreshold;
		this.staysTheSameMax = staysTheSameMax;
		this.firstN = firstN;
		evaluate();
	}
	public Evolution(int n, JedinkaFactroy<? extends Jedinka> fact, double mutationChance, double minMutation, double mutationReductionFactor, double mutationIncrement, double samePercentageTreshold, int staysTheSameMax, boolean oneParent) {
		this(n, 1, fact, mutationChance, minMutation, mutationReductionFactor, mutationIncrement, samePercentageTreshold, staysTheSameMax, oneParent);
	}
	public Evolution(int n, JedinkaFactroy<? extends Jedinka> fact, double mutationChance, boolean oneParent) {
		this(n, fact, mutationChance, 0.05, 1.0, 0.0, 0.0, 10, oneParent);
	}
	public Evolution(int n, JedinkaFactroy<? extends Jedinka> fact, double mutationChance) {
		this(n, fact, mutationChance, false);
	}

	private void evaluate() {
		minGoodness = Double.MAX_VALUE;
		goodnessSum=0;
		for (int i = 0; i < generationSize; i++) {
			goodness[i] = generation.get(i).fitness();
			minGoodness = Math.min(minGoodness, goodness[i]);
		}
		best=0;
		for (int i = 0; i < generationSize; i++) {
			goodnessSum+=goodness[i]-minGoodness;
			if (goodness[best]<goodness[i]) {
				best = i;
			}
		}
	}

	public void nextGeneration() {
		sideGoodnessSum = 0;
		copyBestN();
		
		for (int i = firstN; i < generationSize; i++) {
			if (oneParent) {
				Jedinka par1;
				par1 = selectParent(sideGoodness, sideGoodnessSum, minSideGoodness, sideGeneration);
				sideGeneration.get(i).copy(par1);
				sideGeneration.get(i).mutate(mutationChance);
			}
			else {
				Jedinka par1,par2;
				par1 = selectParent(goodness, goodnessSum, minGoodness, generation);
				par2 = selectParent(goodness, goodnessSum, minGoodness, generation);
				sideGeneration.get(i).fromParents(par1, par2);
				sideGeneration.get(i).mutate(mutationChance);
			}
		}
		for (int i = 0; i < generationSize; i++) {
			generation.get(i).copy(sideGeneration.get(i));
		}
		evaluate();
	}
	
	// kopira ukupno n jedinki iz bolje polovice u novu generaciju
	// najbolji se uvijek kopira
	private void copyBestN() {
		double[] sortedGoodnes = Arrays.copyOf(goodness, goodness.length);
		Arrays.sort(sortedGoodnes);
		double median = sortedGoodnes[generationSize / 2];

		sideGeneration.get(0).copy(getBest());
		sideGoodness[0] = goodness[best];
		sideGoodnessSum += goodness[best];
		minSideGoodness = goodness[best];
		for (int i = 1, j = 0; i < firstN && j < generationSize; j++) {
			if (j == best) {
				continue;
			}
			if (goodness[j] > median) {
				sideGeneration.get(i).copy(generation.get(j));
				sideGoodness[i++] = goodness[j];
				sideGoodnessSum += goodness[j];
				minSideGoodness = Math.min(minSideGoodness, goodness[j]);
			}
		}
	}
	
	public Jedinka selectParent(double[] goodness, double goodnessSum, double minGoodness, List<? extends Jedinka> generation) {
		double x = Math.random();
		double X = x*goodnessSum;
		int i;
		for (i = 0; X >= 0 && i<generationSize; i++) {
			X -= goodness[i]-minGoodness;
		}
		i--;
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
		while(target-goodness[best]>tresh && k++ < maxIter) {
			nextGeneration();
			if (k%10 == 0 || k == 1) {
				double median = calculateAndPrintStatistics(k);

				// ako ne dolazi do promjena u populaciji povecaj vjerojatnost mutacija da se populacija "rasiri"
				if (mutationReductionFactor != 1.0) {
					if (lastMedian != -1) {
						double percentage = Math.abs((lastMedian - median) / lastMedian);
						if (percentage <= samePercentageTreshold) {
							staysTheSame++;
						}
						else {
							staysTheSame = 0;
							lastMedian = median;
						}
						if (staysTheSame >= staysTheSameMax && Math.random() < (1.0 - mutationChance)) {
							mutationChance = Math.min(1.0, mutationChance + mutationIncrement);
							staysTheSame = 0;
							lastMedian = median;
							System.out.println(String.format("Povecana vjerojatnost mutacije! (%.2f)", mutationChance));
							System.out.println("------------------------------------------------------------");
						}
						// postupno smanjuj vjerojatnost mutacije da se populacija moze koncentrira
						// na tocke lokalnih maksimuma
						else if (Math.random() < mutationChance) {
							mutationChance = Math.max(minMutation, mutationChance * mutationReductionFactor);
							System.out.println(String.format("Smanjena vjerojatnost mutacije! (%.2f)", mutationChance));
							System.out.println("------------------------------------------------------------");
						}
					}
					else {
						lastMedian = median;
					}
				}
			}
			if (k % 25 == 0 || k == 1) {
				refreshBestScores(k);
				AsteroidsAI.saveObject(generation.get(best), "src/main/resources/" + generation.get(best).getClass().toString().substring(generation.get(best).getClass().toString().lastIndexOf(".") + 1) + k);
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
		double[] sortedGoodnes = Arrays.copyOf(goodness, goodness.length);
		Arrays.sort(sortedGoodnes);

		if (bestMedian == -1 && bestBest == -1 && bestWorst == -1) {
			bestBest = goodness[best];
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
		if (bestBest < goodness[best]) {
			bestBest = goodness[best];
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
		double[] sortedGoodnes = Arrays.copyOf(goodness, goodness.length);
		Arrays.sort(sortedGoodnes);

		printStatistics(sortedGoodnes[sortedGoodnes.length / 2], goodness[best], sortedGoodnes[0], k);

		// first print
		if (firstBest == -1 && firstMedian == -1 && firstWorst == -1) {
			firstBest = goodness[best];
			firstMedian = sortedGoodnes[sortedGoodnes.length / 2];
			firstWorst = sortedGoodnes[0];
		}

		return sortedGoodnes[sortedGoodnes.length / 2];
	}
}
