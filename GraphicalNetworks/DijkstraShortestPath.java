package GraphicalNetworks;

import GraphElements.Graph;
import javafx.scene.paint.Color;

import java.util.*;

public class DijkstraShortestPath implements GraphElements.GraphAlgorithm<CircleNode, LinkNode> {
    /**
     * The color used to distinguish the node we are currently considering
     */
    public static final Color CURRENT_MIN = Color.RED;
    /**
     * The color used to distinguish the the end points of our path, the start and end nodes
     */
    public static final Color ENDPOINTS = Color.GOLD;
    /**
     * The color used to distinguish CircleNodes that have been visited but are not on the path. Their strokes and possibly fills will be set to gray.
     */
    public static final Color VISITED =  Color.GRAY;
    /**
     * The color used to distinguish nodes on the current path. Their strokes and links will be set to red.
     */
    public static final Color ON_CURRENT_PATH = Color.RED;

    /**
     * Tracks whether we have started the algorithm or not
     */

    private boolean started = false;

    /**
     * Tracks the source and end of our shortest path algorithm
     */
    private CircleNode source, end;

    /**
     * The graph we will apply the Shortest Path Algorithm on. Will be used to get the nodes.
     */
    private Graph<CircleNode, LinkNode> g;

    /**
     * The current node in our step
     */
    private CircleNodePair currentPair;
    //TODO: may have to change the key values of dist and prev to Integeer
    /**
     * Key is hashcode of any Vertex V in the graph, value is the distance associated with it
     */
    private Hashtable<CircleNode, Double> dist;

    /**
     * For every node V define by its hashcode, which is tracked by the key in this Hashtable,
     * this Hashtable will point to the node previous in the shortest path
     * from from start to V
     */
    private Hashtable<CircleNode, CircleNode> prev;

    /**
     * HashSet vertexSet will hold all the nodes we are considering and will serve as a way to track the unvisited or visited nodes
     */
    private PriorityQueue<CircleNodePair> vertexSet;

    /**
     * Used to track the CircleNodes that are still in vertexSet
     */
    private HashSet<CircleNode> q;


    /**
     * Will track the current step number in the algorithm and/or the total number of steps it takes to complete it
     */
    private int stepNumber;

    public DijkstraShortestPath(GraphElements.Graph<CircleNode, LinkNode> g, CircleNode start, CircleNode end){
        this.g = g;
        this.source = start;
        this.end = end;
        int numNodes = g.getNumNodes();
        vertexSet = new PriorityQueue<>(numNodes);
        q = new HashSet<>(numNodes);
        dist = new Hashtable<>(numNodes);
        prev = new Hashtable<>(numNodes);
        stepNumber = 1;
        //start(); //not sure whether to include this in the constructor... it is just a design decision whether we want to use a constructor or not...
        //if(start.getDegree() == 0) finish();
    }

    public void runAlg(){
        if(!started) start();
        while(true) if(!step()) break;
        finish();
    }

    /**
     * perform steps to initialize the algorithm
     * @return true if class contains data to initialize algorithm, false if not
     */
    public boolean start(){
        if(this.source == null || this.end == null || this.g == null) return false;
        this.started = true;
        source.setCircleStrokeFill(ENDPOINTS);
        end.setCircleStrokeFill(ENDPOINTS);
        dist.put(source, 0.0);
        CircleNode undefinedNode = new CircleNode();
        Collection<CircleNode> vertices = g.getVertices();
        for(CircleNode vertex : vertices){
            if(!vertex.equals(source)) dist.put(vertex, Double.MAX_VALUE);
            prev.put(vertex, undefinedNode);
            vertexSet.add(new CircleNodePair(vertex, dist.get(vertex)));
            q.add(vertex);
        }
        return true;
    }

    /**
     * one step of the shortest path algorithm
     * @return true if the step could be completed, false if not
     */
    public boolean step(){
        if(!started){
            this.started = true;
            start();
            stepNumber++;
            return true;
        }
        if(!vertexSet.isEmpty()){
            currentPair = vertexSet.remove();
            q.remove(currentPair.getFirst());
            if(!currentPair.getFirst().equals(source) && ! currentPair.getFirst().equals(end)) currentPair.getFirst().setCircleStrokeFill(VISITED);
            update();
            stepNumber++;
            return true;
        }
        return false;
    }

    public void finish(){
        System.out.println("Algorithm completed in " + stepNumber + " steps");
        buildPath();
        //Set<CircleNode> keys = prev.keySet();
        //for(CircleNode key : keys) System.out.println(prev.get(key) + "->" + key);
        //reset(g);
    }

    public void reset(GraphElements.Graph<CircleNode, LinkNode> g){
        this.g = g;
        this.started = false;
    }

    /**
     * The currentPair holds the node we are considering with its tentative distance calculated thus far.
     * Here we update the tentative distance of each node. Tentative distance is defined as current distance of node we
     * are considering (held in currentPair) + the distance of an arbitrary node from our current node
     * Update the tentative distances from the currentNode to its neighbors. Replace the distance if the new one is smaller
     */
    private void update(){
        Collection<LinkNode> links = currentPair.getFirst().getEdges();
        for(LinkNode edge : links){
            CircleNode destination = edge.getEnd();
            if(q.contains(destination)){
                double tentativeDistance = dist.get(currentPair.getFirst()), linkDistance = edge.getDistance();
                double alt = tentativeDistance + linkDistance;
                if(alt < dist.get(destination)){
                    vertexSet.remove(new CircleNodePair(destination, dist.get(destination)));
                    dist.replace(destination, alt);
                    prev.replace(destination, currentPair.getFirst());
                    vertexSet.add(new CircleNodePair(destination, dist.get(destination)));
                }
            }
        }
    }

    /**
     * Once the algorithm is done, buildPath is called to determine if a shortest path between the nodes could be found.
     * If so, it will build and color the path on the GUI.
     */
    private void buildPath(){
        Stack<CircleNode> shortestPath = new Stack<>();
        shortestPath.push(end);
        CircleNode previousNode = end;
        while(prev.get(previousNode).getName() != null) shortestPath.add( previousNode = prev.get(previousNode) );
        while(!shortestPath.isEmpty()){
            CircleNode currentNode = shortestPath.pop();
            if(shortestPath.isEmpty() || shortestPath.peek().getName() == null) break;
            currentNode.setCircleStrokeFill(ON_CURRENT_PATH);
            LinkNode linkNode = currentNode.getLinksToVertex(shortestPath.peek()), oppositeLink = shortestPath.peek().getLinksToVertex(currentNode);
            linkNode.setLineColor(ON_CURRENT_PATH);
            oppositeLink.setLineColor(ON_CURRENT_PATH);
        }
        source.setCircleStrokeFill(ENDPOINTS);
        end.setCircleStrokeFill(ENDPOINTS);
    }

}
