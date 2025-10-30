// CommunicationManager.java
package com.example.practortorestaurantedesktopp;

public class CommsManager {
    private static CommsManager instance;
    private MainController mainController;

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

    public void notificarPedido(WebSocketController.Message mensaje) {
        if (mainController != null) {
            mainController.manejarPedido(mensaje);
        }
    }
}