/**
 * @author Jacky Feng
 * @date 2024/3/23 13:59
 */
package top.fexample.qchat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class FriendListUserController {
    @FXML
    private Label accountIdLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView imageHead;


    // 初始化每一个好友的信息
    public void setUserController(String accountId, String status) {
        accountIdLabel.setText(accountId);
        statusLabel.setText(status);

        // 更新在线好友头像
        if (!"online".equals(status)) {
            // 30%透明度和灰色
            imageHead.setStyle("-fx-opacity: 0.3;-fx-background-color: rgba(0, 0, 0, 0.5)");
        }
    }
}
