package com.company;


import com.company.DataTypes.TextFieldAndDouble;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

public class ThreadDBUpdater extends Thread
implements ActionListener,WindowListener{
    private DB DBConnection=new DB();

    private final static String ADD_ARTICLE_COMMAND ="Добавить изделие";
    private final static String ADD_SUBSYSTEM_TO_EXISTING_COMMAND ="Добавить бортовую аппаратуру к существующему изделию";
    private final static String ADD_DEVICE_TO_EXISTING_COMMAND ="Добавить прибор к существующей бортовой аппаратуре";
    private final static String ADD_MODE_TO_EXISTING_COMMAND ="Добавить режим к существующему устройству";
    private final static String DEVICE="Устройство";
    private final static String SENSOR="Датчик";
    private JFrame DBUpdaterFrame;
    private JPanel mainPanel,startPanel;
    //private JScrollPane resourcesPanelScrollPane=new JScrollPane();
    //Для контроля за вводимыми значениями и получением данных из полей
    private Vector<TextFieldAndDouble> resourceTextFieldAndMaxVal=new Vector<>();
    private int state;
    private String selectedArticle,selectedSubsystem,selectedDevice;
    public void run(){
        setUI();

    }
    private void setUI(){
        mainPanel=new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
        startPanel=new JPanel();
        JRadioButton addArticle=new JRadioButton(ADD_ARTICLE_COMMAND);
        addArticle.setActionCommand(ADD_ARTICLE_COMMAND);
        addArticle.addActionListener(this);
        JRadioButton addSubsystemToExisting=new JRadioButton(ADD_SUBSYSTEM_TO_EXISTING_COMMAND);
        addSubsystemToExisting.setActionCommand(ADD_SUBSYSTEM_TO_EXISTING_COMMAND);
        addSubsystemToExisting.addActionListener(this);
        JRadioButton addDeviceToExisting=new JRadioButton(ADD_DEVICE_TO_EXISTING_COMMAND);
        addDeviceToExisting.setActionCommand(ADD_DEVICE_TO_EXISTING_COMMAND);
        addDeviceToExisting.addActionListener(this);
        JRadioButton addModeToExisting=new JRadioButton(ADD_MODE_TO_EXISTING_COMMAND);
        addModeToExisting.setActionCommand(ADD_MODE_TO_EXISTING_COMMAND);
        addModeToExisting.addActionListener(this);
        ButtonGroup radioButtonGroup=new ButtonGroup();
        radioButtonGroup.add(addArticle);
        radioButtonGroup.add(addSubsystemToExisting);
        radioButtonGroup.add(addDeviceToExisting);
        radioButtonGroup.add(addModeToExisting);
        startPanel.setLayout(new BoxLayout(startPanel,BoxLayout.Y_AXIS));
        startPanel.add(addArticle);
        startPanel.add(addSubsystemToExisting);
        startPanel.add(addDeviceToExisting);
        startPanel.add(addModeToExisting);
        mainPanel.add(startPanel);
        mainPanel.setPreferredSize(new Dimension(460,300));

        mainPanel.setOpaque(true);
        DBUpdaterFrame =new JFrame("Добавление");
        DBUpdaterFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DBUpdaterFrame.setContentPane(mainPanel);
        DBUpdaterFrame.addWindowListener(this);
        DBUpdaterFrame.setLocationRelativeTo(MainWindow.getMainFrame());
        DBUpdaterFrame.pack();
        DBUpdaterFrame.setVisible(true);

    }
    private void resetUI(){
        mainPanel.removeAll();
        mainPanel.add(startPanel);
        DBUpdaterFrame.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD_ARTICLE_COMMAND)){
            addArticle();
            updateWindow();

        }
        if(e.getActionCommand().equals(ADD_SUBSYSTEM_TO_EXISTING_COMMAND)){
            addSubsystem();
            updateWindow();
        }
        if(e.getActionCommand().equals(ADD_DEVICE_TO_EXISTING_COMMAND)){
            addDevice();
            updateWindow();
        }
        if(e.getActionCommand().equals(ADD_MODE_TO_EXISTING_COMMAND)){
            addMode();
            updateWindow();
        }
    }
    //TODO : Добавлять в дерево только, если добавление в бд прошло
    private void addArticle(){
        ArrayList<Integer> intList=new ArrayList<>(1);
        Vector<String> usedArticleNames=DBConnection.queryToArticles(intList);
        Integer lastIndex=intList.get(0);
        mainPanel.removeAll();
        JPanel enterPanel=new JPanel();
        JTextField newArticleName=new JTextField(20);
        JLabel nameLabel=new JLabel("Введите имя нового изделия");
        nameLabel.setLabelFor(newArticleName);
        JButton confirmButton=new JButton("Подтвердить");
        //Количество ресурсов, их наименования и значения. В зависимости от количества, определенное количество полей
        JTextField resourcesFieldsCount=new JTextField(5);
        JLabel resourcesFieldsCountLabel=new JLabel("Количество ресурсов");
        resourcesFieldsCountLabel.setLabelFor(resourcesFieldsCount);
        JPanel resourcesPanel=new JPanel();
        ArrayList<JTextField> resourceTextFields=new ArrayList<>();
        resourcesFieldsCount.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                resourcesPanel.removeAll();
                //resourcesPanelScrollPane.removeAll();
                //resourcesPanel.setLayout(new GridLayout(0,6));
                resourceTextFields.clear();
                int resourcesCount=Integer.parseInt(resourcesFieldsCount.getText());
                //В зависимостти от числа, добавть поля
                for(int i=0;i<resourcesCount;i++){
                    JLabel resourceLabel=new JLabel("Имя ресурса"+(i+1));
                    JTextField resourceTextField=new JTextField(20);
                    resourceLabel.setLabelFor(resourceTextField);
                    resourcesPanel.add(resourceLabel);
                    resourcesPanel.add(resourceTextField);
                    JTextField resourceValueField=new JTextField(6);
                    JLabel resourceValueLabel=new JLabel("Значние ресурса "+(i+1));
                    resourceValueLabel.setLabelFor(resourceValueField);
                    //Еденицы измерения
                    JTextField resourceMeasurment=new JTextField(6);
                    JLabel resourceMeasurmentLabel=new JLabel("Еденицы измерения");
                    resourceMeasurmentLabel.setLabelFor(resourceMeasurment);

                    resourcesPanel.add(resourceValueLabel);
                    resourcesPanel.add(resourceValueField);
                    resourcesPanel.add(resourceMeasurmentLabel);
                    resourcesPanel.add(resourceMeasurment);
                    resourceTextFields.add(resourceTextField);
                    resourceTextFields.add(resourceValueField);
                    resourceTextFields.add(resourceMeasurment);
                    updateWindow();
                }
                //resourcesPanelScrollPane=new JScrollPane(resourcesPanel);
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chosenName=newArticleName.getText();
                boolean matching=testForMatching(usedArticleNames,chosenName);
                if(!matching&&!chosenName.isEmpty()){

                    ArrayList<String> columns=new ArrayList<>();
                    ArrayList<Double> columnsValues=new ArrayList<>();
                    ArrayList<String> valuesMeasurements=new ArrayList<>();
                    for(int i=0;i<resourceTextFields.size();i+=3){
                        String columnName=resourceTextFields.get(i).getText();
                        Double columnValue=Double.parseDouble(resourceTextFields.get(i+1).getText());
                        String valueMeasurement =resourceTextFields.get(i+2).getText();
                        columns.add(columnName);
                        columnsValues.add(columnValue);
                        valuesMeasurements.add(valueMeasurement);
                    }
                    int result=DBConnection.addArticle(chosenName,lastIndex,columns,columnsValues,valuesMeasurements);
                    //Adding to tree place here

                    JTree tree=MainWindow.getTree();
                    DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
                    DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode) treeModel.getRoot();
                    treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName),rootNode,rootNode.getChildCount());
                    //Если rootNode.getChildCount==1, то выбрать первый элемент, если нет, то найти
                    Object articleNode;
                    if(rootNode.getChildCount()==1)
                        articleNode=rootNode.getChildAt(0);
                    else{
                        int i=0;
                        while (!((articleNode = treeModel.getChild(rootNode, i)).toString().equals(chosenName))) {
                            if (i == rootNode.getChildCount()-1) {
                                JOptionPane.showMessageDialog(DBUpdaterFrame, "error in first cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            i++;
                        }
                    }
                    DefaultMutableTreeNode subsystemNode=new DefaultMutableTreeNode("Бортовая аппаратура");
                    DefaultMutableTreeNode resourceNode=new DefaultMutableTreeNode("Ресурсы");
                    DefaultMutableTreeNode algorithmNode=new DefaultMutableTreeNode("Алгоритмы");
                    DefaultMutableTreeNode variantNode=new DefaultMutableTreeNode("Варианты");
                    treeModel.insertNodeInto(subsystemNode,((DefaultMutableTreeNode)articleNode),((DefaultMutableTreeNode)(articleNode)).getChildCount());
                    treeModel.insertNodeInto(resourceNode,((DefaultMutableTreeNode)articleNode),((DefaultMutableTreeNode)(articleNode)).getChildCount());
                    treeModel.insertNodeInto(algorithmNode,((DefaultMutableTreeNode)articleNode),((DefaultMutableTreeNode)(articleNode)).getChildCount());
                    treeModel.insertNodeInto(variantNode,algorithmNode,algorithmNode.getChildCount());
                    Vector<String> resourceNames=DBConnection.getResourcesNames(chosenName);
                    Vector<String> resourceValues=DBConnection.getArticleResources(chosenName);
                    Vector<String> resourceMeasurements=DBConnection.getArticleMeasurements(chosenName);
                    for(int i=0;i<resourceNames.size();i++){
                        String resultString=resourceNames.get(i)+": "+resourceValues.get(i)+" "+resourceMeasurements.get(i);
                        DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(resultString);
                        resourceNode.add(newNode);
                    }
                    verifyResult(result);
                    resetUI();
                }
            }
        });
        enterPanel.add(nameLabel);
        enterPanel.add(newArticleName);
        enterPanel.add(confirmButton);
        enterPanel.add(resourcesFieldsCountLabel);
        enterPanel.add(resourcesFieldsCount);
        mainPanel.add(enterPanel);
        //mainPanel.add(resourcesPanelScrollPane);
        mainPanel.add(resourcesPanel);
    }
    private void addSubsystem(){
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        MainWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Integer> intList=new ArrayList<>(1);
                String selectedArticle=resultList.getSelectedValue().toLowerCase();
                //Использованные имена подсистем
                Vector<String> result=DBConnection.queryToArticle(selectedArticle,intList);
                Integer lastIndex=intList.get(0);
                MainWindow.verifyResult(result,DBUpdaterFrame);
                mainPanel.removeAll();
                JPanel enterPanel=new JPanel();
                JTextField newSubsystemName=new JTextField(20);
                JLabel nameLabel=new JLabel("Имя:");
                nameLabel.setLabelFor(newSubsystemName);
                JButton submitButton=new JButton("Подтвердить");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String chosenName = newSubsystemName.getText();
                        boolean matching = testForMatching(result, chosenName);
                        if (!chosenName.isEmpty() && !matching) {
                            //Если все ок, то запрос
                            int result = DBConnection.addSubsystem(selectedArticle, chosenName, lastIndex);

                            JTree tree = MainWindow.getTree();
                            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                            int i = 0;
                            Object articleNode;
                            while (!((articleNode = treeModel.getChild(rootNode, i)).toString().equals(selectedArticle))) {
                                if (i == rootNode.getChildCount()-1) {
                                    JOptionPane.showMessageDialog(DBUpdaterFrame, "error in first cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                i++;
                            }
                            Object subsystemNode = treeModel.getChild(articleNode, 0);
                            treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName), (DefaultMutableTreeNode) subsystemNode, ((DefaultMutableTreeNode) subsystemNode).getChildCount());

                            verifyResult(result);
                            resetUI();
                        }

                        if (chosenName.isEmpty()) {
                            JOptionPane.showMessageDialog(DBUpdaterFrame, "Введите имя", "Введите имя", JOptionPane.ERROR_MESSAGE);
                        }
                        if (matching) {
                            JOptionPane.showMessageDialog(DBUpdaterFrame, "Имя", "Это имя уже занято", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                enterPanel.add(nameLabel);
                enterPanel.add(newSubsystemName);
                enterPanel.add(submitButton);
                mainPanel.add(enterPanel);
                updateWindow(DBUpdaterFrame.getHeight(),DBUpdaterFrame.getWidth());
            }
        });
        JPanel listPanel=new JPanel();
        listPanel.add(resultList);
        listPanel.add(nextButton);
        mainPanel.add(listPanel);
        DBUpdaterFrame.repaint();
    }
    //Combo box blocked on device
    private void addDevice(){
        final int QUERY_TO_ARTICLES=11;
        final int QUERY_TO_ARTICLE=12;
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        MainWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        state=QUERY_TO_ARTICLES;
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(state==QUERY_TO_ARTICLE){
                    //0-device,1-sensor
                    ArrayList<Integer> intList=new ArrayList<>();
                    selectedSubsystem=resultList.getSelectedValue().toLowerCase();
                    mainPanel.removeAll();
                    Vector<String> result=DBConnection.queryToSubsys(selectedArticle,selectedSubsystem,intList);
                    if(intList.size()==0){
                        intList.add(0);
                        intList.add(0);
                    }
                    MainWindow.verifyResult(result,DBUpdaterFrame);
                    JPanel enterPanel=new JPanel();
                    //Датчик или устройство
                    String[] checkBoxVariants={DEVICE,SENSOR};
                    JComboBox<String> deviceChooser=new JComboBox<>(checkBoxVariants);
                    deviceChooser.setVisible(false);

                    JTextField newDeviceName=new JTextField(20);
                    JLabel label=new JLabel("Имя прибора");
                    label.setLabelFor(newDeviceName);
                    JButton submitButton=new JButton("Подтвердить");
                    submitButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String chosenName=newDeviceName.getText();
                            boolean matching=testForMatching(result,chosenName);
                            if(!chosenName.isEmpty()&&!matching){
                                //Если все ок, то запрос
                                if(deviceChooser.getSelectedItem().equals(DEVICE)) {
                                    int result = DBConnection.addDevice(selectedArticle, selectedSubsystem, chosenName, intList.get(0));

                                    JTree tree=MainWindow.getTree();
                                    DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
                                    DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode) treeModel.getRoot();
                                    int i=0;
                                    Object articleNode;
                                    while(!((articleNode=treeModel.getChild(rootNode,i)).toString().equals(selectedArticle))){
                                        if(i==rootNode.getChildCount()-1) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame,"error in first cycle","Error",JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }
                                    DefaultMutableTreeNode systemNode=(DefaultMutableTreeNode) treeModel.getChild(articleNode,0);
                                    Object subsystemNode;
                                    i = 0;
                                    while (!(subsystemNode = treeModel.getChild(systemNode, i)).toString().toLowerCase().equals(selectedSubsystem)) {
                                        if (rootNode.getChildCount()>1&&i == rootNode.getChildCount()-1) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame, "error in second cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }

                                    treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName),(DefaultMutableTreeNode)subsystemNode,((DefaultMutableTreeNode) subsystemNode).getChildCount());

                                    verifyResult(result);
                                    resetUI();
                                }
                                if(deviceChooser.getSelectedItem().equals(SENSOR)){
                                    int result = DBConnection.addSensor(selectedArticle, selectedSubsystem, chosenName, intList.get(1));

                                    JTree tree=MainWindow.getTree();
                                    DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
                                    DefaultMutableTreeNode rootNode=(DefaultMutableTreeNode) treeModel.getRoot();
                                    int i=0;
                                    Object articleNode;
                                    while(!((articleNode=treeModel.getChild(rootNode,i)).toString().equals(selectedArticle))){
                                        if(i==rootNode.getChildCount()) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame,"error in first cycle","Error",JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }
                                    DefaultMutableTreeNode systemNode=(DefaultMutableTreeNode) treeModel.getChild(articleNode,0);
                                    Object subsystemNode;
                                    i = 0;
                                    while (!(subsystemNode = treeModel.getChild(systemNode, i)).toString().toLowerCase().equals(selectedSubsystem)) {
                                        if (i == systemNode.getChildCount()-1) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame, "error in second cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }

                                    treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName),(DefaultMutableTreeNode)subsystemNode,((DefaultMutableTreeNode) subsystemNode).getChildCount());

                                    verifyResult(result);
                                    resetUI();
                                }
                            }
                            if(chosenName.isEmpty()){
                                JOptionPane.showMessageDialog(DBUpdaterFrame,"Введите имя","Введите имя",JOptionPane.ERROR_MESSAGE);
                            }
                            if(matching){
                                JOptionPane.showMessageDialog(DBUpdaterFrame,"Имя","Это имя уже занято",JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    enterPanel.add(label);
                    enterPanel.add(newDeviceName);
                    enterPanel.add(deviceChooser);
                    enterPanel.add(submitButton);
                    mainPanel.add(enterPanel);
                    updateWindow(DBUpdaterFrame.getHeight(),DBUpdaterFrame.getWidth());

                }
                if(state==QUERY_TO_ARTICLES) {
                    selectedArticle = resultList.getSelectedValue().toLowerCase();
                    Vector<String> result = DBConnection.queryToArticle(selectedArticle);
                    MainWindow.verifyResult(result, DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_ARTICLE;
                }
            }
        });
        JPanel listPanel=new JPanel();
        listPanel.add(resultList);
        listPanel.add(nextButton);
        mainPanel.add(listPanel);
        DBUpdaterFrame.repaint();
        mainPanel.repaint();
    }
    private void addMode(){
        final int QUERY_TO_ARTICLES=11;
        final int QUERY_TO_ARTICLE=12;
        final int QUERY_TO_SUBSYSTEM=13;
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        MainWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        state=QUERY_TO_ARTICLES;
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateWindow(DBUpdaterFrame.getHeight(),DBUpdaterFrame.getWidth());
                if(state==QUERY_TO_SUBSYSTEM){
                    ArrayList<Integer> intList=new ArrayList<>();
                    selectedDevice=resultList.getSelectedValue().toLowerCase();
                    Vector<String> result=DBConnection.queryToDevice(selectedArticle,selectedSubsystem,selectedDevice,intList);
                    if(intList.size()==0){
                        result=DBConnection.queryToSensor(selectedArticle,selectedSubsystem,selectedDevice,intList);
                    }
                    MainWindow.verifyResult(result,DBUpdaterFrame);
                    Integer lastIndex=intList.get(0);
                    Vector<String> nameTest=new Vector<>();
                    for(String str:result){
                        String modeName=str.split("\t")[0];
                        nameTest.add(modeName);
                    }

                    JPanel enterPanel=new JPanel();

                    JTextField newModeName=new JTextField(20);
                    JLabel newModeNameLabel=new JLabel("Имя режима");
                    newModeNameLabel.setLabelFor(newModeName);
                    enterPanel.add(newModeNameLabel);
                    enterPanel.add(newModeName);
                    Vector<Vector<String>> resourceNamesAndValues=DBConnection.getResourcesCountAndNamesAndMaxValue(selectedArticle);
                    verifyResult(resourceNamesAndValues);
                    Vector<String> resourceNames=resourceNamesAndValues.get(0);

                    for(int i=0;i<resourceNames.size();++i){
                        JTextField resourceTextField=new JTextField(20);
                        JLabel resourceLabel=new JLabel(resourceNames.get(i));
                        resourceLabel.setLabelFor(resourceTextField);
                        enterPanel.add(resourceTextField);
                        enterPanel.add(resourceLabel);
                        Double maxVal=Double.parseDouble(resourceNamesAndValues.get(1).get(i));
                        TextFieldAndDouble temp=new TextFieldAndDouble(resourceTextField,maxVal);
                        resourceTextFieldAndMaxVal.add(temp);
                    }
                    JButton submitButton=new JButton("Подтвердить");
                    submitButton.addActionListener(new ActionListener() {
                        //Добавить matching, получать имена режимов из запроса
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Vector<String> modeNames = DBConnection.getModeNames(selectedArticle);
                            String chosenName = newModeName.getText();
                            boolean matching = testForMatching(modeNames, chosenName);
                            if (!chosenName.isEmpty()&&!matching) {
                                Vector<Double> resources = new Vector<>();
                                Vector<Double> maxValaues = new Vector<>();
                                int i=0;
                                boolean okFlag=true;
                                for (TextFieldAndDouble tf : resourceTextFieldAndMaxVal) {
                                    resources.add(Double.parseDouble(tf.getTextField().getText()));
                                    maxValaues.add(tf.getDoubleValue());
                                    if(resources.get(i)>maxValaues.get(i)){
                                        okFlag=false;
                                    }
                                }
                                resourceTextFieldAndMaxVal.clear();
                                if (okFlag) {
                                    int result = DBConnection.addMode(selectedArticle, selectedSubsystem, selectedDevice, chosenName, resources, lastIndex);

                                    JTree tree = MainWindow.getTree();
                                    DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                                    i = 0;
                                    Object articleNode;
                                    while (!((articleNode = treeModel.getChild(rootNode, i)).toString().equals(selectedArticle))) {
                                        if (rootNode.getChildCount()>1&&i == rootNode.getChildCount() - 1) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame, "error in first cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }
                                    DefaultMutableTreeNode systemNode = (DefaultMutableTreeNode) treeModel.getChild(articleNode, 0);
                                    Object subsystemNode;
                                    i = 0;
                                    while (!(subsystemNode = treeModel.getChild(systemNode, i)).toString().toLowerCase().equals(selectedSubsystem)) {
                                        if (systemNode.getChildCount()>1&&i == systemNode.getChildCount() - 1 && systemNode.getChildCount() > 1) {
                                            JOptionPane.showMessageDialog(DBUpdaterFrame, "error in second cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        i++;
                                    }
                                    Object deviceNode;
                                    i = 0;
                                    DefaultMutableTreeNode temp = (DefaultMutableTreeNode) subsystemNode;
                                    if (((DefaultMutableTreeNode) subsystemNode).getChildCount() != 0) {
                                        while (!((deviceNode = treeModel.getChild(subsystemNode, i)).toString().toLowerCase().equals(selectedDevice))) {
                                            if (((DefaultMutableTreeNode) subsystemNode).getChildCount()>1&&i == ((DefaultMutableTreeNode) subsystemNode).getChildCount() - 1) {
                                                JOptionPane.showMessageDialog(DBUpdaterFrame, "error in third cycle", "Error", JOptionPane.ERROR_MESSAGE);
                                                break;
                                            }
                                            i++;
                                        }
                                        for (Double dbl : resources) {
                                            double td = dbl;
                                            int q = (int) td;
                                            chosenName += "\t"+q;
                                        }
                                        treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName), (DefaultMutableTreeNode) deviceNode, ((DefaultMutableTreeNode) deviceNode).getChildCount());
                                    } else {
                                        for (Double dbl : resources) {
                                            double td = dbl;
                                            int q = (int) td;
                                            chosenName += q;
                                            treeModel.insertNodeInto(new DefaultMutableTreeNode(chosenName), (DefaultMutableTreeNode) subsystemNode, ((DefaultMutableTreeNode) subsystemNode).getChildCount());
                                        }
                                    }
                                    verifyResult(result);
                                    resetUI();

                                }
                            }
                        }
                    });
                    mainPanel.removeAll();
                    enterPanel.add(submitButton);
                    mainPanel.add(enterPanel);

                }
                if(state==QUERY_TO_ARTICLE){
                    selectedSubsystem=resultList.getSelectedValue().toLowerCase();
                    Vector<String> result=DBConnection.queryToSubsys(selectedArticle,selectedSubsystem);
                    MainWindow.verifyResult(result,DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_SUBSYSTEM;

                }
                if(state==QUERY_TO_ARTICLES) {
                    selectedArticle = resultList.getSelectedValue().toLowerCase();
                    Vector<String> result = DBConnection.queryToArticle(selectedArticle);
                    MainWindow.verifyResult(result, DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_ARTICLE;
                }
            }
        });
        JPanel panel=new JPanel();
        panel.add(resultList);
        panel.add(nextButton);
        mainPanel.add(panel);
    }
    //true if this name already exists
    private boolean testForMatching(Vector<String> usedNames,String name){
        for(String str:usedNames){
            if(str.equals(name)){
                return true;
            }
        }
        return false;
    }
    private void verifyResult(int testingValue) {
        if (testingValue == DB.CLASS_NOT_FOUND) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (testingValue == DB.SQL_EXCEPTION) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (testingValue == DB.CLASS_CAST_EXCEPTION) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void verifyResult(Vector<Vector<String>> vectorStringVector){
        if(vectorStringVector.get(0).size()>1){
            return;
        }
        try{
            int testingValue=Integer.parseInt(vectorStringVector.get(0).get(0));
            if (testingValue == DB.CLASS_NOT_FOUND) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (testingValue == DB.SQL_EXCEPTION) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch (ClassCastException cCExc){
            return;
        }
        catch (NumberFormatException numberExc){
            return;
        }
    }
    private void updateWindow(){
        Dimension dimension;
        if(DBUpdaterFrame.getSize().height<300){
            dimension=new Dimension(800,DBUpdaterFrame.getSize().height+1);
        }
        else {
            dimension = new Dimension(800, DBUpdaterFrame.getSize().height - 1);
        }
        DBUpdaterFrame.setSize(dimension);
    }
    private void updateWindow(int height,int width){
        Dimension dimension;
        if(DBUpdaterFrame.getSize().height<height){
            dimension=new Dimension(width,DBUpdaterFrame.getSize().height+1);
        }
        else {
            dimension = new Dimension(width, DBUpdaterFrame.getSize().height - 1);
        }
        DBUpdaterFrame.setSize(dimension);
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

        MainWindow.getMainFrame().setEnabled(true);
        DBUpdaterFrame.dispose();
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
}
