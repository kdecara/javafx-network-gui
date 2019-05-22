package GraphElements;

import java.util.Collection;

public interface Vertex<V, E> {
    int getDegree();
    Collection<V> getNeighbors();
    Collection<E> getEdges();
}
