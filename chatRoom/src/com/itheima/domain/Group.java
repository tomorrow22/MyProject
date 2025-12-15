package com.itheima.domain;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

//群聊
public class Group implements Serializable {
    @Serial
    private static final long serialVersionUID = 45685231L;
    private String UID;//群聊UID
    private String name;//群聊名称
    private String refer;//群聊描述
    private ImageIcon icon;//群聊头像
    private String kingUID;//群主UID
    private ArrayList<FriendPublicInfo> list;//群聊用户


    public Group() {
    }

    public Group(String UID, String name, String refer, ImageIcon icon, String kingUID, ArrayList<FriendPublicInfo> list) {
        this.UID = UID;
        this.name = name;
        this.refer = refer;
        this.icon = icon;
        this.kingUID = kingUID;
        this.list = list;
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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return refer
     */
    public String getRefer() {
        return refer;
    }

    /**
     * 设置
     * @param refer
     */
    public void setRefer(String refer) {
        this.refer = refer;
    }

    /**
     * 获取
     * @return icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * 设置
     * @param icon
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    /**
     * 获取
     * @return kingUID
     */
    public String getKingUID() {
        return kingUID;
    }

    /**
     * 设置
     * @param kingUID
     */
    public void setKingUID(String kingUID) {
        this.kingUID = kingUID;
    }

    /**
     * 获取
     * @return list
     */
    public ArrayList<FriendPublicInfo> getList() {
        return list;
    }

    /**
     * 设置
     * @param list
     */
    public void setList(ArrayList<FriendPublicInfo> list) {
        this.list = list;
    }

    public String toString() {
        return "Group{serialVersionUID = " + serialVersionUID + ", UID = " + UID + ", name = " + name + ", refer = " + refer + ", icon = " + icon + ", kingUID = " + kingUID + ", list = " + list + "}";
    }
}
