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

/**
 * Controller which is in charge of providing access and ability to the user to create
 * their own PDA.
 */
public class QuickDefinitionController implements Initializable {

    //elements
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
    private ObservableList<TransitionTableEntry> temporaryTransitionStore;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup tgAcceptingConditions = addAcceptingConditionsToggleGroup();
        tgAcceptingConditions.selectedToggleProperty().addListener((observable, oldValue, newValue) -> acceptanceSelectionAction());
        forceOpenControlStatePane();

        bAddTransition.setOnAction(event -> addTransition());

        populateNumberDropdown(cbNumberOfStates);
        bGenerateStates.setOnAction(event -> generateStates());
        bAdvance.setOnAction(event -> validateControlStates());
        bSave.setOnAction(event -> openSaveDialog());
        bGenerate.setOnAction(event -> {
            loadDefinition(null, false);
            clearFields();
        });
    }

    //opens the control state pane
    private void forceOpenControlStatePane() {
        aDefinition.setExpandedPane(tpStates);
        tpTransitions.setCollapsible(false);
    }

    //clears all the fields found in the form
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

    /**
     * A method which opens the save dialog when the user accesses the save feature
     */
    public void openSaveDialog() {
        try {
            VBox saveDialog = FXMLLoader.load(getClass().getResource("/layouts/save_confirmation_page.fxml"));
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

    /**
     * A method which examines the definition's saving ID and determines whether its
     * a valid ID. If it is, then the PDA definition is saved to memory and loaded into a new PDA
     *
     * @param saveDialog the save dialog container
     * @param lError     the error message
     * @param tfName     the saving ID text field
     */
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


    //loads the definition into a new PDA and switches to it
    private boolean loadDefinition(String id, boolean toSave) {
        ArrayList<ControlState> states = new ArrayList<>(temporaryControlStateStore.size());
        ControlState initialState = null;
        boolean isTerminateByAccepting = rbAcceptingState.isSelected();
        for (int i = 0; i < temporaryControlStateStore.size(); i++) {
            ControlState state = new ControlState(temporaryControlStateStore.get(i));
            if (isAcceptingState(state, cbAcceptingStates.getCheckModel().getCheckedItems())) {
                state.markAsAccepting();
            }

            if (temporaryInitialState.equals(state.getLabel())) {
                state.markAsInitial();
                initialState = state;
            }
            states.add(state);
        }

        ArrayList<Transition> transitions = new ArrayList<>();
        for (TransitionTableEntry entry : temporaryTransitionStore) {

            String currentStateLabel = entry.getCurrentState();
            char userInputSym = entry.getElementAtHead().charAt(0);
            char topStackSym = entry.getTopOfStack().charAt(0);
            String resultingStateLabel = entry.getResultingState();
            char resultingTopStackSym = entry.getResultingTopOfStack().charAt(0);
            Configuration config = new Configuration(ModelFactory.stateLookup(states, currentStateLabel), userInputSym, topStackSym);
            Action action = new Action(ModelFactory.stateLookup(states, resultingStateLabel), resultingTopStackSym);
            Transition transition = new Transition(config, action);
            entry.setTransition(transition);
            transitions.add(transition);
        }
        Definition definition = new Definition(id, states, initialState, transitions, isTerminateByAccepting);
        PDAMachine model = new PDAMachine(definition);
        ControllerFactory.pdaRunnerController.loadPDA(model);

        if (toSave) {
            MemoryFactory.loadLibrary();
            if (ModelFactory.checkForDefinitionOccurrence(definition)) {
                return false;
            }
            model.markAsSavedInMemory();
            MemoryFactory.saveToLibrary(definition);
        }

        switchToPDARunner();
        return true;
    }

    //a method which determines if a state should be an accepting one
    private boolean isAcceptingState(ControlState state, ObservableList<String> acceptingStates) {
        for (String acceptingState : acceptingStates) {
            if (acceptingState.equals(state.getLabel())) {
                return true;
            }
        }
        return false;
    }

    //switch to PDARunner
    private void switchToPDARunner() {
        ViewFactory.globalPane.setCenter(ViewFactory.pdaRunner);
        ControllerFactory.pdaRunnerController.stop();
        BorderPane.setAlignment(ViewFactory.pdaRunner, Pos.CENTER);
    }


    //validation for Control states provided
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
        lockPDACreationFunctionality(false);
        tpTransitions.setCollapsible(true);
        tpStates.setExpanded(false);
        tpTransitions.setExpanded(true);
    }

    //A method which validates a tradition as defined by the user and adds it
    //the list of transitions, if its a valid one. If not then open error dialog.
    private void addTransition() {

        if (cbStates.getSelectionModel().isEmpty()) {
            ViewFactory.showErrorDialog("No initial control state in transition is chosen!", quickDefinition);
            return;
        }

        if (cbResultingStates.getSelectionModel().isEmpty()) {
            ViewFactory.showErrorDialog("No resulting control state in transition is chosen!", quickDefinition);
            return;
        }

        String initialState = cbStates.getSelectionModel().getSelectedItem();
        String inputElement = tfInputElement.getText().trim().isEmpty() ? "/" : tfInputElement.getText();
        String elementToPop = tfElementToPop.getText().trim().isEmpty() ? "/" : tfElementToPop.getText();
        String resultingState = cbResultingStates.getSelectionModel().getSelectedItem();
        String elementToPush = tfElementToPush.getText().trim().isEmpty() ? "/" : tfElementToPush.getText();
        TransitionTableEntry transitionEntry = new TransitionTableEntry(initialState, inputElement, elementToPop, resultingState, elementToPush);
        if (temporaryTransitionStore.contains(transitionEntry)) {
            ViewFactory.showErrorDialog("No duplicate transitions allowed!", quickDefinition);
        } else {
            temporaryTransitionStore.add(transitionEntry);
            clearTransitionFields();
            lockPDACreationFunctionality(false);
        }

    }

    //clears the transition fields
    private void clearTransitionFields() {
        cbStates.getSelectionModel().clearSelection();
        cbResultingStates.getSelectionModel().clearSelection();
        tfElementToPush.clear();
        tfElementToPop.clear();
        tfInputElement.clear();
    }

    //locks the save and generate buttons in the form
    private void lockPDACreationFunctionality(boolean toDisable) {
        bSave.setDisable(toDisable);
        bGenerate.setDisable(toDisable);
    }

    //updates the transition section with data provided in the control state
    //section
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
        bEdit.setOnAction(event -> modifyTransition());
        bDelete.setOnAction(event -> deleteTransitionFromStore());

    }

    //deletes the transition from the current store of transitions in the terminal
    private TransitionTableEntry deleteTransitionFromStore() {
        TransitionTableEntry toDelete = temporaryTransitionStore.remove(lvTransitions.getSelectionModel().getSelectedIndex());
        return toDelete;
    }

    //allows for re-modification of a transition that has ready been submitted
    private void modifyTransition() {
        TransitionTableEntry toDelete = deleteTransitionFromStore();
        cbStates.getSelectionModel().select(toDelete.getCurrentState());
        cbResultingStates.getSelectionModel().select(toDelete.getResultingState());
        tfInputElement.setText(toDelete.getElementAtHead().equals("/") ? "" : toDelete.getElementAtHead());
        tfElementToPop.setText(toDelete.getTopOfStack().equals("/") ? "" : toDelete.getTopOfStack());
        tfElementToPush.setText(toDelete.getResultingTopOfStack().equals("/") ? "" : toDelete.getResultingTopOfStack());
        lvTransitions.getSelectionModel().clearSelection();
        hbTransitionAction.setVisible(false);
    }


    //a method which populates the dropdown with control states that have been generated
    private void populateControlStateDropdown(ComboBox<String> dropdown) {
        ObservableList<String> data =
                FXCollections.observableArrayList(temporaryControlStateStore);
        dropdown.setItems(data);
    }


    //generate states on terminal and loads them for selection in the control state dropdowns
    private void generateStates() {
        if (!cbNumberOfStates.getSelectionModel().isEmpty()) {
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
    }

    //populate dropdown with numbers from 2-50
    private void populateNumberDropdown(ComboBox<String> cbNumberOfStates) {
        ObservableList<String> data =
                FXCollections.observableArrayList(
                );
        for (int i = 2; i < 50; i++) {
            data.add(Integer.toString(i));
        }
        cbNumberOfStates.setItems(data);
    }

    //sets up the button group defining acceptance condition
    private ToggleGroup addAcceptingConditionsToggleGroup() {
        ToggleGroup tgAcceptingConditions = new ToggleGroup();
        rbAcceptingState.setToggleGroup(tgAcceptingConditions);
        rbEmptyStack.setToggleGroup(tgAcceptingConditions);
        rbAcceptingState.setSelected(true);
        return tgAcceptingConditions;
    }

    //toggle for accepting states field's visibility
    private void acceptanceSelectionAction() {
        if (rbAcceptingState.isSelected()) {
            vbAcceptingStates.setVisible(true);
        } else {
            vbAcceptingStates.setVisible(false);
        }
    }


}
