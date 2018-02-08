package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import view.VisualControlState;
import view.VisualTransition;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class MachineDisplayController {

    private final TreeMap<String, VisualControlState> controlStates;
    private BorderPane pdaDisplay;

    private final Pane pCanvas;
    private double width;
    private double height;

    private boolean displayUpdateLock;

    boolean isInFullScreenMode;


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
//       if(!displayUpdateLock) {
        displayUpdateLock = true;
        resetStates();
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();

        orderStatesInScreen();
//       }else{
//           displayUpdateLock = false;
//       }
    }

    public void addVisualControlState(String stateLabel) {
        controlStates.put(stateLabel, new VisualControlState(stateLabel));
    }


    public void resetStates() {

        pCanvas.getChildren().clear();
    }

    public void addStates() {
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

        VisualTransition vt = new VisualTransition("A,A->B", controlStates.get("Q1"), controlStates.get("Q2"));
        VisualTransition svt = new VisualTransition("A,A->B", controlStates.get("Q1"), controlStates.get("Q3"));
        pCanvas.getChildren().add(vt.getView());
        pCanvas.getChildren().add(svt.getView());

        for (Map.Entry<String, VisualControlState> entry : controlStates.entrySet()) {
            VisualControlState state = entry.getValue();
            pCanvas.getChildren().add(state.getView());
        }
    }


    public BorderPane getCanvas() {
        return pdaDisplay;
    }
}
