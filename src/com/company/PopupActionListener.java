package com.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PopupActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        (new ThreadResourceUpdater(e.getActionCommand())).run();
        //executorService.submit(new ThreadResourceUpdater(e.getActionCommand()));
    }
}
