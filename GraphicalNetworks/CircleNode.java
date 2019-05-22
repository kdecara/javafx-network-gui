package GraphicalNetworks;
import java.util.*;

import GraphElements.Vertex;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;

/**
 * The CircleNode class with define the nodes, or vertices, seen on the GUI. Each CircleNode will keep
 * track of its own links, position, and graphical elements such as text, color, and so on.
 * The CircleNode will extend Group because when you want to make a custom node in JavaFX it must either extend
 * already define in JavaFX, like Group, Pane, etc.
 * Group is usually the default choice because it's bounds are set by it's children so you can customize it more.
 */
class CircleNode extends Group implements Comparable<CircleNode>, Vertex<CircleNode, LinkNode>{
    /**
     * Tracks the name of this CircleNode
     * Must be unique for algorithms to work, as the equals() method relies on names being unique.
     */
    private String name;
    /**
     * The x and y position of this circleNode on the screen
     */
    private double x, y;

    /**
     * The circle itself
     */
    private Circle circle;

    /**
     * The text of this circle will consists of its name.
     */
    private Text text;

    /**
     * Tracks the color - is random.
     */
    private Color color;

    /**
     * Every layout ended up centering its children - as such I didn't use it. However, this class could be improved in thee future by implementing
     * a layout, as such I will leave it.
     */
    private Pane layout;

    /**
     * A label could serve as a better way to track a CircleNode's name on the GUI. However, I couldn't get the label to stay near
     * or on the CircleNode. As such, I didn't use it. I leave it as it is in case this class is improved in the future.
     */
    private Label label;

    /**
     * The boolean value that decides whether this CircleNode is able to be dragged or not.
     */
    private boolean drag;

    /**
     * The Observable list of LinkNodes.
     */
    private final ObservableList<LinkNode> links = FXCollections.observableArrayList();

    /**
     * Random value for color
     */
    private final Random rng = new Random();

    /**
     * Tracks the constant radius value which is 15 pixels
     */
    private static final int R = 15;

    /**
     * Creates a CirleNode at position x, y with said Name
     * <p> @param x </p> x position
     * <p> @param y </p> y position
     * <p> @param name </p> name
     */
    public CircleNode(double x, double y, String name){
        drag = false;
        layout = new Pane();
        this.name = name;
        label = new Label(name, layout);
        label.setAlignment(Pos.CENTER);
        this.x = x;
        this.y = y;
        this.circle = createCircle(x, y);
        this.text = createText();
        this.createLinkHandler();
        getChildren().addAll(circle, text);
    }

    /**
     * creates an undefine circle.
     */
    public CircleNode(){ name = null; }

    /**
     * creates a circle at coordinates a and y
     *<p> @param x </p>
     *<p> @param y </p>
     * @return the circle created at coordinates x and y
     */

