package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import model.ControlState;
import model.Transition;
import model.TransitionEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class TransitionTableController {

    private HBox transitionTableView;
    private TableView tvTransitionTable;
    private ObservableList<TableColumn> columns;
    private final ObservableList<TransitionEntry> data =
            FXCollections.observableArrayList(
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e"),
                    new TransitionEntry("A", "b", "c", "d", "e")
            );
    private ArrayList<ControlState> states;


    public TransitionTableController(ArrayList<ControlState> states) {
        try {
            transitionTableView = FXMLLoader.load(getClass().getResource("../layouts/transition_table_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.states = states;
        tvTransitionTable = (TableView) transitionTableView.lookup("#tvTransitionTable");
        tvTransitionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columns = tvTransitionTable.getColumns();
        setUpColumns();
        tvTransitionTable.setItems(data);
    }

    private void setUpColumns() {
        ((TableColumn) columns.get(0).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("currentState"));
        ((TableColumn) columns.get(0).getColumns().get(0)).setCellFactory(getStateColumnModificationCallback(1));
        ((TableColumn) columns.get(0).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("elementAtHead"));
        ((TableColumn) columns.get(0).getColumns().get(1)).setCellFactory(getCharacterModificationCallback(1));
        ((TableColumn) columns.get(0).getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("topOfStack"));
        ((TableColumn) columns.get(0).getColumns().get(2)).setCellFactory(getCharacterModificationCallback(2));
        ((TableColumn) columns.get(1).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("resultingState"));
        ((TableColumn) columns.get(1).getColumns().get(0)).setCellFactory(getStateColumnModificationCallback(2));
        ((TableColumn) columns.get(1).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionEntry, String>("resultingTopOfStack"));
        ((TableColumn) columns.get(1).getColumns().get(1)).setCellFactory(getCharacterModificationCallback(3));
    }

    private Callback getStateColumnModificationCallback(int columnNumber) {
        StringConverter<String> converter = new DefaultStringConverter();
        return t -> new TextFieldTableCell<TransitionEntry, String>(converter) {
            @Override
            public void commitEdit(String newValue) {

                ControlState newState;
                if (newValue != null && (newState = retrieveStateByLabel(newValue)) != null) {
                    Transition transition = data.get(getIndex()).getTransition();
                    switch (columnNumber) {
                        case 1:
                            transition.getConfiguration().setState(newState);
                            break;
                        case 2:
                            transition.getAction().setNewState(newState);
                            break;
                    }
                    updateVisuals(data.get(getIndex()), transition);
                    super.commitEdit(newValue);
                } else {
                    cancelEdit();
                }
            }


        };
    }

    private void updateVisuals(TransitionEntry transitionEntry, Transition transition) {
        transitionEntry.update();
        ControllerFactory.pdaRunnerController.updateVisualLabel(transition);
        ControllerFactory.pdaRunnerController.closeDeterministicModeIfPresent();
        ControllerFactory.toolBarPartialController.highlightSaveButton();
        ControllerFactory.pdaRunnerController.startAgain();
    }
    private ControlState retrieveStateByLabel(String label) {
        for (ControlState controlState : states) {
            if (controlState.getLabel().equals(label)) {
                return controlState;
            }
        }
        return null;
    }

    private Callback getCharacterModificationCallback(int columnNumber) {
        StringConverter<String> converter = new DefaultStringConverter();
        return t -> new TextFieldTableCell<TransitionEntry, String>(converter) {
            @Override
            public void commitEdit(String newValue) {
                if (newValue != null && newValue.length() < 2) {
                    Transition transition = data.get(getIndex()).getTransition();
                    switch (columnNumber) {
                        case 1:
                            transition.getConfiguration().setInputSymbol(newValue.charAt(0));
                            break;
                        case 2:
                            transition.getConfiguration().setTopElement(newValue.charAt(0));
                            break;
                        case 3:
                            transition.getAction().setElementToPush(newValue.charAt(0));
                            break;
                    }
                    updateVisuals(data.get(getIndex()), transition);
                    super.commitEdit(newValue);
                } else {
                    cancelEdit();
                }
            }
        };
    }

    public void setStates(ArrayList<ControlState> states) {
        this.states = states;
        setUpColumns();
    }


    public void addColumn(Transition transition) {
        TransitionEntry t = new TransitionEntry(transition.getConfiguration().getState().getLabel() + "",
                transition.getConfiguration().getInputSymbol() + "",
                transition.getConfiguration().getTopElement() + "",
                transition.getAction().getNewState().getLabel() + "",
                transition.getAction().getElementToPush() + "");
        t.setTransition(transition);
        data.add(t);
    }

    public HBox getTransitionTableGenerated() {
        return transitionTableView;
    }

    public void highlightRow(int row, boolean single) {
        if (single) {
            tvTransitionTable.getSelectionModel().clearSelection();
        }
        tvTransitionTable.getSelectionModel().select(row);
    }

    public void clear() {
        data.clear();
    }

    public void clearSelection() {
        tvTransitionTable.getSelectionModel().clearSelection();
    }

    public void select(Transition transition, boolean isSingle) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).sameAs(transition)) {
                highlightRow(i, isSingle);
            }
        }
    }

    public void highlightTransitions(Set<Transition> transitions) {
        for (Transition transition : transitions) {
            select(transition, false);
        }
    }
}
