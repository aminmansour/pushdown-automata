package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.ComputationalTree;

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

    public class TransitionEntry {

        public String getCurrentState() {
            return currentState;
        }

        public String getElementAtHead() {
            return elementAtHead;
        }

        public String getTopOfStack() {
            return topOfStack;
        }

        public String getResultingState() {
            return resultingState;
        }

        public String getResultingTopOfStack() {
            return resultingTopOfStack;
        }

        private String currentState;
        private String elementAtHead;
        private String topOfStack;
        private String resultingState;
        private String resultingTopOfStack;

        public TransitionEntry(String currentState, String elementAtHead, String topOfStack, String resultingState, String resultingTopOfStack) {
            this.currentState = currentState;
            this.elementAtHead = elementAtHead;
            this.topOfStack = topOfStack;
            this.resultingState = resultingState;
            this.resultingTopOfStack = resultingTopOfStack;
        }

    }

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
        columns.get(0).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("currentState"));
//        columns.get(1).setCellValueFactory( new PropertyValueFactory<TransitionEntry,String>("elementAtHead"));
//        columns.get(2).setCellValueFactory( new PropertyValueFactory<TransitionEntry,String>("topOfStack"));
//        columns.get(3).setCellValueFactory( new PropertyValueFactory<TransitionEntry,String>("resultingState"));
//        columns.get(4).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("resultingTopOfStack"));
    }

    public HBox getTransitionTableGenerated() {
        return transitionTableView;
    }
}
