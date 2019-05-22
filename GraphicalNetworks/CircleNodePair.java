package GraphicalNetworks;
import Utility.Pair;
public class CircleNodePair implements Pair<CircleNode, Double>, Comparable<CircleNodePair> {

    private CircleNode circleNodeFirst;
    private Double tentaiveDistanceSecond;

    public CircleNodePair(CircleNode first, Double second){
        this.circleNodeFirst = first;
        this.tentaiveDistanceSecond = second;
    }

    public CircleNode getFirst(){return circleNodeFirst;}

    public Double getSecond(){ return tentaiveDistanceSecond;}

    public void setFirst(CircleNode first){this.circleNodeFirst = first;}

    public void setSecond(Double second){this.tentaiveDistanceSecond = second;}

    public int hashCode(){ return circleNodeFirst.hashCode(); }

    public int compareTo(CircleNodePair other){ return Double.compare(tentaiveDistanceSecond, other.getSecond() );}

    public boolean equals(CircleNodePair other){ return circleNodeFirst.equals(other.getFirst()) && tentaiveDistanceSecond.equals(other.getSecond());}

}
