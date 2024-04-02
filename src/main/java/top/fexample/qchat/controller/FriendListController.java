/**
 * @author Jacky Feng
 * @date 2024/3/18 16:17
 */
package top.fexample.qchat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import top.fexample.qchat.Application;
import top.fexample.qchat.Service.ClientConnectServerThread;
import top.fexample.qchat.Service.ManageClientConnectServerThread;
import top.fexample.qchat.Service.ManageFriendList;
import top.fexample.qchat.Service.ManageUserDisplay;
import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FriendListController {
    @FXML
    public VBox friendListContainer;
    private String userId;
    // 存储每个好友节点，便于后续操作此节点
    public static final ConcurrentHashMap<String, Node> friendListNodes = new ConcurrentHashMap<>();

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 更新一次好友列表
    public void updateList(String userId) throws InterruptedException {
        // 等待数据准备就绪
        while (ManageUserDisplay.getFriendList(userId) == null || ManageUserDisplay.getOnlineUserList(userId) == null) {
            // 等待数据准备就绪，可以适当设置超时时间
            TimeUnit.MILLISECONDS.sleep(250);
        }

        String[] friendList = ManageUserDisplay.getFriendList(userId);
//        String[] onlineUserList = ManageUserDisplay.getOnlineUserList(userId);

        for (String friend : friendList) {
            // 加载好友列表用户的视图
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("views/FriendListUserView.fxml"));
            try {
                Node friendNode = loader.load();
                friendListNodes.put(friend, friendNode);

                FriendListUserController friendListUserController = loader.getController();
                friendListUserController.setUserController(friend, "好友");

                // 双击点击事件
                friendNode.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                        //聊天框的显示
                        System.out.println("双击了" + friend);

                        // 调用ChatController的showChatStage方法
                        FXMLLoader loader2 = new FXMLLoader(Application.class.getResource("views/chatView.fxml"));
                        try {
                            Parent chatView = loader2.load();
                            ChatController chatController = loader2.getController();
                            Label statusLabel = (Label) friendNode.lookup("#statusLabel");
                            chatController.showChatStage(userId, friend, statusLabel.getText(), chatView);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                friendListContainer.getChildren().add(friendNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 创建并显示好友列表
    public void showFriendListStage() {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("views/FriendListView.fxml"));
            Parent friendList = loader.load();

            requestFriendList();

            // 这里friendListContainer才被初始化
            FriendListController friendListController = loader.getController();
            ManageFriendList.addFriendList(userId, friendListController);
            friendListController.updateList(userId);

            Stage friendListStage = new Stage();
            friendListStage.getIcons().add(new Image(Objects.requireNonNull(Application.class.getResource("images/qq.gif")).toExternalForm()));
            friendListStage.setResizable(false);
            friendListStage.setTitle(userId);
            friendListStage.setScene(new Scene(friendList));
            friendListStage.show();

            // 关闭好友列表的监听事件
            friendListStage.setOnCloseRequest(event -> {
                logout();
                ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(userId);
                if (clientConnectServerThread != null) {
                    System.out.println("关闭与服务器连接的线程");
                    clientConnectServerThread.interrupt();
                }
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

    // 请求最新好友列表:好友列表显示、添加好友、删除好友时调用
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
