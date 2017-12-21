package com.company.DataTypes;

import java.util.ArrayList;
import java.util.Vector;

public class ArticleBackup {

    private String openedAlgorithmsContent;
    private Vector<SystemInfo> systemInfoVector;
    private ArrayList<String> usingDevices;
    public ArticleBackup(String openedAlgorithmsContent,Vector<SystemInfo> systemInfoVector,ArrayList<String>usedDevices){
        this.openedAlgorithmsContent=openedAlgorithmsContent;
        this.systemInfoVector=systemInfoVector;
        this.usingDevices =usedDevices;
    }
    public ArticleBackup(){
        openedAlgorithmsContent="";
        systemInfoVector=new Vector<>();
    }

    public String getOpenedAlgorithmsContent() {
        return openedAlgorithmsContent;
    }

    public Vector<SystemInfo> getSystemInfoVector() {
        return systemInfoVector;
    }

    public ArrayList<String> getUsingDevices() {
        return usingDevices;
    }

    public void setOpenedAlgorithmsContent(String openedAlgorithmsContent) {
        this.openedAlgorithmsContent = openedAlgorithmsContent;
    }

    public void setSystemInfoVector(Vector<SystemInfo> systemInfoVector) {
        this.systemInfoVector = systemInfoVector;
    }
}
