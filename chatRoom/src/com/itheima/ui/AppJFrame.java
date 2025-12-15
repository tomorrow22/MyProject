package com.itheima.ui;

import com.itheima.domain.*;
import com.itheima.interFace.DisplayListener;
import com.itheima.thread.AllInputStreamThread;
import com.itheima.util.Client;
import com.itheima.util.JDialogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//主界面
public class AppJFrame extends JFrame implements ActionListener {
    Container pane;
    public static User user;
    private final String[] icons = {"chat_dark.png", "friend_dark.png", "user_dark.png"};//默认图像(暗的)
    private final String[] iconsContrary = {"chat_bright.png", "friend_bright.png", "user_bright.png"};//对应图像（亮的）
    JButton chatButton = new JButton(new ImageIcon("images\\" + iconsContrary[0]));
    JButton friendButton = new JButton(new ImageIcon("images\\" + icons[1]));
    JButton myButton = new JButton(new ImageIcon("images\\" + icons[2]));
    //全局按钮
    JButton[] buttons = {chatButton, friendButton, myButton};
    JButton friends = new JButton("好友");
    JButton group = new JButton("群聊");
    JButton search = new JButton("搜索");
    JButton notice = new JButton("通知");
    JPanel miniCardPanel = new JPanel(new CardLayout());
    //局部按钮（好友界面）
    JButton[] miniJFrameButton = {friends, group, search, notice};
    String[] miniCardName = {"myFriends", "myGroup", "mySearch", "myNotice"};
    //使用卡片布局管理多个面板
    //容器的布局管理器。它将容器中的每个组件看作一张卡片。一次只能看到一张卡片，容器则充当卡片的堆栈。详见API帮助文档
    JPanel cardPanel = new JPanel(new CardLayout());//管理主界面（三个大界面）
    String[] cardName = {"chat", "friend", "user"};
    String[] titleName = {"聊天室", "好友", "个人"};
    JButton searchButton = new JButton("搜索");
    String searchStr = "";
    FriendPublicInfo searchResult = null;
    //搜索结果
    JPanel result = new JPanel(new BorderLayout());
    JTextField uidInput = new JTextField();
    //通知个数
    int noticeSize = 0;
    JPanel searchPanel = new JPanel(new BorderLayout());
    JPanel chatJPanel = new JPanel();
    volatile boolean isRepaint = false;
    HashMap<String, ArrayList<String>> chatList ;
    FriendPublicInfo userInfo;
    JLabel[] nothingMessage = new JLabel[4];
    public static HashMap<String,ChatJFrame> isOpen = new HashMap<>();
    public AppJFrame(User user) throws IOException {
        AppJFrame.user = user;
        chatList = user.getChatList();
        System.out.println(user);
        userInfo = new FriendPublicInfo(user.getUserName(),user.getUID(),user.getAvatar(),user.getSignature(),user.isIsOnline());
        //初始化界面
        initJFrame();
        //初始化组件
        initView();

        this.setVisible(true);
    }

