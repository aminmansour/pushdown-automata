package model;

import java.util.ArrayList;

public class ComputationalTree {

    private int size;
    private ConfigurationNode root;
    private ConfigurationNode current;

    public ComputationalTree() {
        this.size = 0;
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
            if(current.getChildren().size()>1){
                break;
            }
        }
    }

    public void addChildren(ConfigurationNode nextConfigNode,ArrayList<ConfigurationNode> children){
            current = nextConfigNode;
            nextConfigNode.getParent();
            nextConfigNode.setVisited(true);
            current.setChildren(children);


    }

    public ArrayList<ConfigurationNode> preOrderTraversalFromRoot(){
        ArrayList<ConfigurationNode> preOrderCollection = new ArrayList<>();
        preOrderTraversal(root, preOrderCollection);
        return preOrderCollection;
    }

    private void preOrderTraversal(ConfigurationNode node,ArrayList<ConfigurationNode> preOrderCollection){
        preOrderCollection.add(node);
        if(node.getChildren().isEmpty()) {
            return;
        }else{
            for (ConfigurationNode configNode : node.getChildren()) {
                if (configNode.isVisited()) {
                    preOrderTraversal(configNode, preOrderCollection);
                }
            }
        }
    }

    public void addChildToCurrent(Configuration newChild,int totalChildren){
        current = current.addChild(newChild);
        current.setTotalChildren(totalChildren);
        current.setVisited(true);
        size++;
    }

    private void setRoot(Configuration root){
        this.root = new ConfigurationNode(root,null);
        this.current = this.root;
    }


    private boolean checkIfReadyExplored(Transition transition) {
        for(ConfigurationNode config : current.getChildren()){
//            if(config.getElement().getTopElement() == transition.getAction().getTopElement()
//                    && config.getElement().getInputSymbol() == configuration.getInputSymbol() &&
//                    config.getElement().getState()==configuration.getState()){
//
//            }
        }
        return false;
    }







}
