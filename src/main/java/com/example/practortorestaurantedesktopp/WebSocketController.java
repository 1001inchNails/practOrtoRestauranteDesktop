package com.example.practortorestaurantedesktopp;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;

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

    private AudioClip notificacionSound;
    private double notificacionVolumen = 1.0;   // volumen (0.0 a 1.0)

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        CommsManager.getInstance().setWebSocketController(this);

        try {
            URL soundUrl = getClass().getResource("/sounds/notificacion.mp3");
            if (soundUrl != null) {
                notificacionSound = new AudioClip(soundUrl.toString());

                notificacionSound.setVolume(notificacionVolumen);
            } else {
                System.err.println("No se pudo cargar el archivo de sonido");
            }
        } catch (Exception e) {
            System.err.println("Error cargando el sonido: " + e.getMessage());
        }

        connectWebSocket("Restaurante");
    }

    private void connectWebSocket(String clientId) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("ws://localhost:8025/websocket/" + clientId));
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> messagesArea.appendText("Fallo al conectar a servidor: " + e.getMessage() + "\n"));
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Platform.runLater(() -> messagesArea.appendText("Conectado a servidor.\n"));
    }

    @OnMessage
    public void onMessage(String message) {
        // "enrutado" para mensajes entrantes
        Platform.runLater(() -> {
            try {
                Message msg = gson.fromJson(message, Message.class);

                // sonido para mensajes no propios
                if (!Objects.equals(msg.sender, "Restaurante")) {
                    reproducirSonidoAlerta();
                }

                if (Objects.equals(msg.type, "chat")) {
                    CommsManager.getInstance().webSocketAmain(msg);
                    messagesArea.appendText("Enviado por: "+msg.sender+" / Mensaje: " + msg.message + " / Timestamp: " + msg.timestamp+"\n");
                }
                else if (Objects.equals(msg.type, "pedido")) {
                    System.out.println("pedido incoming");
                    CommsManager.getInstance().webSocketAmain(msg);
                    CommsManager.getInstance().notificarPedido(msg);
                    CommsManager.getInstance().sendLastPedidoBarraInferior("Nuevo pedido: "+msg.sender+" / Hora: " + timestampAHora(msg.timestamp));
                    messagesArea.appendText("Pedido recibido: " + msg.message + "\n");
                }
                else if (Objects.equals(msg.type, "error")) {

                    messagesArea.appendText(msg.sender+": Error de conexion: " + msg.message + "\n");
                }
                else if (Objects.equals(msg.type, "success") && !Objects.equals(msg.sender, "Restaurante")) {
                    messagesArea.appendText("success / "+msg.sender+": " + msg.message + "\n");
                }
                else if (Objects.equals(msg.type, "client_connect") && !Objects.equals(msg.sender, "Restaurante")) {

                    messagesArea.appendText("Cliente conectado / "+msg.sender+": " + msg.message + "\n");
                    CommsManager.getInstance().webSocketAmain(msg);
                }
                else if (Objects.equals(msg.type, "client_disconnect")) {
                    messagesArea.appendText("Cliente desconectado / "+msg.sender+": " + msg.message + "\n");
                    CommsManager.getInstance().webSocketAmain(msg);
                }

            } catch (Exception e) {
                messagesArea.appendText("Recibido: " + message + "\n");
            }
        });
    }

    @OnClose
    public void onClose() {
        Platform.runLater(() -> messagesArea.appendText("Desconectado de servidor.\n"));
    }

    @OnError
    public void onError(Throwable error) {
        Platform.runLater(() -> messagesArea.appendText("Error WebSocket: " + error.getMessage() + "\n"));
    }

    // ademas mandan info a log
    public void enviarMensajeChat(String message, String destino) {
        if (session != null && session.isOpen()) {
            Message msg = new Message("chat", message, "Restaurante", destino);
            messagesArea.appendText("Mensaje enviado a "+msg.destino+" / Mensaje: "+msg.message+" / timestamp: "+msg.timestamp+ " / Type: "+msg.type);
            session.getAsyncRemote().sendText(gson.toJson(msg));
        }else{
            System.out.println("Well shit. Server down");
        }
    }

    public void enviarMensajePedidoEnviadoAmesaAppM(String destino) {
        if (session != null && session.isOpen()) {
            Message msg = new Message("pedido_enviado_a_mesa", "", "Restaurante", destino);
            messagesArea.appendText("Mensaje enviado a "+msg.destino+" / Mensaje: "+msg.message+" / timestamp: "+msg.timestamp+ " / Type: "+msg.type);
            session.getAsyncRemote().sendText(gson.toJson(msg));
        } else {
            System.out.println("Well shit. Server down");
        }
    }

    public void enviarMensajePedidoCanceladoAmesaAppM(String destino) {
        if (session != null && session.isOpen()) {
            Message msg = new Message("pedido_cancelado_a_mesa", "", "Restaurante", destino);
            messagesArea.appendText("Mensaje enviado  a "+msg.destino+" / Mensaje: "+msg.message+" / timestamp: "+msg.timestamp+ " / Type: "+msg.type);
            session.getAsyncRemote().sendText(gson.toJson(msg));
        } else {
            System.out.println("Well shit. Server down");
        }
    }

    // getter para volumen
    public double getNotificacionVolumen() {
        return notificacionVolumen;
    }

    // setter para volumen
    public void setNotificacionVolumen(double volume) {
        if (volume < 0.0) {
            this.notificacionVolumen = 0.0;
        } else if (volume > 1.0) {
            this.notificacionVolumen = 1.0;
        } else {
            this.notificacionVolumen = volume;
        }
        // actualizar
        if (notificacionSound != null) {
            notificacionSound.setVolume(this.notificacionVolumen);
        }
    }

    // para cambiar el volumen desde MainController
    public void cambiarVolumenNotificacion(double nuevoVolumen) {
        setNotificacionVolumen(nuevoVolumen);
    }

    private void reproducirSonidoAlerta() {
        try {
            if (notificacionSound != null) {
                notificacionSound.play();
            }
        } catch (Exception e) {
            System.err.println("Error reproduciendo sonido: " + e.getMessage());
        }
    }

    private String timestampAHora(long timestamp) {
        return java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                java.time.ZoneId.systemDefault()
        ).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static class Message {
        public String type;
        public String message;
        public String sender;
        public String destino;
        public long timestamp;

        public Message(String type, String message, String sender, String destino) {
            this.type = type;
            this.message = message;
            this.sender = sender;
            this.destino = destino;
            this.timestamp = System.currentTimeMillis();
        }
    }
}