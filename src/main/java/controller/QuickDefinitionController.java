package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.*;
import org.controlsfx.control.CheckComboBox;
import view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QuickDefinitionController implements Initializable {

    @FXML
    private StackPane quickDefinition;
    @FXML
    private Accordion aDefinition;
    @FXML
    private Button bGenerateStates;
    @FXML
    private ComboBox<String> cbNumberOfStates;
    @FXML
    private TextArea taControlStates;
    @FXML
    private ComboBox cbInitialState;
    @FXML
    private CheckComboBox cbAcceptingStates;
    @FXML
    private VBox vbAcceptingStates;
    @FXML
    private Button bAdvance;
    @FXML
    private RadioButton rbAcceptingState;
    @FXML
    private RadioButton rbEmptyStack;
    @FXML
    private TitledPane tpStates;
    @FXML
    private TitledPane tpTransitions;
    @FXML
    private ComboBox<String> cbStates;
    @FXML
    private TextField tfInputElement;
    @FXML
    private TextField tfElementToPop;
    @FXML
    private ComboBox<String> cbResultingStates;
    @FXML
    private TextField tfElementToPush;
    @FXML
    private Button bAddTransition;
    @FXML
    private ListView lvTransitions;
    @FXML
    private HBox hbTransitionAction;
    @FXML
    private Button bEdit;
    @FXML
    private Button bDelete;
    @FXML
    private Button bSave;
    @FXML
    private Button bGenerate;

    private ObservableList<String> temporaryControlStateStore;
    private String temporaryInitialState;
    private ObservableList<TransitionEntry> temporaryTransitionStore;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup tgAcceptingConditions = addAcceptingConditionsToggleGroup();
        tgAcceptingConditions.selectedToggleProperty().addListener((observable, oldValue, newValue) -> acceptanceSelectionAction());
        forceOpenControlStatePane();

        bAddTransition.setOnAction(event -> addTransition());

        populateNumberDropdown(cbNumberOfStates);
        bGenerateStates.setOnAction(event -> generateStateLabels());
        bAdvance.setOnAction(event -> validateControlStates());
        bSave.setOnAction(event -> openSaveDialog());
        bGenerate.setOnAction(event -> {
            loadDefinition(System.currentTimeMillis() + "", false);
            clearFields();
        });
    }

    private void forceOpenControlStatePane() {
        aDefinition.setExpandedPane(tpStates);
        tpTransitions.setCollapsible(false);
    }

    private void clearFields() {
        forceOpenControlStatePane();
        taControlStates.clear();
        cbNumberOfStates.getSelectionModel().clearSelection();
        cbAcceptingStates.getCheckModel().clearChecks();
        cbAcceptingStates.getItems().clear();
        cbInitialState.getSelectionModel().clearSelection();

        rbAcceptingState.setSelected(true);
        clearTransitionFields();
        temporaryTransitionStore.clear();
        lockPDACreationFunctionality(true);
    }

    public void openSaveDialog() {
        try {
            VBox saveDialog = FXMLLoader.load(getClass().getResource("../layouts/save_confirmation_page.fxml"));
            Button bSave = (Button) saveDialog.lookup("#bSave");
            Button bClose = (Button) saveDialog.lookup("#bClose");
            Label lError = (Label) saveDialog.lookup("#lError");
            TextField tfName = (TextField) saveDialog.lookup("#tfName");
            bClose.setOnAction(event -> quickDefinition.getChildren().remove(saveDialog));
            bSave.setOnAction(event -> validatePDANameAndSave(saveDialog, lError, tfName));
            quickDefinition.getChildren().add(saveDialog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validatePDANameAndSave(VBox saveDialog, Label lError, TextField tfName) {
        if (!tfName.getText().trim().isEmpty()) {
            boolean isSuccessful = loadDefinition(tfName.getText().trim(), true);
            saveDialog.getChildren().remove(saveDialog);
            if (!isSuccessful) {
                lError.setVisible(true);
            } else {
                clearFields();
                quickDefinition.getChildren().remove(saveDialog);
            }
        }
    }


    private boolean loadDefinition(String id, boolean toSave) {
        ArrayList<ControlState> states = new ArrayList<>(temporaryControlStateStore.size());
        ControlState initialState = null;
        boolean isTerminateByAccepting = rbAcceptingState.isSelected();
        for (int i = 0; i < temporaryControlStateStore.size(); i++) {
            ControlState state = new ControlState(temporaryControlStateStore.get(i));
            if (ModelFactory.checkIfAcceptingState(state, cbAcceptingStates.getCheckModel().getCheckedItems())) {
                state.markAsAccepting();
            }

            if (temporaryInitialState.equals(state.getLabel())) {
                state.markAsInitial();
                initialState = state;
            }

            states.add(state);
        }

        ArrayList<Transition> transitions = new ArrayList<>();
        for (TransitionEntry entry : temporaryTransitionStore) {

            String currentStateLabel = entry.getCurrentState();
            char userInputSym = entry.getElementAtHead().charAt(0);
            char topStackSym = entry.getTopOfStack().charAt(0);
            String resultingStateLabel = entry.getResultingState();
            char resultingTopStackSym = entry.getResultingTopOfStack().charAt(0);
            Configuration config = new Configuration(ModelFactory.checkForStateOccurrence(states, currentStateLabel), userInputSym, topStackSym);
            Action action = new Action(ModelFactory.checkForStateOccurrence(states, resultingStateLabel), resultingTopStackSym);
            Transition transition = new Transition(config, action);
            entry.setTransition(transition);
            transitions.add(transition);
        }
        Definition definition = new Definition(id, states, initialState, transitions, isTerminateByAccepting);
        PDAMachine model = new PDAMachine(definition);
        ControllerFactory.pdaRunnerController.setModel(model);

        if (toSave) {
            Memory.load();
            if (ModelFactory.checkForDefinitionOccurrence(definition)) {
                return false;
            }
            model.markAsSavedInMemory();
            Memory.save(definition);
        }


        switchToPDARunner();


        return true;
    }

    private void switchToPDARunner() {
        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
    }


    private void validateControlStates() {
        boolean initialSelected = cbInitialState.getSelectionModel().isEmpty();
        ObservableList acceptingStates = cbAcceptingStates.getCheckModel().getCheckedIndices();
        boolean controlStatesCreated = temporaryControlStateStore != null;

        if (!controlStatesCreated) {
            ViewFactory.showErrorDialog("No control states generated!", quickDefinition);
            return;
        }

        if (initialSelected) {
            ViewFactory.showErrorDialog("No initial state specified!", quickDefinition);
            return;
        }

        temporaryInitialState = (String) cbInitialState.getSelectionModel().getSelectedItem();
        if (!temporaryControlStateStore.contains(temporaryInitialState)) {
            ViewFactory.showErrorDialog("Initial state isn't recognized amongst current generated control states!", quickDefinition);
            return;
        }

        if (rbAcceptingState.isSelected() && acceptingStates.isEmpty()) {
            ViewFactory.showErrorDialog("No accepting states defined! (at least one must be defined)", quickDefinition);
            return;
        }


        loadTransitionSection();
        tpTransitions.setCollapsible(true);
        tpStates.setExpanded(false);
        tpTransitions.setExpanded(true);
    }

    private void addTransition() {

        if (cbStates.getSelectionModel().isEmpty()) {
            ViewFactory.showErrorDialog("No initial control state is chosen!", quickDefinition);
            return;
        }

        if (cbResultingStates.getSelectionModel().isEmpty()) {
            ViewFactory.showErrorDialog("No Resulting control state is chosen!", quickDefinition);
            return;
        }

        String initialState = cbStates.getSelectionModel().getSelectedItem();
        String inputElement = tfInputElement.getText().trim().isEmpty() ? "/" : tfInputElement.getText();
        String elementToPop = tfElementToPop.getText().trim().isEmpty() ? "/" : tfElementToPop.getText();
        String resultingState = cbResultingStates.getSelectionModel().getSelectedItem();
        String elementToPush = tfElementToPush.getText().trim().isEmpty() ? "/" : tfElementToPush.getText();
        TransitionEntry transitionEntry = new TransitionEntry(initialState, inputElement, elementToPop, resultingState, elementToPush);
        if (temporaryTransitionStore.contains(transitionEntry)) {
            ViewFactory.showErrorDialog("No duplicate transitions allowed!", quickDefinition);
        } else {
            temporaryTransitionStore.add(transitionEntry);
            clearTransitionFields();
            lockPDACreationFunctionality(false);
        }

    }

    private void clearTransitionFields() {
        cbStates.getSelectionModel().clearSelection();
        cbResultingStates.getSelectionModel().clearSelection();
        tfElementToPush.clear();
        tfElementToPop.clear();
        tfInputElement.clear();
    }

    private void lockPDACreationFunctionality(boolean toDisable) {
        bSave.setDisable(toDisable);
        bGenerate.setDisable(toDisable);
    }

    private void loadTransitionSection() {
        populateControlStateDropdown(cbStates);
        populateControlStateDropdown(cbResultingStates);
        ViewFactory.restrictTextFieldInput(tfInputElement, ".");
        ViewFactory.restrictTextFieldInput(tfElementToPop, ".");
        ViewFactory.restrictTextFieldInput(tfElementToPush, ".");
        temporaryTransitionStore =
                FXCollections.observableArrayList();
        lvTransitions.setItems(temporaryTransitionStore);
        lvTransitions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                hbTransitionAction.setVisible(true));
        bEdit.setOnAction(event -> loadTransition());
        bDelete.setOnAction(event -> deleteTransitionFromStore());

    }

    private TransitionEntry deleteTransitionFromStore() {
        TransitionEntry toDelete = temporaryTransitionStore.remove(lvTransitions.getSelectionModel().getSelectedIndex());
        lockPDACreationFunctionality(temporaryTransitionStore.size() == 0);
        return toDelete;
    }

    private void loadTransition() {
        TransitionEntry toDelete = deleteTransitionFromStore();
        cbStates.getSelectionModel().select(toDelete.getCurrentState());
        cbResultingStates.getSelectionModel().select(toDelete.getResultingState());
        tfInputElement.setText(toDelete.getElementAtHead().equals("/") ? "" : toDelete.getElementAtHead());
        tfElementToPop.setText(toDelete.getTopOfStack().equals("/") ? "" : toDelete.getTopOfStack());
        tfElementToPush.setText(toDelete.getResultingTopOfStack().equals("/") ? "" : toDelete.getResultingTopOfStack());
        lvTransitions.getSelectionModel().clearSelection();
        hbTransitionAction.setVisible(false);
    }



    private void populateControlStateDropdown(ComboBox<String> dropdown) {
        ObservableList<String> data =
                FXCollections.observableArrayList(temporaryControlStateStore);
        dropdown.setItems(data);
    }


    private void generateStateLabels() {
        temporaryControlStateStore = FXCollections.observableArrayList();
        cbAcceptingStates.setDisable(false);
        cbInitialState.setDisable(false);
        tpTransitions.setCollapsible(false);
        int limit = Integer.parseInt(cbNumberOfStates.getSelectionModel().getSelectedItem());
        String toOutput = "";
        for (int i = 0; i < limit; i++) {
            String stateLabel = "Q" + i;
            toOutput += stateLabel + ", ";
            temporaryControlStateStore.add(stateLabel);
        }
        taControlStates.setText(toOutput);
        cbInitialState.setItems(temporaryControlStateStore);
        cbInitialState.getSelectionModel().clearSelection();
        cbAcceptingStates.getCheckModel().clearChecks();
        cbAcceptingStates.getItems().clear();
        cbAcceptingStates.getItems().addAll(temporaryControlStateStore);
    }

    private void populateNumberDropdown(ComboBox<String> cbNumberOfStates) {
        ObservableList<String> data =
                FXCollections.observableArrayList(
                );
        for (int i = 2; i < 50; i++) {
            data.add(Integer.toString(i));
        }
        cbNumberOfStates.setItems(data);
    }


    private ToggleGroup addAcceptingConditionsToggleGroup() {
        ToggleGroup tgAcceptingConditions = new ToggleGroup();
        rbAcceptingState.setToggleGroup(tgAcceptingConditions);
        rbEmptyStack.setToggleGroup(tgAcceptingConditions);
        rbAcceptingState.setSelected(true);
        return tgAcceptingConditions;
    }

    private void acceptanceSelectionAction() {
        if (rbAcceptingState.isSelected()) {
            vbAcceptingStates.setVisible(true);
        } else {
            vbAcceptingStates.setVisible(false);
        }
    }


}
