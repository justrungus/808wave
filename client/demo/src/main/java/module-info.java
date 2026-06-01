module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires java.net.http;
    requires com.google.gson;
    requires jaudiotagger;

    opens com.example.models to com.google.gson;
    opens com.example to javafx.fxml;
    exports com.example;
}
