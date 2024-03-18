/**
 * @author Jacky Feng
 * @date 2024/3/17 22:15
 * @description 该类完成用户登录验证
 */
package top.fexample.qchat.Service;

import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;
import top.fexample.qchat.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {

    // 可能在其他地方使用该信息,因此作为成员属性
    private User user = new User(null, null);
    private Socket socket;

    // 根据用户id和密码到服务器验证用户是否存在
    public boolean checkUser(String userId, String userPassword) {
        user = new User(userId, userPassword);
        user.setUserId(userId);
        user.setUserPassword(userPassword);

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            if (message.getMsgType().equals(MessageType.LOGIN_SUCCESS)) {

                // 创建和服务器端保持通讯的线程,并添加到管理线程池中
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

                // 向服务器申请在线好友列表
                onlineFriendList();

                return true;
            } else {
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    // 向服务器请求在线用户列表
    public void onlineFriendList() {
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
}
