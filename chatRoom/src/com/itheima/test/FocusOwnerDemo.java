package com.itheima.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusOwnerDemo extends JFrame {
    public FocusOwnerDemo() {
        setTitle("组件焦点状态判断");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JTextField textField = new JTextField(15);
        JLabel label = new JLabel("焦点状态：未获取");
        add(textField);
        add(label);

        // 添加焦点监听器：监听组件获取/失去焦点
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                label.setText("焦点状态：已获取");
                // 直接判断组件是否拥有焦点
                boolean isFocus = textField.isFocusOwner();
                System.out.println("文本框是否为焦点所有者：" + isFocus);
            }

            @Override
            public void focusLost(FocusEvent e) {
                label.setText("焦点状态：已失去");
            }
        });

        // 设置组件是否可获取焦点（默认true）
        // textField.setFocusable(false); // 禁用焦点
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FocusOwnerDemo().setVisible(true);
        });
    }
}