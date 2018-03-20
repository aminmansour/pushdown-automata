package controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import model.PushDownStack;

import java.io.IOException;

/**
 * Controller which is in charge of the stack view
 */
public class StackController {


    //component
    private Label lTopPointer;
    private final GridPane gpStack;
    private final GridPane gpTopLocation;

    private PushDownStack pushDownStack;
    private StackPane spFirstStackCell;
    private ScrollPane stackView;

    /**
     * A constructor for a StackController instance
     */
    StackController() {
        try {
            stackView = FXMLLoader.load(getClass().getResource("../layouts/stack_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lTopPointer = (Label) stackView.getContent().lookup("#lTopPointer");
        gpStack = (GridPane) stackView.getContent().lookup("#gpStack");
        gpTopLocation = (GridPane) stackView.getContent().lookup("#gpTopLocation");

    }

    /**
     * A method which empties the visual stack
     */
    void setUpStackContentAFresh() {
        gpStack.getChildren().clear();
        gpTopLocation.getChildren().clear();
        spFirstStackCell = generateStackCell(100, 30, "stack-cell");
        gpStack.addRow(0, spFirstStackCell);
        createTopPointerLabel();
    }

    //adds a top label to the top element in GridPane
    private void createTopPointerLabel() {
        lTopPointer = new Label("Top >");
        lTopPointer.setId("lTopPointer");
        StackPane.setAlignment(lTopPointer, Pos.CENTER);
        lTopPointer.setFont(new Font(15));
        StackPane stackPointerCell = generateStackCell(40, 30, "");
        stackPointerCell.getChildren().add(lTopPointer);
        gpTopLocation.addRow(0, stackPointerCell);
    }


    ScrollPane getStackGenerated() {
        return stackView;
    }

    //adds a char element to the top of stack and points top marker to it
    private void push(char element) {
        Label elementLabel = new Label(element + "");
        elementLabel.setFont(new Font(15));

        if (spFirstStackCell.getChildren().isEmpty()) {
            spFirstStackCell.getChildren().add(elementLabel);
        } else {
            StackPane stStackCell = generateStackCell(100, 30, "stack-cell");
            stStackCell.getChildren().add(elementLabel);
            insertRows(1, gpStack);
            insertRows(1, gpTopLocation);
            gpTopLocation.getChildren().remove(lTopPointer);
            gpStack.addRow(0, stStackCell);
            StackPane stStackPointer = generateStackCell(40, 30, "");
            stStackPointer.getChildren().add(lTopPointer);
            gpTopLocation.addRow(0, stStackPointer);
        }
    }

    //inserts a new row of GridPane at top
    private void insertRows(int count, GridPane grid) {
        for (Node child : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            GridPane.setRowIndex(child, rowIndex == null ? count : count + rowIndex);
        }
    }

    //generates a stack cell
    private StackPane generateStackCell(int minWidth, int minHeigh, String classDescription) {
        StackPane stStackCell = new StackPane();
        stStackCell.getStyleClass().add(classDescription);
        stStackCell.setMinWidth(minWidth);
        stStackCell.setAlignment(Pos.CENTER);
        stStackCell.setMinHeight(minHeigh);
        stStackCell.setMaxWidth(Double.MAX_VALUE);
        return stStackCell;
    }


    /**
     * Sets the model of controller
     *
     * @param stackModel the model instance
     */
    public void setStackModel(PushDownStack stackModel) {
        this.pushDownStack = stackModel;
        setUpStackContentAFresh();
    }


    /**
     * A method which updates the stack based on the stack model
     */
    public void requestInterfaceUpdate() {
        setUpStackContentAFresh();
        for (Character element : pushDownStack.getStackContent()) {
            push(element);
        }
    }
}
