package hr.fer.projektR.evolucijski;


public interface Jedinka {
	void fromParents(Jedinka parent1, Jedinka parent2);
	void mutate();
	void copy(Jedinka source);
	double fitness();
	void randomize();
}
