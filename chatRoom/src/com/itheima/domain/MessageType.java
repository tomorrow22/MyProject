package com.itheima.domain;

import java.io.Serial;
import java.io.Serializable;

//管理消息类型----统一格式
public class MessageType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    //主消息类型
    private String type;//消息类型
    //子消息类型
    private String childType;
    private Object data;//数据


    public MessageType() {
    }

    public MessageType(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    public MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "MessageType{" +
                "type='" + type + '\'' +
                ", childType='" + childType + '\'' +
                ", data=" + data +
                '}';
    }

}
