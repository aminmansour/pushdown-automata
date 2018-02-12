package controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import model.PushDownStack;

import java.io.IOException;

public class StackController {


    private Label lTopPointer;
    private final GridPane gpStack;
    private final GridPane gpTopLocation;

    private PushDownStack pushDownStack;
    private StackPane spFirstStackCell;
    private HBox stackView;

    public StackController() {
        try {
            stackView = FXMLLoader.load(getClass().getResource("../layouts/stack_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lTopPointer = (Label) stackView.lookup("#lTopPointer");
        gpStack = (GridPane) stackView.lookup("#gpStack");
        gpTopLocation = (GridPane) stackView.lookup("#gpTopLocation");

    }

    public void setUpStackContentAFresh() {
        gpStack.getChildren().clear();
        gpTopLocation.getChildren().clear();
        spFirstStackCell = getStackCell(100, 30, "stack-cell");
        gpStack.addRow(0, spFirstStackCell);
        createTopPointerLabel();
    }

    private void createTopPointerLabel() {
        lTopPointer = new Label("Top >");
        lTopPointer.setId("lTopPointer");
        StackPane.setAlignment(lTopPointer, Pos.CENTER);
        lTopPointer.setFont(new Font(15));
        StackPane stackPointerCell = getStackCell(40, 30, "");
        stackPointerCell.getChildren().add(lTopPointer);
        gpTopLocation.addRow(0, stackPointerCell);
    }


    public HBox getStackGenerated() {
        return stackView;
    }


    private void push(char element) {
        Label elementLabel = new Label(element + "");
        elementLabel.setFont(new Font(15));

        if (pushDownStack.size() == 1) {
            spFirstStackCell.getChildren().add(elementLabel);
        } else {
            StackPane stStackCell = getStackCell(100, 30, "stack-cell");
            stStackCell.getChildren().add(elementLabel);
            insertRows(1, gpStack);
            insertRows(1, gpTopLocation);
            gpTopLocation.getChildren().remove(lTopPointer);
            gpStack.addRow(0, stStackCell);
            StackPane stStackPointer = getStackCell(40, 30, "");
            stStackPointer.getChildren().add(lTopPointer);
            gpTopLocation.addRow(0, stStackPointer);
        }
    }

    public void loadState() {
        setUpStackContentAFresh();
        for (Character element : pushDownStack.getStackContent()) {
            push(element);
        }
    }

    public char pop() {
        if (pushDownStack.size() == 1) {
            ((StackPane) gpStack.getChildren().get(0)).getChildren().clear();

        } else if (pushDownStack.size() > 1) {
            removeNodeFromGridPane(gpStack, 0);
            removeNodeFromGridPane(gpTopLocation, 0);
            deleteTopRow(gpStack);
            deleteTopRow(gpTopLocation);
            getNewTopCell(gpTopLocation, 0).getChildren().add(lTopPointer);
        }
        return pushDownStack.pop();
    }

    private void insertRows(int count, GridPane grid) {
        for (Node child : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            GridPane.setRowIndex(child, rowIndex == null ? count : count + rowIndex);
        }
    }

    private void deleteTopRow(GridPane grid) {
        for (Node child : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            GridPane.setRowIndex(child, rowIndex - 1);
        }
    }

    private void removeNodeFromGridPane(GridPane gridPane, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row) {
                gridPane.getChildren().remove(node);
                return;
            }
        }
    }

    private StackPane getNewTopCell(GridPane gridPane, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private StackPane getStackCell(int minWidth, int minHeigh, String classDescription) {
        StackPane stStackCell = new StackPane();
        stStackCell.getStyleClass().add(classDescription);
        stStackCell.setMinWidth(minWidth);
        stStackCell.setAlignment(Pos.CENTER);
        stStackCell.setMinHeight(minHeigh);
        stStackCell.setMaxWidth(Double.MAX_VALUE);
        return stStackCell;
    }


    public void clear() {
        setUpStackContentAFresh();
    }

    public void setStackModel(PushDownStack stackModel) {
        this.pushDownStack = stackModel;
        setUpStackContentAFresh();
    }
}
