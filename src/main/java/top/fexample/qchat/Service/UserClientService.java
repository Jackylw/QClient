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
import java.net.SocketException;

public class UserClientService {

    // 可能在其他地方使用该信息,因此作为成员属性
    private User user = new User(null, null);
    private Socket socket;

    // 根据用户id和密码到服务器验证用户是否存在
    public String checkUser(String userId, String userPassword, String requestType) {
        user = new User(userId, userPassword);
        user.setUserId(userId);
        user.setUserPassword(userPassword);
        user.setRequestType(requestType);

        try {
            try {
                socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            } catch (SocketException e) {
                return MessageType.CONNECT_SERVER_TIMEOUT;
            }
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            if (message.getMsgType().equals(MessageType.LOGIN_SUCCESS)) {

                // 创建和服务器端保持通讯的线程,并添加到管理线程池中
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket, userId);
                clientConnectServerThread.start();
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

                return MessageType.LOGIN_SUCCESS;
            } else {
                socket.close();
                return MessageType.LOGIN_FAIL;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 用户注册
    public String registerUser(String userId, String userPassword, String regUserQuestion, String regUserAnswer, String requestType) {
        user = new User(userId, userPassword);
        user.setUserId(userId);
        user.setUserPassword(userPassword);
        user.setRequestType(requestType);
        user.setSecurityQuestion(regUserQuestion);
        user.setSecurityAnswer(regUserAnswer);

        try {
            try {
                socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            } catch (SocketException e) {
                return MessageType.CONNECT_SERVER_TIMEOUT;
            }

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            // 每一次注册都会创建一个新的socket,进行完一次注册后就关闭socket
            if (message.getMsgType().equals(MessageType.REGISTER_SUCCESS)) {
                socket.close();
                return MessageType.REGISTER_SUCCESS;
            } else if (message.getMsgType().equals(MessageType.REGISTER_EXIST)) {
                socket.close();
                return MessageType.REGISTER_EXIST;
            } else {
                socket.close();
                return MessageType.REGISTER_ERROR;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 找回密码的用户查找方法
    public Message findPassword(String userId, String requestType) {
        user = new User(userId, "null");
        user.setUserId(userId);
        user.setRequestType(requestType);

        try {
            try {
                socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            } catch (SocketException e) {
                Message message = new Message();
                message.setMsgType(MessageType.CONNECT_SERVER_TIMEOUT);
                return message;
            }

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            socket.close();
            return message;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 验证并修改用户密码
    public Message checkAndModifyPassword(String userId, String answer, String newPassword, String requestType) {
        user = new User(userId, newPassword);
        user.setUserId(userId);
        user.setSecurityAnswer(answer);
        user.setUserPassword(newPassword);
        user.setRequestType(requestType);

        try {
            try {
                socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            } catch (SocketException e) {
                Message message = new Message();
                message.setMsgType(MessageType.CONNECT_SERVER_TIMEOUT);
                return message;
            }

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            socket.close();
            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}