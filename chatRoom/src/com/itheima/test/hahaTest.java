package com.itheima.test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class hahaTest {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();

        frame.setSize(500,500);
        //BorderLayout布局允许将容器分为 5 个区域，每个区域可以单独放置一个组件
        //分别为上、中、下、左、右。每个位置放置如果超出一个组件，会重叠
        frame.setLayout(new BorderLayout());
        String s = "images\\用户.png";
        FileInputStream fis = new FileInputStream(s);
        byte[] bytes = new byte[1024 * 1024 * 5];
        int read = fis.read(bytes);
        fis.close();
        Image image = ImageIO.read(new ByteArrayInputStream(bytes)).getScaledInstance(40, 30, Image.SCALE_SMOOTH);


        //因为设置了BorderLayout。意味着只能放五个位置，多了会重叠。每个comp组件如果直接放在frame中，会重叠
        //因此要先放在一个容器统一管理。再把该容器添加到frame中。这样就算只添加一个组件
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // 垂直布局
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 容器内边距（上下左右10像素）

        for (int i = 0; i < 20; i++) {
            haha comp = new haha(new ImageIcon(image),"明天再说吧" + i,"你好烦aaaaaaaaaaaaa...","查看","删除");

            container.add(comp);
            container.add(Box.createVerticalStrut(10)); // 组件之间的垂直间距（10像素）

        }

        // 4. 创建滚动面板：把中间容器套进去
        JScrollPane scrollPane = new JScrollPane(container);
        //滚动的速度
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        // 可选：设置滚动条策略（默认自动显示，也可强制显示）
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 垂直滚动条按需显示
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 禁止水平滚动条

        // 5. 主窗口添加滚动面板(添加到中间位置)
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonJPanel = new JPanel();

        buttonJPanel.setSize(frame.getWidth(),100);

        buttonJPanel.setLayout(new FlowLayout(FlowLayout.CENTER,70,5));
        JButton chatButton = new JButton("聊  天");
        JButton friendButton = new JButton("联系人");
        JButton myButton = new JButton("我  的");
        buttonJPanel.add(chatButton);
        buttonJPanel.add(friendButton);
        buttonJPanel.add(myButton);

        frame.add(buttonJPanel,BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
