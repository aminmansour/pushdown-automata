package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

        inputBox.setButtonInstantRunAction(event -> {
            instantRun();
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

    private void instantRun() {
        if (!inputBox.getInput().isEmpty()) {
            stop();
            tape.setTapeInput(inputBox.getInput());
            tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
            model.getStack().clear();
            stack.setUpStackContentAFresh();

            model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<Character>(), 0, model.getPossibleTransitionsFromCurrent().size());

            while (true) {

                ArrayList<Transition> transitions = model.getPossibleTransitionsFromCurrent();

                if (model.hasAccepted()) {
                    openResultsOutputDialog(true);
                    return;
                } else if (transitions.size() == 0) {
                    while (true) {
                        previous();
                        if (model.getHistory().getCurrent().getExploredChildren().get(0).getTotalSiblings() > model.getHistory().getCurrent().getExploredChildren().size()) {
                            break;
                        } else if (model.getTape().getStep() == 0) {
                            openResultsOutputDialog(false);
                            return;
                        }


                    }
                } else {
                    for (Transition transition : transitions) {
                        ArrayList<Character> content = new ArrayList(model.getStack().getStackContent());
                        if (transition.getConfiguration().getTopElement() != '/') {
                            content.remove(content.size() - 1);
                        }
                        if (transition.getAction().getElementToPush() != '/') {
                            content.add(transition.getAction().getElementToPush());
                        }
                        ConfigurationNode previousState = model.getHistory().getCurrent().getChildIfFound(transition.getAction().getNewState(), content, model.getTape().getStep() + 1);
                        if (previousState == null) {
                            executeTransition(transition, transitions.size());
                            break;
                        }
                    }
                }
            }
        }

    }

    private void stepRun() {
        if (!inputBox.getInput().isEmpty()) {
            stop();
            actionBar.setDisable(false);
            tape.setTapeInput(inputBox.getInput());
            tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
            model.getStack().clear();
            stack.setUpStackContentAFresh();
            model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<Character>(), 0, model.getPossibleTransitionsFromCurrent().size());
        }
    }

    private void initializeUserActionBarAndSetUpActionListeners() {
        actionBar = new UserActionController();
        vbLeftBar.getChildren().add(actionBar.getActionBar());
        actionBar.setButtonNextAction(event -> next());
        actionBar.setButtonPreviousAction(event -> previous());
        actionBar.setButtonStartAgain(event -> startAgain());
        actionBar.setButtonNextBranchAction(event -> nextBranching());
        actionBar.setButtonPreviousBranchAction(event -> previousBranching());
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
                executeTransition(transitions.get(0), transitions.size());
            } else {
                removeUserInteractionWithPDA(true);
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> openTransitionOptionDialog(transitions)));

                timeline.play();

                break;
            }
        }
    }

    public void previousBranching() {
        while (true) {
            previous();
            if (model.getHistory().getCurrent().getExploredChildren().get(0).getTotalSiblings() > 1) {
                removeUserInteractionWithPDA(true);
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> next()));

                timeline.play();
                break;
            } else if (model.getTape().getStep() == 0) {
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
            executeTransition(transitions.get(0), transitions.size());
        } else {
            openTransitionOptionDialog(transitions);
        }
    }

    public void previous(){
        if (model.getTape().getStep() > 0) {
            model.getHistory().getCurrent().markInPath(false);
            ConfigurationNode current = model.getHistory().getCurrent().getParent();
            loadConfigurationState(current);
        }
    }

    public void startAgain(){
        tape.redo();
        model.setCurrentStateToInitial();
        stack.setUpStackContentAFresh();
        stack.clean();
        stack.clean();
        updateCurrentStateAndConfigurationField();
        tape.setStackTopLabel("-");
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<>(), 0, model.getPossibleTransitionsFromCurrent().size());

    }

    public void stop(){
        closeOptionDialogIfPresent();
        actionBar.setDisable(true);
        model.setCurrentStateToInitial();
        tape.clear();
        tape.setCurrentStateLabel(model.getCurrentState().getLabel());
        stack.clean();
        stack.setUpStackContentAFresh();
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
                    executeTransition(transitions.get(finalI), transitions.size());
                });
            }
            ((Button) currentChoiceWindow.lookup("#bRandomSelect")).setOnAction(event -> {
                Random randomGenerator = new Random();
                executeTransition(transitions.get(randomGenerator.nextInt(transitions.size())), transitions.size());
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
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected!"));
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText("Configuration sequence :  " + model.getCurrentSequence(isAccepted));
            ((Button) currentOutputWindow.lookup("#bAnotherInput")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });

            ((Button) currentOutputWindow.lookup("#bPreviousBranch")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                previousBranching();
            });

            ((Button) currentOutputWindow.lookup("#bContinue")).setOnAction(event -> {
                closeOutputDialogIfPresent();
            });
            bpPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openInstantRunResultsOutputDialog(boolean isAccepted) {
        try {
            removeUserInteractionWithPDA(true);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("../layouts/instant_run_output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected!"));
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText("Configuration sequence :  " + model.getCurrentSequence(isAccepted));
            ((Button) currentOutputWindow.lookup("#bAnotherInput")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });
            ((Button) currentOutputWindow.lookup("#bAnotherSolution")).setOnAction(event -> {
                closeOutputDialogIfPresent();
            });
            bpPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeTransition(Transition transition, int totalChildren) {
        ArrayList<Character> content = new ArrayList(model.getStack().getStackContent());
        if (transition.getConfiguration().getTopElement() != '/') {
            content.remove(content.size() - 1);
        }
        if (transition.getAction().getElementToPush() != '/') {
            content.add(transition.getAction().getElementToPush());
        }
        ConfigurationNode previousState = model.getHistory().getCurrent().getChildIfFound(transition.getAction().getNewState(), content, model.getTape().getStep() + 1);
        if (previousState != null) {
            previousState.markInPath(true);
            loadConfigurationState(previousState);
        } else {
            model.executeTransition(transition, totalChildren);
            tape.next();
            stack.update();
            updateCurrentStateAndConfigurationField();

            if (transition.getAction().getElementToPush() != '/') {
                tape.setStackTopLabel(transition.getAction().getElementToPush() + "");
            } else if (transition.getConfiguration().getTopElement() != '/') {
                tape.setStackTopLabel(model.getStack().top() + "");
            }
        }
        closeOptionDialogIfPresent();
    }

    private void loadConfigurationState(ConfigurationNode record) {
        model.getHistory().setCurrent(record);
        model.setCurrentState(record.getState());
        tape.setHeadPosition(record.getHeadPosition());
        stack.loadState(record.getStackState());
        updateCurrentStateAndConfigurationField();
        tape.setStackTopLabel(model.getStack().isEmpty() ? "-" : model.getStack().top() + "");
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
