package GraphicalNetworks;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;

/**
 * The LinkNode will represent the edge in our graph.
 * It extends group to allow for the creation of a custom node.
 * Consists of values such as distance, and tracks references to each node on it's ends. Tracks graphical
 * elements such as Line and Label as well.
 */
class LinkNode extends Group implements GraphElements.Edge<CircleNode, LinkNode>, Comparable<LinkNode>{
    /**
     * Tracks the underlying line in this LinkNode. The Line will be bound to the nodes it tracks at the start and end so that
     * when the either the start or end is moved, the line automatically moves with it.
     * The distance is also updated when this happens via the updateDistance method.
     */
    private Line line;

    /**
     * tracks the start and end of this LinkNode
     */
    private CircleNode start, end;

    /**
     * Tracks the distance from CircleNode start to CircleNode end
     */
    private double distance;

    /**
     * Label which will be implemented in the future to display the distance on the screen
     */
    private Label label;

    /**
     * Creates a LinkNode between CircleNode start and CircleNode end
     * <p> @param start </p>
     * <p> @param end </p>
     */
    public LinkNode(CircleNode start, CircleNode end){
        if(start.getParent() != end.getParent()) throw new IllegalArgumentException("Nodes are in different containers");
        this.start = start;
        this.end = end;
        double x1 = start.getCircle().getCenterX(), y1 = start.getCircle().getCenterY(), x2 = end.getCircle().getCenterX(),
                y2 = end.getCircle().getCenterY();
        //line = new Line(x1, y1, x2, y2);
        line = new Line();
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = start.getCircle().getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2 ;
        }, start.boundsInParentProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = start.getCircle().getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2 ;
        }, start.boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = end.getCircle().getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2 ;
        }, end.boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = end.getCircle().getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2 ;
        }, end.boundsInParentProperty()));
        this.distance = distance(x1, y1, x2, y2);
        /*label = new Label(String.format("%.3f", distance));
        //to move the label to the middle of the line, we need to get the midpoint of the line
        double midX = Math.abs(line.getEndX() - line.getStartX()), midY = Math.abs(line.getEndY() - line.getEndX());
        label.setLabelFor(line);
        label.setTranslateX(midX);
        label.setTranslateY(midY);
        //label.setAlignment(Pos.CENTER); */
        getChildren().addAll(line);
    }

    /**
     * @return the CircleNode start of this LinkNode
     */
    public CircleNode getStart(){return start;}

    /**
     * @return the CircleNode end of this LinkNode
     */
    public CircleNode getEnd(){return end;}

    /**
     * @return the distance of this LinkNode
     */
    public double getDistance(){return new LinkNode(start, end).distance;}

    /**
     * Compares LinkNodes based on their distance
     * <p> @param other </p> is the LinkNode we are comparing to
     * @return positive if this LinkNode has a greater distance than other, 0 if equals, and negative other is greater than this LinkNode
     */
    public int compareTo(LinkNode other){ return Double.compare(this.distance, other.distance); }

    /**
     * Calculates the distance between any two points.
     * Used to calculate distance of this LinkNode from start to end is calculated via the distance formula.
     * Is static because it could possibly have use even when this class has not been instantiated.
     * <p> @param x1 </p> the beginning x value
     * <p> @param y1 </p> the beginning y value
     * <p> @param x2 </p> the end x value
     * <p> @param y2 </p> the end y value
     * @return the distance between two points
     */
    public static double distance(double x1, double y1, double x2, double y2){ return Math.sqrt( Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) ); }

    /**
     * Updates distance during the drag of a CircleNode by creating a new LinkNode and returning it's distance.
     */
    public void updateDistance(){ this.distance = new LinkNode(start, end).distance; }

    /**
     * Used to compare this LinkNode to other
     * <p> @param other </p>
     * @return true if they are equal, false if not
     */
    public boolean equals(LinkNode other){ return start.equals(other.start) && end.equals(other.end) && distance == other.distance; }

    /**
     * This hashCode is guaranteed to be unique as long as the names of each CircleNode remain unique
     * @return a hashCode of this LinkNode which is just the hashCode of the start * hashCode of the end * the distance of this LinkNode
     */
    public int hashCode(){ return start.hashCode()*end.hashCode()*(int)distance; }

    /**
     * @return String value of this LinkNode
     */
    public String toString(){ return start.toString() + "->" + distance + "->" + end.toString(); }

    /**
     * Allows the line color to be changed. Useful for displaying a path
     * <p> @param color </p>
     */
    public void setLineColor(Color color){ line.setStroke(color); }

    /**
     * Resets the color for when you want it to be black again
     */
    public void resetColor(){line.setStroke(Color.BLACK);}
}