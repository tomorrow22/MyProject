package com.itheima.ui;


import com.itheima.domain.MessageType;
import com.itheima.domain.Prompt;
import com.itheima.thread.AllInputStreamThread;
import com.itheima.util.Client;
import com.itheima.util.JDialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

//注册界面
public class RegexJFrame extends JFrame implements ActionListener {
    //隐藏容器
    private Container pane;
    //昵称输入框
    JTextField uNameField = new JTextField();
    //密码输入框
    JTextField uPField = new JTextField();
    JTextField rePField = new JTextField();
    //手机号输入框
    JTextField pField = new JTextField();
    JButton regex = new JButton("注  册");
    JButton remove = new JButton("重  置");
    //返回
    JButton returnButton = new JButton(new ImageIcon("images\\re_resized.png"));
    private final String[] texts = {"请输入1~8位昵称","请输入6~12不含中文位密码","请再次输入密码","请输入手机号"};
    JTextField[] jTextFields = new JTextField[4];
    public RegexJFrame(){
        //初始化界面
        initJFrame();
        //初始化组件
        initView();
        //显示
        this.setVisible(true);
        new Prompt(jTextFields);
    }

    private void initView() {
        // 昵称、密码和手机号码
        JLabel uName = new JLabel("昵        称");
        uName.setBounds(100,150,100,40);
        pane.add(uName);

        uNameField.setBounds(200,160,200,20);
        uNameField.setText(texts[0]);
        pane.add(uNameField);

        JLabel uPassword = new JLabel("密        码");
        uPassword.setBounds(100,200,100,40);
        pane.add(uPassword);
        //密码输入框
        uPField.setBounds(200,210,200,20);
        uPField.setText(texts[1]);
        pane.add(uPField);

        JLabel rePassword = new JLabel("再次输入密码");
        rePassword.setBounds(100,250,100,40);
        pane.add(rePassword);

        rePField.setBounds(200,260,200,20);
        rePField.setText(texts[2]);
        pane.add(rePField);

        JLabel uPhone = new JLabel("手   机   号");
        uPhone.setBounds(100,300,100,40);
        pane.add(uPhone);

        pField.setBounds(200,310,200,20);
        pField.setText(texts[3]);
        pane.add(pField);

        returnButton.setBorderPainted(false);       // 取消边框绘制
        returnButton.setContentAreaFilled(false);   // 取消内容区域填充（去除背景）
        returnButton.setOpaque(false);              // 设置非不透明
        returnButton.setFocusPainted(false);        // 取消焦点框
        returnButton.setBounds(0,0,72,72);
        pane.add(returnButton);

        regex.setBounds(80,420,100,40);
        pane.add(regex);
        remove.setBounds(250,420,100,40);
        pane.add(remove);

        jTextFields[0] = uNameField;
        jTextFields[1] = uPField;
        jTextFields[2] = rePField;
        jTextFields[3] = pField;

        //背景
        JLabel bg = new JLabel(new ImageIcon("images\\regexBG.jpg"));
        bg.setBounds(0,0,600,550);
        pane.add(bg);

        returnButton.addActionListener(this);
        regex.addActionListener(this);
        remove.addActionListener(this);
    }

