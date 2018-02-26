package view;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.Pair;
import model.Transition;

public class VisualTransition {
    private final Transition transition;
    private StackPane view;
    private FlowPane flTransitionLabel;
    private boolean isFocused;
    private VisualControlState resultingState;
    private VisualControlState sourceState;

    final double ARROW_LENGTH = 10;
    final double ARROW_ANGLE = 60;
    private Line arrow;
    private Point2D tan;
    private MoveTo move;
    private double LENGTH_FROM_CORNER;
    private Text transitionLabel;


    public VisualTransition(Transition transition, VisualControlState q1, VisualControlState q2) {
        this.transition = transition;
        sourceState = q1;
        resultingState = q2;
        isFocused = false;
        generateArrowView(q1, q2);
    }

    private void generateArrowView(VisualControlState q1, VisualControlState q2) {
        Pair<Double, Double> centerVector = new Pair<>(q2.getXPos() + q2.getWidth(), q2.getYPos() + q2.getHeight());
        Pair<Double, Double> vector1 = new Pair<>(-centerVector.getKey() + q1.getXPos(), -centerVector.getValue() + q1.getYPos());
        Pair<Double, Double> vector2 = new Pair<>(1.0, 0.0);
        double dotProduct = vector1.getKey() * vector2.getKey() + vector1.getValue() * vector2.getValue();
        double bottomProduct = Math.sqrt(Math.pow(vector2.getKey(), 2) + Math.pow(vector2.getValue(), 2)) + Math.sqrt(Math.pow(vector1.getKey(), 2) + Math.pow(vector1.getValue(), 2));
        double cosineAngle = Math.acos(dotProduct / bottomProduct);
        if (q1.getYPos() < centerVector.getValue()) {
            cosineAngle = 2 * Math.PI - cosineAngle;
        }

        double xEndPoint = centerVector.getKey() + q2.getRadius() * Math.cos(cosineAngle);
        double yEndPoint = centerVector.getValue() + q2.getRadius() * Math.cos(cosineAngle);

        Arrow a = new Arrow(q1.getXPos() + q1.getWidth(), q1.getYPos() + q1.getHeight(), xEndPoint, yEndPoint);

//        arrow = new Line();
//        LENGTH_FROM_CORNER = Math.sqrt(Math.pow(26.5, 2) + Math.pow(26.5, 2)) - 30;
//        arrow.setStartX(q1.getXPos() + (q1.getWidth() / 2) );
//        arrow.setStartY(q1.getYPos() + (q1.getHeight() / 2) );
//        arrow.setEndX(q2.getXPos() - LENGTH_FROM_CORNER);
//        arrow.setEndY(q2.getYPos() + LENGTH_FROM_CORNER);
//        arrow.setStroke(Color.valueOf("607D8B"));
//        arrow.setStrokeWidth(1);
//
//        // the transform for the rotation arrow rotation
//        Rotate rotation = new Rotate(ARROW_ANGLE);
//
//        // direction = inwards from the start point
//        tan = new Point2D(arrow.getEndX(), arrow.getEndY()).normalize().multiply(ARROW_LENGTH);
//
//
//        // transform tangent by rotating with +angle
//        Point2D p = rotation.transform(tan);
//
//        LineTo a1 = new LineTo(p.getX(), p.getY());
//        // position relative to end point
//        a1.setAbsolute(false);
//
//        // same as above, but in oposite direction
//        rotation.setAngle(-ARROW_ANGLE);
//        p = rotation.transform(tan);
//        LineTo a2 = new LineTo(p.getX(), p.getY());
//        a2.setAbsolute(false);
//
//
//        // direction = inwards from the end point
//        tan = new Point2D(
//                -arrow.getEndX(),
//                -arrow.getEndY()
//        ).normalize().multiply(ARROW_LENGTH);
//        move = new MoveTo(arrow.getEndX(), arrow.getEndY());
//        p = rotation.transform(tan);
//        a1 = new LineTo(p.getX(), p.getY());
//        a1.setAbsolute(false);
//        rotation.setAngle(ARROW_ANGLE);
//        p = rotation.transform(tan);
//        a2 = new LineTo(p.getX(), p.getY());
//        a2.setAbsolute(false);
//
//        Path arrowEnd = new Path();
//        arrowEnd.getElements().addAll(move, a1, move, a2);
//        Group g = new Group();
//        g.getExploredChildren().addAll(arrow, arrowEnd);

        transitionLabel = new Text(getLabel());
        transitionLabel.setFill(Color.BLACK);
        transitionLabel.setFont(Font.font(null, FontWeight.NORMAL, 11));
        transitionLabel.setBoundsType(TextBoundsType.VISUAL);

        flTransitionLabel = new FlowPane(Orientation.HORIZONTAL);
        flTransitionLabel.getChildren().add(transitionLabel);
        flTransitionLabel.getStyleClass().add("transition-labels");

        StackPane view = new StackPane();
        view.setLayoutY(Math.min(q1.getYPos() + q1.getHeight(), yEndPoint));
        view.setLayoutX(Math.min(q1.getXPos() + q1.getWidth(), xEndPoint));

        view.getChildren().addAll(a, flTransitionLabel);
        this.view = view;

    }

    public String getLabel() {
        return transition.getConfiguration().getInputSymbol() + "," +
                transition.getConfiguration().getTopElement() + " -> " + transition.getAction().getElementToPush();
    }

    public void updateVisualTransition(VisualControlState newSource, VisualControlState newTarget) {
        transitionLabel.setText(getLabel());
        if (newSource != null) {
            sourceState = newSource;
        }

        if (newTarget != null) {
            resultingState = newTarget;
        }
    }



    public void setFocus(boolean focus) {
        isFocused = focus;
        String background = isFocused ? "#2ab27b;" : "white;";
        String textFill = isFocused ? "white;" : "black;";
        flTransitionLabel.setStyle("-fx-background-color:" + background);

    }

    public boolean isFocused() {
        return isFocused;
    }

    public StackPane getView(double range, double positionInRange) {
        generateArrowView(sourceState, resultingState);
        return view;
    }

    public StackPane getView() {
        return view;
    }

    public void align() {
        generateArrowView(sourceState, resultingState);
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

    public Transition getTransition() {
        return transition;
    }
}
