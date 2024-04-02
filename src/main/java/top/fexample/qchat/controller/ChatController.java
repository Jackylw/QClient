package top.fexample.qchat.controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.fexample.qchat.Service.ManageChatView;
import top.fexample.qchat.Service.ManageClientConnectServerThread;
import top.fexample.qchat.Service.MessageBuffer;
import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;

import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;

public class ChatController {
    @FXML
    public TextArea sendInformation;

    @FXML
    public Button sendInformationButton;

    @FXML
    public TextArea receiveInformation;

    @FXML
    private TextField chatUserId;

    @FXML
    private TextField chatUserStatus;

    // 将消息显示到聊天框
    public void setMessage(Message msg) {
        displayMessage(msg);
    }

    // 处理暂存的消息
    public void processBufferedMessages(String receiverId) {
        List<Message> messagesToDisplay = MessageBuffer.getAndClearBufferedMessages(receiverId);

        if (Objects.nonNull(messagesToDisplay)) {
            for (Message msg : messagesToDisplay) {
                displayMessage(msg);
                System.out.println("处理暂存消息" + msg.getContent());
            }
        }
    }

    public void displayMessage(Message msg) {
        String info = msg.getSender() + " " + msg.getTime() + "\n" + msg.getContent() + "\n";
        System.out.println(info);
        receiveInformation.appendText(info);
    }

    public void showChatStage(String senderId, String receiverId, String status, Parent chat) {
        // 如果聊天框已打开，将其置顶显示而非新建
        Stage exsitChatStage = ManageChatView.openChatStages.get(receiverId);
        if (exsitChatStage != null) {
            exsitChatStage.toFront();
        } else {
            // 初始化聊天框
            setChatStage(receiverId, status);
            Stage chatStage = new Stage();
            chatStage.setResizable(false);
            chatStage.setTitle(senderId + "正在与" + receiverId + "聊天");
            chatStage.setScene(new Scene(chat));
            chatStage.show();

            // 窗口关闭监听器,chatView关闭后移出Map
            chatStage.setOnCloseRequest(event -> ManageChatView.removeChatStage(receiverId));

            // 存储打开的聊天窗口
            ManageChatView.addChatStage(receiverId, chatStage);
            ManageChatView.addChatController(receiverId, this);

            // 处理暂存的消息
            processBufferedMessages(receiverId);

            // 鼠标点击事件
            sendInformationButton.setOnMouseClicked(event -> {
                if (sendInformation.getText().isEmpty()) {
                    sendInformation.setStyle("-fx-border-color: red");
                } else {
                    sendInformation.setStyle("-fx-border-color: gray");
                    onSendButtonClick(senderId, receiverId);
                }
            });
        }
    }

    // 设置聊天框信息
    public void setChatStage(String accountId, String status) {
        chatUserId.setText(accountId);
        chatUserStatus.setText(status);
    }

    // 当点击发送按钮时，将消息发送到服务器
    public void onSendButtonClick(String senderId, String receiverId) {
        Message msg = new Message();
        msg.setSender(senderId);
        msg.setReceiver(receiverId);
        msg.setContent(sendInformation.getText());
        msg.setTime();
        msg.setMsgType(MessageType.COMMON_MESSAGE);
        // 发送消息到服务器
        try {
            // 取得对应userId的socket来建立输出流
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(msg);
            oos.flush();
            sendInformation.clear();
            String info = "->> 你 " + msg.getTime() + "\n" + msg.getContent() + "\n";
            System.out.println(info);
            receiveInformation.appendText(info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
