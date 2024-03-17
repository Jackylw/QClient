/**
 * @author Jacky Feng
 * @date 2024/3/17 22:45
 * @description 管理客户端连接服务器线程
 */
package top.fexample.qclient.Service;

import java.util.HashMap;

public class ManageClientConnectServerThread {
    // Key:UserId Value:客户端连接服务器线程
    private static HashMap<String, ClientConnectServerThread> clientConnectServerThreadHashMap = new HashMap<>();

    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        clientConnectServerThreadHashMap.put(userId, clientConnectServerThread);
    }

    public static void removeClientConnectServerThread(String userId) {
        clientConnectServerThreadHashMap.remove(userId);
    }

    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return clientConnectServerThreadHashMap.get(userId);
    }
}
