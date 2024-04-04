/**
 * @author Jacky Feng
 * @date 2024/4/4 22:47
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.controller.AddUserController;

import java.util.concurrent.ConcurrentHashMap;

public class ManageAddUserController {
    public static ConcurrentHashMap<String, AddUserController> addUserControllerMap = new ConcurrentHashMap<>();

    public static void addAddUserController(String userId, AddUserController addUserController) {
        System.out.println(userId + "打开了添加好友窗口");
        addUserControllerMap.put(userId, addUserController);
    }

    public static AddUserController getAddUserController(String userId) {
        return addUserControllerMap.get(userId);
    }

    public static void removeAddUserController(String userId) {
        System.out.println(userId + "关闭了添加好友窗口");
        addUserControllerMap.remove(userId);
    }
}
