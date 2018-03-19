package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.MemoryFactory;
import view.ViewFactory;

public class ToolBarPartialController {

    @FXML
    private Button bToolbarDeterministic;
    @FXML
    private Button bToolbarSave;
    @FXML
    private Button bToolbarStartAgain;
    @FXML
    private Button bToolbarNewTransition;

    public void switchToHome() {
        ViewFactory.globalPane.setCenter(ViewFactory.home);
        ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
    }


    public void switchToLibrary() {
        ControllerFactory.homeController.switchToLibrary();
        ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
    }

    public void save() {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            MemoryFactory.saveState();
            bToolbarSave.getStyleClass().remove("de-activated-saveToLibrary-button");
        } else {
            disableToolbarButtons(true);
            ControllerFactory.pdaRunnerController.openSaveDialog();
        }
    }


    public void highlightSaveButton() {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            bToolbarSave.getStyleClass().add("de-activated-saveToLibrary-button");
        }
    }

    public void disableToolbarButtons(boolean toDisable) {
        bToolbarStartAgain.setDisable(toDisable);
        bToolbarDeterministic.setDisable(toDisable);
        bToolbarSave.setDisable(toDisable);
        bToolbarNewTransition.setDisable(toDisable);
    }

    public void startAgain(ActionEvent actionEvent) {
        if (!ControllerFactory.pdaRunnerController.isCurrentSavedInMemory() || bToolbarSave.getStyleClass().contains("de-activated-saveToLibrary-button")) {
            ControllerFactory.pdaRunnerController.showConfirmationDialog();
        } else {
            ControllerFactory.pdaRunnerController.switchToQuickDefinition();
        }
        disableToolbarButtons(true);
    }

    public void requestNonDeterministicTransitions(ActionEvent actionEvent) {
        if (ControllerFactory.pdaRunnerController.isInNonDeterministicMode()) {
            bToolbarDeterministic.setText("Open Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.closeDeterministicModeIfPresent();
        } else {
            bToolbarDeterministic.setText("Close Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.openNonDeterministicMode();
        }
    }

    public void requestNewTransition(ActionEvent actionEvent) {
        if (ControllerFactory.pdaRunnerController.isInNonDeterministicMode()) {
            bToolbarDeterministic.setText("Close Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.closeDeterministicModeIfPresent();
        }
        ControllerFactory.pdaRunnerController.openNewTransitionDialog();
    }

    public void switchToHelp() {
        ViewFactory.globalPane.setCenter(ViewFactory.help);
    }

    public void setNonDeterministicModeButtonText(String nonDeterministicModeButtonText) {
        bToolbarDeterministic.setText(nonDeterministicModeButtonText);
    }


    public void switchToExamples() {
        ControllerFactory.homeController.switchToExamples();
    }

    public void switchToQucikDefinition() {
        ControllerFactory.homeController.switchToQuickDefinition();
    }
}
