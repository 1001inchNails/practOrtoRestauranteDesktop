package com.example.practortorestaurantedesktopp;

import com.example.practortorestaurantedesktopp.api.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class MainController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private VBox pedidoContainer1;
    @FXML
    private Button cancelarPedidoBtn1;
    @FXML
    private Button enviarPedidoBtn1;

    @FXML
    private VBox chatContainer1;
    @FXML
    private TextArea messagesArea1;
    @FXML
    private TextField inputField1;
    @FXML
    private Button sendButton1;

    @FXML
    private VBox chatContainer2;
    @FXML
    private TextArea messagesArea2;
    @FXML
    private TextField inputField2;
    @FXML
    private Button sendButton2;

    @FXML
    private VBox chatContainer3;
    @FXML
    private TextArea messagesArea3;
    @FXML
    private TextField inputField3;
    @FXML
    private Button sendButton3;

    @FXML
    private VBox chatContainer4;
    @FXML
    private TextArea messagesArea4;
    @FXML
    private TextField inputField4;
    @FXML
    private Button sendButton4;

    @FXML
    private VBox chatContainer5;
    @FXML
    private TextArea messagesArea5;
    @FXML
    private TextField inputField5;
    @FXML
    private Button sendButton5;



    @FXML
    private VBox pedidoContainer2;
    @FXML
    private Button cancelarPedidoBtn2;
    @FXML
    private Button enviarPedidoBtn2;

    @FXML
    private VBox pedidoContainer3;
    @FXML
    private Button cancelarPedidoBtn3;
    @FXML
    private Button enviarPedidoBtn3;

    @FXML
    private VBox pedidoContainer4;
    @FXML
    private Button cancelarPedidoBtn4;
    @FXML
    private Button enviarPedidoBtn4;

    @FXML
    private VBox pedidoContainer5;
    @FXML
    private Button cancelarPedidoBtn5;
    @FXML
    private Button enviarPedidoBtn5;

    private ApiClient apiClient = null;

    private ArrayList<String> listaIdsMongo1;
    private ArrayList<String> listaIdsMongo2;
    private ArrayList<String> listaIdsMongo3;
    private ArrayList<String> listaIdsMongo4;
    private ArrayList<String> listaIdsMongo5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listaIdsMongo1 = new ArrayList<>();
        listaIdsMongo2 = new ArrayList<>();
        listaIdsMongo3 = new ArrayList<>();
        listaIdsMongo4 = new ArrayList<>();
        listaIdsMongo5 = new ArrayList<>();

        CommsManager.getInstance().setMainController(this);
        apiClient = ApiClient.getInstance();

        // setear handlers pa chat
        setupChatForMesa(1, inputField1, sendButton1, messagesArea1);
        setupChatForMesa(2, inputField2, sendButton2, messagesArea2);
        setupChatForMesa(3, inputField3, sendButton3, messagesArea3);
        setupChatForMesa(4, inputField4, sendButton4, messagesArea4);
        setupChatForMesa(5, inputField5, sendButton5, messagesArea5);

        mainTabPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustTabWidths();
        });
        adjustTabWidths();

        //leerPedidoMesaDeBBDD(apiClient, "Mesa1");// test, borrar

    }

    private void setupChatForMesa(int mesaNumero, TextField inputField, Button sendButton, TextArea messagesArea) {
        // setear action pal butt
        sendButton.setOnAction(event -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                CommsManager.getInstance().mainAwebSocket(text, "Mesa" + mesaNumero);
                messagesArea.appendText("Restaurante: " + text + "\n");
                inputField.clear();
            }
        });

        inputField.setOnAction(event -> {
            sendButton.fire();
        });
    }

    public void manejarChat(WebSocketController.Message mensaje) {
        String sender = mensaje.sender;
        System.out.println("MC sender: "+sender);
        int numeroMesa = 0;
        switch (sender){
            case "Mesa1": numeroMesa = 1;
                break;
            case "Mesa2": numeroMesa = 2;
                break;
            case "Mesa3": numeroMesa = 3;
                break;
            case "Mesa4": numeroMesa = 4;
                break;
            case "Mesa5": numeroMesa = 5;
                break;
        }
        System.out.println("MC numeroMesa: "+numeroMesa);
        System.out.println("MC mensaje.message: "+mensaje.message);
            TextArea targetArea = getMensajeArea(numeroMesa);
            if (targetArea != null) {
                Platform.runLater(() -> {
                    targetArea.appendText(mensaje.sender + ": " + mensaje.message + "\n");
                });
            }

    }

    private TextArea getMensajeArea(int mesaNumero) {
        switch (mesaNumero) {
            case 1: return messagesArea1;
            case 2: return messagesArea2;
            case 3: return messagesArea3;
            case 4: return messagesArea4;
            case 5: return messagesArea5;
            default: return null;
        }
    }

    private void leerPedidoMesaDeBBDD(ApiClient cliente, String mesa) {
        cliente.readMesa(mesa)
                .thenAccept(json -> {
                    try {
                        var jsonObject = json.getAsJsonObject();
                        System.out.println("jsonObject: "+jsonObject);

                        String resultType = jsonObject.get("type").getAsString();
                        boolean success = false;
                        if (Objects.equals(resultType, "success")){
                            success = true;
                        }

                        String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Sin mensaje";

                        if (success) {
                            System.out.println("Victory Achieved: " + message);

                            if (jsonObject.has("data")) {

                                AtomicReference<Double> total = new AtomicReference<>(0.0);
                                var dataArray = jsonObject.getAsJsonArray("data");
                                System.out.println("Data array: " + dataArray);

                                // Obtener el VBox correspondiente a la mesa
                                VBox pedidoContainer = getPedidoContainer(mesa);
                                if (pedidoContainer == null) {
                                    System.err.println("No se encontró contenedor para: " + mesa);
                                    return;
                                }

                                // Limpiar el contenedor y agregar nuevos productos EN EL HILO DE JAVAFX
                                Platform.runLater(() -> {
                                    pedidoContainer.getChildren().clear();

                                    dataArray.forEach(item -> {
                                        var pedidoObj = item.getAsJsonObject();
                                        System.out.println("*** Pedido Obj: " + pedidoObj);

                                        // OBTENER EL MONGO ID
                                        String mongoId = pedidoObj.has("_id") ? pedidoObj.get("_id").getAsString() : "Sin ID";
                                        System.out.println("Mongo ID: " + mongoId);
                                        switch (mesa) {
                                            case "Mesa1":
                                                listaIdsMongo1.add(mongoId);
                                                break;
                                            case "Mesa2":
                                                listaIdsMongo2.add(mongoId);
                                                break;
                                            case "Mesa3":
                                                listaIdsMongo3.add(mongoId);
                                                break;
                                            case "Mesa4":
                                                listaIdsMongo4.add(mongoId);
                                                break;
                                            case "Mesa5":
                                                listaIdsMongo5.add(mongoId);
                                                break;
                                            default:
                                                System.out.println("Mesa no reconocida: " + mesa);
                                                break;
                                        }

                                        boolean haSidoServido = pedidoObj.has("haSidoServido")
                                                && pedidoObj.get("haSidoServido").getAsBoolean();

                                        if (!haSidoServido){
                                            if (pedidoObj.has("pedidos")) {
                                                var pedidosArray = pedidoObj.getAsJsonArray("pedidos");

                                                // Agregar el Mongo ID como primer elemento del pedido
                                                Label idLabel = new Label("Pedido ID: " + mongoId);
                                                idLabel.setWrapText(true);
                                                idLabel.setStyle("-fx-padding: 5px; -fx-background-color: #f0f0f0; -fx-font-weight: bold;");
                                                pedidoContainer.getChildren().add(idLabel);

                                                pedidosArray.forEach(p -> {
                                                    var producto = p.getAsJsonObject();
                                                    int id = producto.get("id").getAsInt();
                                                    String nombre = producto.get("nombre").getAsString();
                                                    double precio = producto.get("precio").getAsDouble();
                                                    total.set(total.get() + precio);

                                                    // Crear un elemento visual para mostrar el producto
                                                    String productoText = String.format("→ Producto %d: %s (%.2f)", id, nombre, precio);

                                                    // Agregar al contenedor
                                                    Label productoLabel = new Label(productoText);
                                                    productoLabel.setWrapText(true);
                                                    productoLabel.setStyle("-fx-padding: 5px; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
                                                    pedidoContainer.getChildren().add(productoLabel);
                                                });

                                                // Agregar separador entre pedidos
                                                Separator separator = new Separator();
                                                separator.setStyle("-fx-padding: 5px 0;");
                                                pedidoContainer.getChildren().add(separator);
                                            }
                                        }
                                    });

                                    Label precioLabel = new Label(String.format("→ Precio Total: %.2f", total.get()));
                                    precioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                                    pedidoContainer.getChildren().add(precioLabel);
                                });
                            }

                        } else {
                            System.out.println("You Died: " + message);
                        }

                    } catch (Exception e) {
                        System.err.println("Error procesando la respuesta JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Error en acceso a BBDD: " + throwable.getMessage());
                    return null;
                });
    }

    private VBox getPedidoContainer(String mesa) {
        switch (mesa) {
            case "Mesa1": return pedidoContainer1;
            case "Mesa2": return pedidoContainer2;
            case "Mesa3": return pedidoContainer3;
            case "Mesa4": return pedidoContainer4;
            case "Mesa5": return pedidoContainer5;
            default: return null;
        }
    }
    @FXML
    public void admitirPedidoAmesa() {
        // Determinar qué mesa está activa basándose en la pestaña seleccionada
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            String tabText = selectedTab.getText();
            String sender = "";

            switch (tabText) {
                case "Mesa 1": sender = "Mesa1"; break;
                case "Mesa 2": sender = "Mesa2"; break;
                case "Mesa 3": sender = "Mesa3"; break;
                case "Mesa 4": sender = "Mesa4"; break;
                case "Mesa 5": sender = "Mesa5"; break;
            }

            if (!sender.isEmpty()) {
                cambiarEstadoApedidos(getListaIdsMongo(sender), sender);
            }
        }
    }

    private ArrayList<String> getListaIdsMongo(String mesa) {
        switch (mesa) {
            case "Mesa1": return listaIdsMongo1;
            case "Mesa2": return listaIdsMongo2;
            case "Mesa3": return listaIdsMongo3;
            case "Mesa4": return listaIdsMongo4;
            case "Mesa5": return listaIdsMongo5;
            default: return new ArrayList<>();
        }
    }

    public void cambiarEstadoApedidos(ArrayList lista, String mesa){

    }


    public void manejarPedido(WebSocketController.Message mensaje) {

        String sender =  mensaje.sender;

        int numeroMesa = 0;
        switch (sender){
            case "Mesa1": numeroMesa = 1;
                break;
            case "Mesa2": numeroMesa = 2;
                break;
            case "Mesa3": numeroMesa = 3;
                break;
            case "Mesa4": numeroMesa = 4;
                break;
            case "Mesa5": numeroMesa = 5;
                break;
        }


        leerPedidoMesaDeBBDD(apiClient, sender);

        mostrarBotonesPedido(numeroMesa);



        System.out.println("implementar movidas BBDD, leer todos los pedidos no servidos, (cojer la mongoId) implementar funcionalidad botones enviar, cancelar pedido");

    }

    private void setupTabWidths() {
        mainTabPane.tabMinWidthProperty().bind(mainTabPane.widthProperty().divide(mainTabPane.getTabs().size()).subtract(20) // Small margin
        );

        mainTabPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustTabWidths();
        });
    }

    private void adjustTabWidths() {
        double tabPaneWidth = mainTabPane.getWidth();
        int tabCount = mainTabPane.getTabs().size();

        if (tabCount > 0 && tabPaneWidth > 0) {
            double tabWidth = (tabPaneWidth / tabCount) - 2;
            mainTabPane.setTabMinWidth(tabWidth);
            mainTabPane.setTabMaxWidth(tabWidth);
        }
    }

    public void mostrarBotonesPedido(int mesaNumero) {
        Button cancelarBtn = getCancelarButton(mesaNumero);
        Button enviarBtn = getEnviarButton(mesaNumero);

        if (cancelarBtn != null && enviarBtn != null) {
            cancelarBtn.setVisible(true);
            enviarBtn.setVisible(true);
        }
    }

    public void ocultarBotonesPedido(int mesaNumero) {
        Button cancelarBtn = getCancelarButton(mesaNumero);
        Button enviarBtn = getEnviarButton(mesaNumero);

        if (cancelarBtn != null && enviarBtn != null) {
            cancelarBtn.setVisible(false);
            enviarBtn.setVisible(false);
        }
    }

    private Button getCancelarButton(int mesaNumero) {
        switch (mesaNumero) {
            case 1:
                return cancelarPedidoBtn1;
            case 2:
                return cancelarPedidoBtn2;
            case 3:
                return cancelarPedidoBtn3;
            case 4:
                return cancelarPedidoBtn4;
            case 5:
                return cancelarPedidoBtn5;
            default:
                return null;
        }
    }


    private Button getEnviarButton(int mesaNumero) {
        switch (mesaNumero) {
            case 1:
                return enviarPedidoBtn1;
            case 2:
                return enviarPedidoBtn2;
            case 3:
                return enviarPedidoBtn3;
            case 4:
                return enviarPedidoBtn4;
            case 5:
                return enviarPedidoBtn5;

            default:
                return null;
        }
    }
}