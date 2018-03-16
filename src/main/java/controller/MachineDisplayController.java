package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.ControlState;
import model.Transition;
import view.VisualControlState;
import view.VisualTransition;
import view.ZoomablePane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MachineDisplayController {

    private final String NON_DETERMINISTIC_FOCUS_COLOR = "#2ab27b;";
    private final String NORMAL_RUN_COLOR = "#2ab27b;";
    private final Slider slider;

    private TreeMap<String, VisualControlState> controlStates;
    private TreeMap<String, ArrayList<VisualTransition>> transitionsByStateMap;
    private ZoomablePane pdaDisplay;

    private Pane pCanvas;
    private double width;
    private double height;



    public MachineDisplayController() {
        try {
            pCanvas = FXMLLoader.load(getClass().getResource("../layouts/pda_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdaDisplay = new ZoomablePane(pCanvas);
        HBox.setHgrow(pdaDisplay, Priority.ALWAYS);
        VBox.setVgrow(pdaDisplay, Priority.ALWAYS);

        pdaDisplay.setOnMouseDragged(e -> {
            pCanvas.setTranslateX(e.getX() - (pCanvas.getWidth() / 2) - 10);
            pCanvas.setTranslateY(e.getY() - (pCanvas.getHeight() / 2) - 10);
        });


        slider = new Slider(0.5, 2, 1);
        slider.setPadding(new Insets(10));
        pdaDisplay.zoomFactorProperty().bind(slider.valueProperty());

        controlStates = new TreeMap<>();
        transitionsByStateMap = new TreeMap<>();

        pdaDisplay.widthProperty().addListener(observable -> repaintDisplay());
        pdaDisplay.heightProperty().addListener(observable -> repaintDisplay());

        Rectangle clipRect = new Rectangle(pCanvas.getWidth(), pCanvas.getHeight());

        // bind properties so height and width of rect
        // changes according pane's width and height
        clipRect.heightProperty().bind(pdaDisplay.heightProperty());
        clipRect.widthProperty().bind(pdaDisplay.widthProperty());

        // set rect as clip rect
        pdaDisplay.setClip(clipRect);
    }


    private void repaintDisplay() {
        pCanvas.getChildren().clear();
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();
        orderStatesInScreen();
        if (ControllerFactory.pdaRunnerController.isInDeterministicMode()) {
            ControllerFactory.pdaRunnerController.openDeterministicMode();
        }
    }

    public void addVisualControlState(ControlState state) {
        VisualControlState stateVisual = new VisualControlState(state.getLabel(), state.isInitial(), state.isAccepting());
        controlStates.put(state.getLabel(), stateVisual);
    }


    public void resetStates() {
        pCanvas.getChildren().clear();
    }


    public void orderStatesInScreen() {
        pCanvas.getChildren().clear();
        int statePerRow = (int) Math.ceil((double) controlStates.size() / 2);
        int columnIndex = 0;
        double toIncreaseYBy = (height) / statePerRow;
        double toIncreaseXBy = (width) / 2;
        double startX = (toIncreaseXBy / 2) - (53 / 2);
        double startY = (toIncreaseYBy / 2) - (53 / 2);
        double yIndex = startY;
        double xIndex = startX;
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            state.setXPos(state.isInitial() ? xIndex - 10 : xIndex);
            state.setYPos(yIndex);
            xIndex += toIncreaseXBy;

            if (columnIndex == 1) {
                columnIndex = 0;
                xIndex = startX;
                yIndex += toIncreaseYBy;
            } else {
                columnIndex++;
            }
        }


        int counter = 0;
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            pCanvas.getChildren().add(entry.getValue().getView());
            entry.getValue().setOrderShown(counter);
            counter++;
        }


        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            ArrayList<VisualTransition> transitionsBySource = getTransitionsBySource(state.getLabel());

            for (int i = 0; i < transitionsBySource.size(); i++) {
                VisualTransition visualTransition = transitionsBySource.get(i);
                ArrayList<VisualTransition> transitionsByTarget = getTransitionsBySource(visualTransition.getResultingState().getLabel());
                ArrayList<VisualTransition> transitionsInScope = new ArrayList<>(transitionsBySource.size() + transitionsByTarget.size());
                transitionsInScope.addAll(transitionsBySource);
                transitionsInScope.addAll(transitionsByTarget);
                visualTransition.align(getAllTransitions());
            }
        }
        pCanvas.getChildren().clear();


        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            ArrayList<VisualTransition> transitionsBySource = getTransitionsBySource(state.getLabel());
            for (int i = 0; i < transitionsBySource.size(); i++) {
                VisualTransition visualTransition = transitionsBySource.get(i);
                pCanvas.getChildren().add(visualTransition.getView());
            }
        }

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            pCanvas.getChildren().add(entry.getValue().getView());
        }

    }

    private ArrayList<VisualTransition> getTransitionBetweenSameStates(VisualTransition transition, ArrayList<VisualTransition> transitionsBySource) {
        ArrayList<VisualTransition> visualTransitions = new ArrayList<>();
        for (VisualTransition visualTransition : transitionsBySource) {
            if ((transition.getSourceState() == visualTransition.getSourceState() && transition.getResultingState() == visualTransition.getResultingState())
                    || (transition.getResultingState() == visualTransition.getSourceState() && transition.getSourceState() == visualTransition.getResultingState())) {
                visualTransitions.add(visualTransition);
            }
        }
        return visualTransitions;
    }

    public void addVisualTransition(Transition transition, boolean update) {
        VisualTransition vTransition = new VisualTransition(transition, controlStates.get(transition.getConfiguration().getState().getLabel()),
                controlStates.get(transition.getAction().getNewState().getLabel()));
        ArrayList<VisualTransition> transitions = getTransitionsBySource(vTransition.getSourceState().getLabel());
        transitions.add(vTransition);
        if (update) {
            orderStatesInScreen();
        }
    }

    private ArrayList<VisualTransition> getTransitionsBySource(String sourceLabel) {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            if (entry.getKey().equals(sourceLabel)) {
                return entry.getValue();
            }
        }
        ArrayList<VisualTransition> value = new ArrayList<>();
        transitionsByStateMap.put(sourceLabel, value);
        return value;
    }


    public BorderPane getCanvas() {
        return pdaDisplay;
    }

    public Slider getSlider() {
        return slider;
    }


    public void update(Transition transition) {

        outer:
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransition : transitionBatch) {
                if (transition == vTransition.getTransition()) {
                    String currentSourceState = transition.getConfiguration().getState().getLabel();
                    String oldSourceState = vTransition.getSourceState().getLabel();
                    if (!currentSourceState.equals(oldSourceState)) {
                        vTransition.updateVisualTransition(controlStates.get(currentSourceState), null);
                        this.transitionsByStateMap.get(oldSourceState).remove(vTransition);
                        this.transitionsByStateMap.get(currentSourceState).add(vTransition);
                        break outer;
                    }
                    String currentTargetState = transition.getAction().getNewState().getLabel();
                    String oldTargetState = vTransition.getResultingState().getLabel();
                    if (!currentTargetState.equals(oldTargetState)) {
                        vTransition.updateVisualTransition(null, controlStates.get(currentTargetState));
                        break outer;
                    }

                    vTransition.updateVisualTransition(null, null);
                    break outer;
                }
            }
        }
        repaintDisplay();
    }

    public void highlightDeterministicTransitions(Set<Transition> deterministicTransitions) {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransitions : transitionBatch) {
                if (deterministicTransitions.contains(vTransitions.getTransition())) {
                    vTransitions.setFocus(true, NON_DETERMINISTIC_FOCUS_COLOR);
                    vTransitions.getSourceState().setFocus(true, NON_DETERMINISTIC_FOCUS_COLOR);
                }
            }
        }
    }

    public void unhighlightAllTransitions() {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransitions : transitionBatch) {
                vTransitions.setFocus(false, "");
                vTransitions.getSourceState().setFocus(false, "");
                bringAllStatesToFront();
            }
        }
    }

    public void clear() {
        resetStates();
        controlStates = new TreeMap<String, VisualControlState>();
        transitionsByStateMap = new TreeMap<String, ArrayList<VisualTransition>>();
    }

    public void focusTransition(Transition transition) {
        ArrayList<VisualTransition> visualTransitions = transitionsByStateMap.get(transition.getConfiguration().getState().getLabel());
        for (VisualTransition vTransition : visualTransitions) {
            if (transition == vTransition.getTransition()) {
                vTransition.setFocus(true, NORMAL_RUN_COLOR);
                vTransition.getSourceState().setFocus(false, "");
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> {

                            vTransition.setFocus(false, "");
                            bringAllStatesToFront();
                            clearTransitionFocus();
                            vTransition.getResultingState().setFocus(true, NORMAL_RUN_COLOR);
                        }));

                timeline.play();
            } else {
                vTransition.setFocus(false, "");
                bringAllStatesToFront();

            }
        }
    }

    public void clearTransitionFocus() {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransition : transitionBatch) {
                vTransition.setFocus(false, "");
                vTransition.getResultingState().setFocus(false, "");
            }
        }
    }

    public void bringAllStatesToFront() {
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            entry.getValue().bringToFront();
        }
    }


    public void focusState(ControlState currentState) {
        if (currentState != null) {
            controlStates.get(currentState.getLabel()).setFocus(true, NORMAL_RUN_COLOR);
        }
    }

    private ArrayList<VisualTransition> getAllTransitions() {
        ArrayList<VisualTransition> toReturn = new ArrayList<>();
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            toReturn.addAll(entry.getValue());
        }
        return toReturn;
    }

}
