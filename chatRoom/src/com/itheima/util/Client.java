package com.itheima.util;

import com.itheima.domain.FriendPublicInfo;
import com.itheima.domain.User;
import com.itheima.domain.MonitorThread;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

public class Client {
    //Socket对象
    public static Socket socket = null;
    //输出流
    public static ObjectOutputStream os = null;
    //输入流
    public static ObjectInputStream is = null;
    //服务器IP
    public static String serverIP;
    //服务器端口号
    public static int serverPort;
    //线程是否已启动
    public static boolean isStart = false;
    //私有化构造方法
    public Client() {
    }

    /*
     * 方法作用：连接服务器
     * 返回值：连接的Socket对象
     * */
    public static void ConnectServer() throws IOException {
        //通过配置文件去连接server
        Properties prop = new Properties();
        prop.load(new FileReader("server.properties"));

        //1.IP是服务器所在的IP或服务器的主机名
        serverIP = (String) prop.get("serverIP");
        //2.端口号
        serverPort = Integer.parseInt((String) prop.get("serverPort"));
        //连接服务器
        try {
            connectServer(serverIP, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!isStart) {
                //启动线程监听
                isStart = true;
                new MonitorThread().start();
            }
        }
    }

    public static void connectServer(String serverIP, int serverPort) throws IOException {
        socket = new Socket(serverIP, serverPort);
        os = new ObjectOutputStream(socket.getOutputStream());
        os.flush();
        is = new ObjectInputStream(socket.getInputStream());
    }

    //更新
    /*
      发送格式为：HashMap<String, ② HashMap<String, ③ HashMap<String,FriendPublicInfo>>>
    * 参数一：最内层 ③ 的k，表示UID
    * 参数二：最内层 ③ 的v，表示可视化FriendPublicInfo类
      参数三：第 ② 层的k，表示子消息类型
      参数四：最外层 ① 的k，表示方法
    * */
    public static void update(String uid,FriendPublicInfo f,String level,String method) throws IOException {
        HashMap<String,HashMap<String,HashMap<String, FriendPublicInfo>>> map = new HashMap<>();
        HashMap<String,HashMap<String,FriendPublicInfo>> map1 = new HashMap<>();
        HashMap<String,FriendPublicInfo> map2 = new HashMap<>();
        map2.put(uid,f);
        map1.put(level,map2);
        map.put(method,map1);
        os.writeObject(map);
    }

    public static void update(User user) throws IOException {
        HashMap<String,User> map = new HashMap<>();
        map.put("UPDATE-USER",user);
        os.writeObject(map);
    }
}
