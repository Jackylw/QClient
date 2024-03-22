/**
 * @author Jacky Feng
 * @date 2024/3/18 16:17
 */
package top.fexample.qchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import top.fexample.qchat.Application;
import top.fexample.qchat.Service.ClientConnectServerThread;
import top.fexample.qchat.Service.ManageClientConnectServerThread;
import top.fexample.qchat.Service.ManageFriendList;
import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class FriendListController {
    @FXML
    public VBox friendListContainer;
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 创建并显示好友列表
    public void showFriendListStage() {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("views/FriendListView.fxml"));
            Parent friendList = loader.load();
            Stage friendListStage = new Stage();
            friendListStage.getIcons().add(new Image(Objects.requireNonNull(Application.class.getResource("images/qq.gif")).toExternalForm()));
            friendListStage.setResizable(false);
            friendListStage.setTitle(userId);
            friendListStage.setScene(new Scene(friendList));
            friendListStage.show();

            requestFriendList();

            // 关闭好友列表的监听事件
            friendListStage.setOnCloseRequest(event -> {

                // 向服务器发送消息,通知服务器该用户下线
                logout();

                // 获取好友列表的监听线程,然后从管理线程中移除该监听线程
                ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(userId);

                // 关闭监听线程
                if (clientConnectServerThread != null) {
                    System.out.println("关闭与服务器连接的线程");
                    clientConnectServerThread.interrupt();
                }

                // 从管理线程中移除该监听线程
                System.out.println("从管理线程中移除该监听线程");
                ManageClientConnectServerThread.removeClientConnectServerThread(userId);
                ManageFriendList.removeFriendList(userId);

                System.exit(0);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 向服务器发送退出消息
    public void logout() {
        Message message = new Message();
        message.setMsgType(MessageType.CLIENT_EXIT);
        message.setSender(userId);
        try {
            // 获取用户对应的socket,然后向服务器发送退出消息
            Socket socket = ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);

            System.out.println(userId + "退出了客户端");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 请求好友列表
    public void requestFriendList() {
        Message message = new Message();
        message.setMsgType(MessageType.REQUEST_FRIEND);
        message.setSender(userId);
        try {
            // 获取用户对应的socket,然后向服务器发送请求好友列表消息
            Socket socket = ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);

            System.out.println(userId + "请求好友列表");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
