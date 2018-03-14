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
import javafx.scene.control.*;
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
    private boolean machineIsNonDeterministic;


    public PDARunnerController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        tape = new TapeDisplayController();
        vbPDAInteraction.getChildren().add(tape.getTapeViewGenerated());

        initializeUserActionBarAndSetUpActionListeners();

        inputBox = new UserInputBoxController();
        vbLeftBar.getChildren().add(inputBox.getInputBox());
        inputBox.setButtonStepRunAction(event -> stepRun());

        inputBox.setButtonInstantRunAction(event -> instantRun());

        machineDisplay = new MachineDisplayController();
        hbCentre.getChildren().add(machineDisplay.getCanvas());

        stack = new StackController();
        hbCentre.getChildren().add(stack.getStackGenerated());

        transitionTable = new TransitionTableController(new ArrayList<>());
        vbLeftBar.getChildren().add(0, transitionTable.getTransitionTableGenerated());
        actionBar.setDisable(true);


    }

    private void instantRun() {
//        if (!inputBox.getInput().isEmpty()) {
            closeDeterministicModeIfPresent();
        actionBar.setDisable(false);
        setUpInstantRunEnvironment();
            runInstantRunDFS(false);
            transitionTable.clearSelection(false);
//        }

    }

    private void setUpInstantRunEnvironment() {
        solutionBuffer = new ArrayList<>();
        solutionPointer = 0;
        moreSolutionsToBeFound = true;
        model.setCurrentState(null);
        model.getTape().clear();
        model.getStack().clear();
        model.setCurrentState(model.getDefinition().getInitialState());
        machineIsNonDeterministic = model.isNonDeterministic();
        model.getTape().setHeadIndex(-1);
        model.getTape().setStep(0);
        ArrayList<Character> inputSymbols = new ArrayList<>(inputBox.getInput().length());
        for (char c : inputBox.getInput().toCharArray()) {
            inputSymbols.add(c);
        }
        model.getTape().setInput(inputSymbols);
        model.setCurrentState(model.getDefinition().getInitialState());
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<>(), 0, 0, model.getPossibleTransitionsFromCurrent().size());
    }

    private boolean runInstantRunDFS(boolean isAlternativeSearch) {
        ArrayList<MemoryPlaceHolder> memory = new ArrayList<>();
        if (isAlternativeSearch) {
            prevAlt();
        }
        int loops = 0;
        boolean stuck = false;
        boolean toBacktrack = false;
        while (true) {

            ArrayList<Transition> transitions = model.getPossibleTransitionsFromCurrent();
            if (model.hasAccepted()) {
                openInstantRunResultsOutputDialog(true, false);
                return true;
            } else if (transitions.size() == 0 || toBacktrack || stuck) {
                if (stuck) {
                    loops = 0;
                    stuck = false;
                }
                toBacktrack = false;
                while (true) {
                    prevAlt();
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
                java.util.Collections.shuffle(transitions);
                transitionsLoop:
                for (Transition transition : transitions) {
                    ConfigurationNode previousState = checkIfPresentInHistory(transition);
                    if (previousState == null) {
                        ArrayList<Character> stackContentBefore = new ArrayList(model.getStack().getStackContent());
                        String remaingInputBefore = model.getTape().getRemainingInputAsString();
                        if (previousState != null) {
                            previousState.markInPath(true);
                            model.setCurrentState(previousState.getState());
                            model.getTape().setStep(previousState.getStep());
                            model.getTape().setHeadIndex(previousState.getHeadPosition());
                            model.getStack().loadState(previousState.getStackState());
                            stuck = true;
                        } else {
//                            ArrayList<Character> stack = new ArrayList<Character>(model.getStack().getStackContent());
//                            ArrayList<Character> tapeContent = new ArrayList<>(model.getTape().getRemainingInput());
                            model.executeTransition(transition, transitions.size());
//                            boolean skipSymbol = transition.getConfiguration().getInputSymbol() == '/';
//                            int headPosition = model.getTape().getHeadPosition();
//                            if (!skipSymbol) {
//                                tapeContent.remove(0);
//                                headPosition++;
//                            }
//
//                            Character topElement = transition.getConfiguration().getTopElement();
//                            if (topElement != '/') {
//                                stack.remove(stack.size()-1);
//                            }
//                            Character elementToPush = transition.getAction().getElementToPush();
//                            if (elementToPush != '/') {
//                                stack.add(elementToPush);
//                            }
//                            ControlState currentState = transition.getAction().getNewState();
                            MemoryPlaceHolder e = new MemoryPlaceHolder(model.getStack().getStackContent(), model.getCurrentState(), model.getTape().getRemainingInput(), model.getTape().getStep(), model.getTape().getHeadPosition(), model.getHistory().getCurrent());
                            boolean toAddToMemory = true;
                            for (int i = 0; i < memory.size(); i++) {
                                MemoryPlaceHolder m = memory.get(i);
                                if (e.isSameAs(memory.get(i))) {
                                    if (m.numberOfVisits == 2) {
                                        m.numberOfVisits = 1;
                                        if (m.current.getParent() != null) {
                                            ConfigurationNode prev = m.current.getParent();
                                            prev.markInPath(true);
                                            model.setCurrentState(prev.getState());
                                            model.getTape().setStep(prev.getStep());
                                            model.getTape().setHeadIndex(prev.getHeadPosition());
                                            model.getStack().loadState(prev.getStackState());
                                        } else {
                                            openInstantRunResultsOutputDialog(false, false);

                                        }

                                        break transitionsLoop;
                                    }
                                    m.increment();
                                    toAddToMemory = false;
                                    break;
                                }
                            }
                            if (toAddToMemory) {
                                memory.add(e);
                            }
                        }
                        toBacktrack = false;
                        if (model.getHistory().getCurrent().hasLooped(transition.getConfiguration().getState(), stackContentBefore, remaingInputBefore)) {
                            loops++;
                            if (loops == 4) {
                                stuck = true;
                                while (loops != 1) {
                                    previous();
                                    loops--;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void prevAlt() {
        if (model.getTape().getStep() > 0) {

            model.getHistory().getCurrent().markInPath(false);

            ConfigurationNode current = model.getHistory().getCurrent().getParent();

            model.getHistory().setCurrent(current);

            model.setCurrentState(current.getState());

            model.getTape().setStep(current.getStep());

            model.getTape().setHeadIndex(current.getHeadPosition());

            model.getStack().loadState(current.getStackState());


        }
    }

    private void stepRun() {
//        if (!inputBox.getInput().isEmpty()) {
        solutionBuffer = new ArrayList<>();
        solutionPointer = 0;
        moreSolutionsToBeFound = true;

        closeDeterministicModeIfPresent();
        machineDisplay.clearTransitionFocus();
        transitionTable.clearSelection(false);
        loadPDAWithInput();
        actionBar.setDisable(false);
//        }
    }

    private void loadPDAWithInput() {
        stop();
        model.setCurrentState(model.getDefinition().getInitialState());
        machineIsNonDeterministic = model.isNonDeterministic();
        machineDisplay.focusState(model.getCurrentState());
        tape.setTapeInput(inputBox.getInput());
        tape.setCurrentConfigurationLabel("( " + model.getCurrentState().getLabel() + " , " + model.getTape().getRemainingInputAsString() + " , " + model.getStack().getStackContentAsString() + " )");
        model.getStack().clear();
        stack.setUpStackContentAFresh();
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<>(), 0, 0, model.getPossibleTransitionsFromCurrent().size());
    }

    public void openTransitionOptionDialog(List<Transition> transitions) {
        try {
            removeUserInteractionWithPDA(true);
            currentChoiceWindow = FXMLLoader.load(getClass().getResource("../layouts/transition_selecter_page.fxml"));
            ScrollPane sp = new ScrollPane();
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setFitToWidth(true);
            BorderPane.setAlignment(sp, Pos.CENTER);
            VBox vbOptions = new VBox(5);
            vbOptions.setPadding(new Insets(5, 5, 5, 5));
            vbOptions.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            vbOptions.setId("vbOptionContainer");
            sp.setContent(vbOptions);
            ArrayList<ConfigurationNode> exploredChildren = model.getHistory().getCurrent().getExploredChildren();
            if (exploredChildren.size() > 0 && exploredChildren.size() == exploredChildren.get(0).getTotalSiblings()) {
                Button bPreviousBranch = (Button) currentChoiceWindow.lookup("#bPreviousBranch");
                bPreviousBranch.setManaged(true);
                bPreviousBranch.setOnAction(event -> {
                    closeOptionDialogIfPresent();
                    previousBranching();
                    if (currentChoiceWindow == null && model.getTape().getStep() == 0) {
                        removeUserInteractionWithPDA(true);
                        Timeline timeline = new Timeline(new KeyFrame(
                                Duration.millis(1300),
                                ae -> openStepRunNonDeterministicOutputDialog()));

                        timeline.play();
                    }
                });
            }
            for (int i = 0; i < transitions.size(); i++) {
                BorderPane option = FXMLLoader.load(getClass().getResource("../layouts/transition_option.fxml"));
                ((Label) option.lookup("#lTransitionOption")).setText(Integer.toString(i + 1));
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
                                executeTransition(transitions.get(finalI), transitions.size(), true);
                                if (tape.isLastStep() && (model.hasAccepted() || model.getPossibleTransitionsFromCurrent().isEmpty())) {
                                    actionBar.restrictToOnlyPlay();

                                }
                            }));

                    timeline.play();

                });
            }
            ((Button) currentChoiceWindow.lookup("#bRandomSelect")).setOnAction(event -> {
                Random randomGenerator = new Random();
                executeTransition(transitions.get(randomGenerator.nextInt(transitions.size())), transitions.size(), true);
                closeOptionDialogIfPresent();
            });
            ((Button) currentChoiceWindow.lookup("#bTerminate")).setOnAction(event -> stop());
            currentChoiceWindow.setCenter(sp);
            spPDARunnerPage.getChildren().add(currentChoiceWindow);

        } catch (IOException e) {
            e.printStackTrace();
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
                openStepRunResultsOutputDialog(true);
                break;
            } else if (transitions.size() == 0) {
                openStepRunResultsOutputDialog(false);
                break;
            } else if (transitions.size() == 1) {
                executeTransition(transitions.get(0), transitions.size(), true);
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
            openStepRunResultsOutputDialog(true);
        } else if (transitions.size() == 0) {
            openStepRunResultsOutputDialog(false);
        } else if (transitions.size() == 1) {
            executeTransition(transitions.get(0), transitions.size(), true);
            if (tape.isLastStep() && ((model.hasAccepted() || model.getPossibleTransitionsFromCurrent().isEmpty()))) {
                actionBar.restrictToOnlyPlay();
            }
        } else {
            openTransitionOptionDialog(transitions);
        }

    }

    public void previous(){
        closeDeterministicModeIfPresent();
        if (model.getTape().getStep() > 0) {
            machineDisplay.clearTransitionFocus();
            actionBar.setDisable(false);
            model.getHistory().getCurrent().markInPath(false);
            ConfigurationNode current = model.getHistory().getCurrent().getParent();
            transitionTable.clearSelection(false);
            loadConfigurationState(current);
        }
    }

    public void startAgain() {
        closeDeterministicModeIfPresent();
        machineDisplay.clearTransitionFocus();
        tape.redo();
        model.setCurrentStateToInitial();
        stack.setUpStackContentAFresh();
        stack.clean();
        updateCurrentStateAndConfigurationField();
        tape.setStackTopLabel("-");
        model.createComputationHistoryStore(model.getDefinition().getInitialState(), new ArrayList<>(), 0, 0, model.getPossibleTransitionsFromCurrent().size());

    }

    public void stop(){
        closeDeterministicModeIfPresent();
        machineDisplay.clearTransitionFocus();
        closeOptionDialogIfPresent();
        actionBar.setDisable(true);
        machineDisplay.clearTransitionFocus();
        model.setCurrentState(null);
        tape.clear();
        tape.setCurrentStateLabel(model.getDefinition().getInitialState().getLabel());
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
        inputBox.clear();
        transitionTable.setStates(model.getDefinition().getStates());
        ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
        ControllerFactory.homeController.showContinueButton();
        machineDisplay.clear();

        for (ControlState controlState : model.getDefinition().getStates()) {
            machineDisplay.addVisualControlState(controlState);

        }

        for (Transition transition : model.getDefinition().getTransitions()) {
            machineDisplay.addVisualTransition(transition, false);
            transitionTable.addRow(transition);
        }

        machineDisplay.orderStatesInScreen();
    }

    public void openStepRunNonDeterministicOutputDialog() {
        try {
            removeUserInteractionWithPDA(true);
            transitionTable.clearSelection(false);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("../layouts/output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (solutionBuffer.size() > 0 ? " accepted!" : " rejected!"));
            String sequence = "No solutions found from the paths that have been searched";
            ((Label) currentOutputWindow.lookup("#lConfigurationSequence")).setText(sequence);

            ((Button) currentOutputWindow.lookup("#bAnotherInput")).setOnAction(event -> {
                closeOutputDialogIfPresent();
                stop();
                inputBox.clearAndFocus();
            });

            currentOutputWindow.lookup("#bPreviousBranch").setManaged(false);
            currentOutputWindow.lookup("#bContinue").setManaged(false);

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

    public void openStepRunResultsOutputDialog(boolean isAccepted) {
        try {
            String sequence = "Configuration sequence :  " + model.getCurrentSequence(isAccepted);
            if (isAccepted && !solutionBuffer.contains(sequence)) {
                solutionBuffer.add(sequence);
                solutionPointer = solutionBuffer.size() - 1;
            }
            removeUserInteractionWithPDA(true);
            transitionTable.clearSelection(false);
            currentOutputWindow = FXMLLoader.load(getClass().getResource("../layouts/output_page.fxml"));
            ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " rejected!"));
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
                ((Label) currentOutputWindow.lookup("#lOutput")).setText("Output  : word \"" + model.getTape().getOriginalWord() + "\" is " + (isAccepted ? " accepted!" : " not accepted for this path!"));
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

    public void openNewTransitionDialog() {
        try {
            stop();
            ControllerFactory.toolBarPartialController.disableToolbarButtons(true);
            VBox currentAddTransitionDialog = FXMLLoader.load(getClass().getResource("../layouts/add_transition_page.fxml"));
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
                TransitionEntry transitionEntry = new TransitionEntry(initialState, inputElement, elementToPop, resultingState, elementToPush);
                if (transitionTable.getEntries().contains(transitionEntry)) {
                    ViewFactory.showErrorDialog("No duplicate transitions allowed!", spPDARunnerPage);
                } else {
                    Configuration configuration = new Configuration(ModelFactory.checkForStateOccurrence(model.getDefinition().getStates(), initialState), inputElement.charAt(0), elementToPop.charAt(0));
                    Action action = new Action(ModelFactory.checkForStateOccurrence(model.getDefinition().getStates(), resultingState), elementToPush.charAt(0));
                    Transition newTransition = new Transition(configuration, action);
                    transitionEntry.setTransition(newTransition);
                    model.addTransition(newTransition);
                    machineDisplay.addVisualTransition(newTransition, true);
                    transitionTable.addRow(transitionEntry);
                    spPDARunnerPage.getChildren().remove(currentAddTransitionDialog);
                    actionBar.setDisable(model.getCurrentState() == null);
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);

                }

            });

            bCancel.setOnAction(event -> {
                spPDARunnerPage.getChildren().remove(currentAddTransitionDialog);
                ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                actionBar.setDisable(model.getCurrentState() == null);

            });


            spPDARunnerPage.getChildren().add(currentAddTransitionDialog);

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

    private void executeTransition(Transition transition, int totalChildren, boolean displayChanges) {
        ConfigurationNode previousState = checkIfPresentInHistory(transition);
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
        if (displayChanges) {
            transitionTable.select(transition, true);
            machineDisplay.focusTransition(transition);
        }
    }

    private ConfigurationNode checkIfPresentInHistory(Transition transition) {
        ArrayList content = new ArrayList(model.getStack().getStackContent());
        int headPosition = model.getTape().getHeadPosition();
        if (transition.getConfiguration().getInputSymbol() != '/') {
            headPosition++;
        }
        if (transition.getConfiguration().getTopElement() != '/') {
            content.remove(content.size() - 1);
        }
        if (transition.getAction().getElementToPush() != '/') {
            content.add(transition.getAction().getElementToPush());
        }
        return model.getHistory().getCurrent().getChildIfFound(transition.getAction().getNewState(), content, headPosition);
    }

    private void loadConfigurationState(ConfigurationNode record) {
        model.getHistory().setCurrent(record);
        model.setCurrentState(record.getState());
        machineDisplay.focusState(record.getState());
        tape.setHeadPositionAndStep(record.getHeadPosition(), record.getStep());
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
        ControllerFactory.toolBarPartialController.disableToolbarButtons(toRemove);

    }

    public boolean isCurrentSavedInMemory() {
        return model != null && model.isSavedInMemory();
    }



    public void openSaveDialog() {
        try {
            VBox currentSaveWindow = FXMLLoader.load(getClass().getResource("../layouts/save_confirmation_page.fxml"));
            Button bSave = (Button) currentSaveWindow.lookup("#bSave");
            Button bClose = (Button) currentSaveWindow.lookup("#bClose");
            TextField tfName = (TextField) currentSaveWindow.lookup("#tfName");
            bClose.setOnAction(event -> {
                spPDARunnerPage.getChildren().remove(currentSaveWindow);
                ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
            });

            bSave.setOnAction(event -> {
                if (!tfName.getText().trim().isEmpty()) {
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                    Memory.load();
                    Definition definition = model.getDefinition();
                    definition.setIdentifier(tfName.getText().trim());
                    if (!ModelFactory.checkForDefinitionOccurrence(definition)) {
                        model.markAsSavedInMemory();
                        Memory.save(definition);
                        spPDARunnerPage.getChildren().remove(currentSaveWindow);
                    }
                }
            });

            spPDARunnerPage.getChildren().add(currentSaveWindow);

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
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                    switchToQuickDefinition();
                },
                event -> {
                    spPDARunnerPage.getChildren().remove(spPDARunnerPage.getChildren().size() - 1);
                    ControllerFactory.toolBarPartialController.disableToolbarButtons(false);
                }, "Proceed", "Close");
    }

    public void switchToQuickDefinition() {
        ViewFactory.globalPane.setCenter(ViewFactory.quickDefinition);
        BorderPane.setAlignment(ViewFactory.quickDefinition, Pos.CENTER);
        BorderPane.setMargin(ViewFactory.quickDefinition, new Insets(0, 0, 0, 0));
    }

    public void updateVisualLabel(Transition transition) {
        machineDisplay.update(transition);
    }

    public void openDeterministicMode() {

        ControllerFactory.toolBarPartialController.setNonDeterministicModeButtonText("Close Non-Deterministic Mode");
        transitionTable.clearSelection(model.getCurrentState() != null);
        machineDisplay.clearTransitionFocus();
        Set<Transition> transitions = model.getNonDeterministicTransitions();
        machineDisplay.highlightDeterministicTransitions(transitions);
        transitionTable.highlightTransitions(transitions);
        inDeterministicMode = true;
    }

    public void closeDeterministicModeIfPresent() {
        if (inDeterministicMode) {
            ControllerFactory.toolBarPartialController.setNonDeterministicModeButtonText("Open Non-Deterministic Mode");
            inDeterministicMode = false;
            machineDisplay.unhighlightAllTransitions();
            if (transitionTable.getHighlightedRowSaved() != -1) {
                transitionTable.highlightRow(transitionTable.getHighlightedRowSaved(), true);
            } else {
                transitionTable.clearSelection(false);
            }

            machineDisplay.focusState(model.getCurrentState());
        }

    }

    public boolean isInDeterministicMode() {
        return inDeterministicMode;
    }


    public void updateModel(ControlState oldSourceState, ControlState newSourceState, Transition transition) {
        model.moveTransitionToNewSource(oldSourceState, newSourceState, transition);
    }

    class MemoryPlaceHolder {
        int step;
        int tapeHead;
        private ArrayList<Character> stackContent;
        private ControlState controlState;
        private ArrayList<Character> inputState;
        private ConfigurationNode current;
        private int numberOfVisits;

        public MemoryPlaceHolder(ArrayList<Character> stackContent, ControlState controlState, ArrayList<Character> inputState, int step, int tapeHead, ConfigurationNode current) {
            this.stackContent = stackContent;
            this.controlState = controlState;
            this.inputState = inputState;
            this.current = current;
            numberOfVisits = 1;
        }

        public int increment() {
            return numberOfVisits++;
        }

        public boolean isSameAs(MemoryPlaceHolder m) {
            if (stackContent.size() != m.stackContent.size()) {
                return false;
            }
            for (int i = 0; i < stackContent.size(); i++) {
                if (stackContent.get(i) != m.stackContent.get(i)) {
                    return false;
                }
            }

            if (inputState.size() != m.inputState.size()) {
                return false;
            }
            for (int i = 0; i < inputState.size(); i++) {
                if (inputState.get(i) != m.inputState.get(i)) {
                    return false;
                }
            }

            return controlState == m.controlState;
        }
    }

    private ObservableList<String> getControlStatesInStringFormat() {
        ObservableList<String> data =
                FXCollections.observableArrayList();
        for (ControlState controlState :
                model.getDefinition().getStates()) {
            data.add(controlState.getLabel());
        }
        return data;
    }
}
