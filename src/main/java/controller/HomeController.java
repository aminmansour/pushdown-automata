package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import view.ViewFactory;

import java.io.IOException;


public class HomeController {

    @FXML
    private Label lContinue;

    @FXML
    private void switchToCodeDefinition(ActionEvent actionEvent) {
       ViewFactory.globalPane.setCenter(ViewFactory.codeDefinition);
       BorderPane.setAlignment(ViewFactory.codeDefinition, Pos.CENTER);
       BorderPane.setMargin(ViewFactory.codeDefinition,new Insets(30,0,0,0));
    }



    @FXML
    private void switchToLibrary(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.library);
        ControllerFactory.libraryLoaderController.retrieveDefinitionStore();
        BorderPane.setAlignment(ViewFactory.library, Pos.CENTER);
    }

    @FXML
    private void switchToExamples(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.examples);
        ControllerFactory.examplesController.alert();
        BorderPane.setAlignment(ViewFactory.examples, Pos.CENTER);
    }


    @FXML
    private void switchToHelp(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.help);
        BorderPane.setAlignment(ViewFactory.help, Pos.CENTER);
    }

    @FXML
    private void switchToInfo(ActionEvent actionEvent) {
        if (ControllerFactory.infoController == null) {
            FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("../layouts/info_page.fxml"));
            try {
                ViewFactory.info = infoLoader.load();
                ControllerFactory.infoController = infoLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        ViewFactory.globalPane.setCenter(ViewFactory.info);
    }

    public void restore(MouseEvent mouseEvent) {
        if (ControllerFactory.pdaRunnerController.isLoaded()) {
            ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
            BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
            BorderPane.setMargin(ViewFactory.codeDefinition, new Insets(30, 0, 0, 0));
            ControllerFactory.pdaRunnerController.closeDeterministicModeIfPresent();
            ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
        }
    }

    public void showContinueButton() {
        lContinue.setVisible(true);
    }

    public void switchToQuickDefinition(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.quickDefinition);
        BorderPane.setAlignment(ViewFactory.quickDefinition, Pos.CENTER);
        BorderPane.setMargin(ViewFactory.quickDefinition, new Insets(0, 0, 0, 0));
    }
}
