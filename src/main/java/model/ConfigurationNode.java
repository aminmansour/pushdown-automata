package model;

import java.util.ArrayList;

public class ConfigurationNode {


    //stadard fields
    private ArrayList<Character> stackState;
    private int headPosition;


    private ConfigurationNode parent;
    private ArrayList<ConfigurationNode> children;

    //flag fields
    private boolean moreChildren;
    private boolean isVisited;
    private boolean isInPath;
    private ControlState controlState;


    public ConfigurationNode(ControlState controlState, ConfigurationNode parent, ArrayList<Character> stackState, int headPosition, boolean moreChildren) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.stackState = stackState;
        this.headPosition = headPosition;
        this.isVisited = true;
        this.moreChildren = moreChildren;
        this.controlState = controlState;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }



    public ConfigurationNode getParent() {
        return parent;
    }

    public ArrayList<ConfigurationNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ConfigurationNode> childrenConfigurations) {
        children = childrenConfigurations;
    }


    public void addChild(ConfigurationNode child) {
        children.add(child);
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

}
