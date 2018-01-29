package controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import model.PushDownStack;

import java.io.IOException;

public class StackController {


    private final Label lTopPointer;
    private final GridPane gpStack;
    private final GridPane gpTopLocation;

    private final PushDownStack pushDownStack;
    private HBox stackView;

    public StackController() {
        try {
            stackView = FXMLLoader.load(getClass().getResource("../layouts/stack_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pushDownStack = new PushDownStack();
        lTopPointer = (Label) stackView.lookup("#lTopPointer");
        gpStack = (GridPane) stackView.lookup("#gpStack");
        gpTopLocation = (GridPane) stackView.lookup("#gpTopLocation");

        push('a');
    }


    public HBox getStackGenerated() {
        return stackView;
    }


    public void push(char element) {
        pushDownStack.push(element);
        Label elementLabel = new Label(element + "");

        if (pushDownStack.isEmpty()) {
//            gpStack.set
        }
        Label elementLabel = new Label(element + "");
        StackPane el = new StackPane(elementLabel);
        el.setAlignment(Pos.CENTER);
        el.setMinHeight(30);
        el.setMaxWidth(Double.MAX_VALUE);
        elementLabel.setFont(new Font(15));
        gpStack.setAlignment(Pos.TOP_CENTER);
        gpStack.addRow(pushDownStack.size(), el);

    }


}
