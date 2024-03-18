module top.fexample.qclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens top.fexample.qchat to javafx.fxml;
    opens top.fexample.qchat.controller to javafx.fxml;
    exports top.fexample.qchat;
    exports top.fexample.qchat.controller to javafx.fxml;

}