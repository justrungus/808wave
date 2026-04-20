module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires javafx.graphics;
    requires java.net.http;
    requires com.google.gson;

    opens com.example.models to com.google.gson;
    opens com.example to javafx.fxml;
    exports com.example;
}
