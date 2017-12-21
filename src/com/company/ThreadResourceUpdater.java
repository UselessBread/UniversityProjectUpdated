package com.company;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class ThreadResourceUpdater implements Runnable,WindowListener {
    private String mode;
    private JPanel mainPanel;
    private JPanel resourcePanel=new JPanel();
    private static JFrame mainFrame;
    private DB DBConnection=new DB();
    private Vector<JTextField> textFieldVector=new Vector<>();
    ThreadResourceUpdater(String mode){
        this.mode=mode;

    }
    ThreadResourceUpdater(){

    }
    @Override
    public void run() {
        resourcePanel.setLayout(new GridLayout(0,6));
        if(mode.equals(MainWindow.getADD_RESOURCE())) {
            updateArticleResources();
        }
        if(mode.equals(MainWindow.getCangeModeConsumption())){
            changeModeConsumption();
        }

    }
    private void setUpResourceAddingUI(String article){
        mainPanel=new JPanel();
        mainPanel.setOpaque(true);
        mainFrame=new JFrame("Изменение ресурсов в "+article+"");
        mainFrame.setContentPane(mainPanel);
        mainFrame.addWindowListener(this);
        mainFrame.setLocationRelativeTo(MainWindow.getMainFrame());
        mainFrame.setPreferredSize(new Dimension(800,500));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    private void updateArticleResources(){
        //Set up interface with three columns: resource name,resource val,resource measure
        JTree tree=MainWindow.getTree();
        int x= MainWindow.getPopupX();
        int y= MainWindow.getPopupY();
        TreePath path=tree.getPathForLocation(x,y);
        String pathString=path.toString().replace("[","");
        pathString=pathString.replace("]","");
        String[] splittedPathString=pathString.split(",");
        String article=splittedPathString[1].trim();
        setUpResourceAddingUI(article);
        Vector<String> resourceNames=DBConnection.getResourcesNames(article);
        Vector<String> articleResources=DBConnection.getArticleResources(article);
        Vector<String> resourcesMeasurements=DBConnection.getArticleMeasurements(article);
        for(int i=0;i<articleResources.size();i++){
            JTextField resourceNameField=new JTextField(resourceNames.get(i));
            JLabel resourceNameFieldLabel=new JLabel("Имя ресурса"+(i+1));
            resourceNameFieldLabel.setLabelFor(resourceNameField);
            JTextField resourceValueField=new JTextField(articleResources.get(i));
            JTextField resourceMeasurement=new JTextField(resourcesMeasurements.get(i));
            JLabel resourceValueFieldLabel=new JLabel("Значение ресурса"+(i+1));
            resourceValueFieldLabel.setLabelFor(resourceValueField);
            JLabel resourceMeasurementLabel=new JLabel("Еденицы измерения");
            resourceMeasurementLabel.setLabelFor(resourceMeasurement);
            resourcePanel.add(resourceNameFieldLabel);
            resourcePanel.add(resourceNameField);
            resourcePanel.add(resourceValueFieldLabel);
            resourcePanel.add(resourceValueField);
            resourcePanel.add(resourceMeasurementLabel);
            resourcePanel.add(resourceMeasurement);
            textFieldVector.add(resourceNameField);
            textFieldVector.add(resourceValueField);
            textFieldVector.add(resourceMeasurement);
        }
        JButton addButton=new JButton ("Добавить");
        resourcePanel.add(addButton);
        addButton.addActionListener(e -> {
            JTextField resourceNameField=new JTextField();
            JLabel resourceNameFieldLabel=new JLabel("Имя ресурса");
            resourceNameFieldLabel.setLabelFor(resourceNameField);
            JTextField resourceValueField=new JTextField();
            JTextField resourceMeasurement=new JTextField();
            JLabel resourceValueFieldLabel=new JLabel("Значение ресурса");
            resourceValueFieldLabel.setLabelFor(resourceValueField);
            JLabel resourceMeasurementLabel=new JLabel("Еденицы измерения");
            resourceMeasurementLabel.setLabelFor(resourceMeasurement);
            resourcePanel.add(resourceNameFieldLabel);
            resourcePanel.add(resourceNameField);
            resourcePanel.add(resourceValueFieldLabel);
            resourcePanel.add(resourceValueField);
            resourcePanel.add(resourceMeasurementLabel);
            resourcePanel.add(resourceMeasurement);
            resourcePanel.remove(addButton);
            resourcePanel.add(addButton);
            textFieldVector.add(resourceNameField);
            textFieldVector.add(resourceValueField);
            textFieldVector.add(resourceMeasurement);
            updateWindow(mainFrame.getHeight(),mainFrame.getWidth());
        });
        JButton confirmButton=new JButton("Изменить");
        JPanel confirmButtonPanel=new JPanel();
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(e -> {
            DBConnection.changeArticleResources(article,textFieldVector);
            mainFrame.dispose();
            //Replace resources with new
            DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode) treeModel.getRoot();
            int i = 0;
            Object articleNode;
            while (!((articleNode = treeModel.getChild(rootNode, i)).toString().equals(article))) {
                if (rootNode.getChildCount()>1&&i == rootNode.getChildCount()-1) {
                    JOptionPane.showMessageDialog(mainFrame, "error in first cycle", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                i++;
            }
            DefaultMutableTreeNode resourcesNode=(DefaultMutableTreeNode) ((DefaultMutableTreeNode)articleNode).getChildAt(1);
            ArrayList<DefaultMutableTreeNode> pastNodes=new ArrayList<>();
            if(resourcesNode.getChildCount()!=0) {
                for (i = 0; i < resourcesNode.getChildCount(); i++) {
                    pastNodes.add((DefaultMutableTreeNode)resourcesNode.getChildAt(i));

                }
            }
            for(DefaultMutableTreeNode node:pastNodes){
                treeModel.removeNodeFromParent(node);
            }

            Vector<String> resourceNamesUpdate=DBConnection.getResourcesNames(article);
            Vector<String> articleResourcesUpdate=DBConnection.getArticleResources(article);
            Vector<String> resourcesMeasurementsUpdate=DBConnection.getArticleMeasurements(article);
            for(i=0;i<resourceNamesUpdate.size();i++) {
                String resultString = resourceNamesUpdate.get(i) + ": " + articleResourcesUpdate.get(i) + " " + resourcesMeasurementsUpdate.get(i);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(resultString);
                treeModel.insertNodeInto(newNode,resourcesNode,resourcesNode.getChildCount());
                //TODO:update modes tomorrow
            }
            ArrayList<DefaultMutableTreeNode> newModeNodes=new ArrayList<>();
            DefaultMutableTreeNode subsystemsNode=(DefaultMutableTreeNode) ((DefaultMutableTreeNode) articleNode).getChildAt(0);
            Vector<String> subsystems=DBConnection.queryToArticle(article);
            for(String subsystem:subsystems){
                Vector<String>devices=DBConnection.queryToSubsys(article,subsystem);
                devices.removeAll(Collections.singleton(null));
                for(String device:devices){

                    Vector<String> modes=DBConnection.queryToDevice(article,subsystem,device);
                    for(String mode:modes){
                        DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(mode);
                        newModeNodes.add(newNode);
                    }
                }
            }
            int count=0;

            for(i=0;i<subsystemsNode.getChildCount();i++){
                DefaultMutableTreeNode subsystemNode=(DefaultMutableTreeNode) subsystemsNode.getChildAt(i);
                for(int j=0;j<subsystemNode.getChildCount();j++){
                    DefaultMutableTreeNode deviceNode=(DefaultMutableTreeNode) subsystemNode.getChildAt(j);
                    int childCount=deviceNode.getChildCount();
                    while(deviceNode.getChildCount()>0) {
                        DefaultMutableTreeNode modeNode = (DefaultMutableTreeNode) deviceNode.getChildAt(0);
                        treeModel.removeNodeFromParent(modeNode);
                    }
                    for(int k=0;k<childCount;k++){
                        treeModel.insertNodeInto(newModeNodes.get(count), deviceNode, deviceNode.getChildCount());
                        count++;
                    }
                }
            }
        });

        mainPanel.add(resourcePanel);
        mainPanel.add(confirmButtonPanel);
    }
    private void setUpModeConsumptionChangingUI(String article,String subsystem,String device,String mode){
        mainPanel=new JPanel();
        mainPanel.setOpaque(true);
        mainFrame=new JFrame("Изменение ресурсов в "+article+" "+subsystem+" "+device+" "+mode);
        mainFrame.setContentPane(mainPanel);
        mainFrame.addWindowListener(this);
        mainFrame.setLocationRelativeTo(MainWindow.getMainFrame());
        mainFrame.setPreferredSize(new Dimension(600,200));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    private void changeModeConsumption(){
        JTree tree=MainWindow.getTree();
        int x= MainWindow.getPopupX();
        int y= MainWindow.getPopupY();
        TreePath path=tree.getPathForLocation(x,y);
        String pathString=path.toString().replace("[","");
        pathString=pathString.replace("]","");
        String[] splittedPathString=pathString.split(",");
        String mode=splittedPathString[5].trim();
        String device=splittedPathString[4].trim();
        String subsystem=splittedPathString[3].trim();
        String article=splittedPathString[1].trim();
        setUpModeConsumptionChangingUI(article,subsystem,device,mode);
        Vector<String> resourceNames=DBConnection.getResourcesNames(article);
        Vector<String> resourceValues=DBConnection.queryToDevice(article,subsystem,device);
        if(resourceValues.size()>0&&resourceValues.get(0).equals(Integer.toString(DBConnection.SQL_EXCEPTION))){
            resourceValues=DBConnection.queryToSensor(article,subsystem,device);
        }
        Vector<String> resourceMeasurement=DBConnection.getArticleMeasurements(article);
        //init window with choose resource consumption params
        String[] splittedMode=mode.split("\t");

        String modeName=splittedMode[0].trim();

        JTextField modeNameTextField=new JTextField(modeName);
        JLabel modeNameLabel=new JLabel("Имя режима:");
        modeNameLabel.setLabelFor(modeNameTextField);
        mainPanel.add(modeNameLabel);
        mainPanel.add(modeNameTextField);
        textFieldVector.add(modeNameTextField);
        String currentMode="";
        for(String str:resourceValues) {
            if(str.contains(modeName))
                currentMode = str;
        }
        String[] splittedCurrentMode=currentMode.split("\t");
        for(int i=0;i<resourceNames.size();i++){
            JTextField textField=new JTextField(splittedCurrentMode[(i+1)]);
            JLabel textLabel=new JLabel(resourceNames.get(i)+":");
            textLabel.setLabelFor(textField);
            JLabel measurementLabel=new JLabel(resourceMeasurement.get(i));
            measurementLabel.setLabelFor(textField);
            mainPanel.add(textLabel);
            mainPanel.add(textField);
            mainPanel.add(measurementLabel);
            textFieldVector.add(textField);
        }
        JButton submitButton=new JButton("Подтвердить");
        mainPanel.add(submitButton);
        submitButton.addActionListener(e->{
            DBConnection.changeModeConsumption(article,subsystem,device,modeName,textFieldVector);
            mainFrame.dispose();

            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
            int i = 0;
            Object articleNode;
            while (!((articleNode = treeModel.getChild(rootNode, i)).toString().equals(article))) {
                if (rootNode.getChildCount()>1&&i == rootNode.getChildCount() - 1) {
                    JOptionPane.showMessageDialog(mainFrame, "error in first cycle", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                i++;
            }
            DefaultMutableTreeNode systemNode = (DefaultMutableTreeNode) treeModel.getChild(articleNode, 0);
            Object subsystemNode;
            i = 0;
            while (!(subsystemNode = treeModel.getChild(systemNode, i)).toString().equals(subsystem)) {
                if (i == systemNode.getChildCount() - 1 && systemNode.getChildCount() > 1) {
                    JOptionPane.showMessageDialog(mainFrame, "error in second cycle", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                i++;
            }
            Object deviceNode;
            i = 0;
            DefaultMutableTreeNode temp = (DefaultMutableTreeNode) subsystemNode;
            if (((DefaultMutableTreeNode) subsystemNode).getChildCount() > 0) {
                while (!((deviceNode = treeModel.getChild(subsystemNode, i)).toString().equals(device))) {
                    if (((DefaultMutableTreeNode) subsystemNode).getChildCount()>1&&i == ((DefaultMutableTreeNode) subsystemNode).getChildCount() - 1) {
                        JOptionPane.showMessageDialog(mainFrame, "error in third cycle", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                    i++;
                }

            } else {
                deviceNode=((DefaultMutableTreeNode) subsystemNode).getChildAt(0);
                }
            Object modeNode;
            DefaultMutableTreeNode deviceMutableNode=(DefaultMutableTreeNode)deviceNode;
            i=0;
            while(!((modeNode=deviceMutableNode.getChildAt(i)).toString().equals(mode))){
                if(deviceMutableNode.getChildCount()>1&&i==deviceMutableNode.getChildCount()-1){
                    JOptionPane.showMessageDialog(mainFrame, "error in fourth cycle", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                i++;
            }

            String newModeName=textFieldVector.get(0).getText()+"\t";
            for(i=1;i<textFieldVector.size();i++){
                double tempDouble=Double.parseDouble(textFieldVector.get(i).getText());
                int tempInt=(int)tempDouble;
                newModeName+=tempInt+"\t";
            }
            DefaultMutableTreeNode newModeNode=new DefaultMutableTreeNode(newModeName);

            treeModel.insertNodeInto(newModeNode,deviceMutableNode,deviceMutableNode.getChildCount());
            treeModel.removeNodeFromParent(((DefaultMutableTreeNode) modeNode));
        });
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }
    @Override
    public void windowClosing(WindowEvent e) {
        Thread.currentThread().interrupt();
    }
    @Override
    public void windowClosed(WindowEvent e) {

    }
    @Override
    public void windowIconified(WindowEvent e) {

    }
    @Override
    public void windowDeiconified(WindowEvent e) {

    }
    @Override
    public void windowActivated(WindowEvent e) {

    }
    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    private void updateWindow(int height,int width){
        Dimension dimension;
        if(mainFrame.getSize().height<height){
            dimension=new Dimension(width,mainFrame.getSize().height+1);
        }
        else {
            dimension = new Dimension(width, mainFrame.getSize().height - 1);
        }
        mainFrame.setSize(dimension);
    }
    private void reset(){
        mode="";
        mainPanel.removeAll();
        resourcePanel.removeAll();
        textFieldVector.clear();
    }
    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
