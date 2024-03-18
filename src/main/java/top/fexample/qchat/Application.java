package top.fexample.qchat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.fexample.qchat.controller.LoginController;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 加载登录界面
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("views/LoginView.fxml"));
        Parent loginView = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.setLoginStage(stage);
        stage.setTitle("QChat");
        stage.setScene(new Scene(loginView));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("images/qq.gif")).toExternalForm()));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}