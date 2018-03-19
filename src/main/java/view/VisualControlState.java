package view;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;

/**
 * A graphic object which represents the control state on the screen
 */
public class VisualControlState {

    //fields
    private String label;
    private double xPos;
    private double yPos;
    private HBox view;
    private boolean isFocused;
    private boolean isInitial;
    private final int RADIUS = 26;
    private StackPane controlStateView;
    private int orderShown;

    /**
     * A constructor for a VisualControlState instance
     *
     * @param label       label of control state
     * @param isInitial   defines whether control state is the initial state
     * @param isAccepting defines whether the control state is one of the accepting states
     */
    public VisualControlState(String label, boolean isInitial, boolean isAccepting) {
        this.label = label;
        isFocused = false;
        generateControlStateView(label, isInitial, isAccepting);
    }

    private void generateControlStateView(String label, boolean isInitial, boolean isAccepting) {
        view = new HBox();
        controlStateView = new StackPane();
        view.getChildren().add(controlStateView);

        Circle state = new Circle();
        state.setStroke(Color.web("#388E3C"));
        state.setStrokeWidth(1);
        state.setRadius(RADIUS);
        state.setFill(Color.web("#546E7A"));
        state.setCache(true);
        controlStateView.getChildren().add(state);

        if (isAccepting) {
            generateNestedAcceptingCircle(controlStateView);
        }

        if (isInitial) {
            generateInitialStateArrow();
        }

        Text stateLabel = new Text(label);
        stateLabel.setFill(Color.WHITE);
        stateLabel.setFont(Font.font(null, FontWeight.BOLD, 16));
        stateLabel.setBoundsType(TextBoundsType.VISUAL);
        controlStateView.getChildren().add(stateLabel);

        this.isInitial = isInitial;
    }

    private void generateNestedAcceptingCircle(StackPane controlStateView) {
        Circle nestedCircle = new Circle();
        nestedCircle.setStroke(Color.WHITE);
        nestedCircle.setFill(null);
        nestedCircle.setStrokeWidth(2);
        nestedCircle.setRadius(22.0);
        nestedCircle.setCache(true);
        controlStateView.getChildren().addAll(nestedCircle);
    }

    private void generateInitialStateArrow() {
        // the transform for the rotation arrow rotation
        Rotate rotation = new Rotate(-45);

        Point2D tan = new Point2D(-20, 26.5).normalize().multiply(10);
        Point2D p = rotation.transform(tan);
        LineTo a1 = new LineTo(p.getX(), p.getY());
        a1.setAbsolute(false);

        rotation.setAngle(-75);
        p = rotation.transform(tan);
        LineTo a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);

        tan = new Point2D(
                -10,
                -10
        ).normalize().multiply(10);
        MoveTo move = new MoveTo(10, 10);
        p = rotation.transform(tan);
        a1 = new LineTo(p.getX(), p.getY());
        a1.setAbsolute(false);
        rotation.setAngle(0);
        p = rotation.transform(tan);
        a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);

        Path arrowHead = new Path();
        arrowHead.getElements().addAll(move, a1, move, a2);
        arrowHead.setStroke(Color.BLACK);
        arrowHead.setStrokeWidth(2);

        StackPane initialArrowContainer = new StackPane();
        initialArrowContainer.setAlignment(Pos.CENTER_RIGHT);
        initialArrowContainer.getChildren().add(arrowHead);
        view.getChildren().add(0, initialArrowContainer);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
        view.setLayoutX(xPos);
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
        view.setLayoutY(yPos);
    }

    /**
     * A method which highlights the visual control state instance
     *
     * @param toFocus specifies whether to focus or un-focus the control state
     * @param color   the color to focus the control state
     */
    public void setFocus(boolean toFocus, String color) {
        isFocused = toFocus;
        controlStateView.setStyle("-fx-background-color:" + (isFocused ? color : "transparent"));
    }


    public HBox getView() {
        return view;
    }

    public double getWRadius() {
        return isInitial ? 11 + 26.5 : 26.5;
    }

    public double getHRadius() {
        return 26.5;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public int getOrderShown() {
        return orderShown;
    }

    public void setOrderShown(int orderShown) {
        this.orderShown = orderShown;
    }

    public void bringToFront() {
        view.toFront();
    }
}
