package controller;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import view.ViewFactory;

public class ToolBarPartialController {
    public void switchToHome(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.homePage);
    }


    public void switchToLibrary(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.libraryLoader);
        ControllerFactory.libraryLoaderController.retrieveDefinitionStore();
        BorderPane.setAlignment(ViewFactory.libraryLoader, Pos.CENTER);
    }
}
