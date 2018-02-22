package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import view.ViewFactory;


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
        ViewFactory.globalPane.setCenter(ViewFactory.libraryLoader);
        ControllerFactory.libraryLoaderController.retrieveDefinitionStore();
        BorderPane.setAlignment(ViewFactory.libraryLoader, Pos.CENTER);
    }

    public void restore(MouseEvent mouseEvent) {
        if (ControllerFactory.pdaRunnerController.isLoaded()) {
            ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
            BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
            BorderPane.setMargin(ViewFactory.codeDefinition, new Insets(30, 0, 0, 0));
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
