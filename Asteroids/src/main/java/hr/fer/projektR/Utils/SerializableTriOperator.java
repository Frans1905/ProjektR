package hr.fer.projektR.Utils;

public interface SerializableTriOperator<T> extends java.io.Serializable {
    T apply(T first, T second, T third);
}
