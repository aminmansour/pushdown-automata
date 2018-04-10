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
import model.TransitionTableEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Controller which is in charge of the transition table view
 */
public class TransitionTableController {

    private HBox transitionTableView;
    private TableView tvTransitionTable;
    private ObservableList<TableColumn> columns;
    private final ObservableList<TransitionTableEntry> data = FXCollections.observableArrayList();
    private ArrayList<ControlState> states;
    private int highlightedRow;


    /**
     * A constructor for A TransitionTableController object
     *
     * @param states The states of the PDA
     */
    public TransitionTableController(ArrayList<ControlState> states) {
        try {
            transitionTableView = FXMLLoader.load(getClass().getResource("/layouts/transition_table_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.states = states;
        tvTransitionTable = (TableView) transitionTableView.lookup("#tvTransitionTable");
        tvTransitionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columns = tvTransitionTable.getColumns();
        highlightedRow = -1;
        setUpColumns();
        tvTransitionTable.setItems(data);
    }

    //set up the columns of the TableView
    private void setUpColumns() {
        ((TableColumn) columns.get(0).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionTableEntry, String>("currentState"));
        ((TableColumn) columns.get(0).getColumns().get(0)).setCellFactory(getStateColumnModificationCallback(1));
        ((TableColumn) columns.get(0).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionTableEntry, String>("elementAtHead"));
        ((TableColumn) columns.get(0).getColumns().get(1)).setCellFactory(getCharacterModificationCallback(1));
        ((TableColumn) columns.get(0).getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TransitionTableEntry, String>("topOfStack"));
        ((TableColumn) columns.get(0).getColumns().get(2)).setCellFactory(getCharacterModificationCallback(2));
        ((TableColumn) columns.get(1).getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TransitionTableEntry, String>("resultingState"));
        ((TableColumn) columns.get(1).getColumns().get(0)).setCellFactory(getStateColumnModificationCallback(2));
        ((TableColumn) columns.get(1).getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TransitionTableEntry, String>("resultingTopOfStack"));
        ((TableColumn) columns.get(1).getColumns().get(1)).setCellFactory(getCharacterModificationCallback(3));
    }


    private Callback getStateColumnModificationCallback(int columnNumber) {
        StringConverter<String> converter = new DefaultStringConverter();
        return t -> new TextFieldTableCell<TransitionTableEntry, String>(converter) {
            @Override
            public void commitEdit(String newValue) {

                ControlState newState;
                if (newValue != null && (newState = retrieveStateByLabel(newValue)) != null) {
                    Transition transition = data.get(getIndex()).getTransition();
                    switch (columnNumber) {
                        case 1:
                            ControllerFactory.pdaRunnerController.updateTransition(transition.getConfiguration().getState(), newState, transition);
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

    //updates the PDA after a modification request has been made
    private void updateVisuals(TransitionTableEntry transitionEntry, Transition transition) {
        transitionEntry.update();
        ControllerFactory.pdaRunnerController.updateVisualLabel(transition);
        ControllerFactory.pdaRunnerController.closeNonDeterministicModeIfPresent();
        ControllerFactory.toolBarPartialController.highlightSaveButton(true);
        ControllerFactory.pdaRunnerController.redo();
    }

    //retrieves the state by label
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
        return t -> new TextFieldTableCell<TransitionTableEntry, String>(converter) {
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

    /**
     * A method which set the states to represent
     * @param states the states of PDA
     */
    public void setStates(ArrayList<ControlState> states) {
        this.states = states;
        setUpColumns();
    }


    /**
     * A method which adds a row to the TransitionTable view
     * @param transition the transition to add
     */
    public void addRow(Transition transition) {
        TransitionTableEntry t = new TransitionTableEntry(transition.getConfiguration().getState().getLabel() + "",
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

    /**
     * A method to highlight a specific row representing a transition
     * @param row the row to highlight
     * @param single a single highlight or a multi cumulative highlight
     */
    public void highlightRow(int row, boolean single) {
        if (single) {
            tvTransitionTable.getSelectionModel().clearSelection();
            highlightedRow = -1;
        }
        tvTransitionTable.getSelectionModel().select(row);
    }

    /**
     * clear the data from the TransitionTableController
     */
    public void clear() {
        data.clear();
    }

    /*
    * A method which exits modification mode
     */
    public void clearSelection(boolean save) {
        highlightedRow = save ? tvTransitionTable.getSelectionModel().getFocusedIndex() : -1;
        tvTransitionTable.getSelectionModel().clearSelection();
    }

    /**
     * A method which highlights the row corresponding to the transition
     *
     * @param transition the transition to highlight
     * @param isMulti    determines whether to singly highlight one row or not
     */
    public void select(Transition transition, boolean isMulti) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).sameAs(transition)) {
                highlightRow(i, isMulti);
            }
        }
    }

    /**
     * A method which highlights several rows corresponding to a set of Transition instances
     * @param transitions set of transitions to highlight
     */
    public void highlightTransitions(Set<Transition> transitions) {
        for (Transition transition : transitions) {
            select(transition, false);
        }
    }

    public int getHighlightedRowSaved() {
        return highlightedRow;
    }

    public ObservableList<TransitionTableEntry> getEntries() {
        return data;
    }

    /**
     * A method which adds a row to the TransitionTable view
     * @param transitionEntry the transition to add
     */
    public void addRow(TransitionTableEntry transitionEntry) {
        data.add(transitionEntry);
    }
}
