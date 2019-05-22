package GraphElements;

public interface GraphAlgorithm<V, E> {
    public boolean start();
    public boolean step();
    public void finish();
    public void reset(Graph<V, E> g);
    public void runAlg();
}
