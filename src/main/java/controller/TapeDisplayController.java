package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import model.InputTape;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class TapeDisplayController {


    private final GridPane gpPointerLocation;
    private final GridPane gpStepLocation;
    private final Label lCurrentState;
    private final GridPane gpTape;
    private final Label lStackTop;
    private final Label lHeadSymbol;
    private final Label lCurrentConfiguration;
    private final Label lStep;
    private BorderPane tapeView;


    private InputTape tape;

    public TapeDisplayController(){
        try {
          tapeView = FXMLLoader.load(getClass().getResource("../layouts/tape_display_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gpTape = (GridPane) tapeView.lookup("#gpTape");
        gpPointerLocation = (GridPane) tapeView.lookup("#gpPointerLocation");
        gpStepLocation = (GridPane) tapeView.lookup("#gpPointerLocation");
        lCurrentState =  (Label) tapeView.lookup("#lCurrentState");
        lStackTop =  (Label) tapeView.lookup("#lStackTop");
        lHeadSymbol =  (Label) tapeView.lookup("#lHeadSymbol");
        lCurrentConfiguration = (Label) tapeView.lookup("#lCurrentConfiguration");
        lStep = (Label) tapeView.lookup("#lStep");

        tape = new InputTape();

        ArrayList<Character> t  = new ArrayList<>();
        t.add('1');
        t.add('2');
        t.add('3');
        t.add('4');
        t.add('5');
        t.add('6');
        t.add('7');
        t.add('8');
        t.add('9');
        t.add('0');
        t.add('a');
        t.add('b');
        t.add('c');
        t.add('d');
        tape.setInput(t);
        setTapeCells(t);

    }

    private void setTapeCells(List<Character> content) {

        for (Node node : gpTape.getChildren()) {
            String elementLabel = (content.size() > GridPane.getColumnIndex(node)) ? content.get( GridPane.getColumnIndex(node)) + "" : "";
            StackPane tapeCell = (StackPane) node;
            Label cellElement = new Label(elementLabel);
            cellElement.setFont(new Font(44));
            tapeCell.getChildren().clear();
            tapeCell.getChildren().add(cellElement);
        }
    }

    private StackPane getNodeFromGridPane(GridPane gridPane, int col, int row) {
        System.out.println(gridPane.getChildren().size());
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col) {
                return (StackPane)node;
            }
        }
        return null;
    }

    public BorderPane getTapeViewGenerated(){
        return tapeView;
    }

    public void next(){
        tape.readSymbol();
        lStep.setText("Step "+tape.getStep());
        setTapeCells(tape.getRemainingInput());
        setHeadSymbolLabel(tape.getStringAtHead());
    }

    public void previous(){
        tape.previous();
        lStep.setText("Step "+tape.getStep());
        setTapeCells(tape.getRemainingInput());
        setHeadSymbolLabel(tape.getStringAtHead());
    }

    public String getElementAtHead(){
        return tape.getStringAtHead();
    }

    public int getStep(){
        return tape.getStep();
    }

    public void setStep(int step){
        tape.setStep(step);
        lStep.setText("Step "+step);
    }

    public void setTapeInput(String input) {
        ArrayList<Character> inputSymbols = new ArrayList<Character>(input.length());
        for (char c : input.toCharArray()) { inputSymbols.add(c); }
        tape.clear();
        tape.setInput(inputSymbols);
        tape.setHeadIndex(0);
        setStep(0);

        setTapeCells(tape.getRemainingInput());
        setHeadSymbolLabel(tape.getSymbolAtHead()+"");
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

    public void setHeadSymbolLabel(String element) {
        lHeadSymbol.setText("Symbol at head : " + (tape.getStringAtHead()));
    }

    public void clear(){
        lStep.setText("Step 0");
        setTapeCells(new ArrayList<>());
        lHeadSymbol.setText("Symbol at head : none");
        lCurrentConfiguration.setText("Current configuration : none");
        lStackTop.setText("Top stack symbol : none");
        lHeadSymbol.setText("Current state : none");
    }

    public void redo() {
        tape.setStep(0);
        tape.setHeadIndex(0);
        setTapeCells(tape.getRemainingInput());
        setHeadSymbolLabel(tape.getSymbolAtHead()+"");
        setStep(0);
    }
}
