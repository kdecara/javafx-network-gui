package GraphicalNetworks;


import GraphElements.GraphAlgorithm;
import Utility.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.beans.value.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * The NetworkGUI is used to display graph algorithms
 */
public class NetworkGUI extends Application{
    private static final int W = 1200;
    private static final int H = 600;
    /**
     * tracks the main scene
     */
    private Scene scene;
    /**
     * tracks the stage
     */
    private Stage stage;

    /**
     * Root of this scene will be the main GridPane
     */
    private GridPane root;

    /**
     * root of the SubScene will be the GraphicalNetwork netRoot
     */
    private GraphicalNetwork netRoot;

    /**
     * Allows for creating node names
     */
    private Lexicon lexicon;

    /**
     * The algorithm we are using. Will change depending on what the user selects.
     */
    private GraphAlgorithm<CircleNode, LinkNode> alg = null;

    /**
     * Tracks the mode of the subScene to be either move nodes or create links
     */
    private Label modeLabel;

    /**
     * The console in which we type commands
     * Credit to @skiwi on stack exchange code review for giving me the basic skeleton
     */
    private Console console;

    /**
     * Observable list we will use to track our nodes to make sure they don't intersect with anything
     */
    private final ObservableList<CircleNode> circleNodes = FXCollections.observableArrayList();

    /**
     * the string to track the create links of mode of the subScene
     */
    private final String mode1 = "Create Links";

    /**
     * The string to track the move nodes mode of the subScene
     */
    private final String mode2 = "Edit and Transform Nodes";
    //final ObservableList<ShapePair> intersections = FXCollections.observableArrayList();

    /**
     * And enum which holds our bounds types
     * LAYOUT_BOUNDS = boundary of the shape and effect
     * BOUNDS_IN_LOCAL = what you see
     * BOUNDS_IN_PARENT = boundary of the shape, effect and transforms and co-ordinates of what you see.
     */
    enum BoundsType { LAYOUT_BOUNDS, BOUNDS_IN_LOCAL, BOUNDS_IN_PARENT }

    /**
     * Launches the application
     * <p> @param args </p> program arguments not used for this program...
     */
    public static void main(String [] args){ Application.launch(args); }

    /**
     * Starts the program
     * <p> @param stage </p>
     */
    public void start(Stage stage){
        stage.setWidth(W);
        stage.setHeight(H);
        modeLabel = new Label("");
        //define what goes on in the scene. We start with a root node
        //The scene will be made up of two horizontal boxes: One to display/interact with the network
        //and the other for buttons and a console log
        lexicon = new Lexicon(); // this is for naming nodes
        netRoot = new GraphicalNetwork();

        //create the console
        console = createConsole();
        //and the other to serve as a log or button holder or both
        root = createMainPane();
        //Scene scene = new Scene(box,400, 350);
        scene = new Scene(root,W, H);
        root.setPrefWidth(scene.getWidth());
        //define the parameters for the stage
        this.stage = stage;
        stage.setScene(scene);
        stage.setTitle("Network GUI");
        stage.show();
    }

    /**
     * Interprets the argument from the console.
     * <p> @param argument </p> will be a string of the following form: algorithm Nodes_involved_in_Algorithm
     */
    private void interpretArgument(String argument){
        Scanner scanner = new Scanner(argument.toLowerCase().trim());
        ArrayList<String> terms = new ArrayList<>();
        while(scanner.hasNext()){
            String s = scanner.next();
            terms.add(s);
        }
        scanner.close();
        switch (terms.get(0)){
            case "sp":

        }
        if(terms.get(0).equals("dijkstra") || terms.get(0).equals("sp")){
            CircleNode start = netRoot.getCircleNode(terms.get(1));
            CircleNode end = netRoot.getCircleNode(terms.get(2));
            alg = new DijkstraShortestPath(netRoot, start, end);
            alg.runAlg();
        }
    }

