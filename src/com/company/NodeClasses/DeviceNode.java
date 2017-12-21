package com.company.NodeClasses;

import javax.swing.tree.DefaultMutableTreeNode;

public class DeviceNode {
    private String articleName;
    private String deviceName;
    private String subsystemNodeName;
    private DefaultMutableTreeNode deviceNode;

    public DeviceNode(String articleName,String deviceName,String subsystemNodeName,DefaultMutableTreeNode deviceNode){
        this.articleName=articleName;
        this.deviceName=deviceName;
        this.subsystemNodeName=subsystemNodeName;
        this.deviceNode=deviceNode;
    }

    public String getArticleName() {
        return articleName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public DefaultMutableTreeNode getDeviceNode() {
        return deviceNode;
    }

    public String getSubsystemNodeName() {
        return subsystemNodeName;
    }

}
