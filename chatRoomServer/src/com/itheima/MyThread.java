package com.itheima;

import com.itheima.domain.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyThread implements Runnable {
    //volatile：保证数据直接操作主内存，避免极端高并发出现UID重复
    //volatile：确保数据的可见性  数据是加载在主内存中，每条线程操作的是主内存数据的副本
    //          （以当前作用为例）然后经历读、改、写后再写回主内存
    //          volatile可以直接操作主内存数据
    private volatile static long userUID = 100L;
    private static final Object obLock = new Object();

    //通过map映射执行对应方法。消息类型：存活、登录、注册、发送个人信息、发送群聊信息...
    private final HashMap<String, execute> METHOD = new HashMap<>();
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private User user;

    public MyThread(Socket socket) throws IOException {
        this.socket = socket;
        is = new ObjectInputStream(socket.getInputStream());
        os = new ObjectOutputStream(socket.getOutputStream());
        METHOD.put("ON_LINE", obj -> online(obj));
        METHOD.put("LOGIN", obj -> login(obj));
        METHOD.put("REGISTER", obj -> register(obj));
        METHOD.put("SEARCH", obj -> search(obj));
        METHOD.put("NOTICE", obj -> notice(obj));
        METHOD.put("UPDATE", obj -> update(obj));
        METHOD.put("UPDATE-USER", obj -> updateUser(obj));
    }

    @Override
    public void run() {
        while (true) {
            if (!socket.isClosed()) {
                //处理信息
                try {
                    HashMap<String, Object> o;
                    if (is != null) {
                        o = (HashMap<String, Object>) is.readObject();
                        Set<String> set = o.keySet();
                        for (String s : set) {
                            //传入的数据
                            Object value = o.get(s);
                            System.out.println("请求=------------类型为：" + s);
                            //获取方法并执行
                            try {
                                METHOD.get(s).method(value);
                            } catch (IOException e) {
                                System.out.println("无匹配方法类型：" + s);
                            }
                        }
                    }
                } catch (SocketException ex) {
                    try {
                        socket.close();
                        is.close();
                        os.close();
                        //移出在线集合
                        Server.ONLINE_MAP.remove(user.getUID());
                        user.setIsOnline(false);
                        System.out.println(socket.getInetAddress().getHostAddress() + "已退出");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (EOFException eof) {
                    // 单独捕获EOFException（客户端正常关闭连接）
                    try {
                        socket.close();
                        is.close();
                        os.close();
                        //移出在线集合
                        Server.ONLINE_MAP.remove(user.getUID());
                        user.setIsOnline(false);
                        System.out.println(socket.getInetAddress().getHostAddress() + "正常退出");
                        break; // 跳出循环，不再继续读取
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // 兜底捕获其他IO异常（非EOF/Socket）
                    System.out.println("读取数据异常：" + e.getMessage());
                    e.printStackTrace();
                    // 异常时也关闭资源
                    try {
                        if(!socket.isClosed()) socket.close();
                        if(is != null) is.close();
                        if(os != null) os.close();
                        Server.ONLINE_MAP.remove(user.getUID());
                        user.setIsOnline(false);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //存活
    private void online(Object code) throws IOException {

    }

    //登录（可通过手机号进行登录）
    private void login(Object obj) throws IOException {
        String[] arr = (String[]) obj;
        //验证账号是否存在
        String uid = arr[0];
        String password = arr[1];
        HashMap<String, User> map = new HashMap<>();
        MessageType m = new MessageType("LOGIN-TYPE");
        if (!Server.USER_MESSAGES.containsKey(uid) && !Server.USER_MESSAGES_FROM_PHONENUMBER.containsKey(uid)) {
            //用户不存在
            map.put("NULL", null);
            m.setData(map);
            os.writeObject(m);
            return;
        }
        //获取（如果uid序号跟某个手机号一致的话会导致获取的是别人的账号，不过到时候再说吧。毕竟UID从1开始。手机号11位呢）
        user = Server.USER_MESSAGES.get(uid) == null ? Server.USER_MESSAGES_FROM_PHONENUMBER.get(uid) : Server.USER_MESSAGES.get(uid);
        if (!user.getPassword().equals(password)) {
            //密码错误
            map.put("ERROR", null);
            m.setData(map);
            os.writeObject(m);
            return;
        }
        if (user.isIsLock()) {
            //账号已封禁
            map.put("BAN", null);
            m.setData(map);
            os.writeObject(m);
            return;
        }
        //账号已在其他地方登录
        if (Server.ONLINE_MAP.containsKey(uid)) {
            map.put("LOGGED-IN", null);
            m.setData(map);
            os.writeObject(m);
            return;
        }
        //正确
        map.put("RIGHT", user);
        //添加到在线集合
        Server.ONLINE_MAP.put(uid, this);
        //设置状态
        user.setIsOnline(true);
        m.setData(map);
        os.writeObject(m);
    }

    //注册
    private void register(Object obj) throws IOException {
        //用户发起注册请求，obj为数组，内容有 昵称、密码和手机号码
        String[] arr = (String[]) obj;
        String username = arr[0];
        String password = arr[1];
        String phoneNumber = arr[3];
        String[] result = new String[2];
        MessageType m = new MessageType("REGEX-TYPE");
        long uid;
        //一个手机号只能注册一个账号
        if (Server.USER_MESSAGES_FROM_PHONENUMBER.containsKey(phoneNumber)) {
            result[0] = "REPEAT";
            result[1] = null;
        } else {
            //锁对象保证UID唯一且不重复
            synchronized (obLock) {
                uid = userUID;
                userUID++;
            }
            //创建对象
            user = new User(uid + "", username, password, socket.getInetAddress().getHostAddress()
                    , phoneNumber, Server.defaultAvatar, null, null,
                    new HashMap<>(), new HashMap<>(), new HashMap<>(), false, false
            );
            //添加到用户信息集合
            Server.USER_MESSAGES.put(uid + "", user);
            Server.USER_MESSAGES_FROM_PHONENUMBER.put(phoneNumber, user);
            result[0] = "RIGHT";
            result[1] = uid + "";
        }
        //将对象写回客户端
        m.setData(result);
        os.writeObject(m);
    }

    //更新
    private void updateUser(Object obj) {
        System.out.println("更新前：" + user);
        user = (User) obj;
        Server.USER_MESSAGES.put(user.getUID(), user);
        Server.USER_MESSAGES_FROM_PHONENUMBER.put(user.getPhoneNumber(), user);
        System.out.println("更新用户信息成功------------------------");
        System.out.println("更新后：" + user);
        System.out.println("服务器中的数据为：" + Server.USER_MESSAGES.get(user.getUID()));
    }

    //搜索方法（用户之间请求添加/申请进入群聊方法）
    private void search(Object obj) throws IOException {
        String uid = (String) obj;
        //定义消息类型
        MessageType m = new MessageType("APP-SEARCH-TYPE");
        HashMap<String, FriendPublicInfo> map = new HashMap<>();
        //获取用户信息
        User u = Server.USER_MESSAGES.get(uid) == null ? Server.USER_MESSAGES_FROM_PHONENUMBER.get(uid) : Server.USER_MESSAGES.get(uid);
        System.out.println(u);
        if (u == null) {
            map.put("NOT-USER", null);
            m.setData(map);
            os.writeObject(m);
            return;
        }
        //封装可展示的用户信息
        FriendPublicInfo fp = new FriendPublicInfo(u.getUserName(), u.getUID(), u.getAvatar(), u.getSignature(), u.isIsOnline());
        //发出
        map.put(u.getUID(), fp);
        m.setData(map);
        os.writeObject(m);
    }

    //更新方法
    private void update(Object obj) throws IOException {
        //1.更新对方信息
        //2.向对方发送更新消息
        MessageType m = new MessageType("APP-NOTICE-TYPE");
        HashMap<String, Object> map = (HashMap<String, Object>) obj;
        for (String s : map.keySet()) {
            SystemNotice(map.get(s), m, "REMOVE-UPDATE");
        }
    }

    //通知方法
    /*
    目前想到的通知类型：
    1.消息通知：A发送信息给B时要推送给B  就是B的消息通知
        A客户端发送的消息格式应为： HashMap<String,HashMap<String,Send>>
        最外层k是通知   NOTICE 才能执行notice的方法  v是消息
        内层k是子消息类型（Send_Message）  v是消息类型Send类

        服务端推送B的消息格式为：
        new MessageType("APP-NOTICE-TYPE","CHAT",send);

    2.系统通知：A请求添加B为好友时推送给B 就是B的系统通知
        A客户端发送的消息格式应为：
        ① HashMap<String, ② HashMap<String, ③ HashMap<String,FriendPublicInfo>>>

        ① k是消息类型  v是数据
        ② k是子消息类型 v是数据
        ③ k是B的UID   v是A的可视化类

        服务端推送B的消息格式为：
        new MessageType("APP-NOTICE-TYPE","SYSTEM",FriendPublicInfo);
        FriendPublicInfo 是A的可视化类

    3.添加更新通知：A接受B添加好友的请求后推送给B 就是B的更新通知
        A接受后客户端发送的格式为：
         ① HashMap<String, ② HashMap<String, ③ HashMap<String,FriendPublicInfo>>>
        ① k是消息类型  v是数据
        ② k是子消息类型 v是数据
        ③ k是B的UID   v是A的可视化类
        内层k是子消息类型   v是A的FriendPublicInfo类

        服务端推送B的消息格式为：
        new MessageType("APP-NOTICE-TYPE","ADD-UPDATE",FriendPublicInfo);
            FriendPublicInfo 是A的可视化类

     4.删除更新通知：A删除B的好友后，应该推送B更新    就是B的删除更新通知
        A删除后：发送信息格式为：
            ① HashMap<String, ② HashMap<String, ③ HashMap<String,FriendPublicInfo>>>
            ①   k是方法的消息类型（执行什么方法）   v是方法参数的Object
            ②   k是方法的子消息类型（做什么事）
            ③   k是对方UID                       v是我方FriendPublicInfo类

            推送B的消息格式为：
            new MessageType("APP-NOTICE-TYPE","REMOVE-UPDATE",FriendPublicInfo);
            FriendPublicInfo 是A的可视化类

     5.退出群聊通知 A退出了群聊B，  应该推送给群聊B的群主  就是群聊群主的推送通知
        A退出后：发送信息格式为：
            ① HashMap<String, ② HashMap<String, ③ HashMap<String,FriendPublicInfo>>>
            ①   k是方法的消息类型（执行什么方法）   v是方法参数的Object
            ②   k是方法的子消息类型（做什么事）
            ③   k是群主UID，                      v是我方FriendPublicInfo类
                                             这个FriendPublicInfo有点特殊：
                                        参数一：群聊的UID（正常是【我】的用户名）偷个懒，主要是不想再嵌套了
                                        其他一样，还是用户的基本信息
    * */
    private void notice(Object obj) throws IOException {
        //1.定义消息类型
        MessageType m = new MessageType("APP-NOTICE-TYPE");
        //2.解析信息
        HashMap<String, Object> map = (HashMap<String, Object>) obj;

        for (String s : map.keySet()) {
            System.out.println("当前处理的消息类型为：" + s);
            System.out.println("-------------------------");
            if ("CHAT".equals(s)) {
                sendMessage(map.get(s), m);
            } else if ("SYSTEM".equals(s)) {
                SystemNotice(map.get(s), m, "SYSTEM");
            } else if ("ADD-UPDATE".equals(s)) {
                SystemNotice(map.get(s), m, "ADD-UPDATE");
            } else if ("EXIT-GROUP".equals(s)) {
                //退出群聊通知
                SystemNotice(map.get(s), m, "EXIT-GROUP");
            }
        }
    }

    //发送信息
    private void sendMessage(Object obj, MessageType m) throws IOException {
        System.out.println("准备转换");
        Send send = (Send) obj;
        for (FriendPublicInfo friendList : send.getList()) {
            //用户是否在线（离线消息未设置）
            System.out.println(Server.ONLINE_MAP.containsKey(friendList.getUID()));
            if (Server.ONLINE_MAP.containsKey(friendList.getUID())) {
                //获取好友的线程对象
                MyThread thread = Server.ONLINE_MAP.get(friendList.getUID());
                System.out.println("获取对方线程成功");
                //更新双方聊天记录
                System.out.println("获取线程成功");
                HashMap<String, ArrayList<String>> chatList = thread.user.getChatList();
                ArrayList<String> friendChat = thread.user.getChatList().get(send.getUID());
                if (friendChat == null) {
                    thread.user.getChatList().put(send.getUID(), new ArrayList<>());
                }
                if (friendChat != null) {
                    friendChat.add(send.getStr());
                }
                chatList.put(send.getUID(), friendChat);

                HashMap<String, ArrayList<String>> list = user.getChatList();
                if (list.get(friendList.getUID()) == null) {
                    user.getChatList().put(friendList.getUID(), new ArrayList<>());
                }
                ArrayList<String> list1 = list.get(friendList.getUID());
                list1.add(send.getStr());

                System.out.println("更新聊天记录完成");
                //封装
                m.setChildType("CHAT");
                m.setData(send);
                System.out.println("准备发送至：" + thread.user.getUID() + ",发送的信息为：" + send.getStr());
                //发送
                thread.os.writeObject(m);
            }
            System.out.println("结束了...");
        }
    }

    //好友申请通知  /  更新通知   /  删除通知
    private void SystemNotice(Object obj, MessageType m, String type) throws IOException {
        HashMap<String, FriendPublicInfo> map = (HashMap<String, FriendPublicInfo>) obj;
        System.out.println("方法传递的参数是：" + type);
        for (String s : map.keySet()) {
            System.out.println("map里面的k为：" + s);
            System.out.println(Server.ONLINE_MAP.containsKey(s));
            if (Server.ONLINE_MAP.containsKey(s)) {
                //更新双方信息
                MyThread myThread = Server.ONLINE_MAP.get(s);
                FriendPublicInfo info = map.get(s);
                //添加好友更新
                if ("ADD-UPDATE".equals(type)) {
                    User user1 = myThread.user;
                    user.getFriendList().put(user1.getUID(), new FriendPublicInfo(user1.getUserName(), user1.getUID(), user1.getAvatar(),
                            user1.getSignature(), user1.isIsOnline()
                    ));
                    user.getChatList().put(user1.getUID(), new ArrayList<>());
                    user1.getFriendList().put(user.getUID(), info);
                    user1.getChatList().put(user1.getUID(), new ArrayList<>());
                }
                //删除好友更新
                if ("REMOVE-UPDATE".equals(type)) {
                    //更新双方信息
                    System.out.println(s);
                    myThread.user.getFriendList().remove(s);
                    System.out.println("对方删除成功");
                    user.getFriendList().remove(s);
                    System.out.println("我方删除成功");
                }
                if ("EXIT-GROUP".equals(type)) {
                    //退出群聊，推送给群主
                    //1.更新群主的群聊好友(在群主群中删除)
                    Group group = myThread.user.getGroupList().get(info.getUsername());//通过群UID获取群
                    group.getList().remove(info);//删除群内的个人信息
                    //2.更新自己的群聊
                    user.getGroupList().remove(info.getUsername());
                    //3.推送
                }
                //封装并发送
                m.setChildType(type);
                m.setData(info);
                System.out.println(m);
                myThread.os.writeObject(m);
            }
        }

    }

}
