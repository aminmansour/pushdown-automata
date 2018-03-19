package view;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.Pair;
import model.Transition;

import java.util.ArrayList;

/**
 * A visual transition represents a transition which can be seen on screen. Each is accompanied with
 * a label containing the transition itself, and the source and target position
 */
public class VisualTransition {

    private final Transition transition;
    private Pane view;
    private FlowPane flTransitionLabel;
    private boolean isFocused;
    private VisualControlState resultingState;
    private VisualControlState sourceState;
    private Text transitionLabel;
    private Pair<Double, Double> labelCoordinate;
    private ArrayList<VisualTransition> transitionsThatExist;
    private VisualArrow arrowPath;


    /**
     * A constructor for a VisualTransition instance
     *
     * @param transition the transition itself which the visual will represent
     * @param source     the visual source state
     * @param target     the visual target state
     */
    public VisualTransition(Transition transition, VisualControlState source, VisualControlState target) {
        this.transition = transition;
        transitionsThatExist = new ArrayList<>();
        sourceState = source;
        resultingState = target;
        isFocused = false;
        generateArrowView(source, target);
    }

    //generates the arrow tip visual
    private void generateArrowView(VisualControlState q1, VisualControlState q2) {
        Pane view = new Pane();
        arrowPath = new VisualArrow(q1, q2);
        generateLabel();
        Pair<Double, Double> midCoordinate = arrowPath.getMidCoordinate();
        double labelX = midCoordinate.getKey();
        double labelY = midCoordinate.getValue();
        if (q1 == q2) {
            if (q1.getOrderShown() % 2 == 0) {
                labelX += 10;
            } else {
                labelX -= 50;
            }
            labelY += 10;
        } else {
            labelX += 4;
        }
        labelY += 5;
        while (true) {
            if (checkForOccurrence(labelX, labelY)) {
                labelY += 19;
                continue;
            }
            break;
        }
        labelCoordinate = new Pair<>(labelX, labelY);


        flTransitionLabel.setLayoutX(labelX);
        flTransitionLabel.setLayoutY(labelY);
        labelCoordinate = new Pair<>(labelX, labelY);


        view.getChildren().addAll(arrowPath, flTransitionLabel);

        this.view = view;
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);

    }

    //checks if new label position isnt overlapping any ready drawn labels.
    private boolean checkForOccurrence(double labelX, double labelY) {

        for (VisualTransition visualTransition : transitionsThatExist) {
            Pair<Double, Double> labelCoordinate = visualTransition.labelCoordinate;
            if (transition != visualTransition.getTransition()) {
                if (labelCoordinate.getKey() == labelX && labelCoordinate.getValue() == labelY) {
                    return true;
                }

                if (labelCoordinate.getKey() == labelX && (labelCoordinate.getValue() < labelY + 16) && (labelCoordinate.getValue() + 16 > labelY)) {
                    return true;
                }

                if (labelCoordinate.getValue() <= labelY && labelY <= labelCoordinate.getValue()) {
                    if (labelCoordinate.getKey() < labelX + 50 && labelCoordinate.getKey() + 50 > labelX) {
                        if (visualTransition.sourceState.getLabel().equals(resultingState.getLabel()) && visualTransition.resultingState.getLabel().equals(sourceState.getLabel())) {
                            if (labelCoordinate.getKey() <= labelX) {
                                visualTransition.transitionLabel.setText(visualTransition.transitionLabel.getText() + "   (>)");
                                transitionLabel.setText(transitionLabel.getText() + "   (<)");
                            } else {
                                visualTransition.transitionLabel.setText(visualTransition.transitionLabel.getText() + "   (<)");
                                transitionLabel.setText(transitionLabel.getText() + "    (>)");
                            }
                        }

                        return true;
                    }
                }
            }
        }
        return false;

    }

    //creates label view
    private void generateLabel() {
        transitionLabel = new Text(getLabel());
        transitionLabel.setFill(Color.BLACK);
        transitionLabel.setFont(Font.font(null, FontWeight.NORMAL, 11));
        transitionLabel.setBoundsType(TextBoundsType.VISUAL);

        flTransitionLabel = new FlowPane(Orientation.HORIZONTAL);
        flTransitionLabel.getChildren().add(transitionLabel);
        flTransitionLabel.getStyleClass().add("transition-labels");


    }

    //returns label content
    public String getLabel() {
        return transition.getConfiguration().getInputSymbol() + " , " +
                transition.getConfiguration().getTopElement() + "  ->  " + transition.getAction().getElementToPush();
    }

    /**
     * A method which updates a VisualTransition's endpoints. Specifically pertains to the case
     * when the user decides to change either a source state or target state of a pre-existing VisualTransition
     * instance.
     * @param newSource the source state to start transition from
     * @param newTarget the target state to end transition to
     */
    public void updateVisualTransition(VisualControlState newSource, VisualControlState newTarget) {
        transitionLabel.setText(getLabel());
        if (newSource != null) {
            sourceState = newSource;
        }

        if (newTarget != null) {
            resultingState = newTarget;
        }
    }


    /**
     * A method which highlights the label of the VisualTransition instance. by
     * changing the container of label to the color specified.
     * @param focus a boolean value which defines whether to gain or lose focus on the VisualTransition instance
     * @param color the color to represent the focus.
     */
    public void setFocus(boolean focus, String color) {
        isFocused = focus;
        String background = isFocused ? color : "#f3f3f3;";
        arrowPath.setStrokeWidth(isFocused ? 4 : 1);
        flTransitionLabel.setStyle("-fx-background-color:" + background);
        Node label = flTransitionLabel.getChildren().get(0);
        label.setStyle("-fx-font-weight: " + (isFocused ? "500" : "100"));
        if (isFocused) {
            view.toFront();
            flTransitionLabel.toFront();
            sourceState.bringToFront();
            resultingState.bringToFront();
        }
    }

    /**
     * @return the view representation of the VisualTransition
     */
    public Pane getView() {
        return view;
    }


    /**
     * A method which redraws the VisualTransition onto screen, much like repaint.
     * @param transitionsBySource the other VisualTransitions which have been drawn up to this point
     */
    public void align(ArrayList<VisualTransition> transitionsBySource) {
        this.transitionsThatExist = transitionsBySource;
        generateArrowView(sourceState, resultingState);
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