    /**
     * Tests if a newly added node to the subScene intersects with any other nodes.
     * This is done by testing the newly added node against the LayoutBounds of each
     * node already in the subScene, which are tracked in our circleNode array above.
     * @return true if newly added node intersects, false if not.
     */
    // test if a newly added node intersects with any other circleNodes
    private boolean intersects() {
        // for each shape test it's intersection with all other shapes.
        for (CircleNode circleNode : circleNodes) {
            for (CircleNode other : circleNodes) {
                ShapePair pair = new ShapePair(circleNode.getCircle(), other.getCircle());
                if(pair.intersects(BoundsType.LAYOUT_BOUNDS)){
                    System.out.println("layout bounds intersection between " + circleNode.getName() + " and " + other.getName());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates the main paine, which serves as the root for the scene. It consists of 3 rows, which
     * hold the menuBar, the subscene, and the bottom grid (the button pane and the console), respectively.
     * Only 1 column is needed.
     * @return main pane
     */
    private GridPane createMainPane(){
        GridPane box = new GridPane();
        ColumnConstraints col = new ColumnConstraints();
        RowConstraints row_1 = new RowConstraints(), row_2 = new RowConstraints(), row_3 = new RowConstraints();
        //create the column
        col.setPercentWidth(100); // use 100% of GridPane's available space to cover the whole screen
        box.getColumnConstraints().add(col);
        row_1.setPercentHeight(7);
        row_2.setPercentHeight(70);
        row_3.setPercentHeight(23);
        box.getRowConstraints().addAll(row_1, row_2, row_3);
        VBox menuBar = createMenuBar();
        box.add(menuBar, 0, 0);
        //ADDING THE SUBSCENE
        SubScene netScene = createSubScene();
        box.add(netScene, 0, 1);
        GridPane bottom_grid = createBottomGrid();
        box.add(bottom_grid, 0, 2);
       // box.setGridLinesVisible(true); // -> set grid lines visible for practice
        return box;
    }

    /**
     * Creates the console for which we can type commands in.
     * @return console
     */
    private Console createConsole(){
        Console console = new Console();
        Consumer<String> consumer = i -> interpretArgument(i);
        console.setOnMessageReceivedHandler(consumer);
        return console;
    }

    /**
     * Creates the menuBar, which at the top of the mainPane
     * @return the menuBar
     */
    private VBox createMenuBar(){
        VBox box = new VBox();
        MenuBar menuBar = new MenuBar();
        Menu menuMode = new Menu("Mode"), menuInstrcutions = new Menu("Instructions");
        MenuItem programIntro = new MenuItem("Program Introduction"), consoleInstructions = new MenuItem("Console Instructions"),
                generalInstructions = new MenuItem("General Instructions");
        programIntro.setOnAction( e -> createProgramInstructionsWindow());
        consoleInstructions.setOnAction(e -> createConsoleInstructionsWindow());
        menuInstrcutions.getItems().addAll(programIntro, generalInstructions, consoleInstructions);
        ToggleGroup modes = new ToggleGroup();
        RadioMenuItem edit = new RadioMenuItem(mode1), link = new RadioMenuItem(mode2);
        edit.setToggleGroup(modes);
        link.setToggleGroup(modes);
        modes.selectToggle(edit);
        modes.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioMenuItem rb = (RadioMenuItem) modes.getSelectedToggle();
                if (rb != null) {
                    if(rb.getText().equals(mode1)) toggleDrag(false);
                    else toggleDrag(true);
                    modeLabel.setText(rb.getText());
                }
            }
        });
        menuMode.getItems().addAll(edit, link);
        menuBar.getMenus().addAll(menuMode, menuInstrcutions);
        box.getChildren().add(menuBar);
        box.setFillWidth(true);
        box.setOnMouseClicked(e -> System.out.println("Pressed"));
        VBox.setVgrow(menuBar, Priority.ALWAYS);
        return box;
    }

    /**
     * Creates and returns the bottom grid of the scene, which contains the console for typing commands on the right side
     * and the button panel on the left side. The bottom grid is itself a grid pane with 1 row and  2 columns.
     * @return bottom grid.
     */
    private GridPane createBottomGrid(){
        GridPane bottom_grid = new GridPane();
        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);
        bottom_grid.getRowConstraints().add(row);
        ColumnConstraints bottom_cols = new ColumnConstraints();
        bottom_cols.setPercentWidth(50); // set width of both columns to 50% of available space
        bottom_grid.getColumnConstraints().add(bottom_cols); //first column
        bottom_grid.getColumnConstraints().add(bottom_cols); // second column
        bottom_grid.setGridLinesVisible(true);
        bottom_grid.add(createButtons(bottom_grid.getHeight(), bottom_grid.getWidth()), 0, 0);
        bottom_grid.add(this.console, 1, 0);
        return bottom_grid;
    }

