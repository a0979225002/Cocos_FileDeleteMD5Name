module tw.com.lipin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.jfoenix;


    opens tw.com.lipin to javafx.fxml;
    opens tw.com.lipin.controller to javafx.fxml;

    exports tw.com.lipin;
}