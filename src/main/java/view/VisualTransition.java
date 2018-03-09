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
    private Arrow arrowPath;


    public VisualTransition(Transition transition, VisualControlState q1, VisualControlState q2) {
        this.transition = transition;
        transitionsThatExist = new ArrayList<>();
        sourceState = q1;
        resultingState = q2;
        isFocused = false;
        generateArrowView(q1, q2);
    }

    private void generateArrowView(VisualControlState q1, VisualControlState q2) {
        Pane view = new Pane();
        arrowPath = new Arrow(q1, q2);
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
            int r = checkOccurrence(labelX, labelY);
            if (r == 1) {
                labelY += 19;
                continue;
            } else if (r == 2) {
                labelY += 19;
                continue;
            }
            break;
        }
        labelCoordinate = new Pair<>(labelX, labelY);


        flTransitionLabel.setLayoutX(labelX);
        flTransitionLabel.setLayoutY(labelY);
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

    private int checkOccurrence(double labelX, double labelY) {

        for (VisualTransition visualTransition : transitionsThatExist) {
            Pair<Double, Double> labelCoordinate = visualTransition.labelCoordinate;
            if (transition != visualTransition.getTransition()) {
                if (labelCoordinate.getKey() == labelX && labelCoordinate.getValue() == labelY) {
                    return 1;
                }

                if (labelCoordinate.getValue() <= labelY && labelY <= labelCoordinate.getValue()) {
                    if (labelCoordinate.getKey() < labelX + 50 && labelCoordinate.getKey() + 50 > labelY) {
                        if (visualTransition.sourceState.getLabel().equals(sourceState.getLabel()) || visualTransition.sourceState.equals(resultingState.getLabel())) {
                            if (labelCoordinate.getKey() <= labelX) {
                                visualTransition.transitionLabel.setText(visualTransition.transitionLabel.getText() + "   (<)");
                                transitionLabel.setText(transitionLabel.getText() + "   (>)");
                            } else {
                                visualTransition.transitionLabel.setText(visualTransition.transitionLabel.getText() + "   (>)");
                                transitionLabel.setText(transitionLabel.getText() + "    (<)");
                            }
                        }

                        return 2;
                    }
                }
            }
        }
        return 0;

    }

    public void bringLabelToFront() {
        flTransitionLabel.toFront();
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

    public Pane getView() {
        return view;
    }

    public void align(ArrayList<VisualTransition> transitionsBySource) {
        this.transitionsThatExist = transitionsBySource;
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


    public Pair<Double, Double> getLabelCoordinate() {
        return new Pair<>(flTransitionLabel.getLayoutX(), flTransitionLabel.getLayoutY());
    }
}
