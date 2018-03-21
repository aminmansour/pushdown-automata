package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.MemoryFactory;
import view.ViewFactory;

/**
 * Controller which is in charge of the toolbar view
 */
public class ToolBarPartialController {

    //components
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

    /**
     * A method which opens the save dialog for the current loaded PDA
     */
    public void save() {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            MemoryFactory.saveState();
            bToolbarSave.getStyleClass().remove("de-activated-saveToLibrary-button");
        } else {
            disableToolbarButtons(true);
            ControllerFactory.pdaRunnerController.openSaveDialog();
        }
    }


    /**
     * A method which highlights the save button
     */
    public void highlightSaveButton() {
        if (ControllerFactory.pdaRunnerController.isCurrentSavedInMemory()) {
            bToolbarSave.getStyleClass().add("de-activated-saveToLibrary-button");
        }
    }

    /**
     * A method which disables user interaction with the toolbar
     *
     * @param toDisable boolean value which defines whether to disable user iteraction or not
     */
    public void disableToolbarButtons(boolean toDisable) {
        bToolbarStartAgain.setDisable(toDisable);
        bToolbarDeterministic.setDisable(toDisable);
        bToolbarSave.setDisable(toDisable);
        bToolbarNewTransition.setDisable(toDisable);
    }

    /**
     * A method which switches to the quick definition view from PDA view
     */
    public void switchToQuickDefinition() {
        if (!ControllerFactory.pdaRunnerController.isCurrentSavedInMemory() || bToolbarSave.getStyleClass().contains("de-activated-saveToLibrary-button")) {
            ControllerFactory.pdaRunnerController.showConfirmationDialog();
        } else {
            ControllerFactory.pdaRunnerController.switchToQuickDefinition();
        }
        disableToolbarButtons(true);
    }

    /**
     * A method which switches to the quick definition view for sideview
     */
    public void switchToQuickDefinitionAlt() {
        ControllerFactory.pdaRunnerController.switchToQuickDefinition();
    }

    /**
     * A method which opens the non-deterministic mode
     */
    public void requestNonDeterministicTransitionsMode() {
        if (ControllerFactory.pdaRunnerController.isInNonDeterministicMode()) {
            bToolbarDeterministic.setText("Open Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.closeNonDeterministicModeIfPresent();
        } else {
            bToolbarDeterministic.setText("Close Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.openNonDeterministicMode();
        }
    }

    /**
     * A method which opens up new transition dialog
     */
    public void requestNewTransition() {
        if (ControllerFactory.pdaRunnerController.isInNonDeterministicMode()) {
            bToolbarDeterministic.setText("Close Non-Deterministic Mode");
            ControllerFactory.pdaRunnerController.closeNonDeterministicModeIfPresent();
        }
        ControllerFactory.pdaRunnerController.openNewTransitionDialog();
    }

    public void switchToHelp() {
        ViewFactory.globalPane.setCenter(ViewFactory.help);
    }

    /**
     * A method which toggles the text of the non-deterministic mode button
     * @param nonDeterministicModeButtonText
     */
    public void setNonDeterministicModeButtonText(String nonDeterministicModeButtonText) {
        bToolbarDeterministic.setText(nonDeterministicModeButtonText);
    }


    public void switchToExamples() {
        ControllerFactory.homeController.switchToExamples();
    }

}
