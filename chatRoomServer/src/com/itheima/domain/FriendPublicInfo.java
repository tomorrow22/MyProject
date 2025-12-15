package com.itheima.domain;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;

//对外展示可被外人看到的属性
public class FriendPublicInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1245786346L;
    private String username;
    private String UID;
    private ImageIcon avatar;//头像
    private String signature;//个性签名
    private boolean isOnline;//是否在线

    public FriendPublicInfo() {
    }

    public FriendPublicInfo(String username, String UID, ImageIcon avatar, String signature, boolean isOnline) {
        this.username = username;
        this.UID = UID;
        this.avatar = avatar;
        this.signature = signature;
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
