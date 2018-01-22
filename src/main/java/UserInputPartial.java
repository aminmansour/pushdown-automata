import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import model.Transition;

import javax.swing.text.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class UserInputPartial implements Initializable{

    @FXML
    private BorderPane userInputPartial;
    @FXML
    private TableView tvTransitions;
    class Transition extends RecursiveTreeObject<Transition> {
        String currentState;
        String elementAtHead;
        String topStack;
        String resultingControlState;
        String resultingTopElement;

        public Transition(String currentState,String elementAtHead,String topStack,String resultingControlState,String resultingTopElement) {
            this.currentState = currentState ;
            this.elementAtHead = elementAtHead;
            this.topStack = topStack;
            this.resultingControlState = resultingControlState;
            this.resultingTopElement = resultingTopElement;
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }
}
