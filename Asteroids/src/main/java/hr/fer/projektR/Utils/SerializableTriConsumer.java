package hr.fer.projektR.Utils;

public interface SerializableTriConsumer<T, U, V> extends java.io.Serializable {
    public void accept (T first, U second, V third);
}
