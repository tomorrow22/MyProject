package com.itheima.domain;

import com.itheima.interFace.DisplayListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.server.UID;

//用于表示展示框的类
//JLabel：轻量级组件，用于展示图片/文字（里面嵌套容器可能会出问题）
//JPanel：轻量级容器，里面可以添加其他组件
public class Display_Case extends JPanel implements MouseListener {
    private String UID;//绑定UID，这样每个条目都是一个唯一项。不怕名字重复导致操作失误
    private JLabel imageLabel;
    private JLabel nameJlabel;
    private JLabel textJLabel;
    private JButton bt1;
    private JButton bt2;

    public Display_Case() {
    }
    /*
    * 参数一：图片
    * 参数二：名字
    * 参数三：展示的内容
    * 参数四：按钮一的文本（从左到右）
    * 参数五：按钮二的文本
    * 参数六：按钮一点击后执行的方法
    * 参数七：按钮二点击后执行的方法
    * 参数八：该条目的UID（唯一值）
    *
    * 前五是展示。后面是方法
    * */
    public Display_Case(ImageIcon image, String name, String text, String bt1Text, String bt2Text,  DisplayListener bt1Listener, DisplayListener bt2Listener,String uid) {
        //初始化组件
        this.imageLabel = new JLabel(image);
        this.nameJlabel = new JLabel(name);
        this.textJLabel = new JLabel(text);
        this.bt1 = new JButton(bt1Text);
        this.bt2 = new JButton(bt2Text);
        this.UID = uid;
        //添加到容器内
        this.add(imageLabel);
        this.add(nameJlabel);
        this.add(textJLabel);
        this.add(this.bt1);
        this.add(this.bt2);
        //绑定监听
        bt1.addActionListener( e -> bt1Listener.method(Display_Case.this));
        bt2.addActionListener( e ->bt2Listener.method(Display_Case.this));
        //设置布局
        //FlowLayout参数一：对齐的方式
        //FlowLayout参数二：组件间水平间隙
        //FlowLayout参数三：组件间垂直间隙（组件到上和下的距离分别是多少）
        this.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));

        //为其他组件绘制边框
        imageLabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        nameJlabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        textJLabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        this.bt1.setBorder(new EtchedBorder(1,Color.BLACK,Color.BLACK));
        this.bt2.setBorder(new EtchedBorder(1,Color.BLACK,Color.BLACK));

        //给整个JPanel添加边框
        this.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));

        //允许修改背景色
        this.setOpaque(true);
        //绑定监听
        this.addMouseListener(this);
    }

    /**
     * 获取
     * @return imageLabel
     */
    public JLabel getImageLabel() {
        return imageLabel;
    }

    /**
     * 设置
     * @param imageLabel
     */
    public void setImageLabel(JLabel imageLabel) {
        this.imageLabel = imageLabel;
    }

    /**
     * 获取
     * @return nameJlabel
     */
    public JLabel getNameJlabel() {
        return nameJlabel;
    }

    /**
     * 设置
     * @param nameJlabel
     */
    public void setNameJlabel(JLabel nameJlabel) {
        this.nameJlabel = nameJlabel;
    }

    /**
     * 获取
     * @return textJLabel
     */
    public JLabel getTextJLabel() {
        return textJLabel;
    }

    /**
     * 设置
     * @param textJLabel
     */
    public void setTextJLabel(JLabel textJLabel) {
        this.textJLabel = textJLabel;
    }

    /**
     * 获取
     * @return bt1
     */
    public JButton getBt1() {
        return bt1;
    }

    /**
     * 设置
     * @param bt1
     */
    public void setBt1(JButton bt1) {
        this.bt1 = bt1;
    }

    /**
     * 获取
     * @return bt2
     */
    public JButton getBt2() {
        return bt2;
    }

    /**
     * 设置
     * @param bt2
     */
    public void setBt2(JButton bt2) {
        this.bt2 = bt2;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String toString() {
        return "DisplayCase{imageLabel = " + imageLabel + ", nameJlabel = " + nameJlabel + ", textJLabel = " + textJLabel + ", bt1 = " + bt1 + ", bt2 = " + bt2 + "}";
    }

    //完整单机
    @Override
    public void mouseClicked(MouseEvent e) {

    }
    //按住不松
    @Override
    public void mousePressed(MouseEvent e) {

    }
    //松开
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    //鼠标移入
    @Override
    public void mouseEntered(MouseEvent e) {
        this.setBackground(Color.cyan);
    }
    //鼠标移出
    @Override
    public void mouseExited(MouseEvent e) {
        this.setBackground(Color.white);
    }
}
