package view.pages;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;


public class Home  {

    @FXML
    private void switchToCodeRunner(ActionEvent actionEvent) {
       ViewFactory.globalPane.setCenter(ViewFactory.codeRunnerPage);
       BorderPane.setAlignment(ViewFactory.codeRunnerPage, Pos.CENTER);
       BorderPane.setMargin(ViewFactory.codeRunnerPage,new Insets(30,0,0,0));
    }





}
