package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;


public class HomeController {

    @FXML
    private void switchToCodeDefinition(ActionEvent actionEvent) {
       ViewFactory.globalPane.setCenter(ViewFactory.codeDefinition);
       BorderPane.setAlignment(ViewFactory.codeDefinition, Pos.CENTER);
       BorderPane.setMargin(ViewFactory.codeDefinition,new Insets(30,0,0,0));
    }

    @FXML
    private void switchToPDARunner(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
        BorderPane.setMargin(ViewFactory.codeDefinition,new Insets(30,0,0,0));
    }

}
