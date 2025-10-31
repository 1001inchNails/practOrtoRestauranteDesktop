package com.example.practortorestaurantedesktopp;

import com.example.practortorestaurantedesktopp.api.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommsManager.getInstance().setMainController(this);
        ApiClient apiClient = ApiClient.getInstance();

        mainTabPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustTabWidths();
        });
        adjustTabWidths();
        leerPedidoMesaDeBBDD(apiClient, "Mesa1");
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
                                var dataArray = jsonObject.getAsJsonArray("data");
                                System.out.println("Data array: " + dataArray);


                                dataArray.forEach(item -> {
                                    var pedidoObj = item.getAsJsonObject();
                                    System.out.println("*** Pedido Obj: " + pedidoObj);

                                    boolean haSidoServido = pedidoObj.has("haSidoServido")
                                            && pedidoObj.get("haSidoServido").getAsBoolean();

                                    if (!haSidoServido){

                                        if (pedidoObj.has("pedidos")) {
                                            var pedidosArray = pedidoObj.getAsJsonArray("pedidos");
                                            pedidosArray.forEach(p -> {
                                                var producto = p.getAsJsonObject();
                                                int id = producto.get("id").getAsInt();
                                                String nombre = producto.get("nombre").getAsString();
                                                double precio = producto.get("precio").getAsDouble();
                                                String descripcion = producto.get("descripcion").getAsString();
                                                System.out.printf("→ Producto %d: %s (%.2f) - %s%n", id, nombre, precio, descripcion);
                                            });
                                        }

                                    }
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


    public void manejarPedido(WebSocketController.Message mensaje) {
        System.out.println("Main: "+mensaje);
        System.out.println("Main: "+mensaje.type);
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


        Button cancelarBtn = getCancelarButton(numeroMesa);
        Button enviarBtn = getEnviarButton(numeroMesa);

        if (cancelarBtn != null && enviarBtn != null) {
            cancelarBtn.setVisible(true);
            enviarBtn.setVisible(true);

            Platform.runLater(() -> {
                System.out.println("implementar movidas BBDD, leer todos los pedidos no servidos, (cojer la mongoId) implementar funcionalidad botones enviar, cancelar pedido");
            });
        }
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