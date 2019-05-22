package Utility;

public interface Pair<V, E>{

     V getFirst();

     E getSecond();

     void setFirst(V first);

     void setSecond(E second);

     int hashCode();
}
