package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.InputTape;

import java.io.IOException;
import java.util.ArrayList;

public class TapeDisplayController {


    private final Label lCurrentState;
    private final GridPane gpTape;
    private final Label lStackTop;
    private final Label lHeadSymbol;
    private final Label lCurrentConfiguration;
    private final Label lStep;
    private BorderPane tapeView;


    private InputTape tape;

    public TapeDisplayController() {

        try {
            tapeView = FXMLLoader.load(getClass().getResource("../layouts/tape_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gpTape = (GridPane) tapeView.lookup("#gpTape");
        gpTape.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(gpTape, Priority.ALWAYS);
        lCurrentState = (Label) tapeView.lookup("#lCurrentState");
        lStackTop = (Label) tapeView.lookup("#lStackTop");
        lHeadSymbol = (Label) tapeView.lookup("#lHeadSymbol");
        lCurrentConfiguration = (Label) tapeView.lookup("#lCurrentConfiguration");
        lStep = (Label) tapeView.lookup("#lStep");
    }

    private void setTapeCells(ArrayList<Character> content) {
        for (Node node : gpTape.getChildren()) {
            String elementLabel = (content.size() > GridPane.getColumnIndex(node)) ? content.get( GridPane.getColumnIndex(node)) + "" : "";
            StackPane tapeCell = (StackPane) node;
            Label cellElement = new Label(elementLabel);
            cellElement.setFont(new Font(44));
            tapeCell.getChildren().clear();
            tapeCell.getChildren().add(cellElement);
        }
    }


    public BorderPane getTapeViewGenerated(){
        return tapeView;
    }

    public void setStep(int step){
        lStep.setText("Step "+step);
    }


    public void setCurrentStateLabel(String element) {
        lCurrentState.setText("Current state : "+element);
    }

    public void setStackTopLabel(String element) {
        lStackTop.setText("Top stack symbol : "+element);
    }

    public void setCurrentConfigurationLabel(String config) {
        lCurrentConfiguration.setText("Current configuration : "+config);
    }

    private void setHeadSymbolLabel() {
        lHeadSymbol.setText("Symbol at head : " + (tape.getStringSymbolAtHead()));
    }

    public void clear(){
        lStep.setText("Step 0");
        setTapeCells(new ArrayList<>());
        lHeadSymbol.setText("Symbol at head : -");
        lCurrentConfiguration.setText("Current configuration : -");
        lStackTop.setText("Top stack symbol : -");
    }


    public void setTapeInputModel(InputTape tapeInputModel) {
        this.tape = tapeInputModel;
        clear();
    }

    public boolean isLastStep() {
        return tape.getStep() == tape.getOriginalWord().length();
    }

    public void requestUpdate() {
        setTapeCells(tape.getRemainingInput());
        setStep(tape.getStep());
        setHeadSymbolLabel();
    }
}
