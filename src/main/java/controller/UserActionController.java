package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import view.ViewFactory;

import java.io.IOException;

/**
 * Controller which is in charge of the user action bar view
 */
public class UserActionController {

    //components
    private final Label lNext;
    private final Label lPrevious;
    private final Label lStartAgain;
    private final Label lNextBranch;
    private final Label lPreviousBranch;
    private final Label lStop;
    private HBox actionBarView;

    /**
     * A constructor for a UserActionController instance
     */
    public UserActionController(){
        try {
            actionBarView = FXMLLoader.load(getClass().getResource("/layouts/user_action_bar_partial.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lNext =  (Label) actionBarView.lookup("#lNext");
        lPrevious =  (Label) actionBarView.lookup("#lPrevious");
        lStartAgain =  (Label) actionBarView.lookup("#lStartAgain");
        lNextBranch = (Label) actionBarView.lookup("#lNextBranch");
        lPreviousBranch = (Label) actionBarView.lookup("#lPreviousBranch");
        lStop = (Label) actionBarView.lookup("#lStop");

        ViewFactory.setLabelGraphic(lNext, "fa-next", 14);
        ViewFactory.setLabelGraphic(lPrevious, "fa-previous", 14);
        ViewFactory.setLabelGraphic(lNextBranch, "fa-next-branch", 14);
        ViewFactory.setLabelGraphic(lPreviousBranch, "fa-previous-branch", 14);
        ViewFactory.setLabelGraphic(lStop, "fa-stop", 14);
        ViewFactory.setLabelGraphic(lStartAgain, "fa-redo", 14);
    }

    public HBox getActionBar(){
        return actionBarView;
    }

    public void setButtonNextAction(EventHandler<MouseEvent> action){
        lNext.setOnMouseClicked(action);
    }


    public void setButtonStartAgain(EventHandler<MouseEvent> action){
        lStartAgain.setOnMouseClicked(action);
    }


    public void setButtonPreviousAction(EventHandler<MouseEvent> action){
        lPrevious.setOnMouseClicked(action);
    }


    public void setButtonNextBranchAction(EventHandler<MouseEvent> action){
        lNextBranch.setOnMouseClicked(action);
    }


    public void setButtonPreviousBranchAction(EventHandler<MouseEvent> action){
        lPreviousBranch.setOnMouseClicked(action);
    }

    public void setButtonStopAction(EventHandler<MouseEvent> action){
        lStop.setOnMouseClicked(action);
    }

    /**
     * A method which disables user interaction with the action bar
     *
     * @param toDisable a boolean value determining whether to disable or enable user interaction
     */
    public void setDisable(boolean toDisable) {
        lStartAgain.setDisable(toDisable);
        lStop.setDisable(toDisable);
        lNext.setDisable(toDisable);
        lNext.setText("Next");
        lPrevious.setDisable(toDisable);
        lPreviousBranch.setDisable(toDisable);
        lNextBranch.setDisable(toDisable);
    }

    /**
     * A method which restricts all buttons except for the play button
     */
    public void restrictToOnlyPlay() {
        lNext.setDisable(false);
        lNext.setText("Finalize");
        lPrevious.setDisable(true);
        lPreviousBranch.setDisable(true);
        lNextBranch.setDisable(true);
        lStartAgain.setDisable(true);
        lStop.setDisable(true);
    }


}
