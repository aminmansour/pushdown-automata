package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PDARunnerController implements Initializable{

    @FXML
    private BorderPane bpPDARunnerPage;
    @FXML
    private VBox vbPDAInteraction;
    @FXML
    private VBox vbLeftBar;
    @FXML
    private HBox hbCentre;
    @FXML
    private TapeDisplayController tape;
    private UserActionController actionBar;
    private UserInputBoxController inputBox;
    private StackController stack;
    private TransitionTableController transitionTable;
    private MachineDisplayController machineDisplay;


    public PDARunnerController(){


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        tape = new TapeDisplayController();
        vbPDAInteraction.getChildren().add(tape.getTapeViewGenerated());

        actionBar = new UserActionController();
        vbLeftBar.getChildren().add(actionBar.getActionBar());
        actionBar.setButtonNextAction(event -> next());
        actionBar.setButtonPreviousAction(event -> previous());
        actionBar.setButtonStartAgain(event -> startAgain());
        actionBar.setButtonNextBranchAction(event -> next());
        actionBar.setButtonPreviousBranchAction(event -> next());
        actionBar.setButtonStopAction(event -> stop());
        inputBox = new UserInputBoxController();
        vbLeftBar.getChildren().add(inputBox.getInputBox());
        inputBox.setButtonStepRunAction(event -> {
            stop();
            tape.setTapeInput(inputBox.getInput());

        });

        machineDisplay = new MachineDisplayController();
        hbCentre.getChildren().add(machineDisplay.getCanvas());

        stack = new StackController();
        hbCentre.getChildren().add(stack.getStackGenerated());

        transitionTable = new TransitionTableController();
        vbLeftBar.getChildren().add(0, transitionTable.getTransitionTableGenerated());
        transitionTable.highlightRow(0);

    }


    public void next(){
        tape.next();
    }

    public void previous(){
        tape.previous();
    }

    public void startAgain(){
        tape.redo();
        stack.setUpStackContentAFresh();
    }

    public void stop(){
        tape.clear();
    }
}
