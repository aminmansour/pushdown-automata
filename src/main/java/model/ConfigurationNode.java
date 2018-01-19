package model;

import java.util.ArrayList;

public class ConfigurationNode {

    //stadard fields
    private Configuration element;
    private ConfigurationNode parent;
    private ArrayList<ConfigurationNode> children;

    //flag fields
    private boolean isVisited;
    private int totalChildren;


    public ConfigurationNode(Configuration element,ConfigurationNode parent) {
        this.element = element;
        this.parent = parent;
        this.children = new ArrayList<>();

        this.isVisited = true;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public Configuration getElement() {
        return element;
    }

    public void setElement(Configuration element) {
        this.element = element;
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

    public ConfigurationNode addChild(Configuration configuration){
        ConfigurationNode configNode = new ConfigurationNode(configuration, this);
        children.add(configNode);
        return configNode;
    }

    public void setTotalChildren(int totalChildren) {
        this.totalChildren = totalChildren;
    }
}
