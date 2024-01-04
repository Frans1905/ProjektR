package hr.fer.projektR.evolucijski;


public interface Jedinka extends java.io.Serializable {
	void fromParents(Jedinka parent1, Jedinka parent2);
	void mutate(double mutatuinChance);
	void copy(Jedinka source);
	double fitness();
	void randomize();
}
