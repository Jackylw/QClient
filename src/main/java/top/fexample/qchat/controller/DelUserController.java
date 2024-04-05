/**
 * @author Jacky Feng
 * @date 2024/4/5 14:35
 */
package top.fexample.qchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import top.fexample.qchat.Application;
import top.fexample.qchat.Service.ManageAddUserController;
import top.fexample.qchat.Service.ManageClientConnectServerThread;
import top.fexample.qchat.common.Message;
import top.fexample.qchat.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DelUserController {
    @FXML
    public Button onDelUserId;
    @FXML
    public TextField delUserId;
    @FXML
    public Label status;
    public String userId;

    public void initialize() {
        delUserId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                delUserId.setStyle(null);
            }
        });
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void showAddStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("views/DelUserView.fxml"));
        Parent root = loader.load();

        DelUserController controller = loader.getController();
        Stage stage = new Stage();
        status = controller.status;
        status.setText("无");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("删除好友");
        stage.show();

        // 提交按钮监听事件
        controller.onDelUserId.setOnAction(event -> {
            controller.onDelUserId(userId);
        });

        // 关闭窗口
        stage.setOnCloseRequest(event -> {
            ManageAddUserController.removeAddUserController(userId);
        });
    }

    public void onDelUserId(String userId) {
        onDelUserId.setDisable(true);
        if (delUserId.getText().isEmpty()) {
            delUserId.setStyle("-fx-border-color: red");
            status.setText("请输入好友ID");
            onDelUserId.setDisable(false);
            return;
        }
        if (delUserId.getText().equals(userId)) {
            status.setText("不能删除自己");
            onDelUserId.setDisable(false);
            return;
        }

        Message message = new Message();
        message.setMsgType(MessageType.DELETE_USER);
        message.setSender(userId);
        message.setReceiver(delUserId.getText());

        // 发送添加好友请求
        try {
            Socket socket = ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        status.setText("等待服务器响应");
        onDelUserId.setDisable(false);
    }

    public void setDelStatus(String statusText) {
        status.setText(statusText);
    }
}
