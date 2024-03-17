/**
 * @author Jacky Feng
 * @date 2024/3/17 16:53
 */
package top.fexample.qclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import top.fexample.qclient.Service.UserClientService;

public class LoginController {
    @FXML
    public TextField inputUserId;
    @FXML
    public PasswordField inputUserPassword;

    // 用于用户的登录/注册
    private UserClientService userClientService = new UserClientService();

    public void initialize() {
        // 监听输入框变化,若输入后不为空,则移除红色边框
        inputUserId.textProperty().addListener((observable, oldValue, newValue)->{
            if (!newValue.isEmpty()){
                inputUserId.setStyle(null);
            }
        });
        inputUserPassword.textProperty().addListener((observable, oldValue, newValue)->{
            if (!newValue.isEmpty()){
                inputUserPassword.setStyle(null);
            }
        });
    }

    @FXML
    public void onLoginButtonClick(ActionEvent actionEvent) {
        // 输入框只要有一个为空,提示并终止后续操作
        if (inputUserId.getText().isEmpty()){
            inputUserId.setStyle("-fx-border-color: red;");
            return;
        }
        if (inputUserPassword.getText().isEmpty()){
            inputUserPassword.setStyle("-fx-border-color: red;");
            return;
        }

        System.out.println("用户输入的账号是:" + inputUserId.getText() + "用户输入的密码是:" + inputUserPassword.getText());

        if (userClientService.checkUser(inputUserId.getText(), inputUserPassword.getText())){

            System.out.println("登录成功");

        }

    }
}
