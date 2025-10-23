package com.example.practortorestaurantedesktopp;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ChatClient extends Application {
    private Session session;
    private TextArea messagesArea;
    private TextField inputField;
    private static final Gson gson = new Gson();

    @Override
    public void start(Stage stage) {
        messagesArea = new TextArea();
        messagesArea.setEditable(false);
        messagesArea.setWrapText(true);

        inputField = new TextField();
        inputField.setPromptText("Enter message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        VBox root = new VBox(10, messagesArea, inputField, sendButton);
        root.setPadding(new Insets(10));

        stage.setTitle("JavaFX WebSocket Chat");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();

        connectWebSocket("javafx-client");
    }

    private void connectWebSocket(String clientId) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://localhost:8025/websocket/" + clientId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Platform.runLater(() -> messagesArea.appendText("Connected to server.\n"));
    }

    @OnMessage
    public void onMessage(String message) {
        Platform.runLater(() -> {
            Message msg = gson.fromJson(message, Message.class);
            messagesArea.appendText(msg.sender + ": " + msg.message + "\n");
        });
    }

    @OnClose
    public void onClose() {
        Platform.runLater(() -> messagesArea.appendText("Disconnected.\n"));
    }

    private void sendMessage() {
        if (session != null && session.isOpen()) {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                Message msg = new Message("javafx-client", text);
                session.getAsyncRemote().sendText(gson.toJson(msg));
                inputField.clear();
            }
        }
    }

    public static class Message {
        public String sender;
        public String message;

        public Message(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }
    }


    public static void main(String[] args) {
        launch();
    }
}