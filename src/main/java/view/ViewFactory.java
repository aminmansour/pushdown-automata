package view;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    public static BorderPane globalPane;
    public static BorderPane homePage;
    public static BorderPane library;
    public static StackPane codeDefinition;
    public static StackPane pdaRunner;
    public static Stage stage;
    public static StackPane quickDefinition;
    public static Pane examples;
    public static Pane info;

    public static void showStandardDialog(Pane parent, boolean oneButtonDialog, String title, String additonalMessage,
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

    public static void showErrorDialog(String error, Pane primaryView) {
        ViewFactory.showStandardDialog(primaryView, true, "Error :",
                error, event -> primaryView.getChildren().remove(primaryView.getChildren().size() - 1),
                null, "Ok", null);
    }


    public static ChangeListener<String> restrictTextFieldInput(TextField textField, String regex) {
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && oldValue.length() == 1) {
                textField.setText(oldValue);
            } else {
                if (newValue.matches(regex)) {
                    textField.setText(newValue);
                } else {
                    textField.clear();
                }
            }
        };
        textField.textProperty().addListener(changeListener);
        return changeListener;

    }

}
