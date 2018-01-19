package view.partials;

import javafx.event.ActionEvent;
import view.pages.ViewFactory;

public class ToolBarPartial {
    public void switchToHome(ActionEvent actionEvent) {
        ViewFactory.globalPane.setCenter(ViewFactory.homePage);
    }


}
