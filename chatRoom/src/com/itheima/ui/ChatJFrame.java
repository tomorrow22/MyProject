package com.itheima.ui;

import com.itheima.domain.*;
import com.itheima.util.Client;
import com.itheima.util.JDialogUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

//聊天界面
public class ChatJFrame extends JFrame implements ActionListener{
    Container pane;
    String uid;
    ArrayList<String> chat;
    boolean online;
    FriendPublicInfo friend;
    JButton send = new JButton("发送");
    JTextArea area = new JTextArea();
    ArrayList<FriendPublicInfo> list = new ArrayList<>();//发给谁
    JPanel item = new JPanel();//信息管理界面
    //参数一：聊天记录
    //参数二：用户对象
    //参数三：对方信息
    //参数四：对方UID
    public ChatJFrame(ArrayList<String> chat, Object obj,String UID){
        uid = UID;
        if(obj instanceof Group group){
            online = true;
            list.addAll(group.getList());
        }else{
            friend = (FriendPublicInfo) obj;
            online = friend.isOnline();
            list.add(friend);
        }
        this.chat = chat;

        //初始化界面
        initJFrame();
        //初始化组件
        initView();

        this.setVisible(true);
    }


/*    可以试试：
        线程作用不变，然后信息来了更新外面聊天栏的信息
        然后看看聊天界面是否开着：
                    如果开着，那将信息传进来添加条目更新UI界面：：：记得将信息添加到信息集合更新
                    如果没开就不管了。毕竟开这个聊天界面是需要传递信息集合的*/

    private void initView() {
        //头部
        headPanel();
        //中间
        middlePanel();
        //下面
        belowPanel();
    }

    private void belowPanel() {
        JPanel below = new JPanel(new FlowLayout(FlowLayout.LEFT,100,50));
        //输入框
        JPanel areaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        area.setBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.BLACK,Color.BLACK));
        areaPanel.setSize(300,50);
        area.setSize(200,50);
        areaPanel.add(area);
        send.addActionListener(this);
        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        /*        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<FriendPublicInfo> list = new ArrayList<>();
                list.add(friend);
                Send s = new Send(user.getUID(),list ,user.getUserName()+":测试发送数据2", ZonedDateTime.now());

                HashMap<String,HashMap<String,Send>> map = new HashMap<>();
                HashMap<String,Send> sendMap = new HashMap<>();
                sendMap.put("CHAT",s);
                map.put("NOTICE",sendMap);

                try {
                    Client.os.writeObject(map);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


            }
        });*/

        sendPanel.add(send);

        below.add(areaPanel);
        below.add(sendPanel);

        pane.add(below,BorderLayout.SOUTH);
    }

    private void middlePanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        //遍历信息集合添加

        item.setLayout(new BoxLayout(item,BoxLayout.Y_AXIS));
        for (String s : chat) {
            JPanel childItem = addChatPanel(s);
            item.add(childItem);
        }
        //添加滚动
        JScrollPane scroll = new JScrollPane(item);
        scroll.getVerticalScrollBar().setUnitIncrement(50);
        chatPanel.add(scroll);
        //添加边框
        chatPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.BLACK,Color.BLACK));
        //添加到界面
        pane.add(chatPanel,BorderLayout.CENTER);
    }

    private JPanel addChatPanel(String s) {
        JPanel childItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //头像
        String[] split = s.split(":");
        ImageIcon icon;
        if(split[0].equals(AppJFrame.user.getUserName())){
            icon = AppJFrame.user.getAvatar();
        }else{
            icon = friend.getAvatar();
        }
        childItem.setSize(this.getWidth(),icon.getIconHeight());
        childItem.add(new JLabel(icon));
        //聊天记录
        JLabel comp = new JLabel(split[1]);
        comp.setFont(new Font("幼圆",Font.BOLD,20));
        childItem.add(comp);
        return childItem;
    }

    //新增信息用以更新UI界面
    public void add_Message(String str){
        SwingUtilities.invokeLater(()->{
            item.add(addChatPanel(str));
            item.revalidate();//重新计算布局
            item.repaint();//重绘界面
        });
    }

    private void headPanel() {
        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //对方状态
        JLabel onlineImage ;
        if(online){
            onlineImage = new JLabel(new ImageIcon("images\\online.png"));
        }else{
            onlineImage = new JLabel(new ImageIcon("images\\offline.png"));
        }
        //边框
        head.setBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.BLACK,Color.BLACK));

        head.add(onlineImage);
        //对方名字
        JLabel comp = new JLabel(friend.getUsername());
        comp.setFont(new Font("幼圆",Font.BOLD,16));
        head.add(comp);
        //添加到界面
        pane.add(head,BorderLayout.NORTH);

    }

    private void initJFrame() {
        //1.定义界面大小
        this.setSize(610, 570);
        //2.设置标题
        this.setTitle("聊天窗口");
        //3.设置关闭模式---------------
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//关闭该窗口不影响主界面，但是主界面关闭则所有聊天窗口关闭
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AppJFrame.isOpen.remove(uid);//一个对应的好友聊天窗口只能打开一个
            }
            @Override
            public void windowOpened(WindowEvent e) {
                AppJFrame.isOpen.put(uid,ChatJFrame.this);
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
        if(obj == send){
            SwingUtilities.invokeLater(()->{
                if("".equals(area.getText().trim())){
                    JDialogUtil.showJDialog(ChatJFrame.this,"不可发送空字符","提示",()->{},()->{},"好的","知道了");
                    return;
                }
                HashMap<String,HashMap<String,Send>> map = new HashMap<>();//封装要发送的对象（固定）
                HashMap<String,Send> sendMap = new HashMap<>();
                map.put("NOTICE",sendMap);
                Send s = new Send();//信息格式
                s.setUID(AppJFrame.user.getUID());
                s.setList(list);
                String str = AppJFrame.user.getUserName() + ":" + area.getText();
                s.setStr(str);
                System.out.println("发送的文本为：" + str);
                s.setTime(ZonedDateTime.now());
                sendMap.put("CHAT",s);
                //更新自己的信息
                AppJFrame.user.getChatList().get(uid).add(str);
                //加载信息到界面中
                item.add(addChatPanel(str));
                item.revalidate();//重新计算布局
                item.repaint();//重绘界面
                //设置文本
                area.setText("");
                try {
                    Client.os.writeObject(map);
                    Client.os.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
