package com.itheima;

import com.itheima.domain.FriendPublicInfo;
import com.itheima.domain.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

//当前方式是BIO
public class Server {
    //ConcurrentHashMap：支持高并发下使用不会出现安全问题的集合。用法跟普通集合差不多

    //2.定义静态集合存储用户Socket方便外部调用
    //用户在线集合 k:UID  v:线程对象
    public static final ConcurrentHashMap<String,MyThread> ONLINE_MAP = new ConcurrentHashMap<>();
    //用户信息集合 k:UID   v:用户信息对象
    public static final ConcurrentHashMap<String, User> USER_MESSAGES = new ConcurrentHashMap<>();
    //用户信息集合 k；手机号  v；用户信息对象
    public static final ConcurrentHashMap<String, User> USER_MESSAGES_FROM_PHONENUMBER = new ConcurrentHashMap<>();

    public static  ImageIcon defaultAvatar ;
    public static ImageIcon myAvatar;

    static {
        try {
            byte[] bytes = new byte[1024 * 1024 * 5];
            FileInputStream fis = new FileInputStream("images\\user.png");
            int read = fis.read(bytes);
            fis.close();
            defaultAvatar = new ImageIcon(ImageIO.read(new ByteArrayInputStream(bytes)).getScaledInstance(40, 30, Image.SCALE_SMOOTH));

            byte[] bytes1 = new byte[1024 * 1024 * 5];
            FileInputStream fis1 = new FileInputStream("images\\my.jpg");
            int read1 = fis1.read(bytes1);
            fis1.close();
            myAvatar = new ImageIcon(ImageIO.read(new ByteArrayInputStream(bytes1)).getScaledInstance(40, 30, Image.SCALE_SMOOTH));

        } catch (IOException e) {
            e.printStackTrace();
        }

        USER_MESSAGES.put("123",new User("123","明天再说吧","123","123.7.5.3","12345678910"
        , defaultAvatar,
                "hahaha",
                "11",
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                false,
                true
        ));
        USER_MESSAGES.put("2",new User("2","明天也不一定","1","123.7.5.3","12345678911"
                , myAvatar,
                "测试账号1",
                "11",
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                false,
                false
        ));
        USER_MESSAGES.put("1",new User("1","测试一号","1","123.7.5.3","12345678911"
                , defaultAvatar,
                "吃吃吃吃吃吃吃吃吃",
                "11",
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                false,
                false
        ));
    }
    public static void main(String[] args) throws IOException {
        //服务端

        //1.定义线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
               5,
               12,
               60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //3.创建ServerSocket对象等待连接
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 10086));
        System.out.println("服务端启动成功，监听端口：10086，绑定地址：0.0.0.0");

        while(true){
            Socket socket = ss.accept();
            //提交任务
            pool.submit(new MyThread(socket));
        }

    }
}
