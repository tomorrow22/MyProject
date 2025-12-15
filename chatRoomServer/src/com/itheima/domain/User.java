package com.itheima.domain;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

//封装用户信息对象
//                                      聊天记录是很有必要的！
//                                      本地聊天记录和云端备份！
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 552486512336L;
    private String UID;//每个用户的唯一标识
    private String userName;//用户名（昵称）
    private String password;//密码
    private String IP;//IP
    private String phoneNumber;//手机号
    private ImageIcon avatar;//头像
    private String signature;//个性签名
    private String lastLoginDate;//最后一次登录时间
    private HashMap<String,FriendPublicInfo> friendList;//好友列表（好友列表得单独定义）
    //聊天记录 k 与谁的  v聊天内容
    private HashMap<String, ArrayList<String>> chatList;
    //群聊列表：k 群的UID， v群
    private HashMap<String,Group> groupList;//群聊列表
    private boolean isOnline;//是否在线
    private boolean isLock;//账号是否封禁

    public User(String UID, String userName, String password, String IP, String phoneNumber, ImageIcon avatar,
                String signature, String lastLoginDate, HashMap<String,FriendPublicInfo> friendList,
                HashMap<String, ArrayList<String>> chatList, HashMap<String,Group> groupList, boolean isOnline, boolean isLock) {
        this.UID = UID;
        this.userName = userName;
        this.password = password;
        this.IP = IP;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.signature = signature;
        this.lastLoginDate = lastLoginDate;
        this.friendList = friendList;
        this.chatList = chatList;
        this.groupList = groupList;
        this.isOnline = isOnline;
        this.isLock = isLock;
    }

    /**
     * 获取
     * @return UID
     */
    public String getUID() {
        return UID;
    }

    /**
     * 设置
     * @param UID
     */
    public void setUID(String UID) {
        this.UID = UID;
    }

    /**
     * 获取
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取
     * @return IP
     */
    public String getIP() {
        return IP;
    }

    /**
     * 设置
     * @param IP
     */
    public void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * 获取
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 获取
     * @return avatar
     */
    public ImageIcon getAvatar() {
        return avatar;
    }

    /**
     * 设置
     * @param avatar
     */
    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取
     * @return signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 设置
     * @param signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 获取
     * @return lastLoginDate
     */
    public String getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * 设置
     * @param lastLoginDate
     */
    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * 获取
     * @return friendList
     */
    public HashMap<String,FriendPublicInfo> getFriendList() {
        return friendList;
    }

    /**
     * 设置
     * @param friendList
     */
    public void setFriendList(HashMap<String,FriendPublicInfo> friendList) {
        this.friendList = friendList;
    }

    /**
     * 获取
     * @return chatList
     */
    public HashMap<String, ArrayList<String>> getChatList() {
        return chatList;
    }

    /**
     * 设置
     * @param chatList
     */
    public void setChatList(HashMap<String, ArrayList<String>> chatList) {
        this.chatList = chatList;
    }

    /**
     * 获取
     * @return groupList
     */
    public HashMap<String,Group> getGroupList() {
        return groupList;
    }

    /**
     * 设置
     * @param groupList
     */
    public void setGroupList(HashMap<String,Group> groupList) {
        this.groupList = groupList;
    }

    /**
     * 获取
     * @return isOnline
     */
    public boolean isIsOnline() {
        return isOnline;
    }

    /**
     * 设置
     * @param isOnline
     */
    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * 获取
     * @return isLock
     */
    public boolean isIsLock() {
        return isLock;
    }

    /**
     * 设置
     * @param isLock
     */
    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public String toString() {
        return "User{serialVersionUID = " + serialVersionUID + ", UID = " + UID + ", userName = " + userName + ", password = " + password + ", IP = " + IP + ", phoneNumber = " + phoneNumber + ", avatar = " + avatar + ", signature = " + signature + ", lastLoginDate = " + lastLoginDate + ", friendList = " + friendList + ", chatList = " + chatList + ", groupList = " + groupList + ", isOnline = " + isOnline + ", isLock = " + isLock + "}";
    }

    //不需要空参，因为每个用户都会有信息

}
