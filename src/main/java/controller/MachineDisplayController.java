package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

/**
 * A Controller in charge of the Display's view
 */
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


    /**
     * A constructor to create a MachineDisplayController instance
     */
    public MachineDisplayController() {
        try {
            pCanvas = FXMLLoader.load(getClass().getResource("/layouts/pda_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdaDisplay = new ZoomablePane(pCanvas);
        HBox.setHgrow(pdaDisplay, Priority.ALWAYS);
        VBox.setVgrow(pdaDisplay, Priority.ALWAYS);

        pdaDisplay.setOnMouseDragged(e -> {
            pCanvas.setTranslateX(e.getX() - (pCanvas.getWidth() / 2) - (pCanvas.getWidth() / 4) + 40);
            pCanvas.setTranslateY(e.getY() - (pCanvas.getHeight() / 2) - 30);
        });


        slider = new Slider(0.5, 2, 1);
        slider.setPadding(new Insets(10, 5, 10, 5));
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


    //repaints display of control states and transitions on screen
    private void repaintDisplay() {
        pCanvas.getChildren().clear();
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();
        redrawStates();
        if (ControllerFactory.pdaRunnerController.isInNonDeterministicMode()) {
            ControllerFactory.pdaRunnerController.openNonDeterministicMode();
        }
    }

    /**
     * A method which adds a visual control state for the ControlState instance provided
     *
     * @param state the ControlState to represent
     */
    public void addVisualControlState(ControlState state) {
        VisualControlState stateVisual = new VisualControlState(state.getLabel(), state.isInitial(), state.isAccepting());
        controlStates.put(state.getLabel(), stateVisual);
    }


    /**
     * A method which re-computes the locations of the VisualControlState instances
     * and draws them on screen along side the VisualTransitions between them
     */
    public void redrawStates() {
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


    /**
     * A method which adds a new VisualTransition instance based off the the Transition instance provided, so it can be seen
     * on screen
     * @param transition the transition the VisualTransition will represent
     * @param update identify whether view needs to be re-drawn after the VisualTransition is added
     */
    public void addVisualTransition(Transition transition, boolean update) {
        VisualTransition vTransition = new VisualTransition(transition, controlStates.get(transition.getConfiguration().getState().getLabel()),
                controlStates.get(transition.getAction().getNewState().getLabel()));
        ArrayList<VisualTransition> transitions = getTransitionsBySource(vTransition.getSourceState().getLabel());
        transitions.add(vTransition);
        if (update) {
            redrawStates();
        }
    }

    // returns a collection of VisualTransition instances by source state
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


    public ZoomablePane getCanvas() {
        return pdaDisplay;
    }

    public Slider getSlider() {
        return slider;
    }


    /**
     * A method which updates the VisualTransition associated with the transition by
     * re-defining its components.
     * @param transition the transition that has changed
     */
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

    /**
     * A method which highlights a group of VisualTransitions, as specified by the set of transition inputted
     *
     * @param transitions set of Transitions to be highlighted
     */
    public void highlightTransitionBatch(Set<Transition> transitions) {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransitions : transitionBatch) {
                if (transitions.contains(vTransitions.getTransition())) {
                    vTransitions.setFocus(true, NON_DETERMINISTIC_FOCUS_COLOR);
                    vTransitions.getSourceState().setFocus(true, NON_DETERMINISTIC_FOCUS_COLOR);
                }
            }
        }
    }

    /**
     * A method which unhighlight all VisualTransition instances
     */
    public void removeFocusFromAllTransitions() {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            ArrayList<VisualTransition> transitionBatch = entry.getValue();
            for (VisualTransition vTransitions : transitionBatch) {
                vTransitions.setFocus(false, "");
                bringAllStatesToFront();
            }

            removeFocusFromAllStates();
        }
    }

    /**
     * A method which resets the MachineDisplay
     */
    public void clear() {
        pCanvas.getChildren().clear();
        controlStates = new TreeMap<>();
        transitionsByStateMap = new TreeMap<>();
    }


    /**
     * A method which focuses on the a particular transition on screen
     * @param transition the transition to highlight
     */
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
                            removeFocusFromAllTransitions();
                            vTransition.getResultingState().setFocus(true, NORMAL_RUN_COLOR);
                        }));

                timeline.play();
            } else {
                vTransition.setFocus(false, "");
                bringAllStatesToFront();
            }
        }
    }


    //brings all VisualControlState to the front
    private void bringAllStatesToFront() {
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            entry.getValue().bringToFront();
        }
    }

    /**
     * Removes focus from all visual states
     */
    public void removeFocusFromAllStates() {
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            entry.getValue().setFocus(false, "");
        }
    }


    /**
     * A method which focus onto a VisualControlState
     * @param currentState the state to focus on
     * @param toFocus a boolean value determining whether state should be highlighted or un-highlighted
     */
    public void focusState(ControlState currentState, boolean toFocus) {
        if (currentState != null) {
            controlStates.get(currentState.getLabel()).setFocus(toFocus, NORMAL_RUN_COLOR);
        }
    }


    //retrieves all VisualTransition instances found in display
    private ArrayList<VisualTransition> getAllTransitions() {
        ArrayList<VisualTransition> toReturn = new ArrayList<>();
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitionsByStateMap.entrySet()) {
            toReturn.addAll(entry.getValue());
        }
        return toReturn;
    }


    /**
     * A method which resets the zoom to its default value
     */
    public void resetZoom() {
        pdaDisplay.setPivot(0, 0);
        slider.setValue(1);
    }


}
