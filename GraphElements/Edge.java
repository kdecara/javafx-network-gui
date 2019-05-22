package GraphElements;

public interface Edge<V, E> {
    public V getStart();
    public V getEnd();
    public double getDistance();
}
