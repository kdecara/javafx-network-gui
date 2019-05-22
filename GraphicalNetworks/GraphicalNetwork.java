package GraphicalNetworks;
import java.util.*;
import javafx.scene.Group;

public class GraphicalNetwork extends Group implements GraphElements.Graph<CircleNode, LinkNode> {
    /**
     * The root of our graphical network. May not be needed
     */
    private CircleNode root;

    /**
     * A collection of the circleNodes in the GraphicalNetwork - used to retrieve a node or for other functions
     */
    private ArrayList<CircleNode> circleNodes = new ArrayList<>();

    /**
     * A hashTable of our circleNodes for fast access
     */
    private Hashtable<Integer, CircleNode> circleNodeHashtable = new Hashtable<>();

    /**
     * An collection of our links. May not be needed
     */
    private ArrayList<LinkNode> linkNodes = new ArrayList<>();

    /**
     * Construct a graphical Network with the given root
     * <p> @param root </p>
     */
    public GraphicalNetwork(CircleNode root){
        this.root = root;
        circleNodeHashtable = new Hashtable<>();
        circleNodeHashtable.put(root.hashCode(), root);
    }

    /**
     * Default constructor
     */
    public GraphicalNetwork(){}

    public boolean addCircleNode(LinkNode link, CircleNode dest, CircleNode newNode){
        if(circleNodeHashtable.containsKey(newNode.hashCode())) return false;
        return this.getChildren().add(newNode) && linkNodes.add(link);
    }

    public boolean addCircleNode(CircleNode newNode){
        if(circleNodeHashtable.containsKey(newNode.hashCode())) return false;
        if( circleNodes.add(newNode) && this.getChildren().add(newNode)) return true;
        else throw new UnsupportedOperationException("Could not add " + newNode.getName() + " to the Graphical Network");
    }

    //TODO: still need to fix this
    public void clearAll(){
        for(CircleNode circleNode : circleNodes) getChildren().remove(circleNode);
        linkNodes = null;
        circleNodes = null;
        circleNodeHashtable = null;
        root = null;
    }

    /**
     * Get a cirleNode with a given name
     * <p> @param name </p>
     * @return the circleNode found with that name
     * @throws NullPointerException if name is null
     * @throws NoSuchElementException if element not founds
     */
    public CircleNode getCircleNode(String name){
        if(name == null) throw new NullPointerException("Cannot retrieve a null circleNode");
        name = name.toUpperCase();
        for(CircleNode circleNode : circleNodes) if(name.equals(circleNode.getName())) return circleNode;
        throw new NoSuchElementException("could not find : " + name);
    }

    /**
     * Returns the number of nodes in this graph by looking at the size of the circleNodes array
     * @return number of nodes
     */
    public int getNumNodes(){return circleNodes.size();}

    /**
     * @return all the circleNodes in this graph by looking by simply returning the circleNodes array
     */
    public Collection<CircleNode> getVertices(){ return circleNodes; }

    /**
     * @return all the edges in this graph by iterating through every node in the graph, adding their links, and returning them as a collection
     */
    public Collection<LinkNode> getEdges(){
        List<LinkNode> linkNodes = new ArrayList<>(circleNodes.size());
        for(CircleNode circleNode : circleNodes) linkNodes.addAll(circleNode.getEdges());
        return linkNodes;
    }

    public Collection<CircleNode> getNeighbors(CircleNode c){return c.getNeighbors();}
}
