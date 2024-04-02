/**
 * @author Jacky Feng
 * @date 2024/3/23 14:18
 * @description 管理用户的列表显示数据, 包括好友列表和在线用户列表
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.controller.FriendListController;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ManageUserDisplay {
    public static ConcurrentHashMap<String, String[]> onlineUserMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String[]> friendListMap = new ConcurrentHashMap<>();

    public static void addOnlineUser(String userId, String[] onlineUserList) {
        onlineUserMap.put(userId, onlineUserList);
        System.out.println("服务器更新了当前在线列表:" + Arrays.toString(onlineUserList));

    }

    public static void addFriendList(String userId, String[] friendList) {
        friendListMap.put(userId, friendList);
        System.out.println("服务器更新了当前好友列表:" + Arrays.toString(friendList));
    }

    public static void removeOnlineUser(String userId) {
        onlineUserMap.remove(userId);
    }

    public static void removeFriendList(String userId) {
        friendListMap.remove(userId);
    }

    public static String[] getOnlineUserList(String userId) {
        System.out.println(userId + "获取当前在线用户列表:" + Arrays.toString(onlineUserMap.get(userId)));
        return onlineUserMap.get(userId);
    }

    public static String[] getFriendList(String userId) {
        System.out.println(userId + "获取当前好友列表:" + Arrays.toString(friendListMap.get(userId)));
        return friendListMap.get(userId);
    }
}
