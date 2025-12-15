package com.itheima.ui;

import com.itheima.domain.MessageType;
import com.itheima.domain.Prompt;
import com.itheima.domain.User;
import com.itheima.thread.AllInputStreamThread;
import com.itheima.util.Client;
import com.itheima.util.ComponentUtil;
import com.itheima.util.JDialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

//登录界面
public class LoginJFrame extends JFrame implements ActionListener {

    //隐藏容器
    private Container pane;
    //登录按钮
    JButton login = new JButton("登      录");
    //注册按钮
    JButton regex = new JButton("注      册");
    //账号输入框
    JTextField UID = new JTextField();
    //密码输入框
    JTextField password = new JTextField();
    //设置按钮
    JButton reconnect = new JButton(new ImageIcon("images\\a.png"));
    JTextField[] jTextFields = new JTextField[2];
    private final String[] texts = {"可用手机号/UID登录", "输入密码"};

    public LoginJFrame() throws IOException {
        //线程池优化
        AllInputStreamThread.pool.submit(() -> {
            if (Client.socket == null) {
                try {
                    //启动线程连接服务端
                    Client.ConnectServer();
                    Thread.sleep(100);
                    //启动接收线程
                    AllInputStreamThread.startGlobalListener();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //1.初始化界面
        initJFrame();
        //2.初始化组件
        initView();
        //显示界面
        this.setVisible(true);
        new Prompt(jTextFields);
    }

    private void initView() {

        UID.setBounds(210, 350, 280, 30);
        UID.setText(texts[0]);
        pane.add(UID);
        //提示词
        JLabel promptUsername = new JLabel("UID");
        promptUsername.setBounds(180, 345, 50, 50);
        JLabel promptPassword = new JLabel("密码");
        promptPassword.setBounds(180, 395, 50, 50);

        pane.add(promptUsername);
        pane.add(promptPassword);

        password.setBounds(210, 400, 280, 30);
        password.setText(texts[1]);
        pane.add(password);

        login.setBounds(50, 480, 200, 45);
        pane.add(login);

        regex.setBounds(350, 480, 200, 45);
        pane.add(regex);

        //头像
        JLabel avatar = new JLabel(new ImageIcon("images\\dog2.png"));
        avatar.setBounds(50, 320, 109, 109);
        pane.add(avatar);


        //设置按钮大小
        reconnect.setBounds(10, 20, 30, 30);
        //去除按钮背景
        reconnect.setBorderPainted(false);
        reconnect.setContentAreaFilled(false);
        pane.add(reconnect);

        //1.加载背景图片
        JLabel background = new JLabel(new ImageIcon("images\\background.png"));
        //2.设置位置大小
        background.setBounds(0, 0, 595, 468);
        //3.添加到界面中
        pane.add(background);

        jTextFields[0] = UID;
        jTextFields[1] = password;

        //添加监听
        login.addActionListener(this);
        regex.addActionListener(this);
        reconnect.addActionListener(this);
    }

    private void initJFrame() {
        //1.定义界面大小
        this.setSize(610, 570);
        //2.设置标题
        this.setTitle("登    录");
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
        if (obj == login) {
            AllInputStreamThread.pool.submit(() -> {
                String uid = UID.getText().trim();
                String password1 = password.getText().trim();
                //登录
                //获取账号和密码
                if (uid.equals(texts[0]) || password1.equals(texts[1])) {
                    JDialogUtil.showJDialog(this, "账号或密码不可为空", "表单有误",
                            () -> {
                            },
                            () -> {
                            },
                            "确认",
                            "取消"
                    );
                    return;
                }
                HashMap<String, String[]> loginMap = new HashMap<>();

                loginMap.put("LOGIN", new String[]{uid, password1});
                try {
                    //向服务端验证
                    if (Client.os == null) {
                        JDialogUtil.showJDialog(this, "连接服务器失败，请检查网络", "网络异常",
                                () -> {
                                },
                                () -> {
                                },
                                "确认",
                                "取消"
                        );
                        return;
                    }
                    Client.os.writeObject(loginMap);
                    //获取服务端验证结果
                    long start = System.currentTimeMillis();
                    MessageType message = null;
                    //等待五秒钟
                    while(message == null && System.currentTimeMillis() - start < 5000){
                        message = AllInputStreamThread.LoginQueue.poll();
                        Thread.sleep(100);
                    }
                    if(message == null){
                        JDialogUtil.showJDialog(this,"等待超时，请稍后重试","登录失败",
                                ()->{},
                                ()->{},
                                "确定",
                                "取消"
                        );
                        return;
                    }
                    HashMap<String, User> o = (HashMap<String, User>) message.getData();
                    //验证
                    User u = null;
                    for (Map.Entry<String, User> entry : o.entrySet()) {
                        String key = entry.getKey();
                        u = entry.getValue();
                        if ("ERROR".equals(key)) {
                            JDialogUtil.showJDialog(this, "UID或密码错误", "登录失败", () -> {
                            }, () -> {
                            }, "确认", "取消");
                            return;
                        }
                        if ("NULL".equals(key)) {
                            JDialogUtil.showJDialog(this, "用户不存在", "登录失败", () -> {
                            }, () -> {
                            }, "确认", "取消");
                            return;
                        }
                        if ("BAN".equals(key)) {
                            JDialogUtil.showJDialog(this, "账号已封禁，请联系管理人员", "登录失败", () -> {
                            }, () -> {
                            }, "确认", "取消");
                            return;
                        }
                        if("LOGGED-IN".equals(key)){
                            JDialogUtil.showJDialog(this,"账号已在其他地方登录","登录失败",()->{},()->{},"确认","取消");
                            return;
                        }
                    }
                    //验证成功，进入界面
                    this.setVisible(false);
                    new AppJFrame(u);
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });

        } else if (obj == regex) {
            this.setVisible(false);
            //注册
            new RegexJFrame();
        } else if (obj == reconnect) {
            //尝试重新连接
            if (!Client.isStart) {
                try {
                    //关闭旧Socket
                    if (Client.socket != null) {
                        try {
                            Client.os.close();
                            Client.is.close();
                            Client.socket.close();
                        } catch (IOException ea) {
                            ea.printStackTrace();
                        }
                    }
                    Client.ConnectServer();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
