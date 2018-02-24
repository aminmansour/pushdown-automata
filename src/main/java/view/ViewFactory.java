package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    public static BorderPane globalPane;
    public static BorderPane homePage;
    public static BorderPane libraryLoader;
    public static StackPane codeDefinition;
    public static StackPane pdaRunner;
    public static Stage stage;
    public static StackPane quickDefinition;

    public static void showStandardDialog(StackPane parent, boolean oneButtonDialog, String title, String additonalMessage,
                                          EventHandler<ActionEvent> primaryListener,
                                          EventHandler<ActionEvent> secondaryListener,
                                          String primaryButtonText, String secondaryButtonText) {
        try {
            VBox dialog = FXMLLoader.load(ViewFactory.class.getResource("../layouts/standard_dialog.fxml"));
            ((Label) dialog.lookup("#lTitle")).setText(title);
            ((Label) dialog.lookup("#lAdditional")).setText(additonalMessage);
            Button primary = (Button) dialog.lookup("#bPrimary");
            primary.setOnAction(primaryListener);
            primary.setText(primaryButtonText);
            Button secondary = (Button) dialog.lookup("#bSecondary");
            if (oneButtonDialog) {
                secondary.setDisable(true);
                secondary.setVisible(false);
            } else {
                secondary.setOnAction(secondaryListener);
                secondary.setText(secondaryButtonText);
            }
            parent.getChildren().add(dialog);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
