/**
 * @author Jacky Feng
 * @date 2024/3/17 22:29
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    // 线程持有的socket
    private Socket socket;
    private String userId;

    public ClientConnectServerThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器没有数据发送,线程阻塞
                Message message = (Message) ois.readObject();

                // 判断类型,处理消息
                switch (message.getMsgType()) {
                    case MessageType.RECEIVE_ONLINE_FRIEND:
                        // 取出在线列表,规定返回id值通过一个空格分隔
                        String[] onlineUsers = message.getContent().split(" ");
                        ManageUserDisplay.addOnlineUser(userId, onlineUsers);
                        break;
                    case MessageType.RECEIVE_FRIEND_REQUEST:
                        String[] friendList = message.getContent().split(" ");
                        ManageUserDisplay.addFriendList(userId, friendList);
                        break;
                    default:
                        System.out.println("其他类型");
                        break;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                // 检查是否是由于线程被中断导致的
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("线程被中断，退出监听");
                    return;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
