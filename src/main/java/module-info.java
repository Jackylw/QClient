module top.fexample.qclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens top.fexample.qclient to javafx.fxml;
    opens top.fexample.qclient.controller to javafx.fxml;
    exports top.fexample.qclient;
    exports top.fexample.qclient.controller to javafx.fxml;

}