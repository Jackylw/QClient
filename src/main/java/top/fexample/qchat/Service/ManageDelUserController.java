/**
 * @author Jacky Feng
 * @date 2024/4/5 14:44
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.controller.DelUserController;

import java.util.concurrent.ConcurrentHashMap;

public class ManageDelUserController {
    public static ConcurrentHashMap<String, DelUserController> delUserControllerMap = new ConcurrentHashMap<>();

    public static void delUserController(String userId, DelUserController delUserController) {
        System.out.println(userId + "打开了删除好友窗口");
        delUserControllerMap.put(userId, delUserController);
    }

    public static DelUserController getDelUserController(String userId) {
        return delUserControllerMap.get(userId);
    }

    public static void removeDelUserController(String userId) {
        System.out.println(userId + "关闭了删除好友窗口");
        delUserControllerMap.remove(userId);
    }
}
