package com.company;

import com.company.DataTypes.SystemInfo;

import javax.swing.*;
import java.util.ArrayList;

import static com.company.MainWindow.*;

public class ThreadRunWindow extends Thread {
    private JTextArea executingArea=new JTextArea(10,80);
    private ArrayList<String> usingDevices=new ArrayList<>();

    public void run() {
        executingArea.setEditable(false);
        JPanel main=new JPanel();
        JFrame runWindowFrame=new JFrame("Executing");
        JScrollPane textPane=new JScrollPane(executingArea);
        main.add(textPane);
        main.setOpaque(true);
        runWindowFrame.setContentPane(main);
        runWindowFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        runWindowFrame.pack();
        runWindowFrame.setVisible(true);
        try {
            for (SystemInfo systemInfo : MainWindow.getSystemInfoVector()) {
                if (systemInfo.getDelay().length() == 0) {
                    recount(systemInfo);
                    executingArea.append(systemInfo.getAllInfo()+"\n");
                    usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());
                }
                if (systemInfo.getDelay().length() > 0 && systemInfo.getRelation().length() > 0) {
                    String[] delay = systemInfo.getDelay().split("/");
                    long time = convertToMilliseconds(delay);
                    Thread.sleep(time);
                    recount(systemInfo);
                    executingArea.append(systemInfo.getAllInfo()+"\n");
                    usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());

                }
                if (systemInfo.getDelay().length() > 0 && systemInfo.getRelation().length() == 0) {
                    String delay[] = systemInfo.getDelay().split("/");
                    long time = convertToMilliseconds(delay);
                    Thread.sleep(time);
                    recount(systemInfo);
                    executingArea.append(systemInfo.getAllInfo()+"\n");
                    usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());

                }
            }
        }catch(InterruptedException interruptedEX){}
        Thread.currentThread().interrupt();
    }

    private void recount(SystemInfo systemInfo) {
        //На основаниии количества элементов массива после сплита текста из минтра русерсов, узнать, сколько ресурсов содержит данное
        //изделие. Изделие вытащить из systemInfo, найти тэг этого изделия(если не существует, создать запись) и поменять значения занимаемых ресурсов
        if (usingDevices.contains(systemInfo.getInfoWithoutDelayAndMode())) {
            int index = usingDevices.indexOf(systemInfo.getInfoWithoutDelayAndMode());
            SystemInfo usedDevice = MainWindow.getSystemInfoVector().get(index);
            String[] usingMode = usedDevice.getMode().split("\t");
            String[] currentMode = systemInfo.getMode().split("\t");
            if (currentMode[0].trim().toUpperCase().equals("ВЫКЛ")) {
                //replace with container
                double prevResourceUsage1 = Double.parseDouble(usingMode[1]);
                double prevResourceUsage2 = Double.parseDouble(usingMode[2]);
                double prevResourceUsage3 = Double.parseDouble(usingMode[3]);
                String resources = resourceMonitor.getText();
                String[] tempRes = resources.split(" ");
                double currentResourceUsage1 = Double.parseDouble(tempRes[0].split("/")[0]);
                double currentResourceUsage2 = Double.parseDouble(tempRes[1].split("/")[0]);
                double currentResourceUsage3 = Double.parseDouble(tempRes[2].split("/")[0]);
                if (currentResourceUsage1 != 0)
                    currentResourceUsage1 = currentResourceUsage1 - prevResourceUsage1;
                if (currentResourceUsage2 != 0)
                    currentResourceUsage2 = currentResourceUsage2 - prevResourceUsage2;
                if (currentResourceUsage3 != 0)
                    currentResourceUsage3 = currentResourceUsage3 - prevResourceUsage3;
                resourceMonitor.setText(currentResourceUsage1 + "/" + allResources.get(0) + " " +
                        currentResourceUsage2 + "/" + allResources.get(1) + " " +
                        currentResourceUsage3 + "/" + allResources.get(2));
            }
        }
        if (!usingDevices.contains(systemInfo.getInfoWithoutDelayAndMode())) {//else replacement
            String[] currentMode = systemInfo.getMode().split("\t");
            String[] tempResources = resourceMonitor.getText().split(" ");
            double tempUsage1 = Double.parseDouble(tempResources[0].split("/")[0]);
            double tempUsage2 = Double.parseDouble(tempResources[1].split("/")[0]);
            double tempUsage3 = Double.parseDouble(tempResources[2].split("/")[0]);
            double currentResourceUsage1 = Double.parseDouble(currentMode[1]) + tempUsage1;
            double currentResourceUsage2 = Double.parseDouble(currentMode[2]) + tempUsage2;
            double currentResourceUsage3 = Double.parseDouble(currentMode[3]) + tempUsage3;
            resourceMonitor.setText(currentResourceUsage1 + "/" + allResources.get(0) + " " +
                    currentResourceUsage2 + "/" + allResources.get(1) + " " +
                    currentResourceUsage3 + "/" + allResources.get(2));

        }
    }
    private int convertToMilliseconds(String[] delay){
        int delayVal;
        String delayMetrics=delay[1];
        delayVal=Integer.parseInt(delay[0]);
        if(delayMetrics.equals("Часы")){
            return delayVal*3600000;
        }
        if (delayMetrics.equals("Минуты")) {
            return delayVal*60000;
        }
        if (delayMetrics.equals("Секунды")) {
            return delayVal*1000;
        }
        return delayVal;
    }
    private void writeToUsedResources(ArrayList<String> usedResources){

    }
}
