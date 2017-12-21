package com.company.NodeClasses;

import javax.swing.tree.DefaultMutableTreeNode;

public class SubsystemNode {
    private String topNodeName;
    private String name;
    private DefaultMutableTreeNode subsystemNode;
    public SubsystemNode(String topNodeName,String n,DefaultMutableTreeNode subsystemNode){
        this.topNodeName=topNodeName;
        name=n;
        this.subsystemNode=subsystemNode;
    }

    public String getName() {
        return name;
    }

    public DefaultMutableTreeNode getSubsystemNode() {
        return subsystemNode;
    }

    public String getTopNodeName() {
        return topNodeName;
    }
}
