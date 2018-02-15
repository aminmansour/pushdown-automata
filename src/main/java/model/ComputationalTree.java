package model;

import java.util.ArrayList;

public class ComputationalTree {

    private int size;
    private ConfigurationNode root;
    private ConfigurationNode current;

    public ComputationalTree(ConfigurationNode root) {
        this.root = root;
        current = root;
        this.size = 1;
    }


    public ConfigurationNode getRoot() {
        return root;
    }

    public ConfigurationNode getCurrent() {
        return current;
    }

    public void goToPreviousActionBranching() {
        ConfigurationNode newCurrent = null;
        while (current.getParent() != null ) {
            current = current.getParent();
            if (current.getExploredChildren().size() > 1) {
                break;
            }
        }
    }


    public ArrayList<ConfigurationNode> preOrderTraversalFromRoot(){
        ArrayList<ConfigurationNode> preOrderCollection = new ArrayList<>();
        preOrderTraversal(root, preOrderCollection);
        return preOrderCollection;
    }

    private void preOrderTraversal(ConfigurationNode node,ArrayList<ConfigurationNode> preOrderCollection){
        preOrderCollection.add(node);
        if (node.getExploredChildren().isEmpty()) {
            return;
        }else{
            for (ConfigurationNode configNode : node.getExploredChildren()) {
                if (configNode.isVisited()) {
                    preOrderTraversal(configNode, preOrderCollection);
                }
            }
        }
    }


//    private void setRoot(Configuration root){
//        this.root = new ConfigurationNode(root,null);
//        this.current = this.root;
//    }


    private boolean checkIfReadyExplored(Transition transition) {
        for (ConfigurationNode config : current.getExploredChildren()) {
//            if(config.getElement().getTopElement() == transition.getAction().getTopElement()
//                    && config.getElement().getInputSymbol() == configuration.getInputSymbol() &&
//                    config.getElement().getState()==configuration.getState()){
//
//            }
        }
        return false;
    }


    public void setCurrent(ConfigurationNode current) {
        this.current = current;
    }

    @Override
    public String toString() {
        ConfigurationNode current = getCurrent();
        int i = 1;
        while (current != null) {
            current = current.getParent();
            i++;
        }
        return super.toString();
    }
}
