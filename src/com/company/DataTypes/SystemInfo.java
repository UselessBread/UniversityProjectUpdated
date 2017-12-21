package com.company.DataTypes;

public class SystemInfo {
    private String article;
    private String subsystem;
    private String deviceName;
    private String mode;
    private String delay;
    private String relation;
    public String getArticle(){return article;}
    public String getSubsystem(){
        return subsystem;
    }
    public String getDeviceName(){
        return deviceName;
    }
    public String getMode(){
        return mode;
    }
    public String getDelay(){
        return delay;
    }
    public String getRelation(){
        return relation;
    }
    public SystemInfo(){
        this.article="";
        this.subsystem="";
        this.deviceName="";
        this.mode="";
        this.delay="";
        this.relation="";
    }
    public SystemInfo(String str){
        String[] splittedStr=str.split(" ");
        article=splittedStr[0];
        subsystem=splittedStr[1];
        deviceName=splittedStr[2];
        mode = splittedStr[3];
        if(splittedStr.length==6) {
            delay = splittedStr[4];
            relation = splittedStr[5];
        }
        else if(splittedStr.length==5){
            delay=splittedStr[4];
            relation="";
        }
        else if(splittedStr.length==4){
            delay="";
            relation="";
        }

    }
    public SystemInfo(String article,String subsystem,String deviceName,String mode,String delay,String relation){
        this.article=article;
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    public void setAll(String article,String subsystem,String deviceName,String mode,String delay,String relation){
        this.article=article;
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    public String getInfoWithoutDelay(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode();
    }
    public String getInfoWithoutDelayAndMode(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName();
    }

    public String getAllInfo(){
        return getArticle()+" "+ getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" "+getRelation();
    }
    public String getInfoWithRelation(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" После "+getRelation();
    }
    public String getInfoWithoutRelation(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay();
    }
}
