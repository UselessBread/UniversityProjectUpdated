package com.company.NodeClasses;

import javax.swing.tree.DefaultMutableTreeNode;

public class TopNode {
    private String name;
    private DefaultMutableTreeNode topNode;
    public TopNode(String n,DefaultMutableTreeNode node){
        name=n;
        topNode=node;
    }
    public String getName(){
        return name;
    }
    public DefaultMutableTreeNode getTopNode(){
        return topNode;
    }
}
