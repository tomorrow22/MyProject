package com.itheima.test;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class haha extends JPanel {

    private JLabel imageLabel;
    private JLabel nameJlabel;
    private JLabel textJLabel;
    private JButton bt1;
    private JButton bt2;

    public haha() {

    }
    public haha(ImageIcon icon,String name,String text,String bt1,String bt2){

        imageLabel = new JLabel(icon);
        nameJlabel = new JLabel(name);
        textJLabel = new JLabel(text);
        this.bt1 = new JButton(bt1);
        this.bt2 = new JButton(bt2);

        this.add(imageLabel);
        this.add(nameJlabel);
        this.add(textJLabel);
        this.add(this.bt1);
        this.add(this.bt2);

        //FlowLayout参数一：对齐的方式
        //FlowLayout参数二：组件间水平间隙
        //FlowLayout参数三：组件间垂直间隙
        this.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));

        //为其他组件添加边框
        imageLabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        nameJlabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        textJLabel.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
        this.bt1.setBorder(new EtchedBorder(1,Color.BLACK,Color.BLACK));
        this.bt2.setBorder(new EtchedBorder(1,Color.BLACK,Color.BLACK));


        //给整个JPanel添加边框
        this.setBorder(new EtchedBorder(2,Color.BLACK,Color.BLACK));
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(Image image) {
        this.imageLabel = new JLabel(new ImageIcon(image));
    }

    public JLabel getNameJlabel() {
        return nameJlabel;
    }

    public void setNameJlabel(JLabel nameJlabel) {
        this.nameJlabel = nameJlabel;
    }

    public JLabel getTextJLabel() {
        return textJLabel;
    }

    public void setTextJLabel(JLabel textJLabel) {
        this.textJLabel = textJLabel;
    }

    public JButton getBt1() {
        return bt1;
    }

    public void setBt1(JButton bt1) {
        this.bt1 = bt1;
    }

    public JButton getBt2() {
        return bt2;
    }

    public void setBt2(JButton bt2) {
        this.bt2 = bt2;
    }
}
