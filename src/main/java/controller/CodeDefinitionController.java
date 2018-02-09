package controller;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import view.TransitionTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CodeDefinitionController implements Initializable{

    @FXML
    private Label lControlStateHelp;
    @FXML
    private Label lInitialStateHelp;
    @FXML
    private Label lFurtherInfoHelp;
    @FXML
    private Label lAddTransitionHelp;
    @FXML
    private Label lAcceptingCritereaHelp;
    @FXML
    private Label lControlStateInstruction;
    @FXML
    private Label lInitialStateInstruction;
    @FXML
    private Label lFurtherInfoInstruction;
    @FXML
    private Label lAddTransitionInstruction;
    @FXML
    private TextArea taControlStates;
    @FXML
    private RadioButton rbAcceptingState;
    @FXML
    private RadioButton rbEmptyStack;
    @FXML
    private TextArea taTransitions;
    @FXML
    private Label lFurtherInfo;
    @FXML
    private Button bAddTransition;
    @FXML
    private HBox hbAddTransition;
    @FXML
    private Label lClearTransitions;
    @FXML
    private TextField tfInitialState;
    @FXML
    private TextField tfFurtherInfo;


    private ArrayList<char[]> transitionsInputed;
    private ChangeListener furtherInfoFieldListener;
    private ArrayList<String> currentErrorList;
    private TransitionTextField tfAddTransition;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        transitionsInputed = new ArrayList<char[]>();
        currentErrorList = new ArrayList<>();
        tfAddTransition = addSpecialTransitionTextField();
        ToggleGroup tgAcceptingConditions = addAcceptingConditionsToggleGroup();
        restrictTextFieldInput(tfInitialState,"[a-zA-Z0-9]");
        tgAcceptingConditions.selectedToggleProperty().addListener((observable, oldValue, newValue) -> acceptanceSelectionAction());
        setUpHelpRequestFeatureForEachField();
    }

    private ToggleGroup addAcceptingConditionsToggleGroup() {
        ToggleGroup tgAcceptingConditions = new ToggleGroup();
        rbAcceptingState.setToggleGroup(tgAcceptingConditions);
        rbEmptyStack.setToggleGroup(tgAcceptingConditions);
        rbAcceptingState.setSelected(true);
        return tgAcceptingConditions;
    }

    private void acceptanceSelectionAction() {
        if(rbAcceptingState.isSelected()){
            lFurtherInfo.setText("Accepting States");
            lFurtherInfoHelp.getTooltip().setText("Enter the accepting states seperated by commas i.e. q,r,s");
            tfFurtherInfo.textProperty().removeListener(furtherInfoFieldListener);
            tfFurtherInfo.clear();
        }else{
            lFurtherInfo.setText("Empty Stack Symbol");
            lFurtherInfoHelp.getTooltip().setText("Enter the initial stack symbol i.e. Z");
            furtherInfoFieldListener = restrictTextFieldInput(tfFurtherInfo, "[a-zA-Z0-9]");
            tfFurtherInfo.clear();
        }
        lFurtherInfoInstruction.setVisible(false);
    }

    private void showError(Label instruction, String output){
        instruction.setText(output);
        instruction.setVisible(true);
    }

    private boolean checkForValidControlStates(char[] transitionItems, String errorString, String[] controlStatesInput) {
        //look up current state
        if(transitionItems[0]!='/'&&!stringArrayLookUp(controlStatesInput, transitionItems[0])){
            errorString = "*The current control state is not recognized! Add "+transitionItems[0]+" to control states!";
        }
        //look up resulting state
        if(transitionItems[3]!='/'&&!stringArrayLookUp(controlStatesInput,transitionItems[3])){
            if(errorString==null) {
                errorString = "*The resulting control state is not recognized! Add " + transitionItems[3] + " to control states!";
            }else{
                errorString = "*The current and resulting control states are not recognized! Add to control states!";
            }
        }
        if(errorString!=null){
            showError(lAddTransitionInstruction,errorString);
            return false;
        }else {
            return true;
        }
    }

    private void setUpHelpRequestFeatureForEachField() {
        lFurtherInfoHelp.setOnMouseClicked(event -> showToolTip("Enter the accepting states seperated by commas i.e. q,r,s",lFurtherInfoHelp));
        lControlStateHelp.setOnMouseClicked(event -> showToolTip(rbAcceptingState.isSelected()?"Enter the accepting states seperated by commas i.e. q,r,s":"Enter the initial stack symbol i.e. Z",lControlStateHelp));
        lAcceptingCritereaHelp.setOnMouseClicked(event -> showToolTip("Determine whether the PDA to be generated accepts input when it terminates on a accepting state, or when the stack is empty",lAcceptingCritereaHelp));
        lInitialStateHelp.setOnMouseClicked(event -> showToolTip("Define the initial state which the PDA starts from i.e. q",lInitialStateHelp));
        lAddTransitionHelp.setOnMouseClicked(event -> showToolTip("Add a transition as specified by the mask. \n Defined as ( <<current state>>,<<input head symbol>>,<<top stack symbol>> ) \n -> (<<resulting state>>,<<new top stack symbol>>). \n / symbol represents the skipping symbol which can occur anywhere.\n Note: there must be a transition involving the initial state",lAddTransitionHelp));
    }

    private TransitionTextField addSpecialTransitionTextField() {
        TransitionTextField tfAddTransition = new TransitionTextField("(? , ? , ? )   >   ( ? , ? )");
        tfAddTransition.setPrefHeight(50);
        HBox.setHgrow(tfAddTransition, Priority.ALWAYS);
        tfAddTransition.maxWidth(Double.MAX_VALUE);
        tfAddTransition.setId("tfAddTransition");
        hbAddTransition.getChildren().add(0,tfAddTransition);
        return tfAddTransition;
    }

    private void showToolTip(String instruction,Label helpLabel) {
        Point2D p = helpLabel.localToScene(0.0, 0.0);
        final Tooltip customTooltip = helpLabel.getTooltip();
        customTooltip.setAutoHide(true);
        customTooltip.show(ViewFactory.stage, p.getX()
                + helpLabel.getScene().getX() + helpLabel.getScene().getWindow().getX()+20, p.getY()
                + helpLabel.getScene().getY() + helpLabel.getScene().getWindow().getY());
    }

    private boolean stringArrayLookUp(String[] collection,char symbol){
        for(String state:collection){
            if (Character.toString(symbol).equals(state)){
                return true;
            }
        }
        return false;
    }

    private boolean containsTransition(char[] transtion){
        for (char[] value : transitionsInputed) {
            boolean isSame = true;
            for (int i = 0 ; i < transtion.length; i++) {
                if (value[i] != transtion[i]){
                    isSame = false;
                    break;
                }
            }
            if(isSame){ return true; }
        }
        return false;
    }

    public void generate(ActionEvent actionEvent) {
        currentErrorList.clear();
        Object[] definitionBuffer = new Object[4];
        //definition assumed true
        boolean definitionValid;
        String[] controlStatesInput = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
        definitionValid = isControlStatesValid(definitionBuffer);
        boolean initialStateValid = isInitialStateValid(controlStatesInput);
        definitionValid = initialStateValid;
        definitionValid = isFurtherInfoFieldValid(controlStatesInput);
        definitionValid = isTransitionsValid(initialStateValid);
        if (definitionValid) {
            loadDefinition(controlStatesInput, rbAcceptingState.isSelected(), transitionsInputed);
        } else { showErrorDialog(); }
    }

    private void loadDefinition(String[] controlStatesInput, boolean isAcceptByFinalState, ArrayList<char[]> transitionsInputed) {
//        Definition definition = new Definition(System.currentTimeMillis()+"",transitionsInputeds)
    }

    private boolean isTransitionsValid(boolean initialStateValid) {
        String transitionError = null;
        if (taTransitions.getText().isEmpty()) {
            transitionError = "*No transitions defined!";
        } else {
            for (char[] transition : transitionsInputed) {
                if (!initialStateValid || transition[0] == tfInitialState.getText().charAt(0)) {
                    transitionError = "*No transition from initial state is defined!";
                    break;
                }
            }
        }
        return evaluateFieldValidity(transitionError, lAddTransitionInstruction, "Transition field : ");
    }

    private boolean isFurtherInfoFieldValid(String[] controlStatesInput) {
        String furtherInfoError = null;
        if (rbAcceptingState.isSelected()) {
            if (tfFurtherInfo.getText().isEmpty()) {
                furtherInfoError = "*No accepting states defined!";
            } else {
                String[] acceptingStates = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
                for (String state : acceptingStates) {
                    if (state.length() > 1) {
                        furtherInfoError = "*Improper format! see hint";
                        break;
                    } else {
                        if (state.isEmpty() || !stringArrayLookUp(controlStatesInput, state.charAt(0))) {
                            furtherInfoError = "*Not all states are recognizable!";
                            break;
                        }
                    }
                }
            }

        } else if (tfFurtherInfo.getText().isEmpty()) {
                furtherInfoError = "*No empty stack symbol is defined!";
        }
        return evaluateFieldValidity(furtherInfoError, lFurtherInfoInstruction,(rbEmptyStack.isSelected()?"Empty stack symbol ":"Accepting states ")+"field : ");
    }

    private boolean isInitialStateValid(String[] controlStatesInput) {
        String initialStateError = null;
        if (tfInitialState.getText().isEmpty()) {
            initialStateError = "*No initial state is defined!";
        } else if (!stringArrayLookUp(controlStatesInput, tfInitialState.getText().charAt(0))) {
            initialStateError = "*State not recognized!";
        }
        return evaluateFieldValidity(initialStateError, lInitialStateInstruction,"Initial state field : ");
    }

    private boolean evaluateFieldValidity(String errorSignature, Label fieldLabel, String fieldIdentifier) {
        //contains error
        if (errorSignature != null) {
            showError(fieldLabel, errorSignature);
            currentErrorList.add(fieldIdentifier+errorSignature.substring(1,errorSignature.length()).toLowerCase());
            return false;
        }
        fieldLabel.setVisible(false);
        return true;
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not generate PDA!");
        alert.setContentText("Fix the errors specified before generating!");
        Label label = new Label("The errors found:");
        String output= "";
        for(String error : currentErrorList){
            output+=error+" \n";
        }
        TextArea textArea = new TextArea(output);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }


    private ChangeListener<String> restrictTextFieldInput(TextField textField, String regex){
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
                if (!newValue.isEmpty() && oldValue.length() == 1) {
                    textField.setText(oldValue);
                } else {
                    if(newValue.matches(regex)) {
                        textField.setText(newValue);
                    }else{
                        textField.clear();
                    };
                }
            };
        textField.textProperty().addListener(changeListener);
        return changeListener;

    }

    public boolean isControlStatesValid(Object[] definitionBuffer) {
        if (taControlStates.getText().isEmpty()) {
            String controlStateError = "*No states are defined!";
            currentErrorList.add("Control states field : "+controlStateError.substring(1,controlStateError.length()).toLowerCase());
            showError(lControlStateInstruction, controlStateError);
            return false;
        }
            lControlStateInstruction.setVisible(false);
        definitionBuffer[1]
            return true;
    }

    public void clearTransitions(MouseEvent mouseEvent) {
        taTransitions.clear();
        transitionsInputed.clear();
    }

    public void addTransition(ActionEvent actionEvent) {
        if (tfAddTransition.isFilled()) {
            String errorString = null;
            char[] transitionItems = tfAddTransition.getTransition(tfAddTransition.getText());
            String[] controlStatesInput = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
            if (checkForValidControlStates(transitionItems, errorString, controlStatesInput)) {
                String transition = "("+transitionItems[0]+" , "+transitionItems[1]+" , "+transitionItems[2]+")     >"+"     ("+transitionItems[3]+" , "+transitionItems[4]+")";
                if(!containsTransition(transitionItems)){
                    transitionsInputed.add(transitionItems);
                    tfAddTransition.clearText();
                    taTransitions.appendText(transition + "\n");
                    lAddTransitionInstruction.setVisible(false);
                } else{
                    showError(lAddTransitionInstruction,"*Duplicate transition!");
                }
            }
        }else{
            showError(lAddTransitionInstruction,"*No transition defined!");
        }
    }
}
