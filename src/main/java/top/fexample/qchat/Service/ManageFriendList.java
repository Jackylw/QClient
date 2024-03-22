/**
 * @author Jacky Feng
 * @date 2024/3/22 15:33
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.controller.FriendListController;

import java.util.concurrent.ConcurrentHashMap;

public class ManageFriendList {
    public static ConcurrentHashMap<String, FriendListController> friendList = new ConcurrentHashMap<>();

    public static void addFriendList(String userId, FriendListController friendListController) {
        friendList.put(userId, friendListController);
    }

    public static void removeFriendList(String userId) {
        friendList.remove(userId);
    }

    public static FriendListController getFriendList(String userId) {
        return friendList.get(userId);
    }
}
