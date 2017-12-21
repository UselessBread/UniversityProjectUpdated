package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch(ClassNotFoundException cNfexc){cNfexc.printStackTrace();}
        catch(InstantiationException iExc){iExc.printStackTrace();}
        catch(IllegalAccessException illegalExc){}
        catch(UnsupportedLookAndFeelException uExc){}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow.createAndShowGUI();
            }
        });
    }
}
