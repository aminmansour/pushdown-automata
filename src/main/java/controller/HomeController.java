package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * A Controller in charge of the Home's view
 */
public class HomeController implements Initializable {

    @FXML
    private Label lContinue;
    @FXML
    private Label lHomeNew;
    @FXML
    private Label lHomeExamples;
    @FXML
    private Label lHomeLibrary;
    @FXML
    private Label lHomeTheory;
    @FXML
    private Label lHomeHelp;

    //listener methods for the individual view boxes
    @FXML
    public void switchToLibrary() {
        if (ControllerFactory.libraryController == null) {
            try {
                FXMLLoader libraryLoader = new FXMLLoader(getClass().getResource("/layouts/library_page.fxml"));
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
                FXMLLoader examplesLoader = new FXMLLoader(getClass().getResource("/layouts/examples_page.fxml"));
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
                FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("/layouts/info_page.fxml"));
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
            ControllerFactory.pdaRunnerController.closeDialogues();
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


    //sets png graphic with certain name on label
    private void setLabelGraphic(Label label, String name) {
        Image icon = new Image("icons/" + name + ".png");
        ImageView graphic = new ImageView(icon);
        graphic.setPreserveRatio(true);
        graphic.setFitWidth(80);
        label.setGraphic(graphic);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewFactory.setLabelGraphic(lHomeNew, "fa-code", 90);
        ViewFactory.setLabelGraphic(lHomeExamples, "fa-examples-alt", 90);
        ViewFactory.setLabelGraphic(lHomeLibrary, "fa-folder-alt", 90);
        ViewFactory.setLabelGraphic(lHomeTheory, "fa-book", 90);
        ViewFactory.setLabelGraphic(lHomeHelp, "fa-help-alt", 90);
    }
}
