package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import model.Memory;
import view.ViewFactory;

public class ToolBarPartialController {
    @FXML
    private Button bToolbarDeterministic;
    @FXML
    private Button bToolbarSave;
    @FXML
    private Button bToolbarStartAgain;

    public void switchToHome(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.homePage);
        ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
    }


    public void switchToLibrary(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.libraryLoader);
        ControllerFactory.libraryLoaderController.retrieveDefinitionStore();
        BorderPane.setAlignment(ViewFactory.libraryLoader, Pos.CENTER);
        ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
    }

    public void save(ActionEvent actionEvent) {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            Memory.saveState();
            bToolbarSave.getStyleClass().remove("de-activated-save-button");
        } else {
            disableToolbarButtons(true);
            ControllerFactory.pdaRunnerController.openSaveDialog();
        }
    }


    public void highlightSaveButton() {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            bToolbarSave.getStyleClass().add("de-activated-save-button");
        }
    }

    public void disableToolbarButtons(boolean toDisable) {
        bToolbarStartAgain.setDisable(toDisable);
        bToolbarDeterministic.setDisable(toDisable);
        bToolbarSave.setDisable(toDisable);
    }

    public void startAgain(ActionEvent actionEvent) {
        if (!ControllerFactory.pdaRunnerController.isCurrentSavedInMemory() || bToolbarSave.getStyleClass().contains("de-activated-save-button")) {
            ControllerFactory.pdaRunnerController.showConfirmationDialog();
        } else {
            ControllerFactory.pdaRunnerController.switchToQuickDefinition();
        }
        disableToolbarButtons(true);
    }

    public void requestDeterministicTransitions(ActionEvent actionEvent) {
        if (ControllerFactory.pdaRunnerController.isInDeterministicMode()) {
            ControllerFactory.pdaRunnerController.closeDeterministicModeIfPresent();
        } else {
            ControllerFactory.pdaRunnerController.openDeterministicMode();
        }
    }
}
