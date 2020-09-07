package com.tiens.multithreading.listener;

import com.tiens.multithreading.ui.UiCreator;
import com.tiens.multithreading.util.CommonUtil;
import com.tiens.multithreading.util.Constant;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class StartListener implements MouseListener {

    private UiCreator uiCreator;

    public StartListener(UiCreator uiCreator) {
        this.uiCreator = uiCreator;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        JTextArea urlTextArea = uiCreator.urlTextArea;
        String urlText = urlTextArea.getText();
        String path = uiCreator.getPath();
        if (CommonUtil.isEmpty(urlText)) {
            JOptionPane.showConfirmDialog(uiCreator, "下载url不能为空！", "url为空", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!CommonUtil.isUrl(urlText)) {
            JOptionPane.showConfirmDialog(uiCreator, "你填写的不是标准的url地址！", "url非法", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (CommonUtil.isEmpty(path)) {
            JOptionPane.showConfirmDialog(uiCreator, "下载文件保存路径不能为空！", "保存路径为空", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs)
                    if (CommonUtil.isEmpty(path)) {
                        JOptionPane.showConfirmDialog(uiCreator, "下载文件保存路径创建失败！", "创建失败", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
            }
            new Thread(() -> {
                try {
                    int threadCount = uiCreator.getCount();
                    if (uiCreator.progressBarList.size() != threadCount) {
                        JOptionPane.showConfirmDialog(uiCreator, "请重新运行软件！", "未知错误", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException("线程数与UI进度条数不一致！");
                    }
                    URL tempUrl = new URL(urlText);
                    HttpURLConnection tempHttpURLConnection = (HttpURLConnection) tempUrl.openConnection();
                    long lengthLong = tempHttpURLConnection.getContentLengthLong();
                    String headerField = tempHttpURLConnection.getHeaderField("Content-Disposition");
                    String fileName = "file.tmp";
                    if (CommonUtil.isNotBlank(headerField)) {
                        Matcher matcher = Pattern.compile("filename=\"(.+\\..+)\"").matcher(headerField);
                        if (matcher.find()) {
                            fileName = URLDecoder.decode(matcher.group(1), "UTF-8");
                        }
                    }
                    tempHttpURLConnection.disconnect();
                    int size;
                    long tempEverySize;
                    if (lengthLong < Constant.SPLIT_LENGTH) {
                        tempEverySize = lengthLong;
                        size = 1;
                    } else {
                        size = threadCount;
                        tempEverySize = lengthLong / threadCount;
                        if (lengthLong % tempEverySize != 0)
                            tempEverySize = lengthLong / (threadCount - 1);
                    }
                    long everySize = tempEverySize;
                    RandomAccessFile randomAccessFile = new RandomAccessFile(path + fileName, "rw");
                    randomAccessFile.setLength(lengthLong);
                    randomAccessFile.close();
                    for (int i = 1; i <= threadCount; i++) {
                        final int count = i;
                        JProgressBar jp = uiCreator.progressBarList.get(i - 1);
                        if (i > size) {
                            jp.setString("分割小于1MB不可用");
                            jp.setValue(UiCreator.MAXVALUE);
                            jp.setToolTipText(String.valueOf(Constant.SPLIT_LENGTH));
                        }
                        String finalFileName = fileName;
                        new Thread(() -> {
                            long begin = (count - 1) * everySize;
                            long end = count * everySize - 1L;
                            long finished = 0, totalSize = end - begin + 1;
                            if (end > lengthLong)
                                end = lengthLong;
                            try {
                                URL url = new URL(urlText);
                                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                                c.setRequestProperty("Range", "bytes=" + begin + "-" + end);
                                RandomAccessFile rw = new RandomAccessFile(path + finalFileName, "rw");
                                rw.seek(begin);
                                int len;
                                byte[] bytes = new byte[10240];
                                InputStream inputStream = c.getInputStream();
                                while ((len = inputStream.read(bytes)) != -1) {
                                    rw.write(bytes, 0, len);
                                    finished += len;
                                    jp.setString(finished + "/" + totalSize + "");
                                    jp.setValue(new BigDecimal(finished).divide(new BigDecimal(totalSize), 9, RoundingMode.CEILING).multiply(new BigDecimal(UiCreator.MAXVALUE)).intValue());
                                }
                                rw.close();
                                inputStream.close();
                                c.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
