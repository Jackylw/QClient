/**
 * @author Jacky Feng
 * @date 2024/3/17 22:29
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;
import top.fexample.qchat.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnectServerThread extends Thread {
    // 线程持有的socket
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器没有数据发送,线程阻塞
                Message message = (Message) ois.readObject();

                // 判断类型,处理消息
                if (message.getMsgType().equals(MessageType.RECEIVE_ONLINE_FRIEND)) {
                    // 取出在线列表,规定返回id值通过一个空格分隔
                    String[] onlineUsers = message.getContent().split(" ");

                    System.out.println("收到在线列表:" + Arrays.toString(onlineUsers));

                } else {
                    System.out.println("其他类型");
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

    // 向服务器请求在线用户列表
    public void onlineFriendList(User user) {
        Message message = new Message();
        message.setSender(user.getUserId());
        message.setMsgType(MessageType.GET_ONLINE_FRIEND);

        // 发送消息到服务器
        // 得到当前线程的socket对应的ObjectOutputStream
        try {
            // 从管理线程池中获取当前用户对应ClientConnectServerThread的socket
            ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId());
            Socket socket = clientConnectServerThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
