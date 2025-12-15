package com.itheima.domain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

//处理提示文字
public class Prompt {
    //装有输入框的数组
    private final JTextField[] components;
    //提示文字数组
    private final String[] hints;
    public Prompt(JTextField[] components) {
        this.components = components;
        this.hints = new String[components.length];
        //初始化提示文字并添加焦点监听器
        for (int i = 0; i < components.length; i++) {
            hints[i] = components[i].getText().trim();
            JTextField field = components[i];
            int index = i;
            //使用焦点监听器
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    //获得焦点时，若文本为提示文字则清空
                    if (hints[index].equals(field.getText().trim())) {
                        field.setText("");
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    //失去焦点时，若文本为空则恢复提示文字
                    if (field.getText().trim().isEmpty()) {
                        field.setText(hints[index]);
                    }
                }
            });
        }
    }
}