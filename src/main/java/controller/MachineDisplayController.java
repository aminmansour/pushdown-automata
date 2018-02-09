package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
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


        addVisualControlState("Q1");
        addVisualControlState("Q2");
        addVisualControlState("Q3");
        addVisualControlState("Q4");
        addVisualControlState("Q5");
        addVisualControlState("Q6");
        addVisualControlState("Q7");
        addVisualControlState("Q8");
        addVisualControlState("Q9");
        addVisualControlState("Q10");
        addVisualControlState("Q11");
        addVisualControlState("Q12");
        addVisualControlState("Q13");
        addVisualControlState("Q14");
        addVisualControlState("Q15");


    }

    private void updateDisplayToNewRatio() {
        pCanvas.getChildren().clear();
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();
        orderStatesInScreen();
    }

    public void addVisualControlState(String stateLabel) {
        controlStates.put(stateLabel, new VisualControlState(stateLabel, true, true));
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
        int statePerRow = (int) Math.floor(controlStates.size() / 3);
        int columnIndex = 0;
        double toIncreaseYBy = (height) / statePerRow;
        double toIncreaseXBy = (width) / 3;
        double startX = (toIncreaseXBy - 52) / 2;
        double startY = (toIncreaseYBy - 52) / 2;
        double yIndex = startY;
        double xIndex = startX;
        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            state.setXPos(xIndex);
            state.setYPos(yIndex);
            xIndex += toIncreaseXBy;
            if (columnIndex == 2) {
                columnIndex = 0;
                xIndex = startX;
                yIndex += toIncreaseYBy;
            } else {
                columnIndex++;
            }
        }

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            for (VisualTransition transition : getTransitionEntryFromSource(state.getLabel())) {
                pCanvas.getChildren().add(transition.getView());
            }
            pCanvas.getChildren().add(state.getView());
        }
    }

    public void addVisualTransition(VisualTransition transition) {
        ArrayList<VisualTransition> transitions = getTransitionEntryFromSource(transition.getSourceState().getLabel());
        if (!transitions.contains(transition)) {
            transitions.add(transition);
        }
    }

    public void addVisualControlState(VisualControlState state) {
        controlStates.put(state.getLabel(), state);
    }


    private ArrayList<VisualTransition> getTransitionEntryFromSource(String sourceLabel) {
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
