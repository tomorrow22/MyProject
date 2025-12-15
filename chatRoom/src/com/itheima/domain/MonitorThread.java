package com.itheima.domain;

import com.itheima.util.Client;
import com.itheima.util.JDialogUtil;

import java.io.IOException;
import java.util.HashMap;

//监听Socket是否正常
public class MonitorThread extends Thread {
    private final String ip = Client.serverIP;
    private final int port = Client.serverPort;
    //最大重连次数
    private final int MAX_RECONNECT_COUNT = 3;
    //下次监听等待时间
    private final long RECONNECT_TIME = 1000 * 60 * 5L;
    private int nowCount = 0;
    //标记用户是否重新尝试连接
    private boolean isAgain = true;
    //标记“重连成功”的弹窗是否已经出现过
    private boolean isConnect = false;

    @Override
    public void run() {
        while (true) {
            //重连失败，等待三秒
            try {
                Thread.sleep(3000);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
            if (!isAgain) {
                Client.isStart = false;
                //关闭旧Socket
                if (Client.socket != null) {
                    try {
                        Client.os.close();
                        Client.is.close();
                        Client.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            try {
                Client.isStart = true;
                //k：消息类型
                //v: 消息对象
                HashMap<String, Send> map = new HashMap<>();
                map.put("ON_LINE", null);
                Client.os.writeObject(map);
                Thread.sleep(RECONNECT_TIME);
            } catch (IOException | NullPointerException e) {
                //不能正常写出
                try {
                    //关闭旧Socket
                    if (Client.socket != null) {
                        Client.os.close();
                        Client.is.close();
                        Client.socket.close();
                    }
                    //尝试重新连接
                    Client.connectServer(ip, port);
                    if (Client.socket != null && !isConnect) {
                        //重连成功,线程进入等待
                        JDialogUtil.showJDialog(null, "连接成功", "成功",
                                () -> {
                                }, () -> {
                                }, "确认", "取消");
                        isConnect = true;
                        Thread.sleep(RECONNECT_TIME);
                    }

                } catch (IOException ex) {
                    //重连失败
                    isConnect = false;
                    JDialogUtil.showJDialog(null, "第" + (nowCount + 1) + "次重连失败...", "连接失败",
                            () -> {
                            }, () -> {
                                isAgain = false;
                            }, "确认", "取消");
                    synchronized (MonitorThread.class) {
                        nowCount++;
                        if (nowCount == MAX_RECONNECT_COUNT) {
                            //给用户自行选择！
                            //用户“重连”时重置nowCount的值
                            //用户“取消”时直接break结束该线程
                            //用户手动连接时调用Client中的ConnectServer方法，会重新监听Socket
                            JDialogUtil.showJDialog(null, "多次重连失败，是否尝试继续？也可手动尝试", "提示", () -> {
                                isAgain = true;
                            }, () -> {
                                isAgain = false;
                            }, "确认", "取消");
                            if (isAgain) {
                                //继续
                                nowCount = 0;
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
