package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import model.ControlState;
import view.VisualControlState;
import view.VisualTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MachineDisplayController {

    private final TreeMap<String, VisualControlState> controlStates;
    private final TreeMap<String, ArrayList<VisualTransition>> transitions;
    private BorderPane pdaDisplay;

    private final Pane pCanvas;
    private double width;
    private double height;


    public MachineDisplayController() {
        try {
            pdaDisplay = FXMLLoader.load(getClass().getResource("../layouts/pda_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        HBox.setHgrow(pdaDisplay, Priority.ALWAYS);
        VBox.setVgrow(pdaDisplay, Priority.ALWAYS);
        pCanvas = (Pane) pdaDisplay.lookup("#pCanvas");

        controlStates = new TreeMap<String, VisualControlState>();
        transitions = new TreeMap<String, ArrayList<VisualTransition>>();


        pdaDisplay.widthProperty().addListener(observable -> updateDisplayToNewRatio());
        pdaDisplay.heightProperty().addListener(observable -> updateDisplayToNewRatio());


    }

    private void updateDisplayToNewRatio() {
        pCanvas.getChildren().clear();
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();
        orderStatesInScreen();
    }

    public void addVisualControlState(ControlState state) {
        VisualControlState stateVisual = new VisualControlState(state.getLabel(), state.isInitial(), state.isAccepting());
        controlStates.put(state.getLabel(), stateVisual);
    }


    public void resetStates() {
        pCanvas.getChildren().clear();
    }

    public void drawStates() {
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            pCanvas.getChildren().add(entry.getValue().getView());
        }
    }


    public void orderStatesInScreen() {
        pCanvas.getChildren().clear();
        int statePerRow = (int) Math.ceil((double) controlStates.size() / 2);
        int columnIndex = 0;
        double toIncreaseYBy = (height) / statePerRow;
        double toIncreaseXBy = (width) / 2;
        double startX = (toIncreaseXBy - 52) / 2;
        double startY = (toIncreaseYBy - 52) / 2;
        double yIndex = startY;
        double xIndex = startX;
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            state.setXPos(xIndex);
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

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            pCanvas.getChildren().add(entry.getValue().getView());
        }


        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            ArrayList<VisualTransition> transitionsBySource = getTransitionsBySource(state.getLabel());
            for (int i = 0; i < transitionsBySource.size(); i++) {
                transitionsBySource.get(i).align();
            }
        }

        pCanvas.getChildren().clear();

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            ArrayList<VisualTransition> transitionsBySource = getTransitionsBySource(state.getLabel());
            for (int i = 0; i < transitionsBySource.size(); i++) {
                pCanvas.getChildren().add(transitionsBySource.get(i).getView());
            }
        }

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            pCanvas.getChildren().add(entry.getValue().getView());
        }


    }

    public void addVisualTransition(String transitionLabel, ControlState source, ControlState destination) {
        VisualTransition transition = new VisualTransition(transitionLabel, controlStates.get(source.getLabel()), controlStates.get(destination.getLabel()));
        ArrayList<VisualTransition> transitions = getTransitionsBySource(transition.getSourceState().getLabel());
        transitions.add(transition);
    }

    private ArrayList<VisualTransition> getTransitionsBySource(String sourceLabel) {
        for (Map.Entry<String, ArrayList<VisualTransition>> entry : transitions.entrySet()) {
            if (entry.getKey().equals(sourceLabel)) {
                return entry.getValue();
            }
        }
        ArrayList<VisualTransition> value = new ArrayList<>();
        transitions.put(sourceLabel, value);
        return value;
    }


    public BorderPane getCanvas() {
        return pdaDisplay;
    }
}
