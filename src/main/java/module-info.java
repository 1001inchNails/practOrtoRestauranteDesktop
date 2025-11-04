module com.example.practortorestaurantedesktopp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires tyrus.standalone.client;
    requires javafx.graphics;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires java.net.http;
    requires java.desktop;
    requires javafx.media;

    opens com.example.practortorestaurantedesktopp to javafx.fxml;
    exports com.example.practortorestaurantedesktopp;
}