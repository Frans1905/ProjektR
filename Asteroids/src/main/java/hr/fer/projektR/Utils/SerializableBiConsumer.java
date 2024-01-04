package hr.fer.projektR.Utils;
import java.util.function.BiConsumer;

public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, java.io.Serializable {

}
