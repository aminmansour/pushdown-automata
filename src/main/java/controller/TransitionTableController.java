package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.ComputationalTree;
import model.TransitionEntry;

import java.io.IOException;

public class TransitionTableController {

    private HBox transitionTableView;
    private TableView tvTransitionTable;
    private ComputationalTree computationalTree;
    //    private TableColumn tcCurrentState;
//    private TableColumn tcElementAtHead;
//    private TableColumn tcTopOfStack;
//    private TableColumn tcResultingState;
//    private TableColumn tcResultingTopOfStack;
    private ObservableList<TableColumn> columns;



    private final ObservableList<TransitionEntry> data =
            FXCollections.observableArrayList(
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e")
            );


    public TransitionTableController() {
        try {
            transitionTableView = FXMLLoader.load(getClass().getResource("../layouts/transition_table_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvTransitionTable = (TableView) transitionTableView.lookup("#tvTransitionTable");
        columns = tvTransitionTable.getColumns();
        setUpColumns();
        tvTransitionTable.setItems(data);
        computationalTree = new ComputationalTree();

    }

    private void setUpColumns() {
        ((TableColumn) columns.get(0).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("currentState"));
        ((TableColumn) columns.get(0).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("elementAtHead"));
        ((TableColumn) columns.get(0).getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("topOfStack"));
        ((TableColumn) columns.get(1).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("resultingState"));
        ((TableColumn) columns.get(1).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("resultingTopOfStack"));
    }

    public HBox getTransitionTableGenerated() {
        return transitionTableView;
    }

    public void highlightRow(int row) {
        tvTransitionTable.getSelectionModel().select(0);
    }

}
