package view;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contains misc methods which pertain to view interaction
 */
public class ViewFactory {

    //views
    public static BorderPane globalPane;
    public static BorderPane home;
    public static BorderPane library;
    public static StackPane pdaRunner;
    public static Stage stage;
    public static StackPane quickDefinition;
    public static Pane examples;
    public static Pane info;
    public static Pane help;

    /**
     * A method which opens up a standard dialog window in the parent view
     *
     * @param parent              the parent view
     * @param oneButtonDialog     defines whether its a single-button dialog or a double
     * @param title               the title of dialog
     * @param additonalMessage    the information of dialog
     * @param primaryListener     the listener to the primary button of dialog
     * @param secondaryListener   the listener to the secondary button of dialog
     * @param primaryButtonText   the primary button text
     * @param secondaryButtonText the secondary button text
     */
    public static Node showStandardDialog(Pane parent, boolean oneButtonDialog, String title, String additonalMessage,
                                          EventHandler<ActionEvent> primaryListener,
                                          EventHandler<ActionEvent> secondaryListener,
                                          String primaryButtonText, String secondaryButtonText) {
        try {
            VBox dialog = FXMLLoader.load(ViewFactory.class.getResource("/layouts/standard_dialog.fxml"));
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
            return dialog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method which opens up a standard error dialog box in parent view
     * @param error the error message
     * @param primaryView the parent view which the dialog will be displayed in
     */
    public static void showErrorDialog(String error, Pane primaryView) {
        ViewFactory.showStandardDialog(primaryView, true, "Error :",
                error, event -> primaryView.getChildren().remove(primaryView.getChildren().size() - 1),
                null, "Ok", null);
    }


    /**
     * A method which restricts a textfield to the regex constraint provided
     *
     * @param textField the textfield to retrict
     * @param regex     the format of entry which the text field will accept
     */
    public static void restrictTextFieldInput(TextField textField, String regex) {
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
    }

    public static void switchToView(Pane view){
        globalPane.setCenter(view);
    }

    //sets png graphic with certain name on label
    public static void setLabelGraphic(Label label, String name, int size) {
        ImageView graphic = loadIcon(name, size);
        label.setGraphic(graphic);
    }


    //sets png graphic with certain name on button
    public static void setButtonGraphic(Button button, String name, int size) {
        ImageView graphic = loadIcon(name, size);
        button.setGraphic(graphic);
    }

    //load png icon to ImageView
    private static ImageView loadIcon(String name, int size) {
        Image icon = new Image("icons/" + name + ".png");
        ImageView graphic = new ImageView(icon);
        graphic.setPreserveRatio(true);
        graphic.setFitWidth(size);
        return graphic;
    }

}
