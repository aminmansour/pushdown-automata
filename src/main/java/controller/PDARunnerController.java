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
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private ComputationalTree history;

    //fields
    private BorderPane currentChoiceWindow;
    private VBox currentOutputWindow;


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
            tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
            model.getStack().clear();
            stack.setUpStackContentAFresh();
            ArrayList<Transition> transitions = model.getPossibleTransitionsFromCurrent();
            if (transitions.size() != 0) {
                model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<Character>(), 0, transitions.size() > 1);
            }
        }
    }

    private void initializeUserActionBarAndSetUpActionListeners() {
        actionBar = new UserActionController();
        vbLeftBar.getChildren().add(actionBar.getActionBar());
        actionBar.setButtonNextAction(event -> next());
        actionBar.setButtonPreviousAction(event -> previous());
        actionBar.setButtonStartAgain(event -> startAgain());
        actionBar.setButtonNextBranchAction(event -> nextBranching());
        actionBar.setButtonPreviousBranchAction(event -> next());
        actionBar.setButtonStopAction(event -> stop());
    }

    public void nextBranching() {
        while (true) {
            ArrayList<Transition> transitions = model.getPossibleTransitionsFromCurrent();
            if (model.hasAccepted()) {
                openResultsOutputDialog(true);
                break;
            } else if (transitions.size() == 0) {
                openResultsOutputDialog(false);
                break;
            } else if (transitions.size() == 1) {
                executeTransition(transitions.get(0), false);
            } else {
                openTransitionOptionDialog(transitions);
                break;
            }
        }
    }


    public void next(){
        List<Transition> transitions = model.getPossibleTransitionsFromCurrent();
        if (model.hasAccepted()) {
            openResultsOutputDialog(true);
        } else if (transitions.size() == 0) {
            openResultsOutputDialog(false);
        } else if (transitions.size() == 1) {
            executeTransition(transitions.get(0), false);
        } else {
            openTransitionOptionDialog(transitions);
        }
    }

    public void previous(){
        ConfigurationNode previous = model.getHistory().getCurrent();
        model.getHistory().setCurrent(previous.getParent());
        ConfigurationNode current = model.getHistory().getCurrent();
        model.setCurrentState(current.getState());
        tape.setHeadPosition(current.getHeadPosition());
        stack.loadState(current.getStackState());
        updateCurrentStateAndConfigurationField();
        tape.setStackTopLabel(model.getStack().isEmpty() ? "-" : model.getStack().top() + "");
    }

    public void startAgain(){
        tape.redo();
        model.setCurrentStateToInitial();
        stack.setUpStackContentAFresh();
        stack.clean();
        updateCurrentStateAndConfigurationField();
        tape.setStackTopLabel("-");
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<>(), 0, false);

    }

    public void stop(){
        closeOptionDialogIfPresent();
        actionBar.setDisable(true);
        model.setCurrentStateToInitial();
        tape.clear();
        tape.setCurrentStateLabel(model.getCurrentState().getLabel());
        stack.clear();

    }

    private void closeOptionDialogIfPresent() {
        if (currentChoiceWindow != null) {
            bpPDARunnerPage.getChildren().remove(currentChoiceWindow);
            removeUserInteractionWithPDA(false);
            currentChoiceWindow = null;
        }
    }

    private void closeOutputDialogIfPresent() {
        if (currentOutputWindow != null) {
            bpPDARunnerPage.getChildren().remove(currentOutputWindow);
            removeUserInteractionWithPDA(false);
            currentOutputWindow = null;
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
                    executeTransition(transitions.get(finalI), true);
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

    public void openResultsOutputDialog(boolean isAccepted) {
        try {
            removeUserInteractionWithPDA(true);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("../layouts/output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lInput")).setText("Input word \"" + model.getTape().getOriginalWord() + "\"  : ");
            ((Label) currentOutputWindow.lookup("#lOutcome")).setText(isAccepted ? " Accepted!" : " Rejected!");
            ((Button) currentOutputWindow.lookup("#bAnotherInput")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });
            ((Button) currentOutputWindow.lookup("#bContinue")).setOnAction(event -> {
                closeOutputDialogIfPresent();
            });
            bpPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeTransition(Transition transition, boolean isBranchingTransition) {
        model.executeTransition(transition, isBranchingTransition);
        tape.next();
        stack.update();
        updateCurrentStateAndConfigurationField();

        if (transition.getAction().getElementToPush() != '/') {
            tape.setStackTopLabel(transition.getAction().getElementToPush() + "");
        } else if (transition.getConfiguration().getTopElement() != '/') {
            tape.setStackTopLabel(model.getStack().top() + "");
        }
        closeOptionDialogIfPresent();
    }

    private void updateCurrentStateAndConfigurationField() {
        tape.setCurrentStateLabel(model.getCurrentState().getLabel());
        tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
    }


    private void removeUserInteractionWithPDA(boolean toRemove) {
        actionBar.setDisable(toRemove);
        inputBox.setDisable(toRemove);
    }
}
