package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.*;
import view.ViewFactory;

import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller which runs a PDAMachine and allows for user interaction with it
 */
public class PDARunnerController implements Initializable {

    //containers
    @FXML
    private StackPane spPDARunnerPage;
    @FXML
    private VBox vbPDAInteraction;
    @FXML
    private VBox vbLeftBar;
    @FXML
    private HBox hbCentre;
    @FXML
    private VBox vbDisplay;
    @FXML
    private Label lPDATitle;

    //sub-controllers
    private TapeController tape;
    private UserActionController actionBar;
    private UserInputBoxController inputBox;
    private StackController stack;
    private TransitionTableController transitionTable;
    private MachineDisplayController machineDisplay;

    //pda
    private PDAMachine pda;
    private ArrayList<String> solutionBuffer;
    private int solutionPointer;
    private boolean moreSolutionsToBeFound;

    //dialog
    private BorderPane currentChoiceWindow;
    private VBox currentOutputWindow;
    private Node currentSaveWindow;
    private boolean inDeterministicMode;
    private boolean machineIsNonDeterministic;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildPDARunnerView();
    }

    //loads the individual components ( views and controllers)
    private void buildPDARunnerView() {
        tape = new TapeController();
        vbPDAInteraction.getChildren().add(tape.getTapeViewGenerated());

        initializeUserActionBarAndSetUpActionListeners();

        inputBox = new UserInputBoxController();
        vbLeftBar.getChildren().add(inputBox.getInputBox());
        inputBox.setButtonStepRunAction(event -> stepRun());

        inputBox.setButtonInstantRunAction(event -> instantRun());

        machineDisplay = new MachineDisplayController();
        vbDisplay.getChildren().add(1, machineDisplay.getCanvas());
        vbDisplay.getChildren().add(2, machineDisplay.getSlider());

        stack = new StackController();
        hbCentre.getChildren().add(stack.getStackGenerated());

        transitionTable = new TransitionTableController(new ArrayList<>());
        vbLeftBar.getChildren().add(0, transitionTable.getTransitionTableGenerated());

        actionBar.setDisable(true);
    }

    //runs instant run on the current input
    private void instantRun() {
        actionBar.setDisable(true);
        transitionTable.clearSelection(false);

        closeNonDeterministicModeIfPresent();
        setUpRunEnvironment();
        runInstantRunDFS(false, 20);
    }

    //clears solution store, and loads the current input into the PDAMachine instance
    private void setUpRunEnvironment() {
        clearSolutionHistory();
        pda.loadInput(inputBox.getInput());
        machineIsNonDeterministic = pda.isNonDeterministic();
    }

    //clears solution store
    private void clearSolutionHistory() {
        solutionBuffer = new ArrayList<>();
        solutionPointer = 0;
        moreSolutionsToBeFound = true;
    }


    /**
     * A method which updates all the individual interface components with the updated information
     */
    private void requestInterfaceUpdate() {
        tape.requestUpdate();
        stack.requestInterfaceUpdate();
        machineDisplay.focusState(pda.getCurrentState(), true);
        ControlState currentState = pda.getCurrentState();
        tape.setCurrentStateLabel(currentState == null ? pda.getDefinition().getInitialState().getLabel() : currentState.getLabel());
        tape.setCurrentConfigurationLabel(currentState == null ? "-" : "( " + pda.getCurrentState().getLabel() + " , " + pda.getTape().getRemainingInputAsString() + " , " + pda.getStack().getStackContentAsString() + " )");
        tape.setStackTopLabel(pda.getStack().isEmpty() ? "-" : pda.getStack().top() + "");
    }

    //runs dfs for instant-run on a given input
    private boolean runInstantRunDFS(boolean isAlternativeSearch, int limit) {
        ArrayList<ConfigurationContext> memory = new ArrayList<>();
        if (isAlternativeSearch) {
            pda.previous();
        }
        int totalMoves = 0;
        boolean toBacktrack = false;
        while (true) {
            ArrayList<Transition> transitions = pda.getPossibleTransitionsFromCurrent();
            if (pda.isAccepted()) {
                openInstantRunResultsOutputDialog(true, false);
                return true;
            } else if (transitions.size() == 0 || toBacktrack) {
                toBacktrack = false;
                while (true) {
                    previous();
                    ArrayList<ConfigurationContext> exploredChildren = pda.getExecutionTree().getCurrent().getExploredChildren();
                    if (exploredChildren.size() > 0 &&
                            exploredChildren.get(0).getTotalSiblings() > exploredChildren.size()) {
                        break;
                    } else if (pda.getTape().getStep() == 0) {
                        openInstantRunResultsOutputDialog(isAlternativeSearch, isAlternativeSearch);
                        return false;
                    }
                }
            } else {
                toBacktrack = true;
                java.util.Collections.shuffle(transitions);
                transitionsLoop:
                for (Transition transition : transitions) {
                    ConfigurationContext previousState = checkIfPresentInHistory(transition);
                    if (previousState == null) {
                        pda.executeTransition(transition, transitions.size());
                        toBacktrack = false;
                        totalMoves++;
                        if (totalMoves % 20 == 0) {
                            ViewFactory.showStandardDialog(spPDARunnerPage, false, "No solution yet found (" + limit + " steps have been made) ", "Do you want to take control of the computation to make finding a solution easier?", event -> {
                                spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                                actionBar.setDisable(false);
                                loadConfigurationContext(pda.getExecutionTree().getCurrent());
                            }, event -> {
                                spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                                Timeline timeline = new Timeline(new KeyFrame(
                                        Duration.millis(1300),
                                        ae -> runInstantRunDFS(isAlternativeSearch, limit + 20)));

                                timeline.play();
                            }, "Enter step-mode", "Continue search");
                            return false;
                        }
                        break transitionsLoop;
                    }
                }

            }
        }
    }


    //step run
    private void stepRun() {
        clearSolutionHistory();
        closeNonDeterministicModeIfPresent();
        machineDisplay.unhighlightAllTransitions();
        transitionTable.clearSelection(false);
        loadPDAWithNewInput();
        actionBar.setDisable(false);
    }

    //load the pda with input and update interface
    private void loadPDAWithNewInput() {
        stop();
        setUpRunEnvironment();
        machineDisplay.focusState(pda.getCurrentState(), true);
        requestInterfaceUpdate();
    }


    //sets up listeners in the action bar
    private void initializeUserActionBarAndSetUpActionListeners() {
        actionBar = new UserActionController();
        vbLeftBar.getChildren().add(actionBar.getActionBar());
        actionBar.setButtonNextAction(event -> next());
        actionBar.setButtonPreviousAction(event -> previous());
        actionBar.setButtonStartAgain(event -> redo());
        actionBar.setButtonNextBranchAction(event -> nextBranching());
        actionBar.setButtonPreviousBranchAction(event -> previousBranching());
        actionBar.setButtonStopAction(event -> stop());
    }

    //A method which goes to next choice branch
    private void nextBranching() {
        closeNonDeterministicModeIfPresent();
        while (true) {
            ArrayList<Transition> transitions = pda.getPossibleTransitionsFromCurrent();
            if (pda.isAccepted()) {
                openStepRunDeterministicOutputDialog(true);
                break;
            } else if (transitions.size() == 0) {
                openStepRunDeterministicOutputDialog(false);
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

    //A method which goes to previous choice branch
    public void previousBranching() {
        if (pda.getExecutionTree().getRoot() == pda.getExecutionTree().getCurrent()) {
            return;
        }
        while (true) {
            previous();
            ArrayList<ConfigurationContext> exploredChildren = pda.getExecutionTree().getCurrent().getExploredChildren();
            if (exploredChildren.size() > 0 && (exploredChildren.get(0).getTotalSiblings() > 1)) {
                removeUserInteractionWithPDA(true);
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> next()));
                timeline.play();
                break;
            } else if (pda.getTape().getStep() == 0) {
                break;
            }
        }
    }


    //A method which geos to the next configuration by applying a transition from
    //a current configuration
    private void next() {
        closeNonDeterministicModeIfPresent();
        List<Transition> transitions = pda.getPossibleTransitionsFromCurrent();

        if (pda.isAccepted()) {
            openStepRunDeterministicOutputDialog(true);
        } else if (transitions.size() == 0) {
            openStepRunDeterministicOutputDialog(false);
        } else if (transitions.size() == 1) {
            executeTransition(transitions.get(0), transitions.size());
            if (tape.isLastStep() && ((pda.isAccepted() || pda.getPossibleTransitionsFromCurrent().isEmpty()))) {
                actionBar.restrictToOnlyPlay();
            }
        } else {
            openTransitionOptionDialog(transitions);
        }
    }

    //a method which goes to the previous configuration from the current one
    private void previous() {
        closeNonDeterministicModeIfPresent();
        transitionTable.clearSelection(false);
        pda.previous();
        machineDisplay.unhighlightAllTransitions();
        actionBar.setDisable(false);
        requestInterfaceUpdate();
    }

    /**
     * A method which cancels the current computation of the PDA and restarts from
     * the beginning with the same. It updates the interface as well.
     */
    void redo() {
        closeNonDeterministicModeIfPresent();
        machineDisplay.unhighlightAllTransitions();
        transitionTable.clearSelection(false);
        pda.redo();
        requestInterfaceUpdate();
    }

    /**
     * restarts current PDA and cancels computation and removes current input
     */
    void stop() {
        closeNonDeterministicModeIfPresent();
        closeOptionDialogIfPresent();
        actionBar.setDisable(true);
        machineDisplay.unhighlightAllTransitions();
        transitionTable.clearSelection(false);
        pda.stop();
        requestInterfaceUpdate();
    }


    /**
     * A method which load a new PDAMachine into this controller instance
     *
     * @param model the PDAMachine to load containing the definition of the machine
     */
    public void loadPDA(PDAMachine model) {
        this.pda = model;
        closeOutputDialogIfPresent();
        closeOptionDialogIfPresent();
        closeNonDeterministicModeIfPresent();
        closeSaveDialogIfPresent();
        machineDisplay.resetZoom();
        tape.setTapeInputModel(model.getTape());
        stack.setStackModel(model.getStack());
        transitionTable.clear();
        inputBox.clear();
        inputBox.setHintText(pda.getDefinition().getExampleHint());
        setPDATitle(model.getDefinition().getIdentifier());
        transitionTable.setStates(model.getDefinition().getStates());
        machineDisplay.clear();
        ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
        ControllerFactory.homeController.showContinueButton();

        for (ControlState controlState : model.getDefinition().getStates()) {
            machineDisplay.addVisualControlState(controlState);
        }
        for (Transition transition : model.getDefinition().getTransitions()) {
            machineDisplay.addVisualTransition(transition, false);
            transitionTable.addRow(transition);
        }
        machineDisplay.redrawStates();
    }

    //Executes chosen transition and updates interface.
    private void executeTransition(Transition transition, int totalChildren) {
        ConfigurationContext previousState = checkIfPresentInHistory(transition);
        if (previousState != null) {
            previousState.markInPath(true);
            loadConfigurationContext(previousState);
        } else {
            pda.executeTransition(transition, totalChildren);
        }
        requestInterfaceUpdate();
        machineDisplay.focusTransition(transition);
        transitionTable.select(transition, true);
    }

    //checks if transition has been executed before. If it has return the
    //ConfigurationContext, else return null.
    private ConfigurationContext checkIfPresentInHistory(Transition transition) {
        ArrayList content = new ArrayList(pda.getStack().getStackContent());
        int headPosition = pda.getTape().getHeadPosition();
        if (transition.getConfiguration().getInputSymbol() != '/') {
            headPosition++;
        }
        if (transition.getConfiguration().getTopElement() != '/') {
            content.remove(content.size() - 1);
        }
        if (transition.getAction().getElementToPush() != '/') {
            content.add(transition.getAction().getElementToPush());
        }
        return pda.getExecutionTree().getCurrent().hasChild(transition.getAction().getNewState(), content, headPosition);
    }

    //loads a ConfigurationContext instance into current PDAMachine and updates interface
    private void loadConfigurationContext(ConfigurationContext context) {
        pda.loadConfigurationContext(context);
        requestInterfaceUpdate();

    }

    //removes user interaction from the PDAMachine and interface
    private void removeUserInteractionWithPDA(boolean toRemove) {
        actionBar.setDisable(toRemove);
        inputBox.setDisable(toRemove);
        ControllerFactory.toolBarPartialController.disableToolbarButtons(toRemove);

    }

    public boolean isCurrentSavedInMemory() {
        return pda != null && pda.isSavedInMemory();
    }

    public boolean isLoaded() {
        return pda != null;
    }


    public void switchToQuickDefinition() {
        ViewFactory.globalPane.setCenter(ViewFactory.quickDefinition);
    }

    public void updateVisualLabel(Transition transition) {
        machineDisplay.update(transition);
    }

    public void openNonDeterministicMode() {
        ControllerFactory.toolBarPartialController.setNonDeterministicModeButtonText("Close Non-Deterministic Mode");
        transitionTable.clearSelection(pda.getCurrentState() != null);
        machineDisplay.unhighlightAllTransitions();
        Set<Transition> transitions = pda.getNonDeterministicTransitions();
        machineDisplay.highlightTransitionBatch(transitions);
        transitionTable.highlightTransitions(transitions);
        inDeterministicMode = true;
    }


    public boolean isInNonDeterministicMode() {
        return inDeterministicMode;
    }


    /**
     * A method which changes the source of the transition's initial state to the new state
     *
     * @param oldSourceState old initial state
     * @param newSourceState new initial state
     * @param transition     the transition to modify
     */
    public void updateTransition(ControlState oldSourceState, ControlState newSourceState, Transition transition) {
        pda.moveTransitionSourceState(oldSourceState, newSourceState, transition);
    }

    //gets control states in string format
    private ObservableList<String> getControlStatesInStringFormat() {
        ObservableList<String> data =
                FXCollections.observableArrayList();
        for (ControlState controlState :
                pda.getDefinition().getStates()) {
            data.add(controlState.getLabel());
        }
        return data;
    }


    //A method which opens up step-run non-deterministic output dialog, where
    //there is more than one solution
    private void openStepRunNonDeterministicOutputDialog() {
        try {
            removeUserInteractionWithPDA(true);
            transitionTable.clearSelection(false);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("/layouts/output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + pda.getTape().getOriginalWord() + "\" is " + (solutionBuffer.size() > 0 ? " accepted!" : " rejected!"));
            String sequence = "No" + (solutionBuffer.isEmpty() ? "" : " more") + " solutions found from the paths that have been searched";
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(sequence);

            ((Button) currentOutputWindow.lookup("#bAnotherInput")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });

            currentOutputWindow.lookup("#bPreviousBranch").setManaged(false);
            ((Button) currentOutputWindow.lookup("#bContinue")).setOnAction(event -> {
                closeOutputDialogIfPresent();
            });

            if (solutionBuffer.size() > 0) {
                solutionBuffer.add(sequence);
                solutionPointer = solutionBuffer.size() - 1;
                Button bPreviousSolution = (Button) currentOutputWindow.lookup("#bPreviousSolution");
                Button bNextSolution = (Button) currentOutputWindow.lookup("#bNextSolution");
                bNextSolution.setManaged(true);
                bPreviousSolution.setManaged(true);
                bNextSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer < solutionBuffer.size() - 1));
                bPreviousSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer > 0));
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
            }

            spPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A method which opens up step-run deterministic output dialog, where
    //there is only at most one solution
    public void openStepRunDeterministicOutputDialog(boolean isAccepted) {
        try {
            String sequence = "Configuration sequence :  " + pda.getCurrentExecutionSequence(isAccepted);
            if (isAccepted && !solutionBuffer.contains(sequence)) {
                solutionBuffer.add(sequence);
                solutionPointer = solutionBuffer.size() - 1;
            }
            removeUserInteractionWithPDA(true);
            transitionTable.clearSelection(false);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("/layouts/output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + pda.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected!"));
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(sequence);

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

            if (machineIsNonDeterministic) {
                ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + pda.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " not accepted for this path!"));
                Button bPreviousSolution = (Button) currentOutputWindow.lookup("#bPreviousSolution");
                Button bNextSolution = (Button) currentOutputWindow.lookup("#bNextSolution");
                bNextSolution.setManaged(true);
                bPreviousSolution.setManaged(true);
                bNextSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer < solutionBuffer.size() - 1));
                bPreviousSolution.setDisable(!(solutionBuffer.size() > 1 && solutionPointer > 0));
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
            }
            spPDARunnerPage.getChildren().add(currentOutputWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method which validates a tradition as defined by the user and adds it
     * the list of transitions, if its a valid one. If not then open error dialog.
     */
    public void openNewTransitionDialog() {
        try {
            stop();
            ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
            VBox currentAddTransitionDialog = FXMLLoader.load(getClass().getResource("/layouts/add_transition_page.fxml"));
            Button bAddTransition = (Button) currentAddTransitionDialog.lookup("#bAddTransition");
            Button bCancel = (Button) currentAddTransitionDialog.lookup("#bCancel");
            ComboBox<String> cbStates = (ComboBox<String>) currentAddTransitionDialog.lookup("#cbStates");
            TextField tfInputElement = (TextField) currentAddTransitionDialog.lookup("#tfInputElement");
            TextField tfElementToPop = (TextField) currentAddTransitionDialog.lookup("#tfElementToPop");
            ComboBox<String> cbResultingStates = (ComboBox<String>) currentAddTransitionDialog.lookup("#cbResultingStates");
            TextField tfElementToPush = (TextField) currentAddTransitionDialog.lookup("#tfElementToPush");
            ObservableList<String> controlStatesInStringFormat = getControlStatesInStringFormat();
            cbStates.setItems(controlStatesInStringFormat);
            cbResultingStates.setItems(controlStatesInStringFormat);
            ViewFactory.restrictTextFieldInput(tfInputElement, ".");
            ViewFactory.restrictTextFieldInput(tfElementToPop, ".");
            ViewFactory.restrictTextFieldInput(tfElementToPush, ".");
            bAddTransition.setOnAction(event -> {
                if (cbStates.getSelectionModel().isEmpty()) {
                    ViewFactory.showErrorDialog("No initial control state is chosen!", spPDARunnerPage);
                    return;
                }

                if (cbResultingStates.getSelectionModel().isEmpty()) {
                    ViewFactory.showErrorDialog("No Resulting control state is chosen!", spPDARunnerPage);
                    return;
                }

                String initialState = cbStates.getSelectionModel().getSelectedItem();
                String inputElement = tfInputElement.getText().trim().isEmpty() ? "/" : tfInputElement.getText();
                String elementToPop = tfElementToPop.getText().trim().isEmpty() ? "/" : tfElementToPop.getText();
                String resultingState = cbResultingStates.getSelectionModel().getSelectedItem();
                String elementToPush = tfElementToPush.getText().trim().isEmpty() ? "/" : tfElementToPush.getText();
                TransitionTableEntry transitionEntry = new TransitionTableEntry(initialState, inputElement, elementToPop, resultingState, elementToPush);
                if (transitionTable.getEntries().contains(transitionEntry)) {
                    ViewFactory.showErrorDialog("No duplicate transitions allowed!", spPDARunnerPage);
                } else {
                    Configuration configuration = new Configuration(ModelFactory.stateLookup(pda.getDefinition().getStates(), initialState), inputElement.charAt(0), elementToPop.charAt(0));
                    Action action = new Action(ModelFactory.stateLookup(pda.getDefinition().getStates(), resultingState), elementToPush.charAt(0));
                    Transition newTransition = new Transition(configuration, action);
                    transitionEntry.setTransition(newTransition);
                    pda.addTransition(newTransition);
                    machineDisplay.addVisualTransition(newTransition, true);
                    transitionTable.addRow(transitionEntry);
                    spPDARunnerPage.getChildren().remove(currentAddTransitionDialog);
                    actionBar.setDisable(pda.getCurrentState() == null);
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);

                }

            });

            bCancel.setOnAction(event -> {
                spPDARunnerPage.getChildren().remove(currentAddTransitionDialog);
                ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                actionBar.setDisable(pda.getCurrentState() == null);

            });


            spPDARunnerPage.getChildren().add(currentAddTransitionDialog);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A method which opens up instant-run output dialog
    private void openInstantRunResultsOutputDialog(boolean isAccepted, boolean hasSingleSolution) {
        try {
            removeUserInteractionWithPDA(true);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("/layouts/instant_run_output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + pda.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected! \n(all possible branches searched!)"));
            String output = "Configuration sequence :  " + (isAccepted && hasSingleSolution ? "No further solutions found!" : pda.getCurrentExecutionSequence(isAccepted));
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
                        moreSolutionsToBeFound = runInstantRunDFS(true, 20);
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

    //A method which opens the save dialog when the user accesses the save feature
    void openSaveDialog() {
        try {
            currentSaveWindow = FXMLLoader.load(getClass().getResource("/layouts/save_confirmation_page.fxml"));
            Button bSave = (Button) currentSaveWindow.lookup("#bSave");
            Button bClose = (Button) currentSaveWindow.lookup("#bClose");
            TextField tfName = (TextField) currentSaveWindow.lookup("#tfName");
            bClose.setOnAction(event -> {
                closeSaveDialogIfPresent();
            });

            bSave.setOnAction(event -> {
                if (!tfName.getText().trim().isEmpty()) {
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                    MemoryFactory.loadLibrary();
                    Definition definition = pda.getDefinition();
                    definition.setIdentifier(tfName.getText().trim());
                    if (!ModelFactory.checkForDefinitionOccurrence(definition)) {
                        pda.markAsSavedInMemory();
                        MemoryFactory.saveToLibrary(definition);
                        setPDATitle(definition.getIdentifier());
                        spPDARunnerPage.getChildren().remove(currentSaveWindow);
                    }
                }
            });

            spPDARunnerPage.getChildren().add(currentSaveWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A method which opens the transition options dialog when there is several transitions which the PDA machine can take
    // in step-run
    private void openTransitionOptionDialog(List<Transition> transitions) {
        try {
            removeUserInteractionWithPDA(true);
            currentChoiceWindow = FXMLLoader.load(getClass().getResource("/layouts/transition_selecter_page.fxml"));
            ScrollPane sp = new ScrollPane();
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setFitToWidth(true);
            BorderPane.setAlignment(sp, Pos.CENTER);
            VBox vbOptions = new VBox(5);
            vbOptions.setPadding(new Insets(5, 5, 5, 5));
            vbOptions.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            vbOptions.setId("vbOptionContainer");
            sp.setContent(vbOptions);
            ArrayList<ConfigurationContext> exploredChildren = pda.getExecutionTree().getCurrent().getExploredChildren();
            if (exploredChildren.size() > 0 && exploredChildren.size() == exploredChildren.get(0).getTotalSiblings()) {
                Button bPreviousBranch = (Button) currentChoiceWindow.lookup("#bPreviousBranch");
                bPreviousBranch.setManaged(true);
                bPreviousBranch.setOnAction(event -> {
                    closeOptionDialogIfPresent();
                    previousBranching();
                    if (currentChoiceWindow == null && pda.getTape().getStep() == 0) {
                        removeUserInteractionWithPDA(true);
                        Timeline timeline = new Timeline(new KeyFrame(
                                Duration.millis(1300),
                                ae -> openStepRunNonDeterministicOutputDialog()));

                        timeline.play();
                    }
                });
            }
            for (int i = 0; i < transitions.size(); i++) {
                BorderPane option = FXMLLoader.load(getClass().getResource("/layouts/transition_option.fxml"));
                ((Label) option.lookup("#lTransitionOption")).setText(Integer.toString(i + 1));
                ViewFactory.setLabelGraphic((Label) option.lookup("#lVisited"), "fa-tick", 10);
                ((Label) option.lookup("#lCurrentState")).setText("Current State : " + transitions.get(i).getConfiguration().getState().getLabel());
                ((Label) option.lookup("#lRead")).setText("Read : " + transitions.get(i).getConfiguration().getInputSymbol());
                ((Label) option.lookup("#lPush")).setText("Push : " + transitions.get(i).getAction().getElementToPush());
                ((Label) option.lookup("#lPop")).setText("Pop : " + transitions.get(i).getConfiguration().getTopElement());
                ((Label) option.lookup("#lResultingState")).setText("Resulting State : " + transitions.get(i).getAction().getNewState().getLabel());
                vbOptions.getChildren().add(option);
                int finalI = i;
                if (checkIfPresentInHistory(transitions.get(i)) != null) {
                    option.lookup("#lVisited").setVisible(true);
                }
                option.setOnMouseClicked(event -> {
                    closeOptionDialogIfPresent();
                    actionBar.setDisable(true);
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(100),
                            ae -> {
                                actionBar.setDisable(false);
                                executeTransition(transitions.get(finalI), transitions.size());
                                if (tape.isLastStep() && (pda.isAccepted() || pda.getPossibleTransitionsFromCurrent().isEmpty())) {
                                    actionBar.restrictToOnlyPlay();

                                }
                            }));

                    timeline.play();

                });
            }
            ((Button) currentChoiceWindow.lookup("#bRandomSelect")).setOnAction(event -> {
                Random randomGenerator = new Random();
                executeTransition(transitions.get(randomGenerator.nextInt(transitions.size())), transitions.size());
                closeOptionDialogIfPresent();
            });
            ((Button) currentChoiceWindow.lookup("#bTerminate")).setOnAction(event -> stop());
            currentChoiceWindow.setCenter(sp);
            spPDARunnerPage.getChildren().add(currentChoiceWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method which open confirmation dialog to confirm user's action
     * */
    public void openConfirmationDialog() {
        ViewFactory.showStandardDialog(spPDARunnerPage, false, "Alert :",
                "This action might lose all unsaved changes! Are you sure you want to proceed without saving!", event -> {
                    spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                    switchToQuickDefinition();
                },
                event -> {
                    spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                }, "Proceed", "Close");
    }

    /**
     * A method which closes non-deterministic mode and restores previouly highlighted states
     * and transitions
     */
    public void closeNonDeterministicModeIfPresent() {
        if (inDeterministicMode) {
            ControllerFactory.toolBarPartialController.setNonDeterministicModeButtonText("Open Non-Deterministic Mode");
            inDeterministicMode = false;
            machineDisplay.unhighlightAllTransitions();
            if (transitionTable.getHighlightedRowSaved() != -1) {
                transitionTable.highlightRow(transitionTable.getHighlightedRowSaved(), true);
            } else {
                transitionTable.clearSelection(false);
            }

            machineDisplay.focusState(pda.getCurrentState(), true);
        }

    }

    //a method which closes the transition option dialog
    private void closeOptionDialogIfPresent() {
        if (currentChoiceWindow != null) {
            spPDARunnerPage.getChildren().remove(currentChoiceWindow);
            removeUserInteractionWithPDA(false);
            currentChoiceWindow = null;
        }
    }

    //a method which closes the result dialog
    private void closeOutputDialogIfPresent() {
        if (currentOutputWindow != null) {
            spPDARunnerPage.getChildren().remove(currentOutputWindow);
            removeUserInteractionWithPDA(false);
            currentOutputWindow = null;
        }
    }

    //a method which closes the save dialog
    private void closeSaveDialogIfPresent() {
        if (currentSaveWindow != null) {
            spPDARunnerPage.getChildren().remove(currentSaveWindow);
            removeUserInteractionWithPDA(false);
            currentSaveWindow = null;
        }
    }

    // A method which sets the PDA title if the currently loaded PDA has a title and displays it
    private void setPDATitle(String PDATitle) {
        if (PDATitle != null) {
            lPDATitle.getParent().setManaged(true);
            lPDATitle.setText(PDATitle);
        } else {
            lPDATitle.getParent().setManaged(false);
        }
    }

}

