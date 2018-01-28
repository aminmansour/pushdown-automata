package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;

public class UserInputBoxController {

    private HBox userInputBox;

    private final Button bStepRun;
    private final TextArea taUserInput;

    public UserInputBoxController(){
        try {
          userInputBox = FXMLLoader.load(getClass().getResource("../layouts/user_input_box_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bStepRun = (Button) userInputBox.lookup("#bStepRun");
        taUserInput = (TextArea) userInputBox.lookup("#taUserInput");


    }


    public HBox getInputBox(){
        return userInputBox;
    }

    public String getInput() {
        return taUserInput.getText();
    }


    public void setButtonStepRunAction(EventHandler<MouseEvent> action){
        bStepRun.setOnMouseClicked(action);
    }

}
