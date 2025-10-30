package com.example.practortorestaurantedesktopp;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.websocket.*;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@ClientEndpoint
public class WebSocketController implements Initializable {

    private Session session;
    private static final Gson gson = new Gson();

    @FXML
    private TextArea messagesArea;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connectWebSocket("javafx-client");

        inputField.setOnAction(event -> sendMessage());
    }

    private void connectWebSocket(String clientId) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://localhost:8025/websocket/" + clientId));
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> messagesArea.appendText("Failed to connect to server: " + e.getMessage() + "\n"));
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
            try {
                Message msg = gson.fromJson(message, Message.class);
                System.out.println("BEEEEEP: "+ msg.type);
                if (Objects.equals(msg.type, "chat")) {
                    messagesArea.appendText(msg.sender + ": " + msg.message + "\n");
                }
                if (Objects.equals(msg.type, "pedido")) {
                    CommsManager.getInstance().notificarPedido(msg);

                }

            } catch (Exception e) {
                messagesArea.appendText("Received: " + message + "\n");
            }
        });
    }

    @OnClose
    public void onClose() {
        Platform.runLater(() -> messagesArea.appendText("Disconnected from server.\n"));
    }

    @OnError
    public void onError(Throwable error) {
        Platform.runLater(() -> messagesArea.appendText("WebSocket error: " + error.getMessage() + "\n"));
    }

    @FXML
    private void sendMessage() {
        if (session != null && session.isOpen()) {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                Message msg = new Message("chat", text, "Restaurante");
                session.getAsyncRemote().sendText(gson.toJson(msg));
                inputField.clear();
                Platform.runLater(() -> {
                    messagesArea.appendText("Yo: " + msg.message + "\n");
                });
            }
        } else {
            messagesArea.appendText("Not connected to server.\n");
        }
    }

    public static class Message {
        public String type;
        public String message;
        public String sender;
        public long timestamp;

        public Message(String type, String message, String sender) {
            this.type = type;
            this.message = message;
            this.sender = sender;
            this.timestamp = System.currentTimeMillis();
        }
    }
}