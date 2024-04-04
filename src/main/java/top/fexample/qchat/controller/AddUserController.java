/**
 * @author Jacky Feng
 * @date 2024/4/4 17:21
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

public class AddUserController {
    @FXML
    public Label status;
    @FXML
    public TextField addUserId;
    @FXML
    public Button onAddUserId;
    public String userId;

    public void initialize() {
        addUserId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                addUserId.setStyle(null);
            }
        });
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void showAddStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("views/AddUserView.fxml"));
        Parent root = loader.load();

        AddUserController controller = loader.getController();
        Stage stage = new Stage();
        status = controller.status;
        status.setText("无");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("添加好友");
        stage.show();

        // 提交按钮监听事件
        controller.onAddUserId.setOnAction(event -> {
            controller.onAddUserId(userId);
        });

        // 关闭窗口
        stage.setOnCloseRequest(event -> {
            ManageAddUserController.removeAddUserController(userId);
        });
    }

    public void onAddUserId(String userId) {
        onAddUserId.setDisable(true);
        if (addUserId.getText().isEmpty()) {
            addUserId.setStyle("-fx-border-color: red");
            status.setText("请输入好友ID");
            onAddUserId.setDisable(false);
            return;
        }
        if (addUserId.getText().equals(userId)) {
            status.setText("不能添加自己为好友");
            onAddUserId.setDisable(false);
            return;
        }

        Message message = new Message();
        message.setMsgType(MessageType.ADD_USER);
        message.setSender(userId);
        message.setReceiver(addUserId.getText());

        // 发送添加好友请求
        try {
            Socket socket = ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        status.setText("等待服务器响应");
        onAddUserId.setDisable(false);
    }

    public void setAddStatus(String statusText) {
        status.setText(statusText);
    }
}
