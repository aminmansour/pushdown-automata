package view.pages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import view.widgits.TransitionTextField;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CodeRunner implements Initializable{

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



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        transitionsInputed = new ArrayList<char[]>();
        ToggleGroup tg = new ToggleGroup();
        rbAcceptingState.setToggleGroup(tg);
        rbEmptyStack.setToggleGroup(tg);
        rbAcceptingState.setSelected(true);

        tg.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(rbAcceptingState.isSelected()){
                lFurtherInfo.setText("Accepting States");
                lFurtherInfoHelp.getTooltip().setText("Enter the accepting states seperated by commas i.e. q,r,s");
            }else{
                lFurtherInfo.setText("Empty Stack Symbol");
                lFurtherInfoHelp.getTooltip().setText("Enter the initial stack symbol i.e. Z");
            }
            lFurtherInfoInstruction.setVisible(false);
        });

        lClearTransitions.setOnMouseClicked(event -> {
            taTransitions.clear();
            transitionsInputed.clear();
        });

        TransitionTextField tfAddTransition = addSpecialTransitionTextField();
        setUpHelpRequestForEachField();

        bAddTransition.setOnAction(event -> {

            if (tfAddTransition.isFilled()) {
                System.out.println("true");
                String errorString = null;
                char[] transitionItems = tfAddTransition.getTransition(tfAddTransition.getText());
                String[] controlStatesInput = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
                if (checkForValidControlStates(transitionItems, errorString, controlStatesInput)) {
                    String transition = "("+transitionItems[0]+" , "+transitionItems[1]+" , "+transitionItems[2]+")     >"+"     ("+transitionItems[3]+" , "+transitionItems[4]+")";
                    if(!containsTransition(transitionItems)){
                        transitionsInputed.add(transitionItems);
                        taTransitions.appendText(transition+"\n");
                    } else{
                        setInstruction(lAddTransitionInstruction,"*Duplicate transition!");
                    }
                }
            }else{
                setInstruction(lAddTransitionInstruction,"*No transition defined!");
            }
        });

    }

    private void setInstruction(Label instruction,String output){
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
            setInstruction(lAddTransitionInstruction,errorString);
            return false;
        }else {
            return true;
        }
    }

    private void setUpHelpRequestForEachField() {
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
            if(isSame){
                return true;
            }
        }
        return false;
    }

    public void generate(ActionEvent actionEvent) {
        boolean definitionValid = true;
        String controlStateError = null;
        String[] controlStatesInput = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
        if(taControlStates.getText().isEmpty()){
            setInstruction(lControlStateInstruction,"*No states are defined!");
            definitionValid = false;
        }


        String initialStateError = null;
        if (tfInitialState.getText().isEmpty()) {
            initialStateError = "*No initial state is defined!";
        }else if(stringArrayLookUp(controlStatesInput,tfInitialState.getText().charAt(0))){
            initialStateError = "*State not recognized!";
        }
        if(initialStateError==null) {
            setInstruction(lInitialStateInstruction, initialStateError);
            definitionValid = false;
        }


        String furtherInfoError = null;
        if (rbAcceptingState.isSelected()) {
            if(tfFurtherInfo.getText().isEmpty()){
                furtherInfoError = "*No accepting states defined!";
            }else {
                String[] acceptingStates = taControlStates.getText().trim().replaceAll("\\s", "").split(",");
                for (String state : acceptingStates) {
                    if (state.length() > 1) {
                        furtherInfoError = "*Improper format! see hint";
                        break;
                    }else{
                        if(!stringArrayLookUp(controlStatesInput,state.charAt(0))){
                            furtherInfoError = "*Not all states are recognizable!";
                            break;
                        }
                    }
                }
            }

        }else{
            if (tfFurtherInfo.getText().isEmpty()) {
                furtherInfoError = "*No empty stack symbol is defined!";
            }
        }

        if(furtherInfoError!=null){
            setInstruction(lFurtherInfoInstruction, furtherInfoError);
            definitionValid = false;
        }

        String transitionError = null;
        if(taTransitions.getText().isEmpty()){
            transitionError = "*No transitions defined!";
        }else{
            for(char[] transition: transitionsInputed){
                if(initialStateError!=null || transition[0]==tfInitialState.getText().charAt(0)){
                    transitionError = "*No transition from initial state is defined!";
                    break;
                }
            }
        }

        if(transitionError !=null){
            setInstruction(lAddTransitionInstruction, furtherInfoError);
            definitionValid = false;
        }


        if(definitionValid){

        }else{
            showErrorDialog(controlStateError, initialStateError, furtherInfoError, transitionError);
        }


    }

    private void showErrorDialog(String controlStateError, String initialStateError, String furtherInfoError, String transitionError) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not generate PDA!");
        alert.setContentText("Fix the errors specified before generating!");
        Label label = new Label("The errors found:");
        String output = (controlStateError==null?"":"Control state field : "+controlStateError.substring(1,controlStateError.length()-1).toLowerCase()+"\n")+
                (initialStateError==null?"":"Initial state field : "+initialStateError.substring(1,initialStateError.length()-1).toLowerCase()+"\n")+
                (furtherInfoError==null?"":(rbAcceptingState.isSelected()?"Empty stack symbol":"Accepting states")+" field : "+furtherInfoError.substring(1,furtherInfoError.length()-1).toLowerCase()+"\n")+
                (transitionError==null?"":"Transitions field : "+transitionError.substring(1,transitionError.length()-1).toLowerCase()+"\n");
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
}
