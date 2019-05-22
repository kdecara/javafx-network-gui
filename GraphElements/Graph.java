package GraphElements;

import java.util.Collection;

public interface Graph<V, E> {
    int getNumNodes();
    Collection<V> getVertices();
    Collection<E> getEdges();
    Collection<V> getNeighbors(V vertex);
}

