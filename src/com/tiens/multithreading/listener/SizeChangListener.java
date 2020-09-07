package com.tiens.multithreading.listener;

import com.tiens.multithreading.ui.UiCreator;
import com.tiens.multithreading.util.ResizeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class SizeChangListener implements ComponentListener {

    private final JFrame jFrame;

    public SizeChangListener(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Rectangle rectangle = e.getComponent().getBounds();
        UiCreator.width.set((int) rectangle.getWidth());
        UiCreator.height.set((int) rectangle.getHeight());
//        System.out.println(UiCreator.oldWidth.get() + "\t" + UiCreator.oldHeight.get());
        resize(jFrame.getRootPane().getComponents());
        UiCreator.oldWidth.set((int) rectangle.getWidth());
        UiCreator.oldHeight.set((int) rectangle.getHeight());
//        System.out.println(UiCreator.width.get() + "\t" + UiCreator.height.get());
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    private void resize(Component[] components) {
        if (components == null || components.length == 0)
            return;
        for (Component component : components) {
            Rectangle newRectangle = ResizeUtil.resize(component.getBounds());
            component.setBounds(newRectangle);
            if (component instanceof Container)
                resize(((Container) component).getComponents());
        }
    }
}
