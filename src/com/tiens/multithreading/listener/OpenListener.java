package com.tiens.multithreading.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class OpenListener implements MouseListener {

    private final String path;

    public OpenListener(String path) {
        this.path = path;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        String[] cmd = new String[5];
        String url = path;
        cmd[0] = "cmd";
        cmd[1] = "/c";
        cmd[2] = "start";
        cmd[3] = " ";
        cmd[4] = url;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
