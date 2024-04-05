/**
 * @author Jacky Feng
 * @date 2024/3/17 22:29
 */
package top.fexample.qchat.Service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import top.fexample.qchat.Application;
import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;
import top.fexample.qchat.controller.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
                    case MessageType.COMMON_MESSAGE:
                        ChatController chatController = ManageChatView.getChatController(message.getSender());

                        // 如果聊天窗口已打开,则将消息传递给chatController
                        if (ManageChatView.getChatStage(message.getReceiver()) != null) {
                            chatController.setMessage(message);
                        } else {
                            // 接收者视角的发送者,即接收者的好友
                            System.out.println("用户未打开" + message.getSender() + "的聊天窗口,缓存消息");
                            MessageBuffer.addBufferedMessage(message.getSender(), message);
                        }
                    case MessageType.ADD_USER_SUCCESS:
                        Platform.runLater(() -> {
                            AddUserController addUserController1 = ManageAddUserController.getAddUserController(userId);
                            if (addUserController1 != null) {
                                addUserController1.setAddStatus(addUserController1.userId + "添加好友成功");
                            } else {
                                System.out.println("添加好友成功，但无法找到相关控制器");
                            }
                        });
                        break;
                    case MessageType.ADD_USER_FAIL:
                        Platform.runLater(() -> {
                            AddUserController addUserController2 = ManageAddUserController.getAddUserController(userId);
                            if (addUserController2 != null) {
                                addUserController2.setAddStatus(addUserController2.userId + "添加好友失败");
                            } else {
                                System.out.println("添加好友失败，addUserController为空");
                            }
                        });
                        break;
                    case MessageType.DELETE_USER_SUCCESS:
                        Platform.runLater(() -> {
                            DelUserController delUserController1 = ManageDelUserController.getDelUserController(userId);
                            if (delUserController1 != null) {
                                delUserController1.setDelStatus(delUserController1.userId + "删除好友成功");
                            } else {
                                System.out.println("删除好友成功，但无法找到相关控制器");
                            }
                        });
                        break;
                    case MessageType.DELETE_USER_FAIL:
                        Platform.runLater(() -> {
                            DelUserController delUserController2 = ManageDelUserController.getDelUserController(userId);
                            if (delUserController2 != null) {
                                delUserController2.setDelStatus(delUserController2.userId + "删除好友失败");
                            } else {
                                System.out.println("删除好友失败，delUserController为空");
                            }
                        });
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
