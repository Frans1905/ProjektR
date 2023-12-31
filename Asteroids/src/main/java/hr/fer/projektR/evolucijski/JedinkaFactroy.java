package hr.fer.projektR.evolucijski;

public interface JedinkaFactroy <T extends Jedinka> {
	T create();
}