    private void initView() throws IOException {
        nothingMessage[0] = notThink("当前无任何聊天记录...");
        nothingMessage[1] = notThink("当前无任何好友...");
        nothingMessage[2] = notThink("当前无任何群聊...");
        nothingMessage[3] = notThink("当前无任何通知...");
        //加载聊天记录（随按钮点击隐藏 or 显示）
        Chat();
        //加载好友&&群聊列表（随按钮点击隐藏 or 显示）
        Friend();
        //加载个人数据（随按钮点击隐藏 or 显示）
        initUser();
        //添加卡片到界面中
        pane.add(cardPanel);
        //添加按钮
        JPanel buttonJPanel = new JPanel();

        buttonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 70, 5));

        buttonJPanel.add(chatButton);
        buttonJPanel.add(friendButton);
        buttonJPanel.add(myButton);
        //去除按钮默认背景
        for (JButton button : buttons) {
            // 1. 取消按钮内容区域填充（去掉默认的按钮背景填充）
            button.setContentAreaFilled(false);
            // 2. 取消按钮边框（去掉默认的按钮边框）
            button.setBorderPainted(false);
            // 3. 可选：取消按钮焦点框（点击后不会出现虚线框）
            button.setFocusPainted(false);
            //绑定监听
            button.addActionListener(this);
        }
        pane.add(buttonJPanel, BorderLayout.SOUTH);
    }

    private void initUser() {
        JPanel userPanel = new JPanel();
        userPanel.setName(cardName[2]);
        userPanel.setLayout(new BorderLayout());
        //头像和个性签名
        JPanel message = new JPanel();
        message.setLayout(new FlowLayout(FlowLayout.CENTER));
        message.add(new JLabel(user.getAvatar()));
        message.add(new JLabel(user.getUserName()));
        message.add(new JLabel("| UID："+user.getUID()));
        message.add(new JLabel("| 个性签名：" + (user.getSignature() == null ? "无" : user.getSignature())));
        //退出和返回登录
        JPanel reButton = new JPanel();
        reButton.setLayout(new FlowLayout(FlowLayout.CENTER, 70, 5));

        JButton exit = new JButton("退出");
        exit.addActionListener(e -> System.exit(0));
        JButton reLogin = new JButton("返回登录");
        reLogin.addActionListener(e -> {
            try {
                AppJFrame.this.setVisible(false);
                new LoginJFrame();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        reButton.add(exit);
        reButton.add(reLogin);
        /*
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        reLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        BorderLayout.CENTER 区域会让子组件（reButton）占据该区域的全部剩余空间，
        但 reButton 作为 JPanel，默认的水平对齐方式是 LEFT_ALIGNMENT（靠左）；所以布局还是靠左显示
        所以如果想要居中展示，里面每个组件都要设置setAlignmentX：（如上所示）
        BorderLayout.CENTER 是 “告诉父容器，把组件放在哪个区域”；
        Component.CENTER_ALIGNMENT 是 “告诉父容器，组件在该区域内如何对齐”。*/
        userPanel.add(message, BorderLayout.NORTH);
        userPanel.add(reButton, BorderLayout.SOUTH);

        cardPanel.add(userPanel, cardName[2]);
    }

    private void Friend() {
        JPanel friendPanel = new JPanel();
        friendPanel.setName(cardName[1]);
        friendPanel.setLayout(new BorderLayout());

        JPanel fButton = new JPanel();
        fButton.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 5));
        //添加到好友界面并添加监听
        for (JButton b : miniJFrameButton) {
            fButton.add(b);
            b.addActionListener(this);
        }
        //添加到顶部位置
        friendPanel.add(fButton, BorderLayout.NORTH);

        //------------------好友列表
        JPanel myFriends = new JPanel();
        myFriends.setLayout(new BoxLayout(myFriends, BoxLayout.Y_AXIS));//垂直摆放
        //添加好友列表数据
        HashMap<String, FriendPublicInfo> friendList = user.getFriendList();
        if (user.getFriendList().isEmpty()) {
            myFriends.add(nothingMessage[1], BorderLayout.CENTER);
        } else {
            for (Map.Entry<String, FriendPublicInfo> entry : friendList.entrySet()) {
                FriendPublicInfo value = entry.getValue();
                //创建好友列表条目
                Display_Case dc = new Display_Case(value.getAvatar(), value.getUsername(), value.getSignature(), "聊天", "删除",
                        new DisplayListener() {
                            @Override
                            public void method(Display_Case d) {
                                openChat(d);
                            }
                        },
                        d -> JDialogUtil.showJDialog(AppJFrame.this, "是否删除好友:" + value.getUsername() + "及其聊天记录", "删除好友",
                                () -> {
                                    //删除组件
                                    removeItem(myFriends, d);
                                    //删除好友
                                    user.getFriendList().remove(entry.getKey());
                                    //删除聊天记录
                                    user.getChatList().remove(entry.getKey());
                                    removeChatItem(cardPanel,cardName[0], d.getUID());

                                    if (user.getFriendList().isEmpty()) {
                                        myFriends.add(nothingMessage[1], BorderLayout.CENTER);
                                        myFriends.repaint();
                                        if(user.getChatList().isEmpty()){
                                            chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                            chatJPanel.repaint();
                                        }
                                    }
                                    try {
                                        //更新
                                        Client.update(d.getUID(),userInfo,"REMOVE-UPDATE","UPDATE");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                },
                                () -> {
                                },
                                "删除",
                                "取消"
                        ),
                        //绑定UID（因为名字可以重复）
                        value.getUID()
                );
                myFriends.add(dc);
                myFriends.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
            }
        }
        //设置滚动条
        JScrollPane friend_Scroll = getScrollPane(myFriends);
        friend_Scroll.setName(miniCardName[0]);
        //添加到卡片并约束名字
        miniCardPanel.add(friend_Scroll, miniCardName[0]);

        //-------------------群聊列表
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));//垂直摆放
        //添加群聊列表
        HashMap<String, Group> groupList = user.getGroupList();
        if (groupList.isEmpty()) {
            group.add(nothingMessage[2], BorderLayout.CENTER);
        } else {
            for (Map.Entry<String, Group> entry : groupList.entrySet()) {
                Group value = entry.getValue();
                Display_Case dc = new Display_Case(value.getIcon(), value.getName(), value.getRefer(), "聊天", "退出",
                        new DisplayListener() {
                            @Override
                            public void method(Display_Case d) {

                            }
                        },
                        d -> JDialogUtil.showJDialog(AppJFrame.this, "是否退出群聊：" + value.getName(), "退出",
                                () -> {
                                    removeItem(group, d);
                                    //删除群聊
                                    user.getGroupList().remove(entry.getKey());
                                    //删除群聊记录
                                    user.getChatList().remove(entry.getKey());
                                    //删除群聊天记录
                                    removeChatItem(cardPanel,cardName[0],d.getUID());

                                    if (user.getGroupList().isEmpty()) {
                                        group.add(nothingMessage[2], BorderLayout.CENTER);
                                        group.repaint();
                                        if(user.getChatList().isEmpty()){
                                            chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                            chatJPanel.repaint();
                                        }
                                    }
                                    try {
                                        //这个FriendPublicInfo有点特殊：
                                        //参数一：群聊的UID（正常是【我】的用户名）
                                        //其他一样，还是用户的基本信息
                                        Client.update(value.getKingUID(),new FriendPublicInfo(
                                                value.getUID(),
                                                user.getUID(),
                                                user.getAvatar(),
                                                user.getSignature(),
                                                user.isIsOnline()
                                                )
                                                ,"EXIT-GROUP","NOTICE");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                },
                                () -> {
                                },
                                "确认",
                                "取消"
                        ),
                        value.getUID()
                );

                group.add(dc);
                group.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
            }
        }
        //添加滚动条并添加到小卡片中
        JScrollPane group_Scroll = getScrollPane(group);
        group_Scroll.setName(miniCardName[1]);
        miniCardPanel.add(group_Scroll, miniCardName[1]);
        //---------------搜索功能

        //搜索栏和按钮
        JPanel column = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        uidInput.setText("请输入手机号/UID进行搜索");
        //提示
        uidInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //获取焦点时
                if ("请输入手机号/UID进行搜索".equals(uidInput.getText())) {
                    uidInput.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                //失去焦点时
                if (uidInput.getText().trim().isEmpty()) {
                    uidInput.setText("请输入手机号/UID进行搜索");
                }
            }
        });

        column.add(uidInput);
        column.add(searchButton);
        searchButton.addActionListener(this);

        searchPanel.add(column, BorderLayout.NORTH);
        searchPanel.add(result, BorderLayout.CENTER);
        //添加界面
        miniCardPanel.add(searchPanel, miniCardName[2]);

        //---------------------通知界面
        JPanel notice = new JPanel();
        notice.setLayout(new BoxLayout(notice, BoxLayout.Y_AXIS));
        notice.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 容器内边距（上下左右10像素）
        notice.add(nothingMessage[3], BorderLayout.CENTER);

        JScrollPane notice_Scroll = getScrollPane(notice);
        notice_Scroll.setName(miniCardName[3]);
        //添加到界面中
        miniCardPanel.add(notice_Scroll,miniCardName[3]);
        //启动线程监听通知信息
        AllInputStreamThread.pool.submit(() -> {
            while (true) {
                MessageType poll = AllInputStreamThread.APP_Notice_Queue.poll();
                if (poll != null) {
                    String childType = poll.getChildType();
                    if(childType.equals("SYSTEM")){
                        SwingUtilities.invokeLater(()->{
                            FriendPublicInfo data = (FriendPublicInfo) poll.getData();
                            synchronized (AppJFrame.this){
                                noticeSize++;
                                try {
                                    notice.remove(nothingMessage[3]);
                                } catch (Exception e) {
                                    System.out.println("删除失败123，无组件");
                                }
                            }
                            //解析data封装成Display_Case对象添加到界面中，然后用线程安全的方式更新UI界面
                            Display_Case dc = new Display_Case(data.getAvatar(), data.getUsername(), data.getUsername() + "请求添加为好友",
                                    "接受",
                                    "忽略",
                                    d -> {
                                        //1.删除该条记录
                                        removeItem(notice, d);
                                        //2.添加到好友列表
                                        user.getFriendList().put(data.getUID(),data);
                                        ArrayList<String> list = new ArrayList<>();
                                        user.getChatList().put(data.getUID(),list);
                                        //发送更新通知
                                        try {
                                            Client.update(data.getUID(),userInfo,"ADD-UPDATE","NOTICE");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        //通知减少
                                        synchronized (AppJFrame.this){
                                            noticeSize--;
                                            if(noticeSize == 0){
                                                notice.add(nothingMessage[3], BorderLayout.CENTER);
                                                notice.repaint();
                                            }
                                        }
                                        //更新界面
                                        SwingUtilities.invokeLater(()->{
                                            try {
                                                myFriends.remove(nothingMessage[1]);
                                            } catch (Exception e) {
                                                System.out.println("111删除失败");
                                            }
                                            Display_Case dc2 = new Display_Case(data.getAvatar(), data.getUsername(), data.getSignature(), "聊天", "删除",
                                                    new DisplayListener() {
                                                        @Override
                                                        public void method(Display_Case d) {
                                                            openChat(d);
                                                        }
                                                    },
                                                    d2 -> JDialogUtil.showJDialog(AppJFrame.this, "是否删除好友:" + data.getUsername() + "及其聊天记录", "删除好友",
                                                            () -> {
                                                                //删除
                                                                removeItem(myFriends, d);
                                                                //删除好友
                                                                user.getFriendList().remove(data.getUID());
                                                                //删除完后应该通知并更新对方
                                                                //删除聊天记录
                                                                user.getChatList().remove(data.getUID());
                                                                removeChatItem(cardPanel,cardName[0], d.getUID());
                                                                if (user.getFriendList().isEmpty()) {
                                                                    myFriends.add(nothingMessage[1], BorderLayout.CENTER);
                                                                    if(user.getChatList().isEmpty()){
                                                                        chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                                                        chatJPanel.repaint();
                                                                    }
                                                                    myFriends.repaint();
                                                                }
                                                                try {
                                                                    //更新
                                                                    Client.update(d2.getUID(),userInfo,"REMOVE-UPDATE","UPDATE");
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            },
                                                            () -> {
                                                            },
                                                            "删除",
                                                            "取消"
                                                    ),
                                                    //绑定UID（因为名字可以重复）
                                                    data.getUID()
                                            );
                                            myFriends.add(dc2);
                                            myFriends.repaint();
                                        });
                                    },
                                    d -> {
                                        //删除该条记录
                                        removeItem(notice, d);
                                        synchronized (AppJFrame.this){
                                            noticeSize--;
                                            if(noticeSize == 0){
                                                notice.add(nothingMessage[3], BorderLayout.CENTER);
                                                notice.repaint();
                                            }
                                        }
                                    },
                                    data.getUID()
                            );
                            //添加到界面
                            notice.add(dc);
                            notice.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
                        });
                    }
                    else if(childType.equals("ADD-UPDATE")){
                        SwingUtilities.invokeLater(()->{
                            FriendPublicInfo data = (FriendPublicInfo) poll.getData();
                            user.getFriendList().put(data.getUID(),data);
                            ArrayList<String> list = new ArrayList<>();
                            user.getChatList().put(data.getUID(),list);
                            try {
                                myFriends.remove(nothingMessage[1]);
                            } catch (Exception e) {
                                System.out.println("删除失1122败，没有该组件");
                            }
                            //创建好友列表条目
                            Display_Case dc = new Display_Case(data.getAvatar(), data.getUsername(), data.getSignature(), "聊天", "删除",
                                    new DisplayListener() {
                                        @Override
                                        public void method(Display_Case d) {
                                            openChat(d);
                                        }
                                    },
                                    d -> JDialogUtil.showJDialog(AppJFrame.this, "是否删除好友:" + data.getUsername() + "及其聊天记录", "删除好友",
                                            () -> {
                                                //删除
                                                removeItem(myFriends, d);
                                                //删除好友
                                                user.getFriendList().remove(data.getUID());
                                                //删除聊天记录
                                                user.getChatList().remove(data.getUID());
                                                removeChatItem(cardPanel,cardName[0], d.getUID());

                                                if (user.getFriendList().isEmpty()) {
                                                    myFriends.add(nothingMessage[1], BorderLayout.CENTER);
                                                    if(user.getChatList().isEmpty()){
                                                        chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                                        chatJPanel.repaint();
                                                    }
                                                    myFriends.repaint();
                                                }
                                                try {
                                                    //更新
                                                    Client.update(d.getUID(),userInfo,"REMOVE-UPDATE","UPDATE");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            () -> {
                                            },
                                            "删除",
                                            "取消"
                                    ),
                                    //绑定UID（因为名字可以重复）
                                    data.getUID()
                            );
                            myFriends.add(dc);
                            myFriends.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
                        });
                    }
                    else if(childType.equals("REMOVE-UPDATE")){
                        SwingUtilities.invokeLater(()->{
                            //删除
                            FriendPublicInfo data = (FriendPublicInfo) poll.getData();
                            for (Component comm : myFriends.getComponents()) {
                                if(comm instanceof Display_Case){
                                    if(((Display_Case) comm).getUID().equals(data.getUID())){
                                        myFriends.remove(comm);
                                        //更新用户列表
                                        user.getFriendList().remove(data.getUID());
                                        if (user.getFriendList().isEmpty()) {
                                            myFriends.add(nothingMessage[1], BorderLayout.CENTER);
                                        }
                                        myFriends.repaint();
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    else if(childType.equals("CHAT")){
                        SwingUtilities.invokeLater(()->{
                            Send data = (Send) poll.getData();
                            boolean isInfo = false;
                            Display_Case comm = null;
                            System.out.println("收到信息：" + data.getStr());
                            for (Component com : chatJPanel.getComponents()) {
                                if(com instanceof Display_Case com1){
                                    String uid = com1.getUID();
                                    if(data.getUID().equals(uid)){
                                        //已存在
                                        comm = com1;
                                        isInfo = true;
                                        break;
                                    }
                                }
                            }
                            //遍历聊天界面，如果存在UID相同的条目那就不增加，然后把信息修改
                            //             如果不存在，则新增条目
                            //将聊天记录更新
                            HashMap<String, ArrayList<String>> list = user.getChatList();
                            ArrayList<String> list1 = list.get(data.getUID());
                            list1.add(data.getStr());
                            list.put(data.getUID(),list1);
                            if(isOpen.containsKey(data.getUID())){
                                //更新UI
                                isOpen.get(data.getUID()).add_Message(data.getStr());
                                System.out.println("UI更新------");
                            }
                            if(isInfo){
                                comm.getTextJLabel().setText(data.getStr());
                            }else{
                                try {
                                    chatJPanel.remove(nothingMessage[0]);
                                } catch (Exception e) {
                                    System.out.println("删除失败1，无该组件");
                                }
                                //此处应该是对方的头像
                                FriendPublicInfo info = user.getFriendList().get(data.getUID());
                                //添加到JPanel内
                                Display_Case comp = new Display_Case(info.getAvatar(), info.getUsername(), data.getStr(), "查看", "删除",
                                        new DisplayListener() {
                                            @Override
                                            public void method(Display_Case d) {
                                                openChat(d);
                                            }
                                        },
                                        d -> {
                                            removeItem(chatJPanel, d);
                                            ArrayList<String> chat = new ArrayList<>();
                                            chat.add(data.getStr());
                                            user.getChatList().put(info.getUID(),chat);
                                            if (user.getChatList().isEmpty()) {
                                                chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                                chatJPanel.repaint();
                                            }
                                        },
                                        info.getUID()
                                );
                                chatJPanel.add(comp);
                                chatJPanel.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
                                chatJPanel.revalidate();
                                chatJPanel.repaint();
                            }
                        });
                    }
                    else if(childType.equals("EXIT-GROUP")){
                        SwingUtilities.invokeLater(()->{
                            try {
                                notice.remove(nothingMessage[3]);
                            } catch (Exception e) {
                                System.out.println("删除失败，无改12组件");
                            }
                            //退出群聊
                            FriendPublicInfo data = (FriendPublicInfo) poll.getData();
                            //FriendPublicInfo里面的用户名是群聊UID，所以要通过自己获取好友名字
                            String oppoUsername = user.getFriendList().get(data.getUID()).getUsername();
                            Display_Case dc = new Display_Case(data.getAvatar(), oppoUsername,
                                    oppoUsername+"已退出群聊"+user.getGroupList().get(data.getUsername()).getName(),
                                    "确定",
                                    "忽略",
                                    d -> {
                                        //删除该条记录
                                        removeItem(notice, d);
                                        synchronized (AppJFrame.this){
                                            noticeSize--;
                                            if(noticeSize == 0){
                                                notice.add(nothingMessage[3], BorderLayout.CENTER);
                                                notice.repaint();
                                            }
                                        }
                                    },
                                    d -> {
                                        //删除该条记录
                                        removeItem(notice, d);
                                        synchronized (AppJFrame.this){
                                            noticeSize--;
                                            if(noticeSize == 0){
                                                notice.add(nothingMessage[3], BorderLayout.CENTER);
                                                notice.repaint();
                                            }
                                        }
                                    },
                                    data.getUID()
                            );
                            //添加到界面
                            notice.add(dc);
                            notice.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
                        });
                    }
                }
                Thread.sleep(100);
            }
        });
        //将小卡片添加到好友界面中
        friendPanel.add(miniCardPanel, BorderLayout.CENTER);
        //将整个大好友界面添加到大卡片中
        cardPanel.add(friendPanel, cardName[1]);
    }

    //通过传递UID删除聊天记录
    /*
    * 参数一：大卡片(三大界面的哪一个--最外层)
    * 参数二：小卡片名字（嵌套的卡片名字）
    * 参数三: 该条目的UID（唯一）
    * */
    private void removeChatItem(JPanel big,String name, String uid) {
        //获取聊天界面
        JScrollPane chatJPanel = (JScrollPane) getPanelByAlias(big, name);
        //获取滚动条下的JPanel
        JPanel view = (JPanel) chatJPanel.getViewport().getView();
        //遍历获取内部组件进行删除
        for (Component c : view.getComponents()) {
            if (c instanceof Display_Case) {
                String panel_UID = ((Display_Case) c).getUID();
                if (panel_UID.equals(uid)) {
                    view.remove(c);
                }
            }
        }
    }

    /*
     * 获取指定卡片中的指定小卡片（指定容器中的指定面板）
     * 参数一：大容器（大卡片）
     * 参数二：要获取的容器（卡片）
     * */
    public Object getPanelByAlias(JPanel cardPanel, String targetAlias) {
        // 遍历所有子组件
        for (Component comp : cardPanel.getComponents()) {
            //  name 匹配目标别名
            if (targetAlias.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }

    //无信息展示
    private static JLabel notThink(String text) {
        JLabel notThink = new JLabel(text);
        notThink.setFont(new Font("幼圆", Font.BOLD, 16));
        notThink.setAlignmentX(Component.CENTER_ALIGNMENT);
        return notThink;
    }

    //添加滚动条
    private static JScrollPane getScrollPane(JPanel friendPanel) {
        //添加滚动条
        JScrollPane scrollPane = new JScrollPane(friendPanel);
        //滚动的速度
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 垂直滚动条按需显示
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 禁止水平滚动条
        return scrollPane;
    }

    //加载聊天记录
    private void Chat() throws IOException {
        //因为设置了BorderLayout。意味着只能放五个位置，多了会重叠。每个comp组件如果直接放在frame中，会重叠
        //因此要先放在一个容器统一管理。再把该容器添加到frame中。这样就算只添加一个组件

        chatJPanel.setLayout(new BoxLayout(chatJPanel, BoxLayout.Y_AXIS)); // 垂直布局
        chatJPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 容器内边距（上下左右10像素）
        //加载聊天记录
        if (user.getChatList().isEmpty()) {
            chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
        } else {
            for (Map.Entry<String, ArrayList<String>> entry : chatList.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> value = entry.getValue();
                //头像
                ImageIcon avatar = user.getAvatar();

                //对方名字：
                HashMap<String, FriendPublicInfo> friendList = user.getFriendList();
                String username = friendList.get(key).getUsername();

                //聊天记录
                String s = value.get(value.size() - 1);
                String text = s.length() >= 12 ? s.substring(0, 12) + "..." : s;

                //添加到JPanel内
                Display_Case comp = new Display_Case(avatar, username, text, "查看", "删除",
                        new DisplayListener() {
                            @Override
                            public void method(Display_Case d) {
                                openChat(d);
                            }
                        },
                        d -> {
                            removeItem(chatJPanel, d);
                            user.getChatList().remove(friendList.get(key).getUID());
                            if (user.getChatList().isEmpty()) {
                                chatJPanel.add(nothingMessage[0], BorderLayout.CENTER);
                                chatJPanel.repaint();
                            }
                        },
                        friendList.get(key).getUID()
                );
                chatJPanel.add(comp);
                chatJPanel.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）
            }
        }
        //添加滚动条
        JScrollPane scrollPane = getScrollPane(chatJPanel);
        //设置名字
        scrollPane.setName(cardName[0]);
        //添加到卡片布局中
        //参数一：添加的组件
        //参数二：表达该组件布局约束的对象（一个别名）
        //第一个添加到CardLayout对象的组件是容器首次显示时的可见组件。
        cardPanel.add(scrollPane, cardName[0]);
    }

    private void openChat(Display_Case d) {
        String uid = d.getUID();
        FriendPublicInfo info = user.getFriendList().get(uid);
        String friendUsername = info.getUsername();
        if(isOpen.containsKey(uid)){
            JDialogUtil.showJDialog(AppJFrame.this,"与"+ friendUsername +"的聊天窗口已打开!",
                    "提示",()->{},()->{},"确认","取消");
            return;
        }
        if(user.getChatList().get(uid) == null){
            user.getChatList().put(uid,new ArrayList<>());
        }
        System.out.println("打开聊天界面前的集合" + user.getChatList().get(uid));
        Object obj = user.getFriendList().get(uid) == null ? user.getGroupList().get(uid) : user.getFriendList().get(uid);
        new ChatJFrame(user.getChatList().get(uid), obj, uid);
    }

    private static void removeItem(JPanel chatJPanel, Display_Case d) {
        // 步骤1：从容器中移除要删除的对象（真正删除）
        // 步骤2：移除组件之间的垂直间距（Box.createVerticalStrut(10)）
        // 先找到间距组件的索引：comp的索引 + 1（因为comp后加了间距）
        int strutIndex = chatJPanel.getComponentZOrder(d) + 1;
        if (strutIndex < chatJPanel.getComponentCount()) {
            chatJPanel.remove(strutIndex);
        }
        chatJPanel.remove(d);
        // 步骤3：刷新容器布局（核心！否则界面不更新）
        chatJPanel.revalidate(); // 重新计算布局
        chatJPanel.repaint();    // 重绘界面
    }

    private void initJFrame() {
        //1.定义界面大小
        this.setSize(610, 570);
        //2.设置标题
        this.setTitle("聊天室");
        //3.设置关闭模式
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    //退出前更新服务端的用户对象
                    Client.update(user);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        //4.设置居中
        this.setLocationRelativeTo(null);
        //5.设置置顶
        this.setAlwaysOnTop(true);
        //6.设置放置模式（布局模式）
        //BorderLayout布局允许将容器分为 5 个区域，每个区域可以单独放置一个组件
        //分别为上、中、下、左、右。每个位置放置如果超出一个组件，会重叠
        this.setLayout(new BorderLayout());
        //设置图标
        this.setIconImage(new ImageIcon("images\\图标.png").getImage());
        //获取隐藏容器
        pane = this.getContentPane();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        Object[] objects = getButtonItem(obj, buttons);
        Object[] item = getButtonItem(obj, miniJFrameButton);
        //主按钮
        if (obj == objects[0]) {
            uidInput.setText("请输入手机号/UID进行搜索");
            if(isRepaint){
                JPanel com = (JPanel) searchPanel.getComponents()[1];
                for (Component component : com.getComponents()) {
                    if(component instanceof Display_Case){
                        com.remove(component);
                    }
                }
                searchPanel.repaint();
                isRepaint = false;
            }
            //修改按钮背景顺便切换界面
            setIcons((JButton) objects[0]);
        } else if (obj == item[0]) {
            uidInput.setText("请输入手机号/UID进行搜索");
            if(isRepaint){
                JPanel com = (JPanel) searchPanel.getComponents()[1];
                for (Component component : com.getComponents()) {
                    if(component instanceof Display_Case){
                        com.remove(component);
                    }
                }
                searchPanel.repaint();
                isRepaint = false;
            }
            //获取卡片的布局管理器
            CardLayout showPanel = (CardLayout) miniCardPanel.getLayout();
            //show方法显示
            //参数一：要显示的容器
            //参数二：组件名称（在添加到卡片容器时约束的别名）
            showPanel.show(miniCardPanel, miniCardName[(int) item[1]]);
        }
        else if (obj == searchButton) {
            AllInputStreamThread.pool.submit(() -> {
                if (uidInput.getText().trim().equals("请输入手机号/UID进行搜索")) {
                    return;
                }
                //发送请求
                try {
                    //重置
                    searchStr = "";
                    searchResult = null;
                    HashMap<String, String> map = new HashMap<>();
                    map.put("SEARCH", uidInput.getText().trim());
                    Client.os.writeObject(map);
                    //等待结果
                    long start = System.currentTimeMillis();
                    MessageType message = null;
                    //等待五秒钟
                    while (message == null && System.currentTimeMillis() - start < 2000) {
                        message = AllInputStreamThread.APP_Search_Queue.poll();
                        Thread.sleep(100);
                    }
                    if (message == null) {
                        JDialogUtil.showJDialog(this, "用户不存在", "搜索失败",
                                () -> {
                                },
                                () -> {
                                },
                                "确定",
                                "取消"
                        );
                        return;
                    }
                    HashMap<String, FriendPublicInfo> o = (HashMap<String, FriendPublicInfo>) message.getData();
                    for (String s : o.keySet()) {
                        searchStr = s;
                        searchResult = o.get(s);
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                //线程监听结果
                AllInputStreamThread.pool.submit(() -> {
                    while (true) {
                        if (searchStr.equals("NOT-USER")) {
                            //invokeLater:异步  invokeAndWait：同步等待
                            JDialogUtil.showJDialog(this,"没找到用户信息，请核对后再试","搜索好友",
                                    ()->{},
                                    ()->{},
                                    "确认",
                                    "取消"
                                    );
                            break;
                        }
                        if (searchResult != null) {
                            //多线程下修改界面必须用SwingUtilities.invokeLater确保线程安全
                            SwingUtilities.invokeLater(() -> {
                                //移除旧组件
                                for (Component com : result.getComponents()) {
                                    if(com instanceof Display_Case dc){
                                        result.remove(dc);
                                    }
                                }
                                result.revalidate();
                                result.repaint();

                                Display_Case dc = new Display_Case(searchResult.getAvatar(), searchResult.getUsername(), searchResult.getSignature(),
                                        "添加",
                                        "查看",
                                        new DisplayListener() {
                                            @Override
                                            public void method(Display_Case d) {
                                                if(user.getFriendList() != null){
                                                    if(searchResult.getUID().equals(user.getUID())){
                                                        JDialogUtil.showJDialog(AppJFrame.this,"不可添加自己","添加失败",
                                                                ()->{},
                                                                ()->{},
                                                                "确认",
                                                                "取消"
                                                        );
                                                        return;
                                                    }
                                                    for (String s : user.getFriendList().keySet()) {
                                                        if(user.getFriendList().get(s).getUID().equals(searchResult.getUID())){
                                                            JDialogUtil.showJDialog(AppJFrame.this,"对方已是好友","添加失败",
                                                                    ()->{},
                                                                    ()->{},
                                                                    "确认",
                                                                    "取消"
                                                            );
                                                            return;
                                                        }
                                                    }
                                                }
                                                //发送请求
                                                try {
                                                    Client.update(searchResult.getUID(),userInfo,"SYSTEM","NOTICE");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                JDialogUtil.showJDialog(AppJFrame.this, "已发送", "添加", () -> {
                                                }, () -> {
                                                }, "确定", "取消");
                                            }
                                        },
                                         d->JDialogUtil.showJDialog(AppJFrame.this,"该功能未实现,敬请期待","查看",
                                                        ()->{},
                                                        ()->{},
                                                        "确定",
                                                        "取消"
                                         ),
                                        searchResult.getUID()
                                );
                                isRepaint = true;
                                result.add(dc, BorderLayout.NORTH);
                                result.revalidate(); // 重新计算布局
                                result.repaint();    // 重绘界面
                                //重置结果
                            });
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            });
        }
    }

    //获取按钮
    private Object[] getButtonItem(Object e, JButton[] allButton) {
        Object[] ms = new Object[2];
        for (int i = 0; i < allButton.length; i++) {
            JButton button = allButton[i];
            if (e == button) {
                ms[0] = button;
                ms[1] = i;
            }
        }
        return ms;
    }

    //修改背景
    private void setIcons(JButton button) {
        for (int i = 0; i < buttons.length; i++) {
            JButton j = buttons[i];
            if (j == button) {
                //点击了该按钮，所以背景改为亮色
                j.setIcon(new ImageIcon("images\\" + iconsContrary[i]));
                this.setTitle(titleName[i]);
                //获取卡片的布局管理器
                CardLayout showPanel = (CardLayout) cardPanel.getLayout();
                //show方法显示
                //参数一：要显示的容器
                //参数二：组件名称（在添加到卡片容器时约束的别名）
                showPanel.show(cardPanel, cardName[i]);
            } else {
                //其他按钮改成暗色
                j.setIcon(new ImageIcon("images\\" + icons[i]));
            }
        }
    }

}
