package com.company;

import com.company.DataTypes.ArticleBackup;
import com.company.DataTypes.SystemInfo;
import com.company.NodeClasses.TopNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.company.NodeClasses.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class MainWindow extends JPanel implements ActionListener,MouseListener,TreeSelectionListener {
    private static final String ADD_RESOURCE="Добавить ресурс";
    private static final String CANGE_MODE_CONSUMPTION="Изменить потребление";
    private final String NEW_ALGORITHM = "Create new algorithm";
    private final String OPEN = "Open algorithm";
    private final String ADD="Add to db";
    private final String CLEAR="Clear";
    private final String SAVE="Save algorithm";
    private final String RUN="Run";
    private static Path used_namesPath= Paths.get("C:\\Prototype v0.3\\src\\com\\company\\used_names.txt");
    private static Path usedResourcesPath=Paths.get("C:\\Prototype v0.3\\src\\com\\company\\used_resources.txt");
    private static Charset charset=Charset.forName("UTF-8");
    private static Vector<SystemInfo> systemInfoVector=new Vector<>();
    private static Vector<SystemInfo> systemInfoVectorForSaving=new Vector<>();
    private DB DBC=new DB();
    private static JPanel mainPanel = new JPanel();
    private static JFrame mainFrame;
    private JTextArea openedAlgorithms=new JTextArea(5,30);
    static JTextArea resourceMonitor=new JTextArea(1,30);
    private JTextArea algorithmInfo=new JTextArea(20,20);
    static ArrayList<Double> allResources;
    private static JTree tree;
    private int clicks;
    private boolean dc;
    private ArrayList<String> usingDevices=new ArrayList<>();
    private ExecutorService executor= Executors.newSingleThreadExecutor();
    private JPopupMenu popupMenu=new JPopupMenu();
    private JPopupMenu modeConsumptionPopup =new JPopupMenu();
    private static int popupX,popupY;
    HashMap<String,ArticleBackup> articleBackupHashMap=new HashMap<>();
    private JLabel algorithmDescription=new JLabel("Время|   " +"Усторйство и режим  "+"                             Потебление ресурсов");

    public MainWindow() {
        resourceMonitor.setEditable(false);
        algorithmInfo.setEditable(false);
        Font resourceMonitorFont=new Font("defaultTextFont",Font.PLAIN,12);
        resourceMonitor.setFont(resourceMonitorFont);
        algorithmInfo.setFont(resourceMonitorFont);
        openedAlgorithms.setEditable(false);
        openedAlgorithms.getFont();
        Font defaultTextFont=new Font("defaultTextFont",Font.PLAIN,14);
        openedAlgorithms.setFont(defaultTextFont);

        JButton runButton=new JButton("Запустить");
        runButton.setActionCommand(RUN);
        runButton.addActionListener(this);

        JButton addToDBButton=new JButton("Добавить");
        addToDBButton.setActionCommand(ADD);
        addToDBButton.addActionListener(this);
        JButton clearButton=new JButton("Очистить");
        clearButton.setActionCommand(CLEAR);
        clearButton.addActionListener(this);
        JButton saveButton=new JButton("Сохранить алгоритм");
        saveButton.setActionCommand(SAVE);
        saveButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        //buttonPanel.add(runButton);
        buttonPanel.add(addToDBButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);

        JSplitPane treeSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        treeSplitPane.setDividerLocation(250);
        treeSplitPane.setPreferredSize(new Dimension(950,450));
        DefaultMutableTreeNode top=new DefaultMutableTreeNode("Изделия");
        tree=new JTree(top);
        createTree(top);
        JScrollPane treeScrollPane=new JScrollPane(tree);
        JSplitPane treePane=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JLabel articleLabel=new JLabel("Изделия:");
        treePane.setTopComponent(articleLabel);
        treePane.setBottomComponent(treeScrollPane);
        treePane.setEnabled(false);

        treeSplitPane.setLeftComponent(treePane);

        JSplitPane treeWithInfo=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        treeWithInfo.setTopComponent(treeSplitPane);

        JScrollPane algorithmInfoScrollPane=new JScrollPane(algorithmInfo);
        algorithmInfoScrollPane.setPreferredSize(new Dimension(950,100));
        treeWithInfo.setBottomComponent(algorithmInfoScrollPane);

        JSplitPane splitPane=new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buttonPanel);
        JScrollPane openedScrollPane=new JScrollPane(openedAlgorithms);

        JSplitPane algorithmWithDescription=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        algorithmWithDescription.setTopComponent(algorithmDescription);
        algorithmWithDescription.setBottomComponent(openedScrollPane);
        algorithmDescription.setEnabled(false);

        splitPane.setBottomComponent(algorithmWithDescription);
        JSplitPane withMonitor=new JSplitPane(JSplitPane.VERTICAL_SPLIT,splitPane,resourceMonitor);
        withMonitor.setDividerLocation(420);
        treeSplitPane.setRightComponent(withMonitor);

        mainPanel.add(treeWithInfo);

        add(mainPanel);
        PopupActionListener popupActionListener=new PopupActionListener();
        JMenuItem menuItem=new JMenuItem("Изменить ресурсы");
        JMenuItem anotherMenuItem=new JMenuItem("Изменить ресурсы");
        popupMenu.add(menuItem);
        JMenuItem modeConsumptionChanging=new JMenuItem("Изменить потребление ресурсов");
        modeConsumptionPopup.add(anotherMenuItem);
        modeConsumptionPopup.add(modeConsumptionChanging);
        menuItem.addActionListener(popupActionListener);
        menuItem.setActionCommand(ADD_RESOURCE);
        anotherMenuItem.addActionListener(popupActionListener);
        anotherMenuItem.setActionCommand(ADD_RESOURCE);
        modeConsumptionChanging.addActionListener(popupActionListener);
        modeConsumptionChanging.setActionCommand(CANGE_MODE_CONSUMPTION);
    }

    static void createAndShowGUI() {
        mainFrame = new JFrame("Система поддержки управления алгоритмами");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainWindow contentPane = new MainWindow();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLocation(500,120);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(RUN)){
            (new ThreadRunWindow()).start();
        }
        if(e.getActionCommand().equals(SAVE)) {
            TreePath selectionPath = tree.getSelectionPath();
            String stringPath = selectionPath.toString();
            String[] splittedStringPath = stringPath.split(",");
            String article = splittedStringPath[1].replace("]", "").trim();

            saveToDB(article);

        }
        if(e.getActionCommand().equals(CLEAR)){
            try {
                BufferedWriter writer=Files.newBufferedWriter(usedResourcesPath,TRUNCATE_EXISTING);
                writer.close();
                usingDevices.clear();
                //Reset resource monitor
                resourceMonitor.setText("");
                setResourceMonitor();

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            openedAlgorithms.setText("");
            systemInfoVector.clear();
        }

        if(e.getActionCommand().equals(ADD)){
            executor.submit(new ThreadDBUpdater());
        }

    }
    static Vector<SystemInfo> getSystemInfoVector(){
        return systemInfoVector;
    }
    static Vector<SystemInfo> getSystemInfoVectorForSaving(){return systemInfoVectorForSaving;}
    private void createTree(DefaultMutableTreeNode top){
        Vector<TopNode> topNodes=createTopNodes(top);
        tree.addMouseListener(this);
        tree.getSelectionModel().addTreeSelectionListener(this);
        //Root hiding
        tree.setRootVisible(false);
        TreePath p=new TreePath(top.getPath());
        tree.expandPath(p);
        Vector<SubsystemNode> subsystemNodes=createSubsystemLeaves(topNodes);
        Vector<DeviceNode> deviceNodes=createDeviceLeaves(subsystemNodes);
        createDeviceModeLeaves(deviceNodes);
    }
    private Vector<TopNode> createTopNodes(DefaultMutableTreeNode top){
        Vector<String> result=DBC.queryToArticles();
        verifyResult(result,mainFrame);
        Vector<TopNode> topNodes=new Vector<>();
        for(String str:result){
            DefaultMutableTreeNode newTop=new DefaultMutableTreeNode(str);
            TopNode topNode=new TopNode(str,newTop);
            topNodes.add(topNode);
            top.add(newTop);
        }
        return topNodes;
    }
    private Vector<SubsystemNode> createSubsystemLeaves(Vector<TopNode> topNodes){
        Vector<SubsystemNode> subsystemNodes=new Vector<>();
        for(TopNode topNode:topNodes) {
            DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode("Бортовая аппаратура");
            topNode.getTopNode().add(systemNode);
            String topNodeName = topNode.getName();
            Vector<String> result = DBC.queryToArticle(topNodeName);
            for (String str : result) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(str);
                systemNode.add(newNode);
                SubsystemNode subsystemNode = new SubsystemNode(topNodeName, str, newNode);
                subsystemNodes.add(subsystemNode);
            }

            DefaultMutableTreeNode resourceNode = new DefaultMutableTreeNode("Ресурсы");
            topNode.getTopNode().add(resourceNode);
            Vector<String> resourceNames=DBC.getResourcesNames(topNodeName);
            Vector<String> resourceValues=DBC.getArticleResources(topNodeName);
            Vector<String> resourceMeasurements=DBC.getArticleMeasurements(topNodeName);
            for(int i=0;i<resourceNames.size();i++){
                String resultString=resourceNames.get(i)+": "+resourceValues.get(i)+" "+resourceMeasurements.get(i);
                DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(resultString);
                resourceNode.add(newNode);
            }

        }

        createAlgorithmLeaves(topNodes);
        return subsystemNodes;
    }
    private void createAlgorithmLeaves(Vector<TopNode> topNodes){
        for(TopNode topNode:topNodes) {
            DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode("Алгоритмы");
            DefaultMutableTreeNode variantNode = new DefaultMutableTreeNode("Варианты");
            systemNode.add(variantNode);
            topNode.getTopNode().add(systemNode);
            String topNodeName = topNode.getName();
            Vector<String> result = DBC.queryToAlgorithms(topNodeName);
            if(result.size()>0) {
                if (!result.get(0).equals("-12")) {
                    for (String str : result) {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(str);
                        variantNode.add(newNode);
                    }
                }
            }
        }
    }
    private Vector<DeviceNode> createDeviceLeaves(Vector<SubsystemNode> subsystemNodes){
        Vector<DeviceNode> deviceNodeVector=new Vector<>();
        for(SubsystemNode subsystemNode:subsystemNodes) {
            String articleName=subsystemNode.getTopNodeName();
            String subsystemName=subsystemNode.getName();
            Vector<String> sensorName = DBC.getSensorNames(articleName,subsystemName);
            Vector<String> deviceName=DBC.getDeviceNames(articleName,subsystemName);
            if(sensorName.size()>0){
                for(String str:sensorName){
                    DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(str);
                    subsystemNode.getSubsystemNode().add(newNode);
                    DeviceNode deviceNode=new DeviceNode(articleName,str,subsystemName,newNode);
                    deviceNodeVector.add(deviceNode);
                }
            }
            if(deviceName.size()>0){
                for(String str:deviceName){
                    DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(str);
                    subsystemNode.getSubsystemNode().add(newNode);
                    DeviceNode deviceNode=new DeviceNode(articleName,str,subsystemName,newNode);
                    deviceNodeVector.add(deviceNode);
                }
            }
        }
        return deviceNodeVector;
    }
    private void createDeviceModeLeaves(Vector<DeviceNode> deviceNodes){
        for(DeviceNode deviceNode:deviceNodes){
            String articleName=deviceNode.getArticleName();
            String subsystemName=deviceNode.getSubsystemNodeName();
            String deviceName=deviceNode.getDeviceName();
            Vector<String> sensorModes=DBC.queryToSensor(articleName,subsystemName,deviceName);
            Vector<String> deviceModes=DBC.queryToDevice(articleName,subsystemName,deviceName);
            if(sensorModes.size()>0&&!sensorModes.get(0).equals(Integer.toString(DBC.SQL_EXCEPTION))){
                for(String str:sensorModes){
                    deviceNode.getDeviceNode().add(new DefaultMutableTreeNode(str));
                }
            }
            if(deviceModes.size()>0&&!deviceModes.get(0).equals(Integer.toString(DBC.SQL_EXCEPTION))){
                for(String str:deviceModes){
                    deviceNode.getDeviceNode().add(new DefaultMutableTreeNode(str));
                }
            }
            if(deviceModes.size()==0||sensorModes.size()==0){
                deviceNode.getDeviceNode().add(new DefaultMutableTreeNode());
            }
        }
    }
    static void verifyResult(Vector<String> stringVector,JFrame parentFrame){
        try {
            if (stringVector.size() != 1) {
                return;
            }
            if (stringVector.size() == 1) {
                String firstResult = stringVector.get(0);
                int testingValue = Integer.parseInt(firstResult);
                if (testingValue == DB.CLASS_NOT_FOUND) {
                    JOptionPane.showMessageDialog(parentFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.SQL_EXCEPTION) {
                    JOptionPane.showMessageDialog(parentFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                    JOptionPane.showMessageDialog(parentFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }catch (NumberFormatException exc){
            return;
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()){
            popupX=e.getX();
            popupY=e.getY();
            TreePath path=tree.getPathForLocation(popupX,popupY);
            String pathString=path.toString().replace("[","");
            pathString=pathString.replace("]","");
            String[] splittedPathString=pathString.split(",");
            if(splittedPathString.length==6&&pathString.contains("Бортовая аппаратура")){
                modeConsumptionPopup.show(e.getComponent(),popupX,popupY);
            }
            else {
                popupMenu.show(e.getComponent(), popupX, popupY);
            }
        }
        clicks=0;
        dc=false;
        if(e.getButton()==MouseEvent.BUTTON1){
            Integer timeInterval=(Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
            if(e.getClickCount()==1){
                int x=e.getX();
                int y=e.getY();
                TreePath path=tree.getPathForLocation(x,y);
                String[] splittedPath=path.toString().split(",");
                if(splittedPath.length==5&&path.toString().contains("Варианты")){
                    Vector<Vector<String>> resultVector=getAlgorithmInfo(splittedPath);

                    algorithmInfo.setText("");
                    for(Vector<String> vector:resultVector) {
                        {
                            String article=vector.get(0);
                            String subsystem=vector.get(1);
                            String deviceName=vector.get(2);
                            String mode=vector.get(3);
                            String delay=vector.get(4);
                            String relation=vector.get(5);
                            //handle with time for algorithm info
                            SystemInfo systemInfo = new SystemInfo(article,subsystem,deviceName,mode,delay,relation);
                            handleWithTimeForAlgorithmInfo(systemInfo);
                        }
                    }
                }
            }
            if(e.getClickCount()==2)
                dc=true;
            Timer timer=new Timer(timeInterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    if(dc){
                        clicks++;
                        int x=e.getX();
                        int y=e.getY();
                        TreePath path=tree.getPathForLocation(x,y);
                        String string=path.toString();
                        String[] splittedPath=string.split(",");
                        if(clicks==2&&splittedPath.length==6&&path.toString().contains("Бортовая аппаратура")) {
                            askForDelay(splittedPath);
                        }
                        if(clicks==2&&splittedPath.length==5&&path.toString().contains("Варианты")){
                            //Добавление алгоритма в окно
                            Vector<Vector<String>> resultVector=getAlgorithmInfo(splittedPath);

                            algorithmInfo.setText("");
                            for(Vector<String> vector:resultVector) {
                                {
                                    String article=vector.get(0);
                                    String subsystem=vector.get(1);
                                    String deviceName=vector.get(2);
                                    String mode=vector.get(3);
                                    String delay=vector.get(4);
                                    String relation=vector.get(5);
                                    //handle with time for algorithm info
                                    SystemInfo systemInfo = new SystemInfo(article,subsystem,deviceName,mode,delay,relation);
                                    systemInfoVector.add(systemInfo);
                                    handleWithTime(systemInfo);
                                    recountResources(systemInfo.getArticle(),systemInfo.getMode(),systemInfo);
                                    usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());
                                }
                            }
                        }
                    }
                }
            });
            timer.start();
            timer.setRepeats(false);
            if(e.getID()==MouseEvent.MOUSE_RELEASED)
                timer.stop();

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()) {
            popupX = e.getX();
            popupY = e.getY();
            TreePath path = tree.getPathForLocation(popupX, popupY);
            String pathString = path.toString().replace("[", "");
            pathString = pathString.replace("]", "");
            String[] splittedPathString = pathString.split(",");
            if (splittedPathString.length == 6&&pathString.contains("Бортовая аппаратура")) {
                modeConsumptionPopup.show(e.getComponent(), popupX, popupY);
            } else {
                popupMenu.show(e.getComponent(), popupX, popupY);
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void askForDelay(String[] splittedPath){
        JPanel delayPanel=new JPanel();
        JTextField delayField=new JTextField(10);
        delayField.setEditable(true);
        String logContent=openedAlgorithms.getText();
        String[] formats={"Часы","Минуты","Секунды"};
        JComboBox<String>delayFormatChooser=new JComboBox<>(formats);
        delayFormatChooser.setSelectedIndex(2);
        String[] logContentSplit = logContent.split("\n");
        Vector<String> deviceToQueueChooser = new Vector<>();
        deviceToQueueChooser.add(" ");
        if(logContent.length()>1) {
            for (String str : logContentSplit) {
                String[] tempStr = str.split(" ");
                deviceToQueueChooser.add(tempStr[2]);
            }
        }
        JComboBox<String>queueChooser = new JComboBox<>(deviceToQueueChooser);
        queueChooser.setVisible(false);
        queueChooser.setSelectedIndex(0);
        //Set delay activator
        JCheckBox delayActivator=new JCheckBox("Включить задержку");
        delayActivator.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(delayActivator.isSelected()){
                    delayFormatChooser.setEnabled(true);
                    queueChooser.setEnabled(true);
                    delayField.setEnabled(true);
                }
                else{
                    delayFormatChooser.setEnabled(false);
                    queueChooser.setEnabled(false);
                    delayField.setEnabled(false);
                }
            }
        });
        delayFormatChooser.setEnabled(false);
        queueChooser.setEnabled(false);
        delayField.setEnabled(false);
        JButton confirmButton=new JButton("Confirm");
        delayPanel.add(delayActivator);
        delayPanel.add(delayField);
        delayPanel.add(delayFormatChooser);
        delayPanel.add(queueChooser);
        delayPanel.add(confirmButton);
        JFrame delayFrame=new JFrame("Delay");
        delayFrame.setContentPane(delayPanel);
        delayFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        delayFrame.setLocationRelativeTo(mainFrame);
        delayFrame.pack();
        delayFrame.setVisible(true);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String articleName=splittedPath[1].trim();
                String subsystemName=splittedPath[3].trim();
                String deviceName=splittedPath[4].trim();
                String mode=splittedPath[5].split("]")[0].trim();
                if(!delayActivator.isSelected()){
                    SystemInfo systemInfo=new SystemInfo(articleName,subsystemName,deviceName,mode,"","");
                    systemInfoVector.add(systemInfo);
                    recountResources(articleName,mode,systemInfo);
                    if(!systemInfo.getMode().toLowerCase().equals("выкл"))
                        usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());

                    handleWithTime(systemInfo);
                    delayFrame.dispose();

                }
                if(delayActivator.isSelected()){
                    SystemInfo systemInfo=new SystemInfo(articleName,subsystemName,deviceName,mode,
                            delayField.getText()+delayFormatChooser.getSelectedItem(),(String)queueChooser.getSelectedItem());
                    systemInfoVector.add(systemInfo);
                    recountResources(articleName,mode,systemInfo);
                    if(!systemInfo.getMode().toLowerCase().equals("выкл"))
                        usingDevices.add(systemInfo.getInfoWithoutDelayAndMode());
                    handleWithTime(systemInfo);
                    delayFrame.dispose();
                }
            }
        });

    }
    private void saveToDB(String article) {

        JPanel mainPanel = new JPanel();
        JTextField nameField = new JTextField(10);
        JButton confirmSaveButton = new JButton("Сохранить");
        mainPanel.add(nameField);
        mainPanel.add(confirmSaveButton);
        mainPanel.setOpaque(true);
        JFrame saveFrame = new JFrame("Сохранение");
        saveFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        saveFrame.setContentPane(mainPanel);
        saveFrame.pack();
        saveFrame.setVisible(true);
        confirmSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (name.length() > 0) {
                    if (compareName(name)) {
                        int res = DBC.saveToDB(article, name, systemInfoVector);
                        if (res == DB.CLASS_NOT_FOUND) {
                            JOptionPane.showMessageDialog(mainFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (res == DB.SQL_EXCEPTION) {
                            JOptionPane.showMessageDialog(mainFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (res == DB.CLASS_CAST_EXCEPTION) {
                            JOptionPane.showMessageDialog(mainFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        writeName(name);
                        saveFrame.dispose();
                        openedAlgorithms.setText("");
                        //TODO:Make it work tomorrow
                        MainWindow.getSystemInfoVectorForSaving().clear();
                       DefaultMutableTreeNode temp=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                       DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
                       DefaultMutableTreeNode articleNode=(DefaultMutableTreeNode) treeModel.getPathToRoot(temp)[1];
                       DefaultMutableTreeNode algorithmsNode=(DefaultMutableTreeNode)treeModel.getChild(articleNode,2);
                       DefaultMutableTreeNode variantNode=(DefaultMutableTreeNode)treeModel.getChild(algorithmsNode,0);
                       treeModel.insertNodeInto(new DefaultMutableTreeNode(name),variantNode,variantNode.getChildCount());

                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Выберите другое имя", "Выберите другое имя", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });


    }
    private void writeName(String name){
        try(BufferedWriter bufferedWriter= Files.newBufferedWriter(used_namesPath,charset,APPEND)){
            bufferedWriter.write(name+"/");
        }catch(IOException ex){}
    }
    private boolean compareName(String name){
        String line;
        try(BufferedReader bufferedReader= Files.newBufferedReader(used_namesPath,charset)){
            while((line=bufferedReader.readLine())!=null){
                String[] usedNames=line.split("/");
                for(String str:usedNames){
                    if(str.equals(name))
                        return false;
                }
            }
        }catch(IOException ex){}
        return true;
    }
    private Vector<Vector<String>> getAlgorithmInfo(String[] splittedPath){
        String article=splittedPath[1].toLowerCase().trim();
        String algorithm=splittedPath[splittedPath.length-1].split("]")[0].toLowerCase().trim();
        Vector<Vector<String>> result=DBC.getAlgorithmInfo(article,algorithm);
        //verifyResult(result,mainFrame);
        return result;
    }
    private void handleWithTime(SystemInfo systemInfo){
        try {
            String text = openedAlgorithms.getText();
            if(!text.equals("")) {
                int lines=openedAlgorithms.getLineCount();
                String lastLine = text.split("\n")[openedAlgorithms.getLineCount()-2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c","").trim();
                int lastTime = Integer.parseInt(lastTimeStr);
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");


                int time = Integer.parseInt(delay[0]);
                String delayFormat=systemInfo.getDelay().split("\\d")[1];
                if(delayFormat.equals("Часы")){
                    time=time*3600;
                }
                if(delayFormat.equals("Минуты")){
                    time=time*60;
                }
                //if delay has been set
                int newTime = time + lastTime;
                openedAlgorithms.append("=" + newTime + "c|" + systemInfo.getArticle() +" "+ systemInfo.getSubsystem()+" " +
                        systemInfo.getDeviceName()+" " + systemInfo.getMode()+ "\n");
            }
            else{
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");

                int time = Integer.parseInt(delay[0]);
                String delayFormat=systemInfo.getDelay().split("\\d")[1];
                if(delayFormat.equals("Часы")){
                    time=time*3600;
                }
                if(delayFormat.equals("Минуты")){
                    time=time*60;
                }
                openedAlgorithms.append("="+time+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
            }

        }catch (NumberFormatException e){
            //if there is no delay
            String text=openedAlgorithms.getText();
            String lastLine;
            int lastTime;
            if(!text.isEmpty()) {
                lastLine = text.split("\n")[openedAlgorithms.getLineCount() - 2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c", "").trim();
                lastTime = Integer.parseInt(lastTimeStr);

            }
            else
                lastTime=0;
            openedAlgorithms.append("="+lastTime+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+ systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
        }
    }
    private void handleWithTimeForAlgorithmInfo(SystemInfo systemInfo){
        try {
            String text = algorithmInfo.getText();
            if(!text.equals("")) {
                int lines=algorithmInfo.getLineCount();
                String lastLine = text.split("\n")[algorithmInfo.getLineCount()-2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c","").trim();
                int lastTime = Integer.parseInt(lastTimeStr);
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");

                int time = Integer.parseInt(delay[0]);
                String delayFormat=systemInfo.getDelay().split("\\d")[1];

                if(delayFormat.equals("Часы")){
                    time=time*3600;
                }
                if(delayFormat.equals("Минуты")){
                    time=time*60;
                }
                //if delay has been set
                int newTime = time + lastTime;
                algorithmInfo.append("=" + newTime + "c|" + systemInfo.getArticle() +" "+ systemInfo.getSubsystem()+" "+systemInfo.getDeviceName()+
                        " "+systemInfo.getMode()+"\n");
            }
            else{
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");
                int time = Integer.parseInt(delay[0]);
                String delayFormat=systemInfo.getDelay().split("\\d")[1];
                if(delayFormat.equals("Часы")){
                    time=time*3600;
                }
                if(delayFormat.equals("Минуты")){
                    time=time*60;
                }
                algorithmInfo.append("="+time+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+
                        systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
            }

        }catch (NumberFormatException e){
            //if there is no delay
            String text=algorithmInfo.getText();
            String lastLine;
            int lastTime;
            if(!text.isEmpty()) {
                lastLine = text.split("\n")[algorithmInfo.getLineCount() - 2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c", "").trim();
                lastTime = Integer.parseInt(lastTimeStr);
            }
            else
                lastTime=0;
            algorithmInfo.append("="+lastTime+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+
                    systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //Make backup and search for saved for chosen article
        TreePath oldSelection = e.getOldLeadSelectionPath();
        if (oldSelection != null&&e.getNewLeadSelectionPath()!=null) {
            String[] oldSplittedPath = oldSelection.toString().split(",");
            String oldArticleName = oldSplittedPath[1].replace("]", "").trim();
            TreePath selectionPath = e.getNewLeadSelectionPath();
            String stringPath = selectionPath.toString();
            String[] splittedStringPath = stringPath.split(",");
            String newArticleName = splittedStringPath[1].replace("]", "").trim();
            if (!oldArticleName.equals(newArticleName)) {
                String openedAlgorithmsText = openedAlgorithms.getText();
                String article = oldSelection.toString().split(",")[1].trim();
                article = article.replace("]", "");
                article = article.trim();
                Vector<String> resourcesForLabel = DBC.getResourcesNames(article);
                String labelString = "Время|   " + "Усторйство и режим  " + "              |             ";
                for (String str : resourcesForLabel) {
                    labelString += str + "              ";
                }
                algorithmDescription.setText(labelString);
                //String[] splittedStringPath = oldSelection.toString().split(",");
                ArticleBackup backup = new ArticleBackup(openedAlgorithmsText, systemInfoVector,usingDevices);
                systemInfoVector.clear();
                usingDevices.clear();
                openedAlgorithms.setText("");
                articleBackupHashMap.put(article, backup);
                if (articleBackupHashMap.size() > 1) {
                    //Find content for new selection
                    String newArticle = e.getNewLeadSelectionPath().toString().split(",")[1].trim();
                    newArticle = newArticle.replace("]", "");
                    newArticle = newArticle.trim();
                    ArticleBackup pastBackup;
                    if ((pastBackup = articleBackupHashMap.get(newArticle)) != null) {
                        openedAlgorithms.setText(pastBackup.getOpenedAlgorithmsContent());
                        systemInfoVector = pastBackup.getSystemInfoVector();
                        usingDevices=pastBackup.getUsingDevices();
                        setResourceMonitor();
                    } else {
                        setResourceMonitor();
                    }
                }
                if (articleBackupHashMap.size() == 1) {
                    setResourceMonitor();
                }
            }
    }
        if (oldSelection == null) {
            setResourceMonitor();
        }
    }
    private void recountResources(String articleName,String mode,SystemInfo systemInfo){
        //Извлекаем название изделия и вносим изменение в использованные ресурсы
        //replace with XML
        if (usingDevices.contains(systemInfo.getInfoWithoutDelayAndMode())) {
            int index = usingDevices.indexOf(systemInfo.getInfoWithoutDelayAndMode());
            SystemInfo usedDevice = MainWindow.getSystemInfoVector().get(index);
            String[] usingMode = usedDevice.getMode().split("\t");
            String[] currentMode = systemInfo.getMode().split("\t");
            if(!currentMode[0].trim().toUpperCase().equals("ВЫКЛ")&&usingMode[0].trim().toUpperCase().equals("ВЫКЛ")){
                MainWindow.getSystemInfoVector().remove(usedDevice);

                boolean foundDublicate=true;
                while(foundDublicate){
                    usedDevice = MainWindow.getSystemInfoVector().get(index);
                    usingMode = usedDevice.getMode().split("\t");
                    if(usingMode[0].trim().toUpperCase().equals("ВЫКЛ")){
                        //MainWindow.getSystemInfoVector().remove(usedDevice);
                        while (MainWindow.getSystemInfoVector().remove(usedDevice)){}
                    }
                    else
                        foundDublicate=false;
                }
            }
            ArrayList<Double> prevResourceUsageList=new ArrayList<>();
            Vector<String> resourcesNames=DBC.getResourcesNames(articleName);
            int size=resourceMonitor.getText().split("\t").length;
            if (currentMode[0].trim().toUpperCase().equals("ВЫКЛ")&&!(usingMode[0].trim().toUpperCase().equals("ВЫКЛ"))) {

                while ((index = usingDevices.indexOf(systemInfo.getInfoWithoutDelayAndMode()))!=-1) {
                    usingDevices.remove(index);
                    usedDevice = MainWindow.getSystemInfoVector().get(index);
                    usingMode = usedDevice.getMode().split("\t");
                    currentMode = systemInfo.getMode().split("\t");
                    //usingDevices.remove(usingDevices.indexOf(systemInfo.getInfoWithoutDelayAndMode()));
                    MainWindow.getSystemInfoVector().remove(usedDevice);
                    String resources = resourceMonitor.getText();
                    String[] tempRes = resources.split("\t");
                    ArrayList<Double> currentResourceUsageList = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        Double prevResourceUsage = Double.parseDouble(usingMode[i + 1]);
                        Double currentResourceUsage = Double.parseDouble(tempRes[i].split("/")[0].split(":")[1]);
                        if (currentResourceUsage > 0) {
                            currentResourceUsage = currentResourceUsage - prevResourceUsage;
                            currentResourceUsageList.add(currentResourceUsage);
                        }
                    }
                    Vector<String> resourcesMeas = DBC.getArticleResourceNames(articleName);
                    String newResourcesString = "";
                    Vector<String> articleResources = DBC.getArticleResources(articleName);
                    for (int i = 0; i < size; i++) {
                        try {
                            newResourcesString += resourcesNames.get(i) + ":" + Double.toString(currentResourceUsageList.get(i)) + "/" + articleResources.get(i) + resourcesMeas.get(i) + "\t";
                        }catch (IndexOutOfBoundsException ex){
                            return;
                        }
                    }
                    resourceMonitor.setText(newResourcesString);
                }
            }
            else{
                String[] tempResources = resourceMonitor.getText().split("\t");
                size=resourceMonitor.getText().split("\t").length;
                ArrayList<Double> currentResourceUsageList=new ArrayList<>();
                for(int i=0;i<size;i++){
                    Double prevResourceUsage=Double.parseDouble(tempResources[i].split("/")[0].split(":")[1]);
                    Double currentResourceUsage= Double.parseDouble(currentMode[i+1]) + prevResourceUsage;
                    currentResourceUsageList.add(currentResourceUsage);
                }
                resourcesNames=DBC.getArticleResourceNames(articleName);
                Vector<String> articleResources=DBC.getArticleResources(articleName);
                Vector<String> resourceNames=DBC.getResourcesNames(articleName);
                String newResourcesString="";
                for(int i=0;i<size;i++){
                    newResourcesString+=resourceNames.get(i)+":"+Double.toString(currentResourceUsageList.get(i)) + "/" + articleResources.get(i) + resourcesNames.get(i)+"\t";
                }
                resourceMonitor.setText(newResourcesString);
            }

        }

        if (!usingDevices.contains(systemInfo.getInfoWithoutDelayAndMode())) {//else replacement
            Vector<String> articleResources=DBC.getArticleResources(articleName);
            Vector<String> resourceNames=DBC.getResourcesNames(articleName);
            String[] currentMode = systemInfo.getMode().split("\t");
            if (currentMode[0].trim().toUpperCase().equals("ВЫКЛ")){
                return;
            }
            String[] tempResources = resourceMonitor.getText().split("\t");
            int size=resourceMonitor.getText().split("\t").length;
            ArrayList<Double> currentResourceUsageList=new ArrayList<>();
            for(int i=0;i<size;i++){
                Double prevResourceUsage=Double.parseDouble(tempResources[i].split("/")[0].split(":")[1]);
                Double currentResourceUsage= Double.parseDouble(currentMode[i+1]) + prevResourceUsage;
                currentResourceUsageList.add(currentResourceUsage);
            }
            Vector<String> resourcesNames=DBC.getArticleResourceNames(articleName);
            String newResourcesString="";
            for(int i=0;i<size;i++){
                newResourcesString+=resourceNames.get(i)+":"+Double.toString(currentResourceUsageList.get(i)) + "/" + articleResources.get(i) + resourcesNames.get(i)+"\t";
            }
            resourceMonitor.setText(newResourcesString);

        }
        rewriteUsedResources(articleName,mode);

    }
    private void rewriteUsedResources(String articleName,String mode){
        //не mode, а нунышнее состояние сонитора
        try {
            String resourceMonitorCurrentState=resourceMonitor.getText();
            BufferedReader reader=Files.newBufferedReader(usedResourcesPath);
            ArrayList<String> resourceContain=new ArrayList<>();
            String line;
            while((line=reader.readLine())!=null){
                if((line.length()>1)&&!line.substring(line.indexOf("<"),line.indexOf(">")+1).equals("<"+articleName+">")){
                    resourceContain.add(line);
                }
            }
            BufferedWriter writer=Files.newBufferedWriter(usedResourcesPath,TRUNCATE_EXISTING);
            String articleTag="<"+articleName+"> ";
            writer.write(articleTag+resourceMonitorCurrentState+"\n");
            for(String str:resourceContain){
                writer.write(str);
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static JFrame getMainFrame() {
        return mainFrame;
    }
    static JTree getTree() {
        return tree;
    }
    static String getADD_RESOURCE() {
        return ADD_RESOURCE;
    }
    static int getPopupX(){
        return popupX;
    }
    static int getPopupY(){
        return popupY;
    }
    static String getCangeModeConsumption(){
        return MainWindow.CANGE_MODE_CONSUMPTION;
    }

    public static Path getUsedResourcesPath() {
        return usedResourcesPath;
    }


    private void setResourceMonitor(){
        TreePath selectionPath = tree.getSelectionPath();
        String stringPath = selectionPath.toString();
        String[] splittedStringPath = stringPath.split(",");
        if (splittedStringPath.length >= 2) {
            String articleName = splittedStringPath[1].replace("]", "").trim();
            Vector<String> resourcesForLabel=DBC.getResourcesNames(articleName);
            String labelString="Время|   " +"Усторйство и режим  "+"              |             ";
            for(String str:resourcesForLabel){
                labelString+=str+"              ";
            }
            algorithmDescription.setText(labelString);
            Vector<String> resourcesNames = DBC.getResourcesNames(articleName);
            Vector<String> resources = DBC.getArticleResources(articleName);
            String usedResources = DBC.getUsedArticleResources(articleName);
            Vector<String> resourcesMeas = DBC.getArticleResourceNames(articleName);

            if (usedResources == null) {
                Vector<String> usedResourcesVector = DBC.getUsedArticleResourcesIfEmpty(articleName);

                algorithmDescription.setText(labelString);
                if (usedResourcesVector.isEmpty()) {
                    for (int i = 0; i < resources.size(); i++) {
                        usedResourcesVector.add("0");
                    }
                }
                String resultString = "";
                for (int i = 0; i < resources.size(); i++) {
                    resultString += resourcesNames.get(i) + ":" + usedResourcesVector.get(i) + "/" + resources.get(i) + resourcesMeas.get(i) + "\t";
                }
                resourceMonitor.setText(resultString);
            } else
                resourceMonitor.setText(usedResources);
        }
    }
}
