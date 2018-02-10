package view;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;

public class VisualTransition {
    private final String transitionLabel;
    private String label;
    private StackPane view;
    private FlowPane flTransitionLabel;
    private boolean isFocused;
    private VisualControlState resultingState;
    private VisualControlState sourceState;

    final double ARROW_LENGTH = 10;
    final double ARROW_ANGLE = 45;
    private Line arrow;
    private Point2D tan;
    private MoveTo move;
    private double LENGTH_FROM_CORNER;


    public VisualTransition(String transitionLabel, VisualControlState q1, VisualControlState q2) {
        this.label = transitionLabel;
        sourceState = q1;
        resultingState = q2;
        isFocused = false;
        generateArrowView(q1, q2);
        this.transitionLabel = transitionLabel;
    }

    private void generateArrowView(VisualControlState q1, VisualControlState q2) {
        arrow = new Line();
        LENGTH_FROM_CORNER = Math.sqrt(Math.pow(26.5, 2) + Math.pow(26.5, 2)) - 30;

        arrow.setStartX(q1.getXPos() + (q1.getWidth() / 2) + 5);
        arrow.setStartY(q1.getYPos() + (q1.getHeight() / 2) + 5);
        arrow.setEndX(q2.getXPos() + LENGTH_FROM_CORNER);
        arrow.setEndY(q2.getYPos() + LENGTH_FROM_CORNER);
        arrow.setStroke(Color.valueOf("607D8B"));
        arrow.setStrokeWidth(1);

        // the transform for the rotation arrow rotation
        Rotate rotation = new Rotate(ARROW_ANGLE);

        // direction = inwards from the start point
        tan = new Point2D(-arrow.getStartX(), -arrow.getStartY()).normalize().multiply(ARROW_LENGTH);


        // transform tangent by rotating with +angle
        Point2D p = rotation.transform(tan);

        LineTo a1 = new LineTo(p.getX(), p.getY());
        // position relative to end point
        a1.setAbsolute(false);

        // same as above, but in oposite direction
        rotation.setAngle(-ARROW_ANGLE);
        p = rotation.transform(tan);
        LineTo a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);


        // direction = inwards from the end point
        tan = new Point2D(
                -arrow.getEndX(),
                -arrow.getEndY()
        ).normalize().multiply(ARROW_LENGTH);
        move = new MoveTo(arrow.getEndX(), arrow.getEndY());
        p = rotation.transform(tan);
        a1 = new LineTo(p.getX(), p.getY());
        a1.setAbsolute(false);
        rotation.setAngle(ARROW_ANGLE);
        p = rotation.transform(tan);
        a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);

        Path arrowEnd = new Path();
        arrowEnd.getElements().addAll(move, a1, move, a2);
        Group g = new Group();
        g.getChildren().addAll(arrow, arrowEnd);

        Text text = new Text(label);
        text.setFill(Color.BLACK);
        text.setFont(Font.font(null, FontWeight.NORMAL, 11));
        text.setBoundsType(TextBoundsType.VISUAL);

        flTransitionLabel = new FlowPane(Orientation.HORIZONTAL);
        flTransitionLabel.getChildren().add(text);
        flTransitionLabel.getStyleClass().add("transition-labels");

        StackPane view = new StackPane();
        view.setLayoutY(q1.getYPos() + (q1.getHeight() / 2) + 5);
        view.setLayoutX(q1.getXPos() + (q1.getWidth() / 2) + 5);

        view.getChildren().addAll(g, flTransitionLabel);
        this.view = view;

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFocus(boolean focus) {
        isFocused = focus;
        flTransitionLabel.setStyle("-fx-background-color:" + (isFocused ? "#2ab27b" : "transparent"));
    }

    public boolean isFocused() {
        return isFocused;
    }

    public StackPane getView() {
        generateArrowView(sourceState, resultingState);
        return view;
    }

    private void reallign() {

    }

    public FlowPane getTransitionLabel() {
        return flTransitionLabel;
    }

    public VisualControlState getSourceState() {
        return sourceState;
    }

    public VisualControlState getResultingState() {
        return resultingState;
    }


}
