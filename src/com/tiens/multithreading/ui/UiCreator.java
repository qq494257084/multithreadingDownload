package com.tiens.multithreading.ui;

import com.tiens.multithreading.listener.*;
import com.tiens.multithreading.util.ResizeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * leeyarlling
 * 2020/9/3
 **/
public class UiCreator extends JFrame {

    public static final Integer MAXVALUE = 10000;

    public static volatile AtomicInteger width = new AtomicInteger(800);

    public static volatile AtomicInteger oldWidth = new AtomicInteger(800);

    public static volatile AtomicInteger height = new AtomicInteger(600);

    public static volatile AtomicInteger oldHeight = new AtomicInteger(600);

    private static Integer retryCount = 99;

    private volatile boolean isPause = true;

    private final String title;

    private int count = 10;

    private String path = "E:\\";

    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    private JPanel progressJPanel;

    public List<JProgressBar> progressBarList = new LinkedList<>();
    public JButton bDownload;
    public JButton bPause;
    public JButton bStop;
    public JTextField tip;
    public JTextArea urlTextArea;
    public JTextField jTThreadCount;

    public UiCreator(String title) {
        this.title = title;
        init();
    }

    public UiCreator(int width, int height, String title) {
        UiCreator.width.set(width);
        UiCreator.height.set(height);
        this.title = title;
    }

    public void init() {
        setTitle(title);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((getScreenWidth() - width.get()) / 2, (getScreenHeight() - height.get()) / 2, width.get(), height.get());
        setBackground(Color.LIGHT_GRAY);
        setMinimumSize(new Dimension(800, 600));
        addComponentListener(new SizeChangListener(this));
        add(addUrl());
        add(addFunction());
        add(addProgress());
        add(addTip());
    }

    private Component addTip() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jPanel.setBounds(0, 530, 800, 30);
        tip = new JTextField();
        tip.setForeground(Color.GRAY);
        tip.setFocusable(false);
        tip.setBackground(null);
        tip.setBorder(null);
        tip.setEditable(false);
        jPanel.add(tip);
        return jPanel;
    }

    private Component addProgress() {
        progressJPanel = new JPanel();
        progressJPanel.setLayout(new BoxLayout(progressJPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(progressJPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setBounds(60, 155, 670, 375);
        jScrollPane.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(40);
        updateProgress(progressJPanel, count);
        return jScrollPane;
    }

    private void updateProgress(JPanel jPanel, int count) {
        jPanel.removeAll();
        progressBarList.clear();
        for (int i = 1; i <= count; i++) {
            JPanel jPanel1 = new JPanel(new FlowLayout());
            jPanel1.setBounds(0, 0, 670, 20);
            JLabel jLabel = new JLabel(String.valueOf(i));
            JProgressBar jProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, UiCreator.MAXVALUE);
            jProgressBar.setPreferredSize(new Dimension(600, 20));
            jProgressBar.setToolTipText("等待下载");
            jProgressBar.setString("等待下载");
            jProgressBar.setStringPainted(true);
            jPanel1.add(jLabel);
            jPanel1.add(jProgressBar);
            jPanel.add(jPanel1);
            progressBarList.add(jProgressBar);
        }
    }

    private Component addFunction() {
        JPanel jPanel = new JPanel(null);
        jPanel.setBounds(ResizeUtil.resize(60, 67, 670, 80));
        jPanel.setBackground(Color.WHITE);
        jPanel.setVisible(true);
        JLabel jLTreadCount = new JLabel("请设置线程数：");
        jLTreadCount.setForeground(Color.BLACK);
        jLTreadCount.setBounds(15, 14, 95, 20);
        this.jTThreadCount = new JTextField(String.valueOf(count));
        jTThreadCount.setForeground(Color.BLACK);
        jTThreadCount.setBounds(115, 14, 30, 20);
        jTThreadCount.setHorizontalAlignment(JTextField.CENTER);
        jTThreadCount.addFocusListener(new ThreadCountListener(this));
        jPanel.add(jLTreadCount);
        jPanel.add(jTThreadCount);
        this.bDownload = new JButton("开始");
        this.bPause = new JButton("暂停");
        this.bStop = new JButton("停止");
        JButton bOpen = new JButton("打开下载位置");
        JButton bSet = new JButton("设置下载位置");
        bDownload.addMouseListener(new StartListener(this));
        bDownload.setForeground(Color.BLACK);
        bDownload.setBounds(150, 15, 60, 25);
        bPause.setForeground(Color.BLACK);
        bPause.setBounds(220, 15, 60, 25);
        bStop.setForeground(Color.BLACK);
        bStop.setBounds(290, 15, 60, 25);
        bOpen.setForeground(Color.BLACK);
        bOpen.setBounds(360, 15, 150, 25);
        bOpen.addMouseListener(new OpenListener(path));
        bSet.setForeground(Color.BLACK);
        bSet.setBounds(518, 15, 150, 25);
        bPause.setEnabled(false);
        bStop.setEnabled(false);
        JPanel jPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel1.setBackground(Color.WHITE);
        jPanel1.setBounds(0, 46, 670, 30);
        JLabel jLabel = new JLabel("下载位置：" + path);
        jLabel.setForeground(Color.GRAY);
        jPanel1.add(jLabel);
        jPanel.add(bDownload);
        jPanel.add(bPause);
        jPanel.add(bStop);
        jPanel.add(bOpen);
        jPanel.add(bSet);
        jPanel.add(jPanel1);
        return jPanel;
    }

    private Component addUrl() {
        JPanel jPanel = new JPanel(null);
        jPanel.setBounds(ResizeUtil.resize(60, 10, 670, 45));
        jPanel.setBackground(Color.WHITE);
        jPanel.setVisible(true);
        JLabel jLabel = new JLabel("下载地址：");
        jLabel.setBounds(15, 14, 65, 20);
        jLabel.setForeground(Color.BLACK);
//        this.urlTextArea = new JTextArea("http://downmini.kugou.com/web/kugou9144.exe");
        this.urlTextArea = new JTextArea("http://iso.mirrors.ustc.edu.cn/CTAN/systems/texlive/Images/texlive.iso");
        urlTextArea.addFocusListener(new UrlInputListener(urlTextArea));
        urlTextArea.setForeground(Color.GRAY);
        urlTextArea.setBackground(new Color(167, 163, 163, 255));
        urlTextArea.setLineWrap(true);
        urlTextArea.addKeyListener(new UrlInputListener(urlTextArea));
        JScrollPane jScrollPane = new JScrollPane(urlTextArea);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setBounds(85, 1, 550, 43);
        jPanel.add(jLabel);
        jPanel.add(jScrollPane);
        return jPanel;
    }

    private int getScreenWidth() {
        return (int) toolkit.getScreenSize().getWidth();
    }

    private int getScreenHeight() {
        return (int) toolkit.getScreenSize().getHeight();
    }

    public int getWidth() {
        return width.get();
    }

    public void setWidth(int width) {
        UiCreator.width.set(width);
    }

    public int getHeight() {
        return height.get();
    }

    public void setHeight(int height) {
        UiCreator.height.set(height);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (isPause) {
            updateProgress(progressJPanel, count);
            progressJPanel.updateUI();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static Integer getRetryCount() {
        return retryCount;
    }

    public static void setRetryCount(Integer retryCount) {
        UiCreator.retryCount = retryCount;
    }

    public List<JProgressBar> getProgressBarList() {
        return progressBarList;
    }

    public void setProgressBarList(List<JProgressBar> progressBarList) {
        this.progressBarList = progressBarList;
    }
}
