package com.tiens.multithreading.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class UrlInputListener implements KeyListener, FocusListener {

    private JTextArea jTextArea;

    private boolean isFocus;

    public UrlInputListener(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        jTextArea.setForeground(Color.ORANGE);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (jTextArea.getText().equals("请输入下载文件的url"))
            jTextArea.setText("");
        isFocus = true;
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (jTextArea.getText().length() == 0) {
            jTextArea.setText("请输入下载文件的url");
            jTextArea.setForeground(Color.GRAY);
        }
    }
}
