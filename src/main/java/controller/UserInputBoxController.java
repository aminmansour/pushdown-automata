package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class UserInputBoxController {

    private HBox userInputBox;

    private final Button bInstantRun;
    private final Button bStepRun;
    private final TextArea taInput;

    public UserInputBoxController(){
        try {
          userInputBox = FXMLLoader.load(getClass().getResource("../layouts/user_input_box_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bStepRun = (Button) userInputBox.lookup("#bStepRun");
        bInstantRun = (Button) userInputBox.lookup("#bInstantRun");
        taInput = (TextArea) userInputBox.lookup("#taInput");
    }


    public HBox getInputBox(){
        return userInputBox;
    }

    public String getInput() {
        return taInput.getText().trim().replaceAll("\\s", "");
    }


    public void setButtonStepRunAction(EventHandler<MouseEvent> action) {
        bStepRun.setOnMouseClicked(action);
    }

    public void setButtonInstantRunAction(EventHandler<MouseEvent> action) {
        bInstantRun.setOnMouseClicked(action);
    }


    public void setDisable(boolean toDisable) {
        bStepRun.setDisable(toDisable);
        bInstantRun.setDisable(toDisable);
    }


    public void clearAndFocus() {
        taInput.clear();
        taInput.requestFocus();
    }


    public void clear() {
        taInput.clear();
    }
}