    private void initJFrame() {
        //1.定义界面大小
        this.setSize(610, 570);
        //2.设置标题
        this.setTitle("注    册");
        //3.设置关闭模式
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //4.设置居中
        this.setLocationRelativeTo(null);
        //5.设置置顶
        this.setAlwaysOnTop(true);
        //6.关闭默认放置模式
        this.setLayout(null);
        //设置图标
        this.setIconImage(new ImageIcon("images\\图标.png").getImage());
        //获取隐藏容器
        pane = this.getContentPane();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if(obj == returnButton){
            //返回
            this.setVisible(false);
            try {
                new LoginJFrame();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if(obj == remove){
            //重置
            uNameField.setText(texts[0]);
            uPField.setText(texts[1]);
            rePField.setText(texts[2]);
            pField.setText(texts[3]);
        }
        else if(obj == regex){
            //注册
            AllInputStreamThread.pool.submit(()->{
                //检查格式
                String username = uNameField.getText().trim();
                String password = uPField.getText().trim();
                String rePassword = rePField.getText().trim();
                String phone = pField.getText().trim();
                String[] messages = {username,password,rePassword,phone};
                if(!format(messages)){
                    return;
                }
                //检查网络
                if(Client.os == null){
                    JDialogUtil.showJDialog(this,"连接服务器失败，请检查网络","网络异常",
                            ()->{},
                            ()->{},
                            "确认",
                            "取消"
                    );
                    return;
                }
                //发送服务器
                HashMap<String,String[]> map = new HashMap<>();
                map.put("REGISTER",messages);
                try {
                    Client.os.writeObject(map);
                    //接收
                    long start = System.currentTimeMillis();
                    MessageType message = null;
                    //等待五秒钟
                    while(message == null && System.currentTimeMillis() - start < 5000){
                        message = AllInputStreamThread.RegexQueue.poll();
                        Thread.sleep(100);
                    }
                    if(message == null){
                        JDialogUtil.showJDialog(this,"等待超时，请稍后重试","注册失败",
                                ()->{},
                                ()->{},
                                "确定",
                                "取消"
                        );
                        return;
                    }
                    String[] o = (String[]) message.getData();
                    if(o[0].equals("REPEAT")){
                        JDialogUtil.showJDialog(this,"一个手机号只能注册一个账号","注册失败",
                                ()->{},
                                ()->{},
                                "确定",
                                "取消"
                        );
                    }else{
                        JDialogUtil.showJDialog(this,"注册成功，您的UID为：" + o[1],"注册成功",
                                ()->{
                                    try {
                                        this.setVisible(false);
                                        new LoginJFrame();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                },
                                ()->{},
                                "返回",
                                "取消"
                        );
                    }
                } catch (IOException  |InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    //用法：检查格式是否正确
    private boolean format(String[] messages) {
        String username = messages[0];
        String password = messages[1];
        String rePassword = messages[2];
        String phone = messages[3];
        //获取文本
        //表单填写不完整
        if(username.equals(texts[0]) || password.equals(texts[1]) || phone.equals(texts[3])){
            JDialogUtil.showJDialog(this,"请填写完表单项","表单有误",
                    ()->{},
                    ()->{},
                    "确认",
                    "取消"
            );
            return false;
        }
        //密码不正确
        if(!password.equals(rePassword)){
            JDialogUtil.showJDialog(this,"两次密码不一致","注册失败",
                    ()->{},
                    ()->{},
                    "确认",
                    "取消"
            );
            return false;
        }
        //长度要求
        if(username.length() > 8 || (password.length() < 6 || password.length() > 12)){
            JDialogUtil.showJDialog(this,"请按要求填写表单项！","注册失败",
                    ()->{},
                    ()->{},
                    "确认",
                    "取消"
            );
            return false;
        }
        if(password.matches(".*([\\u4e00-\\u9fa5]|[\\uD840-\\uD868\\uD86A-\\uD86C\\uD86F-\\uD872\\uD874-\\uD879][\\uDC00-\\uDFFF]|\\uD869[\\uDC00-\\uDED6\\uDF00-\\uDFFF]|\\uD86D[\\uDC00-\\uDF34\\uDF40-\\uDFFF]|\\uD86E[\\uDC00-\\uDC1D\\uDC20-\\uDFFF]|\\uD873[\\uDC00-\\uDEA1\\uDEB0-\\uDFFF]|\\uD87A[\\uDC00-\\uDFE0]).*")){
            JDialogUtil.showJDialog(this,"密码不应含有中文！","注册失败",
                    ()->{},
                    ()->{},
                    "确认",
                    "取消"
            );
            return false;
        }
        if(!phone.matches("(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[5-79])|(?:5[0-35-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[189]))\\d{8}")){
            JDialogUtil.showJDialog(this,"手机号有误！","注册失败",
                    ()->{},
                    ()->{},
                    "确认",
                    "取消"
            );
            return false;
        }
        return true;
    }
}


