package com.tiens.multithreading;

import com.tiens.multithreading.ui.UiCreator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class Starter {

    public static void main(String[] args) {
        UiCreator uiCreator = new UiCreator("多线程下载");
        UiCreator.setRetryCount(99);
        uiCreator.setVisible(true);
    }

}
