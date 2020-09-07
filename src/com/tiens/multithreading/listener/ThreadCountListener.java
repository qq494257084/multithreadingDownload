package com.tiens.multithreading.listener;

import com.tiens.multithreading.ui.UiCreator;
import com.tiens.multithreading.util.CommonUtil;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class ThreadCountListener implements FocusListener {

    private final UiCreator uiCreator;

    public ThreadCountListener(UiCreator uiCreator) {
        this.uiCreator = uiCreator;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        String countText = uiCreator.jTThreadCount.getText();
        if ("0".equals(countText) || !countText.matches("\\d+")) {
            JOptionPane.showConfirmDialog(uiCreator, "请填写合法的进程数！", "进程数合法", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            uiCreator.jTThreadCount.setText(null);
            uiCreator.jTThreadCount.requestFocus();
        }else uiCreator.setCount(Integer.parseInt(countText));
    }
}
