package com.itheima.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// 提示框
public class JDialogUtil {
    private JDialogUtil(){}
    public static JDialog dialog;
    /*
    * 方法作用：提示弹窗，该方法可做不同事情
    * 参数一：居中的父界面
    * 参数二：提示的文本
    * 参数三：提示的标题
    * 参数四：确认键要做的事
    * 参数五：取消键要做的事
    * 参数六：确认按钮的文本
    * 参数七：取消按钮的文本
    * */
    public static JDialog showJDialog(JFrame j,String text,String title,Runnable confirm,Runnable cancel,String bText1,String bText2){
        //1.创建弹窗
         dialog = new JDialog(j,title,true);

        // 核心设置：置顶 + 关闭时调用dispose
        dialog.setAlwaysOnTop(true); // 置顶显示
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 关闭时自动调用dispose
        dialog.setResizable(false); // 禁止调整大小

        // 提示信息面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(15, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 提示文字
        JLabel messageLabel = new JLabel(text);
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));

        // 重连按钮
        JButton retryBtn = new JButton(bText1);
        retryBtn.setPreferredSize(new Dimension(100, 35));
        retryBtn.addActionListener((ActionEvent e) -> {
            confirm.run();
            try {
                dialog.dispose(); // 关闭当前弹窗
            } catch (Exception ex) {

            }
        });

        // 取消按钮
        JButton cancelBtn = new JButton(bText2);
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.addActionListener((ActionEvent e) -> {
            cancel.run();
            try {
                dialog.dispose();
            } catch (Exception ex) {

            }
        });

        buttonPanel.add(retryBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加内容到弹窗
        dialog.setContentPane(contentPanel);

        // 自适应大小 + 居中显示
        dialog.pack();
        dialog.setLocationRelativeTo(j); // 相对于父窗口居中

        // 显示弹窗
        dialog.setVisible(true);

        return dialog;
    }
}
