package com.tiens.multithreading.util;

import com.tiens.multithreading.ui.UiCreator;

import java.awt.*;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class ResizeUtil {

    public static Rectangle resize(double x, double y, double width, double height) {
        return resize(new Rectangle((int) x, (int) y, (int) width, (int) height));
    }

    public static Rectangle resize(Rectangle rectangle) {
        double x = rectangle.getX(), y = rectangle.getY(), width = rectangle.getWidth(), height = rectangle.getHeight();
       /* x = UiCreator.width.get() * x / UiCreator.oldWidth.get();
        y = UiCreator.height.get() * y / UiCreator.oldHeight.get();
        width = UiCreator.width.get() * width / UiCreator.oldWidth.get();
        height = UiCreator.height.get() * height / UiCreator.oldHeight.get();*/
        double horizontal = UiCreator.width.get() / Double.parseDouble(String.valueOf(UiCreator.oldWidth.get()));
        double vertical = UiCreator.height.get() / Double.parseDouble(String.valueOf(UiCreator.oldHeight.get()));
        x *= horizontal;
        y *= vertical;
        width *= horizontal;
        height *= vertical;
        rectangle.setBounds((int) x, (int) y, (int) width, (int) height);
        return rectangle;
    }

}
