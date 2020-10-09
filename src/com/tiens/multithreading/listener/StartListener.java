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
                    setHeader(tempHttpURLConnection);
                    InputStream inputStream1 = tempHttpURLConnection.getInputStream();
                    inputStream1.read();
                    long lengthLong = tempHttpURLConnection.getContentLengthLong();
                    if (lengthLong == -1L) {
                        String s = tempHttpURLConnection.getHeaderField("Content-Length");
                        if (CommonUtil.isNotBlank(s))
                            lengthLong = Long.parseLong(s);
                    }
                    if (lengthLong == -1L)
                        throw new Exception("获取文件大小失败！");
                    String headerField = tempHttpURLConnection.getHeaderField("Content-Disposition");
                    String fileName = "file.tmp";
                    if (CommonUtil.isNotBlank(headerField)) {
                        Matcher matcher = Pattern.compile("filename=\"(.+\\..+)\"").matcher(headerField);
                        if (matcher.find()) {
                            fileName = URLDecoder.decode(matcher.group(1), "UTF-8");
                        }
                    }
                    if ("file.tmp".equals(fileName)) {
                        String substring = urlText.substring(urlText.lastIndexOf("/"));
                        if (substring.contains("?"))
                            substring = substring.split("\\?")[0];
                        Matcher matcher = Pattern.compile(".+\\.[\\w]+").matcher(substring);
                        if (matcher.find())
                            fileName = matcher.group();
                    }
                    if (fileName.startsWith("%"))
                        fileName = URLDecoder.decode(fileName, "UTF-8");
                    tempHttpURLConnection.disconnect();
                    int size;
                    long tempEverySize;
                    if (lengthLong < Constant.SPLIT_LENGTH) {
                        tempEverySize = lengthLong;
                        size = 1;
                    } else {
                        size = threadCount;
                        tempEverySize = lengthLong / threadCount;
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
                        long finalLengthLong = lengthLong;
                        new Thread(() -> {
                            int retry = 0;
                            long begin = (count - 1) * everySize;
                            long end = count * everySize - 1L;
                            if (end > finalLengthLong || count == threadCount)
                                end = finalLengthLong;
                            long finished = 0, totalSize = end - begin;
                            setProgress(jp, 0, totalSize);
                            while (retry < UiCreator.getRetryCount())
                                try {
                                    RandomAccessFile rw = new RandomAccessFile(path + finalFileName, "rw");
                                    rw.seek(begin);
                                    int len;
                                    byte[] bytes = new byte[10240];
                                    InputStream inputStream = getInputStreamByUrl(urlText, begin, end);
                                    while ((len = inputStream.read(bytes)) != -1) {
                                        rw.write(bytes, 0, len);
                                        finished += len;
                                        setProgress(jp, finished, totalSize);
                                    }
                                    rw.close();
                                    inputStream.close();
                                    break;
                                } catch (Exception e) {
                                    retry++;
                                    try {
                                        Thread.sleep(1000L);
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                    System.out.println(count);
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

    private void setProgress(JProgressBar jp, long finished, long totalSize) {
        jp.setString(finished + "/" + totalSize + "");
        if (totalSize != 0L)
            jp.setValue(new BigDecimal(finished).divide(new BigDecimal(totalSize), 9, RoundingMode.CEILING).multiply(new BigDecimal(UiCreator.MAXVALUE)).intValue());
    }

    private void setHeader(HttpURLConnection httpURLConnection) {
        httpURLConnection.setRequestProperty("Accept-Encoding", "identity, gzip, deflate");
        httpURLConnection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
        httpURLConnection.setRequestProperty("Host", "iso.mirrors.ustc.edu.cn");
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        httpURLConnection.setRequestProperty("Cookie", "addr=223.72.98.220");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getInputStreamByUrl(String urlText, long begin, long end) {
        InputStream tempInputStream = null;
        try {
            URL tempUrl = new URL(urlText);
            HttpURLConnection tempHttpURLConnection = (HttpURLConnection) tempUrl.openConnection();
            tempHttpURLConnection.addRequestProperty("Range", "bytes=" + begin + "-" + end);
            setHeader(tempHttpURLConnection);
            tempInputStream = tempHttpURLConnection.getInputStream();
        } catch (IOException e) {
            System.out.println("begin=" + begin + ",end=" + end);
//            e.printStackTrace();
        }
        return tempInputStream;
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
