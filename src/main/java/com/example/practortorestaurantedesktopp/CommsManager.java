// CommunicationManager.java
package com.example.practortorestaurantedesktopp;

public class CommsManager {
    private static CommsManager instance;
    private MainController mainController;
    private WebSocketController webSocketController;

    private CommsManager() {}

    public static CommsManager getInstance() {
        if (instance == null) {
            instance = new CommsManager();
        }
        return instance;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setWebSocketController(WebSocketController controller) {
        this.webSocketController = controller;
    }

    public void notificarPedido(WebSocketController.Message mensaje) {
        if (mainController != null) {
            mainController.manejarPedido(mensaje);
        }
    }

    public void webSocketAmain(WebSocketController.Message mensaje) {
        if (mainController != null) {
            mainController.manejarChat(mensaje);
        }
    }

    public void mainAwebSocket(String message, String destino) {
        if (webSocketController != null) {
            webSocketController.enviarMensajeChat(message, destino);
        }
    }

    public void mainAwebSocketPedidoEnviadoAmesa(String destino) {
        if (webSocketController != null) {
            webSocketController.enviarMensajePedidoEnviadoAmesaAppM(destino);
        }
    }

    public void mainAwebSocketPedidoCanceladoAmesa(String destino) {
        if (webSocketController != null) {
            webSocketController.enviarMensajePedidoCanceladoAmesaAppM(destino);
        }
    }
}