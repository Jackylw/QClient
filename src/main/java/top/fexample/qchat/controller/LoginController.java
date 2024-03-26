/**
 * @author Jacky Feng
 * @date 2024/3/17 16:53
 */
package top.fexample.qchat.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import top.fexample.qchat.Service.ManageFriendList;
import top.fexample.qchat.Service.UserClientService;
import top.fexample.qchat.common.MessageType;
import top.fexample.qchat.common.UserType;

public class LoginController {
    @FXML
    public TextField inputUserId;
    @FXML
    public PasswordField inputUserPassword;
    public Stage loginStage;
    @FXML
    public TextField regUserId;
    @FXML
    public PasswordField regPassword;
    @FXML
    public PasswordField regCheckPassword;
    @FXML
    public TextField regUserAnswer;
    @FXML
    public TextField regUserQuestion;
    @FXML
    public Button regButton;
    @FXML
    public Button loginButton;

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
        regUserId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                regUserId.setStyle(null);
            }
        });
        regPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                regPassword.setStyle(null);
            }
        });
        regCheckPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                regCheckPassword.setStyle(null);
            }
        });
        regUserQuestion.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                regUserQuestion.setStyle(null);
            }
        });
        regUserAnswer.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                regUserAnswer.setStyle(null);
            }
        });
    }

    @FXML
    public void onLoginButtonClick() {
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

        // 禁用登录按钮
        loginButton.setDisable(true);

        switch (userClientService.checkUser(inputUserId.getText(), inputUserPassword.getText(), UserType.LOGIN)) {
            case MessageType.LOGIN_SUCCESS:
                showPrompt(MessageType.LOGIN_SUCCESS);
                loginButton.setDisable(false);

                System.out.println("登录成功");

                // 显示好友列表,并隐藏登录界面
                loginStage.close();

                FriendListController friendListController = new FriendListController();
                friendListController.setUserId(inputUserId.getText());
                friendListController.showFriendListStage();

                // 将好友列表添加到管理列表中
                ManageFriendList.addFriendList(inputUserId.getText(), friendListController);
                break;
            case MessageType.LOGIN_FAIL:
                showPrompt(MessageType.LOGIN_FAIL);
                loginButton.setDisable(false);

                System.out.println("密码错误");

                inputUserPassword.clear();
                break;
            case MessageType.CONNECT_SERVER_TIMEOUT:
                showPrompt(MessageType.CONNECT_SERVER_TIMEOUT);
                loginButton.setDisable(false);

                System.out.println("连接服务器超时");

                break;
            default:
                break;
        }
    }

    public void onRegButtonClick() {
        if (regUserId.getText().isEmpty()) {
            regUserId.setStyle("-fx-border-color: red;");
            return;
        }
        if (regPassword.getText().isEmpty()) {
            regPassword.setStyle("-fx-border-color: red;");
            return;
        }
        if (regCheckPassword.getText().isEmpty()) {
            regCheckPassword.setStyle("-fx-border-color: red;");
            return;
        }
        if (regUserQuestion.getText().isEmpty()) {
            regUserQuestion.setStyle("-fx-border-color: red;");
            return;
        }
        if (regUserAnswer.getText().isEmpty()) {
            regUserAnswer.setStyle("-fx-border-color: red;");
            return;
        }

        if (!regPassword.getText().equals(regCheckPassword.getText())) {
            showPrompt("两次输入的密码不一致");
            return;
        }

        // 禁用注册按钮
        regButton.setDisable(true);
        switch (userClientService.registerUser(regUserId.getText(), regPassword.getText(), regUserQuestion.getText(), regUserAnswer.getText(), UserType.REGISTER)) {
            case MessageType.REGISTER_SUCCESS:
                showPrompt(MessageType.REGISTER_SUCCESS);
                regButton.setDisable(false);

                System.out.println("注册成功");

                regUserId.clear();
                regPassword.clear();
                regCheckPassword.clear();
                regUserQuestion.clear();
                regUserAnswer.clear();

                break;
            case MessageType.REGISTER_EXIST:
                showPrompt(MessageType.REGISTER_EXIST);
                regButton.setDisable(false);

                System.out.println("用户已存在");

                break;
            case MessageType.REGISTER_ERROR:
                showPrompt(MessageType.REGISTER_ERROR);
                regButton.setDisable(false);

                System.out.println("注册失败");

                break;
            case MessageType.CONNECT_SERVER_TIMEOUT:
                showPrompt(MessageType.CONNECT_SERVER_TIMEOUT);
                regButton.setDisable(false);

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
