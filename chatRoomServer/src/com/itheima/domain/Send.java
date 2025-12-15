package com.itheima.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;

//发送信息的类
//信息格式为：
//      测试一（对方用户名）:你好你好
//      测试二(自己用户名):你也好
public class Send implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234546L;
    private String UID;//谁发的
    private ArrayList<FriendPublicInfo> list;//发给谁
    private String str;//发什么信息
    private ZonedDateTime time;//什么时间发的

    public Send() {
    }

    public Send(String UID, ArrayList<FriendPublicInfo> list, String str, ZonedDateTime time) {
        this.UID = UID;
        this.list = list;
        this.str = str;
        this.time = time;
    }


    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ArrayList<FriendPublicInfo> getList() {
        return list;
    }

    public void setList(ArrayList<FriendPublicInfo> list) {
        this.list = list;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Send{" +
                "UID='" + UID + '\'' +
                ", list=" + list +
                ", str='" + str + '\'' +
                ", time=" + time +
                '}';
    }
}
