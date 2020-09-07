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
        /*UiCreator uiCreator = new UiCreator("多线程下载");
        uiCreator.setVisible(true);
        List<String> list = new LinkedList<>();*/
//        trun(100);
        System.out.println(-2147483648-1);
    }

    public static int trun(int n) {
        int i = 1 / n;
        System.out.println(n);
        return trun(n - 1);
    }

}
