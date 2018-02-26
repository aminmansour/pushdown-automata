package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.*;
import view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PDARunnerController implements Initializable{

    //containers
    @FXML
    private StackPane spPDARunnerPage;
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
    private ArrayList<String> solutionBuffer;
    private int solutionPointer;
    private boolean moreSolutionsToBeFound;
    //fields
    private BorderPane currentChoiceWindow;
    private VBox currentOutputWindow;
    private boolean inDeterministicMode;


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

        transitionTable = new TransitionTableController(new ArrayList<>());
        vbLeftBar.getChildren().add(0, transitionTable.getTransitionTableGenerated());
        actionBar.setDisable(true);


    }

    private void instantRun() {
        if (!inputBox.getInput().isEmpty()) {
            solutionBuffer = new ArrayList<>();
            solutionPointer = 0;
            moreSolutionsToBeFound = true;

            closeDeterministicModeIfPresent();
            transitionTable.clearSelection();
            loadPDAWithInput();
            runInstantRunDFS(false);
        }

    }


    private boolean runInstantRunDFS(boolean isAlternativeSearch) {
        if (isAlternativeSearch) {
            previous();
        }
        boolean toBacktrack = false;
        while (true) {
            ArrayList<Transition> transitions = model.getPossibleTransitionsFromCurrent();
            if (model.hasAccepted()) {
                openInstantRunResultsOutputDialog(true, false);
                return true;
            } else if (transitions.size() == 0 || toBacktrack) {
                while (true) {
                    previous();
                    ArrayList<ConfigurationNode> exploredChildren = model.getHistory().getCurrent().getExploredChildren();
                    if (exploredChildren.size() > 0 &&
                            exploredChildren.get(0).getTotalSiblings() > exploredChildren.size()) {
                        break;
                    } else if (model.getTape().getStep() == 0) {
                        openInstantRunResultsOutputDialog(isAlternativeSearch, isAlternativeSearch);
                        return false;
                    }
                }
            } else {
                toBacktrack = true;
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
                        toBacktrack = false;
                        break;
                    }
                }
            }
        }
    }

    private void loadPDAWithInput() {
        stop();
        tape.setTapeInput(inputBox.getInput());
        tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
        model.getStack().clear();
        stack.setUpStackContentAFresh();
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<Character>(), 0, model.getPossibleTransitionsFromCurrent().size());
    }

    private void stepRun() {
        if (!inputBox.getInput().isEmpty()) {
            closeDeterministicModeIfPresent();
            transitionTable.clearSelection();
            loadPDAWithInput();
            actionBar.setDisable(false);
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
        closeDeterministicModeIfPresent();
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
        if (model.getHistory().getRoot() == model.getHistory().getCurrent()) {
            return;
        }
        while (true) {

            previous();
            ArrayList<ConfigurationNode> exploredChildren = model.getHistory().getCurrent().getExploredChildren();
            if (exploredChildren.size() > 0 && (exploredChildren.get(0).getTotalSiblings() > 1)) {
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
        closeDeterministicModeIfPresent();
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
        closeDeterministicModeIfPresent();
        if (model.getTape().getStep() > 0) {
            model.getHistory().getCurrent().markInPath(false);
            ConfigurationNode current = model.getHistory().getCurrent().getParent();
            transitionTable.clearSelection();
            loadConfigurationState(current);
        }
    }

    public void startAgain() {
        closeDeterministicModeIfPresent();
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
        closeDeterministicModeIfPresent();
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
            spPDARunnerPage.getChildren().remove(currentChoiceWindow);
            removeUserInteractionWithPDA(false);
            currentChoiceWindow = null;
        }
    }

    private void closeOutputDialogIfPresent() {
        if (currentOutputWindow != null) {
            spPDARunnerPage.getChildren().remove(currentOutputWindow);
            removeUserInteractionWithPDA(false);
            currentOutputWindow = null;
        }
    }

    public void setModel(PDAMachine model) {
        this.model = model;
        tape.setTapeInputModel(model.getTape());
        stack.setStackModel(model.getStack());
        transitionTable.clear();
        transitionTable.setStates(model.getDefinition().getStates());
        ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
        ControllerFactory.homeController.showContinueButton();

        for (ControlState controlState : model.getDefinition().getStates()) {
            machineDisplay.addVisualControlState(controlState);
        }


        for (Transition transition : model.getDefinition().getTransitions()) {
            machineDisplay.addVisualTransition(transition);
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
            ((Button) currentChoiceWindow.lookup("#bClose")).setOnAction(event -> {
                closeOptionDialogIfPresent();
            });
            ((Button) currentChoiceWindow.lookup("#bTerminate")).setOnAction(event -> stop());
            currentChoiceWindow.setCenter(sp);
            spPDARunnerPage.getChildren().add(currentChoiceWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openResultsOutputDialog(boolean isAccepted) {
        try {
            removeUserInteractionWithPDA(true);
            transitionTable.clearSelection();
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
            spPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openInstantRunResultsOutputDialog(boolean isAccepted, boolean hasSingleSolution) {
        try {
            removeUserInteractionWithPDA(true);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("../layouts/instant_run_output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected! \n(all possible branches searched!)"));
            String output = "Configuration sequence :  " + (isAccepted && hasSingleSolution ? "No further solutions found!" : model.getCurrentSequence(isAccepted));
            solutionBuffer.add(output);
            solutionPointer = solutionBuffer.size() - 1;
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(output);

            Button bAnotherInput = (Button) currentOutputWindow.lookup("#bAnotherInput");
            Button bAnotherSolution = (Button) currentOutputWindow.lookup("#bAnotherSolution");
            Button bPreviousSolution = (Button) currentOutputWindow.lookup("#bPreviousSolution");
            Button bNextSolution = (Button) currentOutputWindow.lookup("#bNextSolution");
            bNextSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer < solutionBuffer.size() - 1));
            bPreviousSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer > 0));


            bAnotherInput.setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });

            if (isAccepted) {
                bAnotherSolution.setOnAction(event -> {
                    if (moreSolutionsToBeFound) {
                        closeOutputDialogIfPresent();
                        moreSolutionsToBeFound = runInstantRunDFS(true);
                    }
                });
            }

            bPreviousSolution.setOnAction(event -> {
                String solutionToOutput = solutionBuffer.get(--solutionPointer);
                ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(solutionToOutput);
                bNextSolution.setDisable(false);
                bPreviousSolution.setDisable(solutionPointer == 0);
            });

            bNextSolution.setOnAction(event -> {
                String solutionToOutput = solutionBuffer.get(++solutionPointer);
                ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(solutionToOutput);
                bPreviousSolution.setDisable(false);
                bNextSolution.setDisable(solutionPointer - 1 < solutionBuffer.size() - 1);
            });

            spPDARunnerPage.getChildren().add(currentOutputWindow);

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
        transitionTable.select(transition, true);
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

    public boolean isCurrentSavedInMemory() {
        return model != null && model.isSavedInMemory();
    }


    public void markCurrentAsSavedInMemory() {
        model.markAsSavedInMemory();
    }

    public void openSaveDialog() {
        try {
            VBox currentsaveWindow = FXMLLoader.load(getClass().getResource("../layouts/save_confirmation_page.fxml"));
            Button bSave = (Button) currentsaveWindow.lookup("#bSave");
            Button bClose = (Button) currentsaveWindow.lookup("#bClose");
            Label lError = (Label) currentsaveWindow.lookup("#lError");
            TextField tfName = (TextField) currentsaveWindow.lookup("#tfName");
            bClose.setOnAction(event -> {
                spPDARunnerPage.getChildren().remove(currentsaveWindow);
            });

            bSave.setOnAction(event -> {
                if (!tfName.getText().trim().isEmpty()) {
                    Memory.load();
                    Definition definition = model.getDefinition();
                    definition.setIdentifier(tfName.getText().trim());
                    if (!ModelFactory.checkForDefinitionOccurrence(definition)) {
                        model.markAsSavedInMemory();
                        Memory.save(definition);
                        spPDARunnerPage.getChildren().remove(currentsaveWindow);
                    }
                }
            });

            spPDARunnerPage.getChildren().add(currentsaveWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return model != null;
    }

    public void showConfirmationDialog() {
        ViewFactory.showStandardDialog(spPDARunnerPage, false, "Alert :",
                "This action might lose all unsaved changes! Are you sure you want to proceed without saving!", event -> {
                    spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                    switchToQuickDefinition();
                },
                event -> {
                    spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                }, "Proceed", "Close");
    }

    public void switchToQuickDefinition() {
        ViewFactory.globalPane.setCenter(ViewFactory.quickDefinition);
        BorderPane.setAlignment(ViewFactory.quickDefinition, Pos.CENTER);
        BorderPane.setMargin(ViewFactory.quickDefinition, new Insets(0, 0, 0, 0));
    }

    public void updateVisualLabel(Transition transition) {
        machineDisplay.updateLabel(transition);
    }

    public void openDeterministicMode() {
        transitionTable.clearSelection();
        Set<Transition> transitions = model.getDeterministicTransitions();
        machineDisplay.highlightDeterministicTransitions(transitions);
        transitionTable.highlightTransitions(transitions);
        inDeterministicMode = true;
    }

    public void closeDeterministicModeIfPresent() {
        if (inDeterministicMode) {
            inDeterministicMode = false;
            machineDisplay.unhighlightAllTransitions();
            transitionTable.clearSelection();
        }

    }

    public boolean isInDeterministicMode() {
        return inDeterministicMode;
    }


}
