package com.itheima.test;

import com.itheima.util.JDialogUtil;

import javax.swing.*;

public class JDialogTest {
    public static void main(String[] args) {
        JDialog jDialog = JDialogUtil.showJDialog(null, "尝试重连中...", "测试", () -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, () -> {
                    System.out.println("取消");
                },
                "确认", "取消");


    }
}
