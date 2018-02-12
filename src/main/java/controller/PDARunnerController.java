package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.ControlState;
import model.PDAMachine;
import model.Transition;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PDARunnerController implements Initializable{

    //containers
    @FXML
    private StackPane bpPDARunnerPage;
    @FXML
    private VBox vbPDAInteraction;
    @FXML
    private VBox vbLeftBar;
    @FXML
    private HBox hbCentre;

    //sub-controllers
    private TapeDisplayController tape;
    private UserActionController actionBar;
    private UserInputBoxController inputBox;
    private StackController stack;
    private TransitionTableController transitionTable;
    private MachineDisplayController machineDisplay;

    //model
    private PDAMachine model;

    //fields
    private BorderPane currentChoiceWindow;


    public PDARunnerController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tape = new TapeDisplayController();
        vbPDAInteraction.getChildren().add(tape.getTapeViewGenerated());

        initializeUserActionBarAndSetUpActionListeners();

        inputBox = new UserInputBoxController();
        vbLeftBar.getChildren().add(inputBox.getInputBox());
        inputBox.setButtonStepRunAction(event -> {
            stepRun();
        });

        machineDisplay = new MachineDisplayController();
        hbCentre.getChildren().add(machineDisplay.getCanvas());

        stack = new StackController();
        hbCentre.getChildren().add(stack.getStackGenerated());

        transitionTable = new TransitionTableController();
        vbLeftBar.getChildren().add(0, transitionTable.getTransitionTableGenerated());
        transitionTable.highlightRow(0);
        actionBar.setDisable(true);


    }

    private void stepRun() {
        if (!inputBox.getInput().isEmpty()) {
            stop();
            actionBar.setDisable(false);
            tape.setTapeInput(inputBox.getInput());
        }

    }

    private void initializeUserActionBarAndSetUpActionListeners() {
        actionBar = new UserActionController();
        vbLeftBar.getChildren().add(actionBar.getActionBar());
        actionBar.setButtonNextAction(event -> next());
        actionBar.setButtonPreviousAction(event -> previous());
        actionBar.setButtonStartAgain(event -> startAgain());
        actionBar.setButtonNextBranchAction(event -> next());
        actionBar.setButtonPreviousBranchAction(event -> next());
        actionBar.setButtonStopAction(event -> stop());
    }


    public void next(){
        List<Transition> transitions = model.getPossibleTransitionsFromCurrent();
        if (transitions.size() == 0) {
            System.out.println("finished");
        } else if (transitions.size() == 1) {
            exectuteTransition(transitions.get(0));
        } else {
            openTransitionOptionDialog(transitions);
        }
    }

    public void previous(){
        tape.previous();
    }

    public void startAgain(){
        tape.redo();
        stack.clear();
        stack.setUpStackContentAFresh();
    }

    public void stop(){
        closeOptionDialogIfPresent();
        tape.clear();
        stack.clear();

    }

    private void closeOptionDialogIfPresent() {
        if (currentChoiceWindow != null) {
            bpPDARunnerPage.getChildren().remove(currentChoiceWindow);
            removeUserInteractionWithPDA(false);
            currentChoiceWindow = null;
        }
    }

    public void setModel(PDAMachine model) {
        this.model = model;
        tape.setTapeInputModel(model.getTape());
        stack.setStackModel(model.getStack());
        transitionTable.clear();

        for (ControlState controlState : model.getDefinition().getStates()) {
            System.out.println(controlState.isInitial());
            machineDisplay.addVisualControlState(controlState);
        }

        for (Transition transition : model.getDefinition().getTransitions()) {
            machineDisplay.addVisualTransition(transition.toString(), transition.getSourceState(), transition.getTargetState());
            transitionTable.addColumn(transition);
        }

        machineDisplay.orderStatesInScreen();
    }


    public void openTransitionOptionDialog(List<Transition> transitions) {
        try {
            removeUserInteractionWithPDA(true);
            currentChoiceWindow = FXMLLoader.load(getClass().getResource("../layouts/transition_selecter_page.fxml"));
            ScrollPane sp = new ScrollPane();
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            BorderPane.setAlignment(sp, Pos.CENTER);
            VBox vbOptions = new VBox(5);
            vbOptions.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            vbOptions.setId("vbOptionContainer");
            sp.setContent(vbOptions);
            for (int i = 0; i < transitions.size(); i++) {
                BorderPane option = FXMLLoader.load(getClass().getResource("../layouts/transition_option.fxml"));
                ((Label) option.lookup("#lTransitionOption")).setText(Integer.toString(i + 1));
                ((Label) option.lookup("#lRead")).setText("Read : " + transitions.get(i).getConfiguration().getInputSymbol());
                ((Label) option.lookup("#lPush")).setText("Push : " + transitions.get(i).getAction().getElementToPush());
                ((Label) option.lookup("#lPop")).setText("Pop : " + transitions.get(i).getConfiguration().getTopElement());
                ((Label) option.lookup("#lResultingState")).setText("Resulting State : " + transitions.get(i).getAction().getNewState().getLabel());
                vbOptions.getChildren().add(option);
                int finalI = i;
                option.setOnMouseClicked(event -> {
                    exectuteTransition(transitions.get(finalI));
                });
            }
            ((Button) currentChoiceWindow.lookup("#bRandomSelect")).setOnAction(event -> {
                closeOptionDialogIfPresent();
            });
            ((Button) currentChoiceWindow.lookup("#bTerminate")).setOnAction(event -> stop());
            currentChoiceWindow.setCenter(sp);
            bpPDARunnerPage.getChildren().add(currentChoiceWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exectuteTransition(Transition transition) {
        System.out.println("transition clicked" + transition);
        model.executeTransition(transition);
        tape.next();
        tape.setCurrentStateLabel(model.getCurrentState().getLabel());
        tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getSymbolAtHead() + " , " + model.getStack().top() + " )");
        stack.loadState();

        if (transition.getAction().getElementToPush() != '/') {
            tape.setStackTopLabel(transition.getAction().getElementToPush() + "");
        } else if (transition.getConfiguration().getTopElement() != '/') {
            tape.setStackTopLabel(model.getStack().top() + "");
        }
        closeOptionDialogIfPresent();
    }


    private void removeUserInteractionWithPDA(boolean toRemove) {
        actionBar.setDisable(toRemove);
        inputBox.setDisable(toRemove);
    }
}