    private Circle createCircle(double x, double y){
        Circle circle = new Circle(x, y, R);
        circle.setFill(color = randomColor());
        circle.setStrokeWidth(2);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    /**
     * Self-Explanatory. Couldn't find a better way to center the text than doing R/4. This can be improved.
     * @return text for this CircleNode.
     */
    private Text createText(){
        final Text text = new Text(name);
        text.setFont(new Font(15));
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setX(circle.getCenterX() - R/4); //TODO: find a better way to center this than dividing by 4...
        text.setY(circle.getCenterY() + R/4);
        return text;
    }

    /**
     * handles the event for dragging and creating links
     */
    private void createLinkHandler(){
        this.setOnDragDetected(de -> {
            this.startFullDrag();
            System.out.println("Full drag started on " + ((CircleNode)de.getSource()).name);
        });

        this.setOnMouseDragged(e -> {
            if(drag) {
                double deltaX = e.getX() - this.x;
                double deltaY = e.getY() - this.y;
                this.x = e.getX();
                this.y = e.getY();
                circle.setCenterX(circle.getCenterX() + deltaX);
                circle.setCenterY(circle.getCenterY() + deltaY);
                text.setX(circle.getCenterX() - R / 4);
                text.setY(circle.getCenterY() + R / 4);
                this.x = e.getX();
                this.y = e.getY();
                updateDistance();
            }
        });

        this.setOnMouseDragReleased(de -> {
            CircleNode source = (CircleNode)de.getGestureSource();
            if(de.getGestureSource() == null){
                //System.out.println("Failed to create link, No CircleNode found to complete the drag");
                return;
            }
            LinkNode link = new LinkNode(this, source), oppositeLink = new LinkNode(source, this);
            //TODO: iterator though collection using equals() method to check if link already exists
            if(!links.contains(link)) {
                this.addLink(link);
                source.addLink(oppositeLink);
            }
            else System.out.println("Failed to complete link");
        });
    }


    /**
     * Reset the circle color to its original value once we are done with the algorithm
     */
    public void resetCircleColor(){
        circle.setFill(color);
        circle.setStroke(Color.BLACK);
    }

    /**
     * Change the color of this CircleNode to the value passed
     * <p> @param color </p>
     */
    public void setCircleStrokeFill(Color color){ circle.setStroke(color); }

    /**
     * Update the distance of each link for when the CircleNode is dragged.
     */
    private void updateDistance(){ for(LinkNode linkNodes : links) linkNodes.updateDistance(); }

    /**
     * will add a link to this CircleNode. Will first check if this link already exists in the CircleNode.
     * This method will need to be modified if this program is ever adjust to allow for multiple links from one CircleNode to another.
     * <p> @param link </p>
     * @return true if this link could be added (it doesn't already exist in this node), false if not
     */
    private boolean addLink(LinkNode link){
        if(this.links.contains(link)) return false;
        links.add(link);
        this.getChildren().add(link);
        return true;
    }

    /**
     * @return all the destinations of this circleNdoe
     */
    public Collection<CircleNode> getDestinations(){
        List<CircleNode> destinations = new ArrayList<>(links.size());
        for(LinkNode linkNode : links) destinations.add(linkNode.getEnd());
        return destinations;
    }

    /**
     * If you want the Circle To have fixed color, this can be used
     * @return a color to fill our circle with
     */
    public Color selectColor(){
        Color[] colors = {Color.VIOLET, Color.GREEN, Color.GRAY, Color.GOLD, Color.BLUE, Color.BLUEVIOLET, Color.YELLOW, Color.DARKGREEN};
        return colors[(int)(Math.random() * (colors.length - 1 - 1) + 1)];
    }

    /**
     * Using an Object from the Random Class named rng, we use the color constructor and 3 random values from rng
     * to create a color with a random RGB value.
     * @return a random color we can use to color the circle
     */
    private Color randomColor() { return new Color(rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), 1); }

    /**
     * @return the circle. Used for testing intersections and calculating distances.
     */
    public Circle getCircle(){ return circle; }

    /**
     * @return the degree of this CircleNode, which tracked via the size of the circleNodes ArrayList
     */
    public int getDegree(){ return links.size(); }

    /**
     * @return all the Neighbors of this circleNode
     */
    public Collection<CircleNode> getNeighbors(){
        List<CircleNode> neighbors = new ArrayList<>(links.size());
        for(LinkNode linkNode : links) neighbors.add(linkNode.getEnd());
        return neighbors;
    }

    /**
     * @return all the edges, or LinkNodes, of this CircleNode
     */
    public Collection<LinkNode> getEdges(){ return links; }

    /**
     * <p> @param other </p> the circleNode we are comparing this CircleNode to
     * @return true this this circleNode and other are equal, false if not
     */
    public boolean equals(CircleNode other){ return this.name.equals(other.name); }

    /**
     * While the name of each node is unique, which must be true for this,
     * program to work, this method will always return a unique value for this CircleNode.
     * Works by multiplying a prime number by each character in the name of this node and then multiplying that by the degree.
     * @return an integer unique to this CircleNode.
     */
    public int hashCode(){
        int hc = 7 * links.size();
        char [] nameArray = name.toCharArray();
        for(char c : nameArray) hc *= (int)c;
        return hc;
    }

    /**
     * returns the linkNode that connects this CircleNode with the one passed as a parameter
     * <p> @param circleNode </p>
     * @return LinkNode if found. Else, throw NullPointer or NoSuchElementexception
     * @throws NoSuchElementException if not found
     */
    public LinkNode getLinksToVertex(CircleNode circleNode){
        if(circleNode == null) throw new NullPointerException("getLinksToVertex() cannot process a null value");
        for(LinkNode linkNode : links) if(linkNode.getEnd().equals(circleNode)) return linkNode;
        throw new NoSuchElementException(" In getLinksToVertex() " + circleNode.getName() + " is not in the links of " + this.getName());
    }

    /**
     * @return the unique name of this string
     */
    public String toString(){ return name; }

    /**
     * Compares this CircleNode with another circleNode via their degree, the number of connections they have.
     * <p> @param other </p>
     * @return positive if this CircleNode is greate than other, 0 if no conclusion could be reached, negative if other is larger than this CircleNode
     */
    public int compareTo(CircleNode other){ return this.getDegree() - other.getDegree(); }

    /**
     * @return the unque name of this circleNode
     */
    public String getName(){ return name; }

    /**
     * <p> @param value </p> enables this CircleNdoe to be dragged if set to true
     */
    public void setDrag(boolean value){ this.drag = value; }

}