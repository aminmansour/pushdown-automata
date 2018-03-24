package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A class responsible for setting up side navigation
 */
public class SideBarPartialController implements Initializable {
    @FXML
    private Button bSideNavHome;
    @FXML
    private Button bSideNavNew;
    @FXML
    private Button bSideNavLibrary;
    @FXML
    private Button bSideNavExamples;
    @FXML
    private Button bSideNavHelp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Button[] buttons = new Button[]{bSideNavHome, bSideNavNew, bSideNavLibrary, bSideNavExamples, bSideNavHelp};
        bSideNavHome.setOnAction(event -> {
            ControllerFactory.toolBarPartialController.switchToHome();
        });
        bSideNavNew.setOnAction(event -> {
            ControllerFactory.toolBarPartialController.switchToQuickDefinitionAlt();
        });
        bSideNavLibrary.setOnAction(event -> {
            ControllerFactory.toolBarPartialController.switchToLibrary();
        });
        bSideNavExamples.setOnAction(event -> {
            ControllerFactory.toolBarPartialController.switchToExamples();
        });
        bSideNavHelp.setOnAction(event -> {
            ControllerFactory.toolBarPartialController.switchToHelp();
        });
        ViewFactory.setButtonGraphic(bSideNavHome, "fa-home", 30);
        ViewFactory.setButtonGraphic(bSideNavNew, "fa-stick-note", 30);
        ViewFactory.setButtonGraphic(bSideNavLibrary, "fa-folder-open-o", 30);
        ViewFactory.setButtonGraphic(bSideNavExamples, "fa-code-fork", 30);
        ViewFactory.setButtonGraphic(bSideNavHelp, "fa-question-mark", 30);
    }


}
