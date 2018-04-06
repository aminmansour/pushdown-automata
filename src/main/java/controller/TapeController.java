package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.InputTape;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller which is in charge of the tape view
 */
public class TapeController {


    //components
    private final Label lCurrentState;
    private final GridPane gpTape;
    private final Label lStackTop;
    private final Label lHeadSymbol;
    private final Label lCurrentConfiguration;
    private final Label lStep;
    private BorderPane tapeView;


    //model
    private InputTape tape;

    /**
     * A constructor for a TapeController instance
     */
    public TapeController() {

        try {
            tapeView = FXMLLoader.load(getClass().getResource("/layouts/tape_display_partial.fxml"));
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

    /**
     * A method which updates the step in the visual view
     *
     * @param step the updated step
     */
    public void setStep(int step){
        lStep.setText("Step "+step);
    }


    /**
     * A method which updates the current control state description in the visual view
     * @param element the current state
     */
    public void setCurrentStateLabel(String element) {
        lCurrentState.setText("Current state : "+element);
    }

    /**
     * A method which updates the current top stack symbol description in the visual view
     * @param element the current top stack symbol
     */
    public void setStackTopLabel(String element) {
        lStackTop.setText("Top stack symbol : "+element);
    }

    /**
     * A method which updates the current configuration description in the visual view
     * @param config the current configuration
     */
    public void setCurrentConfigurationLabel(String config) {
        lCurrentConfiguration.setText("Current configuration : "+config);
    }

    //sets the head symbol to current head symbol of tape
    private void setHeadSymbolLabel() {
        lHeadSymbol.setText("Symbol at head : " + (tape.getSymbolAtHeadString()));
    }

    /**
     * A method which clears the visual descriptions and resets them to initial value
     */
    public void clear(){
        lStep.setText("Step 0");
        setTapeCells(new ArrayList<>());
        lHeadSymbol.setText("Symbol at head : -");
        lCurrentConfiguration.setText("Current configuration : -");
        lStackTop.setText("Top stack symbol : -");
    }


    /**
     * A method which links the tape model to this controller
     * @param tapeInputModel the tape model instance
     */
    public void setTapeInputModel(InputTape tapeInputModel) {
        this.tape = tapeInputModel;
        clear();
    }

    public boolean isLastStep() {
        return tape.getStep() == tape.getOriginalWord().length();
    }

    /**
     * A method which updates the visual tape with the latest tape model changes
     */
    public void requestUpdate() {
        setTapeCells(tape.getRemainingInput());
        setStep(tape.getStep());
        setHeadSymbolLabel();
    }
}
