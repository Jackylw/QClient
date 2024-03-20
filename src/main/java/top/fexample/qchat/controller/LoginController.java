/**
 * @author Jacky Feng
 * @date 2024/3/17 16:53
 */
package top.fexample.qchat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import top.fexample.qchat.Application;
import top.fexample.qchat.Service.UserClientService;
import top.fexample.qchat.common.MessageType;

public class LoginController {
    @FXML
    public TextField inputUserId;
    @FXML
    public PasswordField inputUserPassword;
    public Stage loginStage;

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    // 用于用户的登录/注册
    private UserClientService userClientService = new UserClientService();

    public void initialize() {
        // 监听输入框变化,若输入后不为空,则移除红色边框
        inputUserId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                inputUserId.setStyle(null);
            }
        });
        inputUserPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                inputUserPassword.setStyle(null);
            }
        });
    }

    @FXML
    public void onLoginButtonClick(ActionEvent actionEvent) {
        // 输入框只要有一个为空,提示并终止后续操作
        if (inputUserId.getText().isEmpty()) {
            inputUserId.setStyle("-fx-border-color: red;");
            return;
        }
        if (inputUserPassword.getText().isEmpty()) {
            inputUserPassword.setStyle("-fx-border-color: red;");
            return;
        }

        System.out.println("用户输入的账号是:" + inputUserId.getText() + "用户输入的密码是:" + inputUserPassword.getText());

        switch (userClientService.checkUser(inputUserId.getText(), inputUserPassword.getText())) {
            case MessageType.LOGIN_SUCCESS:
                showPrompt(MessageType.LOGIN_SUCCESS);

                System.out.println("登录成功");

                // 显示好友列表,并隐藏登录界面
                new FriendListController().showFriendListStage(inputUserId.getText());
                loginStage.close();
                break;
            case MessageType.LOGIN_FAIL:
                showPrompt(MessageType.LOGIN_FAIL);

                System.out.println("登录失败");

                break;
            case MessageType.CONNECT_SERVER_TIMEOUT:
                showPrompt(MessageType.CONNECT_SERVER_TIMEOUT);

                System.out.println("连接服务器超时");

                break;
        }
    }

    // 设置提示信息
    public void showPrompt(String messageType) {
        Label label = new Label("状态" + messageType);
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 250, 150);
        Stage stage = new Stage();
        pane.setCenter(label);
        stage.setScene(scene);
        stage.setTitle(messageType);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
