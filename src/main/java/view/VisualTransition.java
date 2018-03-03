package view;

import javafx.geometry.Orientation;
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
    private ArrayList<VisualTransition> transitionBySource;


    public VisualTransition(Transition transition, VisualControlState q1, VisualControlState q2) {
        this.transition = transition;
        sourceState = q1;
        resultingState = q2;
        isFocused = false;
        generateArrowView(q1, q2);
    }

    private void generateArrowView(VisualControlState q1, VisualControlState q2) {
        Pane view = new Pane();
        Arrow arrow = new Arrow(q1, q2);
        generateLabel();
        Pair<Double, Double> midCoordinate = arrow.getMidCoordinate();
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
        flTransitionLabel.setLayoutX(labelX);
        flTransitionLabel.setLayoutY(labelY);
        labelCoordinate = new Pair<>(labelX, labelY);
        view.getChildren().addAll(arrow, flTransitionLabel);

        this.view = view;
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);

    }

    //    private void checkOccurence(double labelX, double labelY) {
//        for(VisualTransition visualTransition : transitionBySource){
//            Pair<Double,Double> labelCoordinate = visualTransition.labelCoordinate;
//            if(labelCoordinate.getKey()==labelX && labelCoordinate.getValue() == labelY){
//                return true;
//            }
//        }
//
//    }
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


    public Pane getView() {
        return view;
    }

    public void align(ArrayList<VisualTransition> transitionsBySource) {
        this.transitionBySource = transitionsBySource;
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
        return labelCoordinate;
    }
}
