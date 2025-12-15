package com.itheima.thread;

import com.itheima.domain.MessageType;
import com.itheima.domain.User;
import com.itheima.util.Client;

import java.util.HashMap;
import java.util.concurrent.*;

//使用单线程接收所有来自服务端的数据，按消息类型再分发给对应的线程
//好处：可以避免信息错乱，也能保证线程的安全
//消息类型：  HashMap<String,HashMap<String,Object>>
public class AllInputStreamThread {

    private AllInputStreamThread(){}

    //登录界面
    public static ConcurrentLinkedQueue<MessageType> LoginQueue = new ConcurrentLinkedQueue<>();
    //注册界面
    public static ConcurrentLinkedQueue<MessageType> RegexQueue = new ConcurrentLinkedQueue<>();
    //主界面-----通知
    public static ConcurrentLinkedQueue<MessageType> APP_Notice_Queue = new ConcurrentLinkedQueue<>();
    //主界面-----搜索
    public static ConcurrentLinkedQueue<MessageType> APP_Search_Queue = new ConcurrentLinkedQueue<>();
    //线程池
    public static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            4,
            6,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3),
            Executors.defaultThreadFactory()
    );

    //启动线程监听输入
    public static void startGlobalListener(){
        pool.submit(()->{
            while(true){
                MessageType o = (MessageType) Client.is.readObject();
                System.out.println("接收到信息：" + o);
                System.out.println("接收到信息类型：" + o.getType());
                //按消息类型分发
                if("LOGIN-TYPE".equals(o.getType())){
                    LoginQueue.offer(o);
                }else if("REGEX-TYPE".equals(o.getType())){
                    RegexQueue.offer(o);
                }else if("APP-NOTICE-TYPE".equals(o.getType())){
                    APP_Notice_Queue.offer(o);
                }else if("APP-SEARCH-TYPE".equals(o.getType())){
                    APP_Search_Queue.offer(o);
                }
                Thread.sleep(100);
            }
        });
    }
}
