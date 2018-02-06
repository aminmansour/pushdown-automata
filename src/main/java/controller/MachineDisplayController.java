package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class MachineDisplayController {

    private final TreeMap<String, StackPane> controlStates;
    private BorderPane pdaDisplay;

    private final AnchorPane apCanvas;
    private double width;
    private double height;

    boolean isInFullScreenMode;


    public MachineDisplayController() {
        try {
            pdaDisplay = FXMLLoader.load(getClass().getResource("../layouts/pda_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        HBox.setHgrow(pdaDisplay, Priority.ALWAYS);
        apCanvas = (AnchorPane) pdaDisplay.lookup("#apCanvas");

        controlStates = new TreeMap<String, StackPane>();

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
        width = pdaDisplay.getWidth();
        height = pdaDisplay.getHeight();
        System.out.println("Helloe ");
        reorderToFitAspectRatio();
    }

    public void addVisualControlState(String stateLabel) {
        Circle c = new Circle();
        c.setStroke(Color.valueOf("388E3C"));
        c.setStrokeWidth(1);
        c.setRadius(26.0);
        c.setFill(Color.valueOf("455A64"));
        c.setCache(true);

        Text text = new Text(stateLabel);
        text.setFill(Color.WHITE);
        text.setFont(Font.font(null, FontWeight.BOLD, 16));
        text.setBoundsType(TextBoundsType.VISUAL);

        StackPane controlState = new StackPane();

        controlState.getChildren().addAll(c, text);
        controlStates.put(stateLabel, controlState);
        apCanvas.getChildren().add(controlState);
    }

    public void clear() {
        controlStates.clear();
    }


    public void reorderToFitAspectRatio() {
        int startX = 50;
        int startY = 50;

        int statePerRow = (int) Math.floor(controlStates.size() / 3);
        int columnIndex = 0;
        double yIndex = startY;
        double toIncreaseYBy = (height) / statePerRow;
        System.out.println("toY " + height);
        double xIndex = startX;
        double toIncreaseXBy = (width) / 3;
        System.out.println("xbt " + width);
        for (Map.Entry<String, StackPane> entry : controlStates.entrySet()) {
            StackPane value = entry.getValue();
            value.setLayoutX(xIndex);
            value.setLayoutY(yIndex);
            xIndex += toIncreaseXBy;
            if (columnIndex == 2) {
                columnIndex = 0;
                xIndex = startX;
                yIndex += toIncreaseYBy;
            } else {
                columnIndex++;
            }

        }
    }


    public BorderPane getCanvas() {
        return pdaDisplay;
    }
}