    /**
     * Creates the button panel for the bottom grid, which will occupy the left side of the bottom grid.
     *  <p> @param prefHeight </p> is the preferred height for the button pane. Will likely be ignored to occupy available space demands.
     * <p> @param prefWidth </p> is the preferred width for the button pane. Will likely be ignored to meet occupy available space.
     * @return the button pane
     */
    private GridPane createButtons(double prefHeight, double prefWidth){
        //create buttons, 6 buttons in total organized in a grid
        GridPane buttonPane = new GridPane();
        buttonPane.setStyle("-fx-background-color: #E3DADA;"); //C4C4C4
        buttonPane.setPrefSize(prefWidth, prefHeight);
        buttonPane.setAlignment(Pos.CENTER);
        Button[][] buttons = { {new Button("is this graph hamiltonian"), new Button("create random graph"), new Button("reset node colors"),},
                {new Button("cear all nodes"), new Button("create random graph"), new Button("stats for nerds")}};
        buttons[1][0].setOnAction( e -> {
            netRoot.clearAll();
            circleNodes.clear();
            lexicon = new Lexicon();
        });
        for(Button [] currentButtonRow: buttons){
            for(Button button: currentButtonRow){
                button.setPrefWidth(150);
                button.setPrefHeight(50);
                button.setAlignment(Pos.CENTER);
            }
        }
        //grid will be 3 columns x 2 rows
        //add two rows
        for(int i = 0; i < 2; i++){
            RowConstraints row = new RowConstraints();
            row.setFillHeight(true);
            row.setPercentHeight(50);
            buttonPane.getRowConstraints().add(row);
        }
        //add 3 columns
        for(int n = 0; n < 3; n++){
            ColumnConstraints col = new ColumnConstraints();
            col.setFillWidth(true);
            col.setPercentWidth(33);
            buttonPane.getColumnConstraints().add(col);
        }
        //add the buttons to a grid pane and return it
        for(int i = 0; i < buttonPane.getRowConstraints().size(); i++) {
            Button[] current_row = buttons[i];
            for(int n = 0; n < buttonPane.getColumnConstraints().size(); n++) {
                Button current_button = current_row[n];
                GridPane.setHalignment(current_button, HPos.CENTER);
                buttonPane.add(current_button, n, i);
                GridPane.setFillWidth(current_button, true);
                GridPane.setFillHeight(current_button, true);
            }
        }
        return buttonPane;
    }

