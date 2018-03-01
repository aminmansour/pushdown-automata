package view;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
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
    private boolean initial;


    public VisualTransition(Transition transition, VisualControlState q1, VisualControlState q2) {
        this.transition = transition;
        sourceState = q1;
        resultingState = q2;
        isFocused = false;
        generateArrowView(q1, q2, false);
    }

    private void generateArrowView(VisualControlState q1, VisualControlState q2, boolean leftColumnSource) {
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

        StackPane view = new StackPane();


        generateLabel();
        boolean isSame = q1 == q2;
        Arrow arrow = new Arrow(q1, q2);
        if (isSame) {
            double xPos = 0;
            if (leftColumnSource) {
                xPos = (q1.isInitial() ? q1.getXPos() - 100 + 16 : q1.getXPos() - 100);
                view.setAlignment(Pos.CENTER_LEFT);
            } else {
                xPos = (q1.isInitial() ? q1.getXPos() + q1.getWidth() + 26.5 : q1.getXPos() + 2 * q1.getWidth() - 10);
                arrow.setRotate(180);
                view.setAlignment(Pos.CENTER_RIGHT);
            }
            view.setLayoutX(xPos);
            view.setLayoutY(q1.getYPos() + q1.getHeight() / 2);
        } else {
            view.setLayoutY(Math.min(q1.getYPos() + q1.getHeight(), yEndPoint));
            view.setLayoutX(Math.min(q1.getXPos() + q1.getWidth(), xEndPoint));
        }

        view.getChildren().add(arrow);


        view.getChildren().add(flTransitionLabel);
        this.view = view;

    }

    private void generateLabel() {
        transitionLabel = new Text(getLabel());
        transitionLabel.setFill(Color.BLACK);
        transitionLabel.setFont(Font.font(null, FontWeight.NORMAL, 11));
        transitionLabel.setBoundsType(TextBoundsType.VISUAL);

        flTransitionLabel = new FlowPane(Orientation.HORIZONTAL);
        flTransitionLabel.getChildren().add(transitionLabel);
        flTransitionLabel.getStyleClass().add("transition-labels");
    }

    public String getLabel() {
        return transition.getConfiguration().getInputSymbol() + " , " +
                transition.getConfiguration().getTopElement() + "  ->  " + transition.getAction().getElementToPush();
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


    public void setFocus(boolean focus, String color) {
        isFocused = focus;
        String background = isFocused ? color : "white;";
        flTransitionLabel.setStyle("-fx-background-color:" + background);

    }

    public boolean isFocused() {
        return isFocused;
    }

    public StackPane getView(double range, double positionInRange) {
        generateArrowView(sourceState, resultingState, true);
        return view;
    }

    public StackPane getView() {
        return view;
    }

    public void align() {
        generateArrowView(sourceState, resultingState, false);
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

    public void redrawArrow(boolean leftColumn) {
        generateArrowView(sourceState, resultingState, leftColumn);
    }

    public boolean isInitial() {
        return initial;
    }
}
