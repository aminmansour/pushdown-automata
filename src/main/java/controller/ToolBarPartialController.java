package controller;

import javafx.event.ActionEvent;
import controller.ViewFactory;

public class ToolBarPartialController {
    public void switchToHome(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.homePage);
    }


}
