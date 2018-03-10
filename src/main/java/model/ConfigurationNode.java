package model;

import java.util.ArrayList;

public class ConfigurationNode {


    //stadard fields
    private ArrayList<Character> stackState;

    private int headPosition;


    private ConfigurationNode parent;
    private int step;
    private ArrayList<ConfigurationNode> exploredChildren;

    //flag fields
    private int totalSiblings;
    private boolean isInPath;
    private ControlState controlState;


    public ConfigurationNode(ControlState controlState, ConfigurationNode parent, ArrayList<Character> stackState, int headPosition, int step, String remainingInput, int totalSiblings) {
        this.parent = parent;
        this.step = step;
        this.exploredChildren = new ArrayList<>();
        this.stackState = stackState;
        this.headPosition = headPosition;
        this.totalSiblings = totalSiblings;
        this.controlState = controlState;
    }


    public ConfigurationNode getParent() {
        return parent;
    }

    public ArrayList<ConfigurationNode> getExploredChildren() {
        return exploredChildren;
    }


    public void addChild(ConfigurationNode child) {
        exploredChildren.add(child);
    }

    public ControlState getState() {
        return controlState;
    }

    public ArrayList<Character> getStackState() {
        return stackState;
    }


    public int getHeadPosition() {
        return headPosition;
    }

    public void markInPath(boolean isInPath) {
        this.isInPath = isInPath;
    }

    public ConfigurationNode getChildIfFound(ControlState newState, ArrayList<Character> stackContent, int step) {
        for (ConfigurationNode configuration : exploredChildren) {
            if (newState.equals(configuration.controlState) && step == configuration.headPosition && compareStackContent(configuration.stackState, stackContent)) {
                return configuration;
            }
        }
        return null;
    }


    public boolean hasLooped(ControlState newState, ArrayList<Character> stackContent, String remaingInput) {
        return newState.equals(controlState) && remaingInput.equals(remaingInput) && compareStackContent(stackState, stackContent);
    }

    private boolean compareStackContent(ArrayList<Character> stackState, ArrayList<Character> stackContent) {

        if (stackState.size() != stackContent.size()) {
            return false;
        }

        for (int i = 0; i < stackState.size(); i++) {
            if (stackState.get(i) != stackContent.get(i)) {
                return false;
            }
        }
        return true;
    }

    public ConfigurationNode getNextChildInPath() {
        for (ConfigurationNode configurationNode : exploredChildren) {
            if (configurationNode.isInPath) {
                return configurationNode;
            }
        }
        return null;
    }


    public String getStackStateInStringFormat() {
        if (stackState.isEmpty()) {
            return " - ";
        }
        String output = "";
        for (Character character : stackState) {
            output += character;
        }
        return output;
    }


    public int getTotalSiblings() {
        return totalSiblings;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
