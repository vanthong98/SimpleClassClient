module com.hw.simpleclassclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.hw.core to javafx.fxml;
    exports com.hw.core;
    exports com.hw.app;
    opens com.hw.app to javafx.fxml;
}