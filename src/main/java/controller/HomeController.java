package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import view.ViewFactory;

import java.io.IOException;


/**
 * A Controller in charge of the Home's view
 */
public class HomeController {

    @FXML
    private Label lContinue;

    //listener methods for the individual view boxes
    @FXML
    public void switchToLibrary() {
        if (ControllerFactory.libraryController == null) {
            try {
                FXMLLoader libraryLoader = new FXMLLoader(getClass().getResource("../layouts/library_page.fxml"));
                ViewFactory.library = libraryLoader.load();
                ControllerFactory.libraryController = libraryLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ControllerFactory.libraryController.retrieveDefinitionStore();
        ViewFactory.globalPane.setCenter(ViewFactory.library);
    }

    @FXML
    public void switchToExamples() {
        if (ControllerFactory.examplesController == null) {
            try {
                FXMLLoader examplesLoader = new FXMLLoader(getClass().getResource("../layouts/examples_page.fxml"));
                ViewFactory.examples = examplesLoader.load();
                ControllerFactory.examplesController = examplesLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ViewFactory.globalPane.setCenter(ViewFactory.examples);
    }

    @FXML
    private void switchToHelp() {
        ViewFactory.globalPane.setCenter(ViewFactory.help);
    }

    @FXML
    public void switchToQuickDefinition() {
        ViewFactory.globalPane.setCenter(ViewFactory.quickDefinition);
    }

    @FXML
    private void switchToInfo() {
        if (ControllerFactory.infoController == null) {
            try {
                FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("../layouts/info_page.fxml"));
                ViewFactory.info = infoLoader.load();
                ControllerFactory.infoController = infoLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ViewFactory.globalPane.setCenter(ViewFactory.info);
    }

    /**
     * A method which continues last PDA session from home view.
     */
    public void restore() {
        if (ControllerFactory.pdaRunnerController.isLoaded()) {
            ControllerFactory.pdaRunnerController.closeNonDeterministicModeIfPresent();
            ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
            ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
        }
    }

    /**
     * A method which makes the continue button visible from the Home view
     **/
    public void showContinueButton() {
        lContinue.setVisible(true);
    }


}