    /**
     * The subScene is the the scene where the action happens. if the mode is "Create Links", Nodes are created
     * with a double click here and links are created by a dragging motion from one node to another. If the mode is
     * "Edit and Transform" nodes are moved by clicking and moving them to their new position via a dragging motion.
     * The color for the subScene is white to contrast with the nodes clearly. Interestingly, the event handler for the
     * subScene did not work until it's color/fill was set to white.
     */
   private SubScene createSubScene(){
       //netRoot = new GraphicalNetwork();
       SubScene netScene = new SubScene(netRoot, W, 420);
       netScene.setFill(Color.WHITE); //TODO: WHY do you have to set color to make the subscene active? CHECK THIS OUT
       netScene.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
           if(me.getButton().equals(MouseButton.PRIMARY)) {
               if(me.getClickCount() == 2){
                   CircleNode node = new CircleNode(me.getX(), me.getY(), lexicon.getNextName());
                   circleNodes.add(node);
                   //if it does intersect then remove it
                   if(intersects()) circleNodes.remove(node);
                   else netRoot.addCircleNode(node);
               }
           }
       });
       return netScene;
    }
    /*
    //The event handler for removing any node on double click
    public void removeNodeHandler(Group parent, CircleNode node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if(me.getButton().equals(MouseButton.SECONDARY)) {
                parent.getChildren().remove(node);
            }
        });
    }*/

    /**
     * Toggle the drag on the nodes.
     * <p> @param value </p>
     */
    private void toggleDrag(boolean value){ for(CircleNode circleNode : circleNodes) circleNode.setDrag(value); }

    /**
     * Creates the program instructions window, which is activated under the instructions menuItem on the menuBar
     */
    private void createProgramInstructionsWindow(){
        final Stage reportingStage = new Stage();
        reportingStage.setWidth(this.stage.getWidth()/2);
        reportingStage.setHeight(this.stage.getHeight()/2);
        reportingStage.setTitle("Instructions Panel");
        reportingStage.initStyle(StageStyle.UTILITY);
        reportingStage.setX(this.scene.getX());
        reportingStage.setY(this.scene.getY());
        WebView boundsExplanation = new WebView();
        boundsExplanation.getEngine().loadContent(
                "<html><body bgcolor='darkseagreen' fgcolor='lightgrey' style='font-size:12px'><dl>" +
                        "<p>NetworkGUI is a program written in JavaFX meant to display different graph algorithms. I wrote this partly " +
                        "to learn JavaFX and partly to assist in understanding common graph algorithms.</p>" +
                        "<p>Start by double clicking on the white space below to add a node. Click and drag from one node to another to " +
                        "create a link. Then, use either the buttons on the bottom left of the scene or the console on the bottom right to type" +
                        " in commands for a specified algorithm."
        );
        boundsExplanation.setStyle("-fx-background-color: transparent");
        VBox layout = new VBox(10);
        layout.getChildren().addAll(boundsExplanation);
        // ensure the utility window closes when the main app window closes.
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent windowEvent) {
                reportingStage.close();
            }
        });
        reportingStage.setScene(new Scene(layout));
        reportingStage.show();
    }

    /**
     * Creates the console instructions window, which is activated under the instructions menuItem on the menuBar
     */
    private void createConsoleInstructionsWindow(){
        final Stage reportingStage = new Stage();
        reportingStage.setWidth(this.stage.getWidth()/2);
        reportingStage.setHeight(this.stage.getHeight()/2);
        reportingStage.setTitle("Instructions Panel");
        reportingStage.initStyle(StageStyle.UTILITY);
        reportingStage.setX(this.scene.getX());
        reportingStage.setY(this.scene.getY());
        WebView boundsExplanation = new WebView();
        boundsExplanation.getEngine().loadContent(
                "<html><body bgcolor='darkseagreen' fgcolor='lightgrey' style='font-size:12px'><dl>" +
                        "<h1><b>Node Commands: </b></h1>" +
                        "<p>To get information on a certain node, simply type in it's id</p><BR>" +
                        "<h1><b>Link Commands: </b></h1>" +
                        "<p>To get all links from Node A to Node B, type the name of A and the name of B separated by a space</p>" +
                        "<h1><b>Algorithm Commands:</b></h1>" +
                        "<p>To display a certain algorithm write the algorithm and the Nodes in it. The algorithm will not run if" +
                        "the number of nodes in the argument is less than required for that algorithm, or if any of the nodes are not in the graph." +
                        "If more nodes are passed than are required for that algorithm, only the most recent ones will be used</p><BR>" +
                        "<p>Example: Dijkstra A B will show the Dijkstra algorithm from A to B.</p><BR>" +
                        "<p>Note that the console is not case sensitive so caps vs lower case does not matter.</p>"
        );
        boundsExplanation.setStyle("-fx-background-color: transparent");
        VBox layout = new VBox(10);
        layout.getChildren().addAll(boundsExplanation);
        // ensure the utility window closes when the main app window closes.
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent windowEvent) {
                reportingStage.close();
            }
        });
        reportingStage.setScene(new Scene(layout));
        reportingStage.show();
    }

    /**
     * Records a pair of possibly intersecting shapes.
     * Credit goes to @jewelsea on github, which were I got this code
     */
    private class ShapePair {
        /**
         * Tracks two pairs of possibly intersecting shapes a and b
         */
        private Shape a, b;

        /**
         * Constructor that creates the shape pair
         * <p> @param src </p> is shape a
         * <p> @param dest </p> is shape b
         */
        public ShapePair(Shape src, Shape dest) { this.a = src; this.b = dest; }

        /**
         * Returns true if the shapes A and B intersect given the bounds type passed in the parameter
         * <p> @param boundsType </p> is the bounds type to test intersection on.
         * @return true if a and b intersect, false if not
         */
        public boolean intersects(BoundsType boundsType) {
            if (a == b) return false;
            a.intersects(b.getBoundsInLocal());
            switch (boundsType) {
                case LAYOUT_BOUNDS:    return a.getLayoutBounds().intersects(b.getLayoutBounds());
                case BOUNDS_IN_LOCAL:  return a.getBoundsInLocal().intersects(b.getBoundsInLocal());
                case BOUNDS_IN_PARENT: return a.getBoundsInParent().intersects(b.getBoundsInParent());
                default: return false;
            }
        }

        /**
         * @return id of the shape pair based on the id of a and b respectively
         */
        @Override public String toString() {
            return a.getId() + " : " + b.getId();
        }

        /**
         * <p> @param other </p> is the Object we are comparing shapePair to
         * @return true if they are equal, false if not
         */
        @Override
        public boolean equals(Object other) {
            ShapePair o = (ShapePair) other;
            return o != null && ((a == o.a && b == o.b) || (a == o.b) &&  (b == o.a));
        }

        /**
         * @return a hashCode based on shapes a and b
         */
        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }
    }
}

