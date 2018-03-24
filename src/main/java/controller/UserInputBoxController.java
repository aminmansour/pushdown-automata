package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Controller which is in charge of the user input box view
 */
public class UserInputBoxController {

    //components
    private HBox userInputBox;
    private final Button bInstantRun;
    private final Button bStepRun;
    private final TextArea taInput;

    /**
     * A constructor for a user input box controller input
     */
    public UserInputBoxController(){
        try {
            userInputBox = FXMLLoader.load(getClass().getResource("/layouts/user_input_box_partial.fxml"));
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


    /**
     * A method which prevents users from running alternative inputs
     *
     * @param toDisable a boolean value determining whether to disable or enable user interaction
     */
    public void setDisable(boolean toDisable) {
        bStepRun.setDisable(toDisable);
        bInstantRun.setDisable(toDisable);
    }


    /**
     *  A method which clears the input text area and focus on the box
     */
    public void clearAndFocus() {
        taInput.clear();
        taInput.requestFocus();
    }

    /**
     * A method which clears the input text area
     */
    public void clear() {
        taInput.clear();
    }
}
