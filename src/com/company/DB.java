package com.company;

/**
 * Created by Игорь on 08.08.2017.
 */

import com.company.DataTypes.SystemInfo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;


public class DB {
    static final int CLASS_NOT_FOUND = -11;
    static final int SQL_EXCEPTION = -12;
    static final int CLASS_CAST_EXCEPTION = -13;
    static final int OK = 12;
    private static final String TO_DELETE="-20";
    private final String url = "jdbc:mysql://127.0.0.1:3306/ка";
    private final String user = "root";
    private final String password = "5986";
    private Connection connection = null;
    private Statement statement = null;
    private Savepoint savepoint;
    int state;

    DB() {
        state = connect(url, user, password);
    }

    private int connect(String url, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException ex) {
            return CLASS_NOT_FOUND;
        } catch (SQLException SQLExc) {
            return SQL_EXCEPTION;
        }
        return OK;
    }

    public int connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException ex) {
            return CLASS_NOT_FOUND;
        } catch (SQLException SQLExc) {
            return SQL_EXCEPTION;
        }
        return OK;
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object execQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException exc) {
            return SQL_EXCEPTION;
        }
    }

    public Vector<String> firstQuery() {
        Vector<String> sVec = new Vector<>();
        String query = "SELECT * FROM подсистемы";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add("Подсистема" + resultSet.getString("idподсистемы"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }

    private int verifyResult(Object resultObject) {
        try {
            int resultInt = (int) resultObject;

            if (resultInt == DB.CLASS_NOT_FOUND) {
                return CLASS_NOT_FOUND;
            } else if (resultInt == DB.SQL_EXCEPTION) {
                return SQL_EXCEPTION;
            }
            return OK;
        } catch (ClassCastException classCastExc) {
            return OK;
        }
    }

    //article=изделие_№, selectedItem-выбранная подсистема
    Vector<String> queryToSubsys(String article, String selectedItem) {
        Vector<String> stringVector = new Vector<>();
        String query = "SELECT название,idприбор_for\n" +
                "FROM ка.изделия\n" +
                "inner join `" + article + "` on изделия.имя_изделия=`" + article + "`.имя\n" +
                "inner join `" + selectedItem + "_" + article + "` on `" + article + "`.подсистемы=`" + selectedItem + "_" + article + "`.idподсистема\n" +
                "left join `приборы_" + article + "_" + selectedItem + "` on `" + selectedItem + "_" + article + "`.idприбора=`приборы_" + article + "_" + selectedItem + "`.idприборы\n" +
                "left join `датчики_" + article + "_" + selectedItem + "` on `" + selectedItem + "_" + article + "`.idдатчика=`датчики_" + article + "_" + selectedItem + "`.idдатчики";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<String> queryToSubsys(String article, String selectedItem, ArrayList<Integer> lastIndex) {
        Vector<String> stringVector = new Vector<>();
        String query = "SELECT название,idприбор_for\n" +
                "FROM ка.изделия\n" +
                "inner join `" + article + "` on изделия.имя_изделия=`" + article + "`.имя\n" +
                "inner join `" + selectedItem + "_" + article + "` on `" + article + "`.подсистемы=`" + selectedItem + "_" + article + "`.idподсистема\n" +
                "inner join `приборы_" + article + "_" + selectedItem + "` on `" + selectedItem + "_" + article + "`.idприбора=`приборы_" + article + "_" + selectedItem + "`.idприборы\n" +
                "inner join `датчики_" + article + "_" + selectedItem + "` on `" + selectedItem + "_" + article + "`.idдатчика=`датчики_" + article + "_" + selectedItem + "`.idдатчики";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        String lasIndexQuery = "SELECT idприбора,idдатчика FROM `ка`.`" + selectedItem + "_" + article + "`;";
        resultObject = execQuery(lasIndexQuery);
        int device = 0, sensor = 0;
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    if (resultSet.getInt("idприбора") != 0) {
                        device = resultSet.getInt("idприбора");
                    }
                    if (resultSet.getInt("idдатчика") != 0) {
                        sensor = resultSet.getInt("idдатчика");
                    }
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            lastIndex.add(device);
            lastIndex.add(sensor);
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<String> queryToArticles() {
        String query = "SELECT * FROM изделия;";
        Vector<String> sVec = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add(resultSet.getString("имя_изделия"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }
    Vector<String> queryToArticles(ArrayList<Integer> lastIndex) {
        String query = "SELECT * FROM изделия;";
        Vector<String> sVec = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add(resultSet.getString("имя_изделия"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        if(lastIndex.isEmpty()){
            lastIndex.add(0);
        }
        return sVec;
    }
    Vector<String> queryToArticle(String prevQueryResult) {
        String query = "SELECT подсистемы\n" +
                "FROM ка.изделия\n" +
                "inner join `" + prevQueryResult + "` on изделия.имя_изделия=`" + prevQueryResult + "`.имя;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("подсистемы"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;

    }
    Vector<String> queryToArticle(String prevQueryResult, ArrayList<Integer> lastIndex) {
        String query = "SELECT `" + prevQueryResult + "`.id,подсистемы\n" +
                "FROM ка.изделия\n" +
                "inner join `" + prevQueryResult + "` on изделия.имя_изделия=`" + prevQueryResult + "`.имя;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("подсистемы"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;

    }
    //selectedItem- имя выбранного прибора
    Vector<String> queryToDevice(String article, String subsystem, String selectedItem) {
        String resourceConsumption="";
        Vector<String> resourceConsumptionVector=getArticleResourceNames(article);
        for(int i=0;i<resourceConsumptionVector.size();i++){
            if(i==resourceConsumptionVector.size()-1){
                resourceConsumption+="потребление_ресурса"+(i+1);
            }
            else
                resourceConsumption+="потребление_ресурса"+(i+1)+", ";
        }
        String query = "SELECT idрежима,"+resourceConsumption+"\n" +
                "FROM ка.изделия\n" +
                "inner join `" + article + "` on изделия.имя_изделия=`" + article + "`.имя\n" +
                "inner join `" + subsystem + "_" + article + "` on `" + article + "`.подсистемы=`" + subsystem + "_" + article + "`.idподсистема\n" +
                "left join `приборы_" + article + "_" + subsystem + "` on `" + subsystem + "_" + article + "`.idприбора=`приборы_" + article + "_" + subsystem + "`.idприборы\n" +
                "inner join `" + selectedItem + "_" + article + "_" + subsystem + "` on `приборы_" + article + "_" + subsystem + "`.idприбор_for=`" + selectedItem + "_" + article + "_" + subsystem + "`.idприбор\n" +
                "inner join `режимы_" + selectedItem + "_" + article + "_" + subsystem + "` on `" + selectedItem + "_" + article + "_" + subsystem + "`.режимы=`режимы_" + selectedItem + "_" + article + "_" + subsystem + "`.idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    String results="";
                    for(int i=0;i<resourceConsumptionVector.size();i++){
                        if(i==resourceConsumptionVector.size()-1){
                            results+=resultSet.getString("потребление_ресурса"+(i+1)+"");
                        }
                        else
                            results+=resultSet.getString("потребление_ресурса"+(i+1)+"") + "\t";
                    }
                    stringVector.add(resultSet.getString("idрежима") + "\t"+results);
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<String> queryToDevice(String article, String subsystem, String selectedItem, ArrayList<Integer> lastIndex) {
        //Определять количество ресурсов
        String resources="";
        Vector<String>resourceConsumption=getArticleResources(article);
        for(int i=0;i<resourceConsumption.size();i++){
            if(i==resourceConsumption.size()-1) {
                //resources += resourceConsumption.get(i) + (i + 1);
                resources+="`потребление_ресурса"+(i+1)+"`";
            }
            else
                //resources += resourceConsumption.get(i) + (i + 1) + ",";
                resources+="`потребление_ресурса"+(i+1)+"`,";
        }

        String query = "SELECT `"+selectedItem + "_" + article + "_" + subsystem + "`.`id`, `режимы_" + selectedItem + "_" + article + "_" + subsystem + "`.`idрежима`,"+resources+"\n" +
                "FROM ка.изделия\n" +
                "inner join `" + article + "` on изделия.имя_изделия=`" + article + "`.имя\n" +
                "inner join `" + subsystem + "_" + article + "` on `" + article + "`.подсистемы=`" + subsystem + "_" + article + "`.idподсистема\n" +
                "left join `приборы_" + article + "_" + subsystem + "` on `" + subsystem + "_" + article + "`.idприбора=`приборы_" + article + "_" + subsystem + "`.idприборы\n" +
                "inner join `" + selectedItem + "_" + article + "_" + subsystem + "` on `приборы_" + article + "_" + subsystem + "`.idприбор_for=`" + selectedItem + "_" + article + "_" + subsystem + "`.idприбор\n" +
                "inner join `режимы_" + selectedItem + "_" + article + "_" + subsystem + "` on `" + selectedItem + "_" + article + "_" + subsystem + "`.режимы=`режимы_" + selectedItem + "_" + article + "_" + subsystem + "`.idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    String consumption="";
                    for(int i=0;i<resourceConsumption.size();i++){
                        if(i<resourceConsumption.size()-1){
                            consumption+=resultSet.getString("потребление_ресурса"+(i+1)+"") + "\t";
                        }
                        else
                            consumption+=resultSet.getString("потребление_ресурса"+(i+1)+"");
                    }
                    stringVector.add(resultSet.getString("idрежима") + "\t" + consumption);
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    //selectedItem- имя выбранного датчика
    Vector<String> queryToSensor(String article, String subsystem, String selectedItem) {
        String resourceConsumption="";
        Vector<String> resourceConsumptionVector=getArticleResourceNames(article);
        for(int i=0;i<resourceConsumptionVector.size();i++){
            if(i==resourceConsumptionVector.size()-1){
                resourceConsumption+="потребление_ресурса"+(i+1);
            }
            else
                resourceConsumption+="потребление_ресурса"+(i+1)+", ";
        }
        String query = "SELECT idрежима,"+resourceConsumption+"\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + subsystem + "_" + article + " on " + article + ".подсистемы=" + subsystem + "_" + article + ".idподсистема\n" +
                "left join датчики_" + article + "_" + subsystem + " on " + subsystem + "_" + article + ".idдатчика=датчики_" + article + "_" + subsystem + ".idдатчики\n" +
                "inner join " + selectedItem + "_" + article + "_" + subsystem + " on датчики_" + article + "_" + subsystem + ".название=" + selectedItem + "_" + article + "_" + subsystem + ".idдатчик\n" +
                "inner join режимы_" + selectedItem + "_" + article + "_" + subsystem + " on " + selectedItem + "_" + article + "_" + subsystem + ".режимы=режимы_" + selectedItem + "_" + article + "_" + subsystem + ".idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    String results="";
                    for(int i=0;i<resourceConsumptionVector.size();i++){
                        if(i==resourceConsumptionVector.size()-1){
                            results+=resultSet.getString("потребление_ресурса"+(i+1)+"");
                        }
                        else
                            results+=resultSet.getString("потребление_ресурса"+(i+1)+"") + "\t";
                    }
                    stringVector.add(resultSet.getString("idрежима") + "\t"+results);
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<String> queryToSensor(String article, String subsystem, String selectedItem, ArrayList<Integer> lastIndex) {
        String resources="";
        Vector<String>resourceConsumption=getArticleResources(article);
        for(int i=0;i<resourceConsumption.size();i++){
            if(i==resourceConsumption.size()-1) {
                //resources += resourceConsumption.get(i) + (i + 1);
                resources+="`потребление_ресурса"+(i+1)+"`";
            }
            else
                //resources += resourceConsumption.get(i) + (i + 1) + ",";
                resources+="`потребление_ресурса"+(i+1)+"`,";
        }

        String query = "SELECT `"+selectedItem + "_" + article + "_" + subsystem + "`.`id`, `режимы_"+ selectedItem + "_" + article + "_" + subsystem + "`.`idрежима`,"+resources+"\n" +
                "FROM ка.изделия\n" +
                "inner join `" + article + " on изделия`.имя_изделия=`" + article + "`.имя\n" +
                "inner join `" + subsystem + "_" + article + "` on `" + article + "`.подсистемы=`" + subsystem + "_" + article + "`.idподсистема\n" +
                "left join `датчики_" + article + "_" + subsystem + "` on `" + subsystem + "_" + article + "`.idдатчика=`датчики_" + article + "_" + subsystem + "`.idдатчики\n" +
                "inner join `" + selectedItem + "_" + article + "_" + subsystem + "` on `датчики_" + article + "_" + subsystem + "`.название=`" + selectedItem + "_" + article + "_" + subsystem + "`.idдатчик\n" +
                "inner join `режимы_" + selectedItem + "_" + article + "_" + subsystem + "` on `" + selectedItem + "_" + article + "_" + subsystem + "`.режимы=`режимы_" + selectedItem + "_" + article + "_" + subsystem + "`.idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    String consumption="";
                    for(int i=0;i<resourceConsumption.size();i++){
                        if(i<resourceConsumption.size()-1){
                            consumption+=resultSet.getString("потребление_ресурса"+(i+1)+"") + "\t";
                        }
                        else
                            consumption+=resultSet.getString("потребление_ресурса"+(i+1)+"");
                    }
                    stringVector.add(resultSet.getString("idрежима") + "\t" + consumption);
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<String> getDeviceNames(String articleName, String subsystemName) {
        String query = "SELECT idприбор_for\n" +
                "FROM `" + subsystemName + "_" + articleName + "`\n" +
                "INNER JOIN `приборы_" + articleName + "_" + subsystemName + "` ON `" + subsystemName + "_" + articleName + "`.idприбора=`приборы_" + articleName + "_" + subsystemName + "`.idприборы";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        }
        return stringVector;
    }
    Vector<String> getSensorNames(String articleName, String subsystemName) {
        String query = "SELECT  название\n" +
                "FROM `" + subsystemName + "_" + articleName + "`\n" +
                "INNER JOIN `датчики_" + articleName + "_" + subsystemName + "` ON `" + subsystemName + "_" + articleName + "`.idдатчика=`датчики_" + articleName + "_" + subsystemName + "`.idдатчики";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        }
        return stringVector;
    }

    private int execUpdate(String query) {
        try {
            savepoint = connection.setSavepoint();
            connection.setAutoCommit(false);
            int result = statement.executeUpdate(query);
            connection.commit();
            return result;
        } catch (SQLException exc) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException rollbackExc) {
                return SQL_EXCEPTION;
            }
            return SQL_EXCEPTION;
        }
    }

    int saveToDB(String article, String name, Vector<SystemInfo> systemInfoVector) {
        String query = "CREATE TABLE `ка`.`" + name + "_" + article + "` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `изделие` VARCHAR(45) NULL,\n" +
                "  `подсистема` VARCHAR(45) NULL,\n" +
                "  `устройство` VARCHAR(45) NULL,\n" +
                "  `режим` VARCHAR(45) NULL,\n" +
                "  `задержка` VARCHAR(45) NULL,\n" +
                "  `отношение` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE INDEX `idname_UNIQUE` (`id` ASC))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8;\n";
        String addQuery = "INSERT INTO `" + article + "_алгоритмы` (`имя_алгоритма`) VALUES('" + name + "');";
        int result1 = execUpdate(addQuery);
        int result = execUpdate(query);

        int verRes = verifyResult(result);
        int verRes1 = verifyResult(result1);
        if (verRes == CLASS_NOT_FOUND || verRes1 == CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if (verRes == SQL_EXCEPTION || verRes1 == SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if (verRes == CLASS_CAST_EXCEPTION || verRes1 == CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        for (SystemInfo systemInfo : systemInfoVector) {

            String insertQuery = "INSERT INTO `ка`.`" + name + "_" + article + "` (`изделие`,`подсистема`,`устройство`,`режим`,`задержка`,`отношение`)\n" +
                    "VALUES('" + systemInfo.getArticle() + "','" + systemInfo.getSubsystem() + "','" + systemInfo.getDeviceName() + "','" + systemInfo.getMode() + "','" + systemInfo.getDelay() + "','" + systemInfo.getRelation() + "');";
            result = execUpdate(insertQuery);
            verRes = verifyResult(result);
            if (verRes == CLASS_NOT_FOUND) {
                return CLASS_NOT_FOUND;
            }
            if (verRes == SQL_EXCEPTION) {
                return SQL_EXCEPTION;
            }
            if (verRes == CLASS_CAST_EXCEPTION) {
                return CLASS_CAST_EXCEPTION;
            }

        }
        return OK;
    }

    Object openQuery(String name) {
        String query = "SELECT * FROM `ка`.`" + name + "`;";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    String article = resultSet.getString("изделие") + " ";
                    String subsystem = resultSet.getString("подсистема") + " ";
                    String deviceName = resultSet.getString("устройство") + " ";
                    String mode = resultSet.getString("режим");
                    String delay = resultSet.getString("задержка");
                    String relation = resultSet.getString("отношение");
                    if (relation.length() == 0)
                        relation = "";
                    SystemInfo systemInfo = new SystemInfo(article, subsystem, deviceName, mode, delay, relation);
                    MainWindow.getSystemInfoVector().add(systemInfo);
                }
            } catch (SQLException e) {
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return MainWindow.getSystemInfoVector();
    }
    ArrayList<Double> getAllResources() {
        ArrayList<Double> resultList = new ArrayList<>();
        String query = "SELECT * FROM `ка`.`ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add((double) CLASS_NOT_FOUND);
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add((double) SQL_EXCEPTION);
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add((double) CLASS_CAST_EXCEPTION);
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_1")));
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_2")));
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_3")));
            }
        } catch (SQLException sqlE) {
            resultList.add((double) SQL_EXCEPTION);
            return resultList;
        }
        return resultList;
    }

    Vector<Vector<String>> getResourcesCountAndNamesAndMaxValue(String articleName) {
        //Подстраиваться под ресуры
        Vector<String> resourceVector = new Vector<>();
        Vector<String> vectorOfNames = getArticleMeasurements(articleName);
        Vector<Vector<String>> resultVector = new Vector<>();
        String resources="";
        Vector<String> vectorOfColumns=new Vector<>();
        for (int i=0;i<vectorOfNames.size();i++){
            vectorOfColumns.add("ресурс_"+(i+1));
        }
        String query = "SELECT * FROM `ка`.`"+articleName+"_ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            vectorOfNames.add(Integer.toString(CLASS_NOT_FOUND));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            vectorOfNames.add(Integer.toString(CLASS_CAST_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        if (test == SQL_EXCEPTION) {
            vectorOfNames.add(Integer.toString(SQL_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }

        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                for(String str:vectorOfColumns){
                    resourceVector.add(resultSet.getString(str));
                }
            }
        } catch (SQLException SQLexc) {
            vectorOfNames.clear();
            vectorOfNames.add(Integer.toString(SQL_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        resultVector.add(vectorOfNames);
        resultVector.add(resourceVector);
        return resultVector;
    }
    Vector<String> getModeNames(String article) {
        Vector<String> modeNames = new Vector<>();
        String query = "SELECT * FROM `ка`.`"+article+"_ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_CAST_EXCEPTION) {
        }
        if (test == CLASS_NOT_FOUND) {
        }
        if (test == SQL_EXCEPTION) {
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                modeNames.add(resultSet.getString("idрежима"));
            }
        } catch (SQLException ex) {
        }
        return modeNames;
    }
    Vector<String> queryToAlgorithms(String article) {
        String query = "SELECT * FROM `" + article + "_алгоритмы`";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("имя_алгоритма"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    Vector<Vector<String>> getAlgorithmInfo(String article, String algorithmName) {
        String query = "SELECT * FROM `ка`.`" + algorithmName + "_" + article + "`;";
        Vector<Vector<String>> result=new Vector<>();
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector=new Vector<>();
                    stringVector.add(resultSet.getString("изделие") );
                    stringVector.add(resultSet.getString("подсистема") );
                    stringVector.add(resultSet.getString("устройство") );
                    stringVector.add(resultSet.getString("режим")  );
                    stringVector.add(resultSet.getString("задержка") );
                    stringVector.add(resultSet.getString("отношение"));
                    result.add(stringVector);
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return result;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return result;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return result;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return result;
        }
        return result;
    }
    Vector<String> getArticleResources(String articleName) {
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы`";
        Vector<String> resultList = new Vector<>();
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;

    }
    Vector<String> getArticleResourceNames(String articleName) {
        Vector<String> resultList = new Vector<>();
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы_наименования`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;
    }
    Vector<String> getUsedArticleResourcesIfEmpty(String articleName) {
        Vector<String> resultVector = new Vector<>();
        try {
            BufferedReader reader = Files.newBufferedReader(MainWindow.getUsedResourcesPath());
            String line = "";
            while ((line = reader.readLine()) != null) {
                if ((line.length()>1)&&line.substring(line.indexOf("<"), line.indexOf(">")).equals(articleName)) {
                    line = line.replace("<" + articleName + ">", "");
                    line = line.trim();
                    String[] splittedLine = line.split("\t");
                    resultVector.addAll(Arrays.asList(splittedLine));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultVector;
    }
    Vector<String> getArticleMeasurements(String articleName){
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы_наименования`";
        Vector<String> resultList = new Vector<>();
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;

    }
    String getUsedArticleResources(String articleName) {
        String line = "";
        try {
            BufferedReader reader = Files.newBufferedReader(MainWindow.getUsedResourcesPath());
            while ((line = reader.readLine()) != null) {
                if ((line.length()>1)&&line.substring(line.indexOf("<"), line.indexOf(">") + 1).equals("<" + articleName + ">")) {
                    line = line.replace("<" + articleName + ">", "");
                    line = line.trim();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
    Vector<String> getResourcesNames(String articleName){
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы_названия`";
        Vector<String> resultList = new Vector<>();
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            int i=0;
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;

    }

    //For ThreadDBUpdater
    int addArticle(String articleName, Integer lastIndex, ArrayList<String> columns, ArrayList<Double> columnsValues, ArrayList<String> valuesMeasurement) {
        lastIndex++;
        String addResourcesQueryString = "";
        String insertResourcesQueryString = "";
        String insertResourcesNamesQueryString = "";
        String insertSecondPart="";
        for (int i = 0; i < columns.size(); i++) {
            int b=i+1;
            //addResourcesQueryString += "`"+columns.get(i)+"` varchar(45) DEFAULT NULL,\n";
            addResourcesQueryString += "`ресурс_"+b+"` varchar(45) DEFAULT NULL,\n";
            if(i!=(columns.size()-1)) {
                insertSecondPart+="'"+columns.get(i)+"',";
                insertResourcesQueryString += "'" + columnsValues.get(i) + "',";
                insertResourcesNamesQueryString += "'" + valuesMeasurement.get(i)+"',";
            }
            else{
                insertSecondPart+="'"+columns.get(i)+"'";
                insertResourcesQueryString += "'" + columnsValues.get(i) + "'";
                insertResourcesNamesQueryString += "'" + valuesMeasurement.get(i)+"'";
            }
        }
        String createResourceNamesQuery="CREATE TABLE `ка`.`"+articleName+"_ресурсы_названия` (\n" +
                "  `id` INT NOT NULL,\n" +addResourcesQueryString+
                "  PRIMARY KEY (`id`))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8;\n";
        String insertIntoResourceNames="INSERT INTO "+articleName+"_ресурсы_названия VALUES (1,"+insertSecondPart+");";
        String createResourcesQuery = " CREATE TABLE `" + articleName + "_ресурсы` (\n" +
                "  `id` int(11) NOT NULL,\n" + addResourcesQueryString +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String insertIntoResourcesQuery = "INSERT INTO `" + articleName + "_ресурсы` VALUES(1," + insertResourcesQueryString + ")";
        String createResourcesNamesQuery = " CREATE TABLE `" + articleName + "_ресурсы_наименования` (\n" +
                "  `id` int(11) NOT NULL,\n" + addResourcesQueryString +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String insertResourcesNamesQuery = "INSERT INTO `" + articleName + "_ресурсы_наименования` VALUES(1," + insertResourcesNamesQueryString + " )";
        String addQuery = "INSERT INTO `ка`.`изделия`(имя_изделия) VALUES('" + articleName + "');";
        String createQuery = " CREATE TABLE `" + articleName + "` (\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  `имя` varchar(45) DEFAULT NULL,\n" +
                "  `подсистемы` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `id_UNIQUE` (`id`),\n" +
                "  UNIQUE KEY `подсистемы_UNIQUE` (`подсистемы`),\n" +
                "  KEY `FK_имя_изделия` (`имя`),\n" +
                "  CONSTRAINT `FK_имя_изделия_" + articleName + "` FOREIGN KEY (`имя`) REFERENCES `изделия` (`имя_изделия`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
        String createAlgQuery = " CREATE TABLE `" + articleName + "_алгоритмы` (\n" +
                "  `id_алгоритмы` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `имя_алгоритма` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id_алгоритмы`),\n" +
                "  UNIQUE KEY `имя алгоритма_UNIQUE` (`имя_алгоритма`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        try {
            savepoint = connection.setSavepoint();
            connection.setAutoCommit(false);
            statement.executeUpdate(createResourceNamesQuery);
            statement.executeUpdate(insertIntoResourceNames);
            statement.executeUpdate(createResourcesQuery);
            statement.executeUpdate(createResourcesNamesQuery);
            statement.executeUpdate(insertIntoResourcesQuery);
            statement.executeUpdate(insertResourcesNamesQuery);
            statement.executeUpdate(createQuery);
            statement.executeUpdate(addQuery);
            statement.executeUpdate(createAlgQuery);

            connection.commit();

        } catch (SQLException ex) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException rollbackExc) {
                return SQL_EXCEPTION;
            }
        }
        return OK;
    }

    int addSubsystem(String articleName,String subsystemName,Integer lastIndex){
        //Поставить константы,Запрос на добавление в подсистемы
        //Вычислять индекс самостоятельно
        String addSensorsTable="CREATE TABLE `ка`.`датчики_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idдатчики` int(11) NOT NULL,\n" +
                "  `название` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`название`,`idдатчики`),\n" +
                "  UNIQUE KEY `название_UNIQUE` (`название`),\n" +
                "  UNIQUE KEY `idдатчики_UNIQUE` (`idдатчики`),\n" +
                "  CONSTRAINT `FK_idдатчики_"+subsystemName+"_"+articleName+"` FOREIGN KEY (`idдатчики`) REFERENCES `"+subsystemName+"_"+articleName+"` (`idдатчика`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String addDevicesTable="CREATE TABLE `ка`.`приборы_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idприборы` int(11) DEFAULT NULL,\n" +
                "  `idприбор_for` varchar(45) DEFAULT NULL,\n" +
                "  `id` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idприбор_1_idx` (`idприбор_for`),\n" +
                "  KEY `FK_idприборы_idx` (`idприборы`),\n" +
                "  CONSTRAINT `FK_idприборы_"+subsystemName+"_"+articleName+"` FOREIGN KEY (`idприборы`) REFERENCES `"+subsystemName+"_"+articleName+"` (`idприбора`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
        String addQuery="INSERT INTO "+articleName+" VALUES ("+lastIndex+1+",'"+articleName+"','"+subsystemName+"');";
        String createQuery=" CREATE TABLE `ка`.`"+subsystemName+"_"+articleName+"` (\n" +
                "  `idподсистема` varchar(45) NOT NULL,\n" +
                "  `idприбора` int(11) DEFAULT NULL,\n" +
                "  `idдатчика` int(11) DEFAULT NULL,\n" +
                "  UNIQUE KEY `idприбора_UNIQUE` (`idприбора`),\n" +
                "  UNIQUE KEY `idдатчика_UNIQUE` (`idдатчика`),\n" +
                "  KEY `FK_idподсистема_idx` (`idподсистема`),\n" +
                "  CONSTRAINT `FK_idподсистема"+articleName+"_"+subsystemName+"` FOREIGN KEY (`idподсистема`) REFERENCES `"+articleName+"` (`подсистемы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8                           ";
        int resultState=execUpdate(createQuery);
        int resultState1=execUpdate(addQuery);
        int resultState2=execUpdate(addSensorsTable);
        int resultState3=execUpdate(addDevicesTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }
    //lastIndex здесь-индекс устройства
    int addDevice(String articleName,String subsystemName, String deviceName,Integer lastIndex){
        lastIndex++;
        String subsystemAddQuery="INSERT INTO `ка`.`"+subsystemName+"_"+articleName+"` (idподсистема,idприбора) VALUES('"+subsystemName+"',"+lastIndex+");\n";
        String addQuery="INSERT INTO `ка`.`приборы_"+articleName+"_"+subsystemName+"` VALUES("+lastIndex+",'"+deviceName+"','"+lastIndex+"');";
        String createQuery=" CREATE TABLE `ка`.`"+deviceName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idприбор` varchar(45) DEFAULT NULL,\n" +
                "  `режимы` varchar(45) NOT NULL,\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  PRIMARY KEY (`id`,`режимы`),\n" +
                "  KEY `idрежима_idx` (`режимы`),\n" +
                "  KEY `FK_прибор` (`idприбор`),\n" +
                "  CONSTRAINT `FK_прибор"+articleName+"_"+deviceName+"` FOREIGN KEY (`idприбор`) REFERENCES `приборы_"+articleName+"_"+subsystemName+"` (`idприбор_for`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        Vector<String> resourceVector=getArticleResources(articleName);
        String resources="";
        for(int i=0;i<resourceVector.size();i++){
            resources+="`потребление_ресурса"+(i+1)+"` double DEFAULT NULL,\n";
        }
        //FK can be tool long for table creation so ive decieded to remove subsystemName from it "+articleName+"_"+subsystemName+"_"+deviceName+"
        String createModeTable=" CREATE TABLE `ка`.`режимы_"+deviceName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idрежима` varchar(45) NOT NULL,\n" +resources+
                "  PRIMARY KEY (`idрежима`),\n" +
                "  UNIQUE KEY `idрежимы_датчик1_UNIQUE` (`idрежима`),\n" +
                "  KEY `FK_idрежима_д1_idx` (`idрежима`),\n" +
                "  CONSTRAINT `FK_idрежимы_"+articleName+"_"+deviceName+"` FOREIGN KEY (`idрежима`) REFERENCES `"+deviceName+"_"+articleName+"_"+subsystemName+"` (`режимы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        int resultState2=execUpdate(subsystemAddQuery);
        int resultState1=execUpdate(addQuery);
        int resultState=execUpdate(createQuery);
        int resultState3=execUpdate(createModeTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }
    //lastIndex здесь-индекс датчика
    int addSensor(String articleName,String subsystemName, String sensorName,Integer lastIndex){
        lastIndex++;
        String subsystemAddQuery="INSERT INTO `ка`.`"+subsystemName+"_"+articleName+"` (idподсистема,idдатчика) VALUES('"+subsystemName+"',"+lastIndex+");\n";
        String addQuery="INSERT INTO `ка`.`датчики_"+articleName+"_"+subsystemName+"` VALUES("+lastIndex+",'"+sensorName+"');";
        String createQuery=" CREATE TABLE `ка`.`"+sensorName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "`idдатчик` varchar(45) DEFAULT NULL,\n" +
                "  `режимы` varchar(45) NOT NULL,\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  PRIMARY KEY (`id`,`режимы`),\n" +
                "  UNIQUE KEY `id_UNIQUE` (`id`),\n" +
                "  UNIQUE KEY `режимы_д1_UNIQUE` (`режимы`),\n" +
                "  KEY `FK_название_датчика_idx_"+articleName+"_"+subsystemName+"_"+sensorName+"+` (`idдатчик`),\n"+
                "  CONSTRAINT `FK_прибор"+articleName+"_"+sensorName+"` FOREIGN KEY (`idдатчик`) REFERENCES `датчики_"+articleName+"_"+subsystemName+"` (`название`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        Vector<String> resourceVector=getArticleResources(articleName);
        String resources="";
        for(int i=0;i<resourceVector.size();i++){
            resources+="`потребление_ресурса"+(i+1)+"` double DEFAULT NULL,\n";
        }

        String createModeTable=" CREATE TABLE `ка`.`режимы_"+sensorName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idрежима` varchar(45) NOT NULL,\n" +resources+
                "  PRIMARY KEY (`idрежима`),\n" +
                "  UNIQUE KEY `idрежимы_датчик1_UNIQUE` (`idрежима`),\n" +
                "  KEY `FK_idрежима_д1_idx` (`idрежима`),\n" +
                "  CONSTRAINT `FK_idрежимы_датчик1_"+articleName+"_"+subsystemName+"_"+sensorName+"` FOREIGN KEY (`idрежима`) REFERENCES `"+sensorName+"_"+articleName+"_"+subsystemName+"` (`режимы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

        int resultState2=execUpdate(subsystemAddQuery);
        int resultState1=execUpdate(addQuery);
        int resultState=execUpdate(createQuery);
        int resultState3=execUpdate(createModeTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }

    int addMode(String articleName,String subsystemName,String deviceName,String mode,Vector<Double> resourceConsumption,Integer lastIndex) {
        //create table with modes,add mode to device and add mode to mode table
        //Получить ресурсы изделия, узнать количество, сформировать запрос
        lastIndex++;
        String addModeToDeviceQuery="INSERT INTO `ка`.`"+deviceName+"_"+articleName+"_"+subsystemName+"` VALUES ('"+deviceName+"','"+mode+"',"+lastIndex+")";
        String secondAddPart="";
        for(int i=0;i<resourceConsumption.size();i++){
            if(i==resourceConsumption.size()-1){
                secondAddPart+=Double.toString(resourceConsumption.get(i));
            }
            else
                secondAddPart+=Double.toString(resourceConsumption.get(i))+", ";

        }
        String addQuery="INSERT INTO `ка`.`режимы_"+deviceName+"_"+articleName+"_"+subsystemName+"` " +
                "VALUES('"+mode+"',"+secondAddPart+");";
        int resultState1=execUpdate(addModeToDeviceQuery);
        int resultState=execUpdate(addQuery);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }

    // TODO: 11/14/2017 Make it better
    //TODO: TRY TO MAKE IT FUCKING BETTER
    void changeArticleResources(String article, Vector<JTextField> textFieldVector) {
        //И обновить таблицы всех ресурсов режжимов и энергопотребление
        int count = 1;
        Vector<Integer> emptyFieldsIndexes=new Vector<>();
        for (int i = 0; i < textFieldVector.size(); i += 3) {
            String textFieldVal = textFieldVector.get(i).getText();
            if(textFieldVal.isEmpty())
                emptyFieldsIndexes.add(i/3);
            if(!textFieldVal.isEmpty())
                count++;
            }

        Vector<String> defaultArticleResources = getArticleResources(article);
        int defaultSize = defaultArticleResources.size();
        int res;

        //TODO: Запрос и изменение таблиц потребления режимов у данного изделия through ALTER
        Vector<String> subsystems = queryToArticle(article);
        for (String subsystem : subsystems) {
            Vector<String> sensorName = getSensorNames(article, subsystem);
            Vector<String> deviceName = getDeviceNames(article, subsystem);
            if (sensorName.size() > 0) {
                for (String str : sensorName) {
                    int differ = (count - 1) - defaultSize;
                    int currentTableSize = getModeColumnsCount(article, subsystem, str);
                    if (differ > 0) {
                        String add = "";
                        int sum = currentTableSize + differ;
                        for (; currentTableSize < sum; currentTableSize++) {
                            if (currentTableSize < (sum - 1)) {
                                add += "ADD COLUMN `потребление_ресурса" + (currentTableSize + 1) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + currentTableSize + "`,\n";

                            } else {
                                add += "ADD COLUMN `потребление_ресурса" + (currentTableSize + 1) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + currentTableSize + "`;\n";

                            }
                        }
                        //ADDING
                        String addQuery = "ALTER TABLE `ка`.`режимы_" + str + "_" + article + "_" + subsystem + "` \n" + add;
                        res = execUpdate(addQuery);
                    }
                    if (differ < 0) {
                        Vector<String> pastModeUsage = new Vector<>();
                        Vector<String> resultVec = queryToSensor(article.toLowerCase(), subsystem.toLowerCase(), str.toLowerCase());

                        String[] splittedVec = resultVec.get(0).split("\t");
                        for (String string : splittedVec)
                            pastModeUsage.add(string);
                        pastModeUsage.remove(0);
                        for (int i : emptyFieldsIndexes) {
                            pastModeUsage.remove(i - 2);
                        }
                        Vector<String> resourceConsumptionVector = new Vector<>();
                        //DROPPING
                        String resourceConsumption = "потребление_ресурса1";
                        resourceConsumptionVector.add(resourceConsumption);
                        String dropPart = "";
                        String addPart = "ADD COLUMN `потребление_ресурса" + 1 + "` DOUBLE NULL DEFAULT 0 AFTER `idрежима`,\n";
                        for (int i = 0; i < defaultSize; i++) {
                            if (i < (defaultSize - 1)) {
                                dropPart += "DROP COLUMN `потребление_ресурса" + (i + 1) + "`,\n";
                            } else {
                                dropPart += "DROP COLUMN `потребление_ресурса" + (i + 1) + "`;\n";
                            }
                        }
                        for (int i = 0; i < (defaultSize + differ - 1); i++) {
                            if (i < (defaultSize - 1)) {
                                addPart += "ADD COLUMN `потребление_ресурса" + (i + 2) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + (i + 1) + "`,\n";
                                resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                            } else {
                                addPart += "ADD COLUMN `потребление_ресурса" + (i + 2) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + (i + 1) + "`;\n";
                                resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                            }
                        }
                        if (dropPart.lastIndexOf(',') == (dropPart.length() - 2)) {
                            dropPart = dropPart.substring(0, dropPart.length() - 2);
                        }
                        if (addPart.lastIndexOf(',') == (addPart.length() - 2)) {
                            addPart = addPart.substring(0, addPart.length() - 2);
                        }
                        String dropModeQuery = "ALTER TABLE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` \n" + dropPart;
                        res = execUpdate(dropModeQuery);
                        String addModeQuery = "ALTER TABLE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` \n" + addPart;
                        res = execUpdate(addModeQuery);
                        Vector<String> secondInsertPart = new Vector<>();
                        for (int i = 0; i < pastModeUsage.size(); i++) {
                            if (i < (pastModeUsage.size() - 1))
                                secondInsertPart.add(resourceConsumptionVector.get(i) + "=" + pastModeUsage.get(i));
                            else
                                secondInsertPart.add(resourceConsumptionVector.get(i) + "=" + pastModeUsage.get(i));
                        }
                        for (int i = 0; i < secondInsertPart.size(); i++) {
                            String insertToModeQuery = "UPDATE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` SET " + secondInsertPart.get(i) + " WHERE idрежима='режим1'";
                            res = execUpdate(insertToModeQuery);
                        }
                        int c = 0;
                    }
                }
            }
            if (deviceName.size() > 0) {
                for (String str : deviceName) {
                    int differ = (count - 1) - defaultSize;
                    int currentTableSize = getModeColumnsCount(article, subsystem, str);
                    if (differ > 0) {
                        String add = "";
                        int sum = currentTableSize + differ;
                        for (; currentTableSize < sum; currentTableSize++) {
                            //TODO : проверка на наличие столбца потребление ресурса. Если нет, то первую вставить после idрежима КАКОГО ХЕРА sum==1?
                            if (currentTableSize < (sum - 1)) {
                                add += "ADD COLUMN `потребление_ресурса" + (currentTableSize + 1) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + currentTableSize + "`,\n";
                            } else
                                add += "ADD COLUMN `потребление_ресурса" + (currentTableSize + 1) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + currentTableSize + "`;\n";
                        }
                        //ADDING
                        String addQuery = "ALTER TABLE `ка`.`режимы_" + str + "_" + article + "_" + subsystem + "` \n" + add;
                        res = execUpdate(addQuery);
                        int c = 0;
                    }
                    if (differ < 0) {
                        Vector<String> pastModeUsage = new Vector<>();
                        Vector<String> resultVec = queryToDevice(article.toLowerCase(), subsystem.toLowerCase(), str.toLowerCase());

                        String[] splittedVec = resultVec.get(0).split("\t");
                        for (String string : splittedVec)
                            pastModeUsage.add(string);
                        pastModeUsage.remove(0);
                        while (pastModeUsage.removeElement("0")) {
                        }
                        //for(int i:emptyFieldsIndexes){
                        //  pastModeUsage.remove(i);
                        //}
                        Vector<String> resourceConsumptionVector = new Vector<>();
                        //DROPPING
                        String resourceConsumption = "потребление_ресурса1";
                        resourceConsumptionVector.add(resourceConsumption);
                        String dropPart = "";
                        String addPart = "ADD COLUMN `потребление_ресурса" + 1 + "` DOUBLE NULL DEFAULT 0 AFTER `idрежима`,\n";
                        for (int i = 0; i < defaultSize; i++) {
                            if (i < (defaultSize - 1)) {
                                dropPart += "DROP COLUMN `потребление_ресурса" + (i + 1) + "`,\n";
                            } else {
                                dropPart += "DROP COLUMN `потребление_ресурса" + (i + 1) + "`;\n";
                            }
                        }
                        for (int i = 0; i < (defaultSize + differ - 1); i++) {
                            if (i < (defaultSize - 1)) {
                                addPart += "ADD COLUMN `потребление_ресурса" + (i + 2) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + (i + 1) + "`,\n";
                                resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                            } else {
                                addPart += "ADD COLUMN `потребление_ресурса" + (i + 2) + "` DOUBLE NULL DEFAULT 0 AFTER `потребление_ресурса" + (i + 1) + "`;\n";
                                resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                            }
                        }
                        if (dropPart.lastIndexOf(',') == (dropPart.length() - 2)) {
                            dropPart = dropPart.substring(0, dropPart.length() - 2);
                        }
                        if (addPart.lastIndexOf(',') == (addPart.length() - 2)) {
                            addPart = addPart.substring(0, addPart.length() - 2);
                        }
                        String dropModeQuery = "ALTER TABLE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` \n" + dropPart;
                        res = execUpdate(dropModeQuery);
                        String addModeQuery = "ALTER TABLE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` \n" + addPart;
                        res = execUpdate(addModeQuery);
                        Vector<String> secondInsertPart = new Vector<>();
                        for (int j = 0; j < resultVec.size(); j++) {
                            pastModeUsage.clear();
                            secondInsertPart.clear();
                            resourceConsumptionVector.clear();
                            splittedVec = resultVec.get(j).split("\t");
                            for (String string : splittedVec)
                                pastModeUsage.add(string);
                            pastModeUsage.remove(0);
                            while (pastModeUsage.removeElement("0")) {
                            }
                            resourceConsumption = "потребление_ресурса1";
                            resourceConsumptionVector.add(resourceConsumption);
                            for (int i = 0; i < (defaultSize + differ - 1); i++) {
                                if (i < (defaultSize - 1)) {
                                    resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                                } else {
                                    resourceConsumptionVector.add("потребление_ресурса" + (i + 2));
                                }
                            }
                            for (int i = 0; i < pastModeUsage.size(); i++) {
                                if (i < (pastModeUsage.size() - 1))
                                    secondInsertPart.add(resourceConsumptionVector.get(i) + "=" + pastModeUsage.get(i));
                                else
                                    secondInsertPart.add(resourceConsumptionVector.get(i) + "=" + pastModeUsage.get(i));
                            }
                            for (int i = 0; i < secondInsertPart.size(); i++) {
                                String mode = resultVec.get(j).split("\t")[0];
                                String insertToModeQuery = "UPDATE `ка`.`режимы_" + str.toLowerCase() + "_" + article + "_" + subsystem.toLowerCase() + "` SET " + secondInsertPart.get(i) + " WHERE idрежима='" + mode + "'";
                                res = execUpdate(insertToModeQuery);
                                int c = 0;
                            }
                        }
                    }
                }
            }
        }
        String secondPart = "";
        String insertResourcesQueryString = "";
        String insertResourcesNamesString="";
         defaultArticleResources = getArticleResources(article);
         defaultSize = defaultArticleResources.size();
        String dropQuery = "DROP TABLE `ка`.`" + article + "_ресурсы`;";
         res = execUpdate(dropQuery);
         count = 1;
        String insertResourcesNamesQueryString = "";
        for (int i = 0; i < textFieldVector.size(); i += 3) {
            String nameFieldVal=textFieldVector.get(i).getText();
            String textFieldVal = textFieldVector.get(i+1).getText();
            if (!textFieldVal.isEmpty()) {
                if (count == (textFieldVector.size() / 3)) {
                    insertResourcesNamesString+="'"+nameFieldVal+"'";
                    insertResourcesQueryString += "'" + textFieldVector.get(i+1).getText() + "'";
                    insertResourcesNamesQueryString += "'" + textFieldVector.get(i + 2).getText() + "'";

                } else {
                    insertResourcesNamesString+="'"+nameFieldVal+"',";
                    insertResourcesQueryString += "'" + textFieldVector.get(i+1).getText() + "',";
                    insertResourcesNamesQueryString += "'" + textFieldVector.get(i + 2).getText() + "',";
                }
                secondPart += "`ресурс_" + count + "` varchar(45) DEFAULT NULL,\n";
                count++;
            }

        }
        if(insertResourcesQueryString.lastIndexOf(',')==(insertResourcesQueryString.length()-1)){
            insertResourcesQueryString=insertResourcesQueryString.substring(0,insertResourcesQueryString.length()-1);
        }
        if(insertResourcesNamesQueryString.lastIndexOf(',')==(insertResourcesNamesQueryString.length()-1)){
            insertResourcesNamesQueryString=insertResourcesNamesQueryString.substring(0,insertResourcesNamesQueryString.length()-1);
        }
        if(insertResourcesNamesString.lastIndexOf(',')==(insertResourcesNamesString.length()-1)){
            insertResourcesNamesString=insertResourcesNamesString.substring(0,insertResourcesNamesString.length()-1);
        }
        String dropResourceNamesQuery="DROP TABLE `ка`.`"+article+"_ресурсы_названия`;";
        res=execUpdate(dropResourceNamesQuery);
        String createResourceNamesQuery="CREATE TABLE `ка`.`"+article+"_ресурсы_названия` (\n" +
                "  `id` INT NOT NULL,\n" +secondPart+
                "  PRIMARY KEY (`id`))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8;\n";
        res=execUpdate(createResourceNamesQuery);
        String insertIntoResourcesNames="INSERT INTO `"+article+"_ресурсы_названия` VALUES (1,"+insertResourcesNamesString+")";
        res=execUpdate(insertIntoResourcesNames);
        String createQuery = " CREATE TABLE `" + article + "_ресурсы` (\n" +
                "  `id` int(11) NOT NULL,\n" + secondPart +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        res = execUpdate(createQuery);
        String insertQuery = "INSERT INTO `" + article + "_ресурсы` VALUES(1," + insertResourcesQueryString + ")";
        res = execUpdate(insertQuery);
        dropQuery = "DROP TABLE `ка`.`" + article + "_ресурсы_наименования`;";
        res = execUpdate(dropQuery);
        String createMeasTable = " CREATE TABLE `" + article + "_ресурсы_наименования` (\n" +
                "  `id` int(11) NOT NULL,\n" + secondPart +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        res = execUpdate(createMeasTable);
        String insertMeas = "INSERT INTO `" + article + "_ресурсы_наименования` VALUES(1," + insertResourcesNamesQueryString + " )";
        res = execUpdate(insertMeas);
        int n = 0;

    }
    void changeModeConsumption(String article,String subsystem,String device,String pastModeName,Vector<JTextField> textFieldVector){
        String newName=textFieldVector.get(0).getText();
        ArrayList<String> newConsumption=new ArrayList<>();
        for(int i=1;i<textFieldVector.size();i++){
            newConsumption.add(textFieldVector.get(i).getText());
        }
        String deleteModeQuery="DELETE FROM `режимы_"+device+"_"+article+"_"+subsystem+"` WHERE idрежима='"+pastModeName+"';";
        int res=execUpdate(deleteModeQuery);
        String secondInsertPart="";
        for(String str:newConsumption ){
            secondInsertPart+=str+",";
        }
        int id=getModeId(article,subsystem,device,pastModeName);
        String updateQuery="UPDATE `ка`.`"+device+"_"+article+"_"+subsystem+"` SET режимы='"+newName+"' WHERE id="+id+"";
        res=execUpdate(updateQuery);
        secondInsertPart=secondInsertPart.substring(0,secondInsertPart.length()-1);
        String insertModeQuery="INSERT INTO `режимы_"+device+"_"+article+"_"+subsystem+"` VALUES ('"+newName+"',"+secondInsertPart+");";
        res=execUpdate(insertModeQuery);
        int i=0;
    }
    private int getModeId(String article,String subsystem,String device,String modeName){
        int resultNumber=0;
        String query="SELECT * FROM `ка`.`"+device+"_"+article+"_"+subsystem+"`;";
        Object resultObject=execQuery(query);
        ResultSet resultSet=(ResultSet)resultObject;
        try {
            while(resultSet.next()){
                String result=resultSet.getString("режимы");
                if(result.equals(modeName)){
                    resultNumber=resultSet.getInt("id");
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultNumber;
    }
    private int getModeColumnsCount(String article,String subsystemName,String deviceName){
        String query="SELECT * FROM `режимы_"+deviceName+"_"+article+"_"+subsystemName+"`";
        int count=0;
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                int i=2;
                while (resultSet.next()) {
                    String result="";
                    while((result=resultSet.getString(i))!=null){
                        count++;
                        i++;
                    }
                }
            } catch (SQLException ex) {

            }
        }
        return count;
    }
}